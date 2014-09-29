/*
 * Dog - Addons
 * 
 * Copyright (c) 2011-2014 Luigi De Russis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.polito.elite.dog.addons.powermodel;

import it.polito.elite.dog.addons.powermodel.api.DevicePowerConsumption;
import it.polito.elite.dog.addons.powermodel.api.PowerModel;
import it.polito.elite.dog.addons.powermodel.device.PowerDevice;
import it.polito.elite.dog.addons.powermodel.device.PowerState;
import it.polito.elite.dog.core.housemodel.semantic.api.OntologyModel;
import it.polito.elite.dog.core.library.model.DeviceCostants;
import it.polito.elite.dog.core.library.semantic.OWLWrapper;
import it.polito.elite.dog.core.library.semantic.xml.Ontologies;
import it.polito.elite.dog.core.library.util.LogHelper;

import java.io.File;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Power;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Extend the Semantic House Model capabilities with power consumption
 * measurement
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 *
 */
public class PowerOntModel implements ManagedService, PowerModel
{
	// OSGi context
	private BundleContext context;
	
	// logger
	private LogHelper logger;
	
	// reference to the Semantic House Model
	private AtomicReference<OntologyModel> houseModel;
	
	// registration object for this bundle
	private ServiceRegistration<?> powerModelRegistration;
	
	// the ontology model representing the power information
	private OWLOntology powerModel;
	
	// map for storing devices power consumption
	private ConcurrentMap<String, PowerDevice> deviceConsumptions;
	
	// ontology descriptor
	private Ontologies ontDescSet;
	
	// JAXB context for reading the XML ontology descriptor
	private JAXBContext jaxbContext;
	
	// ontology model IRI
	private String modelIRI;
	
	// reference to the OWL utility class
	private OWLWrapper owlWrapper;
	
	/**
	 * Default constructor, init need variables
	 */
	public PowerOntModel()
	{
		// init the Atomic reference to the Semantic House Model
		this.houseModel = new AtomicReference<OntologyModel>();
		
		// init the power consumption map
		this.deviceConsumptions = new ConcurrentHashMap<String, PowerDevice>();
	}
	
	/**
	 * Bundle activation, stores a reference to the context object passed by the
	 * framework to get access to system data, e.g., installed bundles, etc.
	 * 
	 * @param context
	 *            the bundle context
	 */
	public void activate(BundleContext context)
	{
		// store the bundle context
		this.context = context;
		
		// init the logger
		this.logger = new LogHelper(this.context);
		
		// log the bundle activation
		this.logger.log(LogService.LOG_INFO, "Activated....");
	}
	
	/**
	 * Prepare the bundle to be deactivated...
	 */
	public void deactivate()
	{
		// null the context
		this.context = null;
		
		// log deactivation
		this.logger.log(LogService.LOG_INFO, "Deactivated...");
		
		// null the logger
		this.logger = null;
	}
	
	/**
	 * Bind the OntologyModel service (before the bundle activation)
	 * 
	 * @param houseModel
	 *            the OntologyModel service to add
	 */
	public void addedOntologyModel(OntologyModel houseModel)
	{
		// store a reference to the HouseModel service
		this.houseModel.set(houseModel);
	}
	
	/**
	 * Unbind the OntologyModel service
	 * 
	 * @param houseModel
	 *            the OntologyModel service to remove
	 */
	public void removedOntologyModel(OntologyModel houseModel)
	{
		this.houseModel.compareAndSet(houseModel, null);
	}
	
	/**
	 * Listen for the configuration and start the XML parsing of the ontology
	 * descriptor...
	 */
	@Override
	public void updated(Dictionary<String, ?> properties)
	{
		if (properties != null)
		{
			// log the update data received
			this.logger.log(LogService.LOG_DEBUG, "received power ontology configuration...");
			
			// get the ontology descriptor file name
			String ontologyFileName = (String) properties.get(DeviceCostants.ONTOLOGY);
			
			if (ontologyFileName != null && !ontologyFileName.isEmpty())
			{
				try
				{
					// init
					this.jaxbContext = JAXBContext.newInstance(Ontologies.class.getPackage().getName());
					Unmarshaller unmarshaller = this.jaxbContext.createUnmarshaller();
					
					// check absolute vs relative
					File xmlFile = new File(ontologyFileName);
					if (!xmlFile.isAbsolute())
						ontologyFileName = System.getProperty("configFolder") + "/" + ontologyFileName;
					
					// unmarshall the ontology descriptor
					this.ontDescSet = (unmarshaller.unmarshal(new StreamSource(ontologyFileName), Ontologies.class))
							.getValue();
				}
				catch (JAXBException e)
				{
					this.logger.log(LogService.LOG_ERROR, "JAXB Error", e);
				}
				
				// start loading the new ontology, if the Semantic House Model
				// is available
				if (this.houseModel.get() != null)
				{
					// set the model IRI
					this.modelIRI = this.ontDescSet.getEntryPoint().getHref();
					// delegate the model loding to the Semantic House Model
					this.delegateModelLoading(this.ontDescSet);
				}
			}
			
		}
		
	}
	
	/**
	 * Delegates the {@link SemanticHouseModel} to load the power ontology used
	 * by this power model.
	 * 
	 * @param ontDescSet
	 *            the {@link OntologyDescriptorSet} describing the power
	 *            ontology to load
	 */
	private void delegateModelLoading(Ontologies ontDescSet)
	{
		// ask the Ontology Model to merge the power model with the house model
		this.houseModel.get().loadAndMerge(ontDescSet);
		
		// debug
		this.logger.log(LogService.LOG_DEBUG, "Power model loaded successfully...");
		
		// get the loaded submodel
		this.owlWrapper = this.houseModel.get().getSubModel(this.modelIRI);
		// obtain the OntModel object and set it as the internal model
		this.powerModel = this.owlWrapper.getOntModel();
		
		// init the ontology model
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new PowerModelInitializer(this));
		executor.shutdown();
	}
	
	/**
	 * Register the services exported by the bundle
	 */
	protected void registerServices()
	{
		this.powerModelRegistration = this.context.registerService(PowerModel.class.getName(), this, null);
		
	}
	
	/**
	 * Unregister the services exported by the bundle
	 */
	public void unRegisterServices()
	{
		if (this.powerModelRegistration != null)
			this.powerModelRegistration.unregister();
		
	}
	
	/**
	 * This method gets the actual device consumption in a declared state, if
	 * any
	 * 
	 * @param deviceURI
	 *            the URI of the device
	 * @param stateName
	 *            the name of the state that gives the consumption we are
	 *            interested in
	 * 
	 * @return the {@link DevicePowerConsumption} storing the deviceURI and the
	 *         actual consumption. Return null if the actual consumption, the
	 *         state or the device does not exist.
	 */
	@Override
	public DevicePowerConsumption getActualDeviceConsumption(String deviceURI, String stateName)
	{
		PowerDevice device = null;
		
		if (this.deviceConsumptions.containsKey(deviceURI))
		{
			device = this.deviceConsumptions.get(deviceURI);
			
			for (PowerState state : device.getStateConsumptions())
			{
				if (state.getStateName().equals(stateName) && state.hasActualConsumption())
					return new DevicePowerConsumption(deviceURI, state.getActualConsumption());
			}
		}
		else
		{
			this.logger.log(LogService.LOG_ERROR, deviceURI + " has no declared power consumption.");
		}
		
		return null;
	}
	
	/**
	 * This method gets the nominal device consumption in a declared state, if
	 * any
	 * 
	 * @param deviceURI
	 *            the URI of the device
	 * @param stateName
	 *            the name of the state that gives the consumption we are
	 *            interested in
	 * 
	 * @return the {@link DevicePowerConsumption} storing the deviceURI and the
	 *         nominal consumption. Return null if the nominal consumption, the
	 *         state or the device does not exist.
	 */
	@Override
	public DevicePowerConsumption getNominalDeviceConsumption(String deviceURI, String stateName)
	{
		PowerDevice device = null;
		
		if (this.deviceConsumptions.containsKey(deviceURI))
		{
			device = this.deviceConsumptions.get(deviceURI);
			
			for (PowerState state : device.getStateConsumptions())
			{
				if (state.getStateName().equals(stateName) && state.hasNominalConsumption())
					return new DevicePowerConsumption(deviceURI, state.getNominalConsumption());
			}
		}
		else
		{
			this.logger.log(LogService.LOG_ERROR, deviceURI + " has no declared power consumption.");
		}
		
		return null;
	}
	
	/**
	 * This method gets the typical device consumption in a declared state, if
	 * any
	 * 
	 * @param deviceURI
	 *            the URI of the device
	 * @param stateName
	 *            the name of the state that gives the consumption we are
	 *            interested in
	 * 
	 * @return the {@link DevicePowerConsumption} storing the deviceURI and the
	 *         typical consumption. Return null if the typical consumption, the
	 *         state or the device does not exist.
	 */
	@Override
	public DevicePowerConsumption getTypicalDeviceConsumption(String deviceURI, String stateName)
	{
		PowerDevice device = null;
		
		if (this.deviceConsumptions.containsKey(deviceURI))
		{
			device = this.deviceConsumptions.get(deviceURI);
			
			for (PowerState state : device.getStateConsumptions())
			{
				if (state.getStateName().equals(stateName) && state.hasTypicalConsumption())
					return new DevicePowerConsumption(deviceURI, state.getTypicalConsumption());
			}
		}
		else
		{
			this.logger.log(LogService.LOG_ERROR, deviceURI + " has no declared power consumption.");
		}
		
		return null;
	}
	
	/**
	 * This method gets the best device consumption in a declared state, if any
	 * 
	 * @param deviceURI
	 *            the URI of the device
	 * @param stateName
	 *            the name of the state that gives the consumption we are
	 *            interested in
	 * 
	 * @return the {@link DevicePowerConsumption} storing the deviceURI and the
	 *         best consumption. Return null if the consumption, the state or
	 *         the device does not exist.
	 */
	@Override
	public DevicePowerConsumption getBestDeviceConsumption(String deviceURI, String stateName)
	{
		// init
		PowerDevice device = null;
		DevicePowerConsumption bestPowerConsumption = null;
		
		if (this.deviceConsumptions.containsKey(deviceURI))
		{
			device = this.deviceConsumptions.get(deviceURI);
			
			DecimalMeasure<Power> bestConsumption = null, nominal = null, actual = null;
			
			for (PowerState state : device.getStateConsumptions())
			{
				if (state.getStateName().equalsIgnoreCase(stateName))
				{
					// has a typical consumption? Set it as the best consumption
					if (state.hasTypicalConsumption())
						bestConsumption = state.getTypicalConsumption();
					
					// has a nominal consumption?
					if (state.hasNominalConsumption())
					{
						nominal = state.getNominalConsumption();
						
						// nominal consumption is less than the best consumption
						// (and the best consumption exists)
						if (bestConsumption == null)
							bestConsumption = nominal;
						else if (bestConsumption.getValue().compareTo(nominal.getValue()) == -1)
							bestConsumption = nominal;
					}
					
					// has a actual consumption?
					if (state.hasActualConsumption())
					{
						actual = state.getActualConsumption();
						
						// actual consumption is less than the best consumption
						// (and the best consumption exists)
						if (bestConsumption == null)
							bestConsumption = actual;
						else if (bestConsumption.getValue().compareTo(actual.getValue()) == -1)
							bestConsumption = actual;
					}
				}
			}
			
			// return the best consumption only if it exists...
			if (bestConsumption != null)
				bestPowerConsumption = new DevicePowerConsumption(deviceURI, bestConsumption);
		}
		else
		{
			// debug
			this.logger.log(LogService.LOG_DEBUG, deviceURI + " has no declared power consumption.");
		}
		
		return bestPowerConsumption;
	}
	
	/**
	 * This method gets the highest power consumption of all devices
	 * 
	 * @return a set of {@link DevicePowerConsumption} storing the highest
	 *         consumption for each device.
	 */
	@Override
	public Set<DevicePowerConsumption> getHighestDeviceConsumptions()
	{
		// init
		Set<DevicePowerConsumption> bestStateConsumption = new HashSet<DevicePowerConsumption>();
		Set<DevicePowerConsumption> highestConsumptions = new HashSet<DevicePowerConsumption>();
		
		for (PowerDevice device : deviceConsumptions.values())
		{
			DecimalMeasure<Power> highestConsumption = DecimalMeasure.valueOf("0.0 W");
			
			for (PowerState state : device.getStateConsumptions())
			{
				// take the most accurate consumption for each device state
				bestStateConsumption.add(getBestDeviceConsumption(device.getDeviceURI(), state.getStateName()));
			}
			// take the higher consumption state
			for (DevicePowerConsumption bestConsumption : bestStateConsumption)
			{
				// if the highest consumption is lower than the best
				// consumption for the current device...
				if ((highestConsumption.getValue().compareTo(bestConsumption.getConsumption().getValue())) == -1)
				{
					highestConsumption = bestConsumption.getConsumption();
				}
			}
			// save the highest consumption
			highestConsumptions.add(new DevicePowerConsumption(device.getDeviceURI(), highestConsumption));
			
			// clear the best consumption set, before passing to another device
			bestStateConsumption.clear();
		}
		
		return highestConsumptions;
	}
	
	/**
	 * @return the owlWrapper
	 */
	public OWLWrapper getOWLWrapper()
	{
		return this.owlWrapper;
	}
	
	/**
	 * @return the powerModel
	 */
	public OWLOntology getPowerModel()
	{
		return this.powerModel;
	}

	/**
	 * @return the logger
	 */
	public LogHelper getLogger()
	{
		return logger;
	}

	/**
	 * @return the deviceConsumptions
	 */
	public ConcurrentMap<String, PowerDevice> getDeviceConsumptions()
	{
		return deviceConsumptions;
	}
	
}
