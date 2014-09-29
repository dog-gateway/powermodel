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

import it.polito.elite.dog.addons.powermodel.device.PowerDevice;
import it.polito.elite.dog.addons.powermodel.device.PowerState;
import it.polito.elite.dog.core.library.semantic.OWLWrapper;
import it.polito.elite.dog.core.library.util.LogHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.log.LogService;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * Init the ontology power model.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * 
 */
public class PowerModelInitializer implements Runnable
{
	// the power model to initialize
	private PowerOntModel modelToInitialize;
	
	// the map holding devices power consumption figures
	private ConcurrentMap<String, PowerDevice> deviceConsumptions;
	
	// logger
	private LogHelper logger;
	
	// the OWL wrapper
	private OWLWrapper owlwrapper;
	
	private final String defaultPrefix = "poweront:";
	
	/**
	 * Default constructor
	 * 
	 * @param modelToInitialize
	 *            the power model to initialize
	 */
	public PowerModelInitializer(PowerOntModel modelToInitialize)
	{
		// init
		this.modelToInitialize = modelToInitialize;
		this.logger = modelToInitialize.getLogger();
		this.deviceConsumptions = modelToInitialize.getDeviceConsumptions();
		this.owlwrapper = modelToInitialize.getOWLWrapper();
	}
	
	@Override
	public void run()
	{
		// init
		OWLReasoner reasoner = this.owlwrapper.getReasoner();
		
		// info
		logger.log(LogService.LOG_INFO, "Extracting values from PowerOnt...");
		
		// ... reason!
		reasoner.precomputeInferences(InferenceType.values());
		
		// extract all the power consumptions
		Set<String> allConsumptions = this.owlwrapper.getAllIndividual(this.defaultPrefix, "ElectricPowerConsumption");
		for (String name : allConsumptions)
		{
			// get when in
			OWLNamedIndividual whenIn = this.owlwrapper.getSingleObjectProperty(owlwrapper.getOWLIndividual(name),
					this.defaultPrefix, "whenIn");
			Set<OWLClass> types = reasoner.getTypes(whenIn, true).getFlattened();
			
			// create a PowerState for the current state
			PowerState current = new PowerState(this.owlwrapper.getShortFormWithoutPrefix(types.iterator().next()));
			
			// get consumption of
			OWLNamedIndividual consumptionOf = this.owlwrapper.getSingleObjectProperty(
					this.owlwrapper.getOWLIndividual(name), "poweront:", "consumptionOf");
			// get device name
			String deviceName = this.owlwrapper.getShortFormWithoutPrefix(consumptionOf);
			
			// get typical consumption
			this.getConsumption(current, name, "typicalConsumptionValue");
			// get nominal consumption
			this.getConsumption(current, name, "nominalConsumptionValue");
			// get actual consumption
			this.getConsumption(current, name, "actualConsumptionValue");
			
			// check if the devices map contains the current device
			if (!deviceConsumptions.containsKey(deviceName))
			{
				// device and states not yet inserted
				Set<PowerState> stateConsumptions = new HashSet<PowerState>();
				stateConsumptions.add(current);
				deviceConsumptions.put(deviceName, new PowerDevice(deviceName, stateConsumptions));
			}
			else
			{
				// device already present; I need to insert a new state with its
				// consumptions
				PowerDevice retrieved = deviceConsumptions.get(deviceName);
				retrieved.addSinglePowerState(current);
				deviceConsumptions.put(deviceName, retrieved);
			}
		}
		
		// debug
		for (String devName : deviceConsumptions.keySet())
		{
			logger.log(LogService.LOG_DEBUG, devName);
			PowerDevice debug = deviceConsumptions.get(devName);
			for (PowerState state : debug.getStateConsumptions())
			{
				logger.log(LogService.LOG_DEBUG, "\ttypical consumption for " + state.getStateName() + " "
						+ state.getTypicalConsumption().toString());
				if (state.hasNominalConsumption() == true)
					logger.log(LogService.LOG_DEBUG, "\tnominal consumption for " + state.getStateName() + " "
							+ state.getNominalConsumption().toString());
				if (state.hasActualConsumption() == true)
					logger.log(LogService.LOG_DEBUG, "\tactual consumption for " + state.getStateName() + " "
							+ state.getActualConsumption().toString());
			}
		}
		
		// info
		logger.log(LogService.LOG_INFO, "... done!");
		
		// init complete: it is time to register the service!
		modelToInitialize.registerServices();
		
	}
	
	/**
	 * Get the consumption values from the ontology
	 * 
	 * @param powerState
	 *            the {@link PowerState} to update
	 * @param name
	 *            the PowerConsumption individual name
	 * @param suffix
	 *            to ask for the consumption type (i.e., typical, nominal,
	 *            actual)
	 */
	private void getConsumption(PowerState powerState, String name, String suffix)
	{
		// get the consumption individual
		OWLNamedIndividual consumptionType = this.owlwrapper.getSingleObjectProperty(
				this.owlwrapper.getOWLIndividual(name), this.defaultPrefix, suffix);
		if (consumptionType != null)
		{
			// extract power value
			Set<OWLLiteral> powerValue = this.owlwrapper.getSpecificDataPropertyValues(consumptionType,
					this.defaultPrefix, "powerValue");
			// extract the unit of measurement and convert the power value in
			// Double
			if ((powerValue != null) && (!powerValue.isEmpty()))
			{
				Double powerDouble = Double.parseDouble(powerValue.iterator().next().getLiteral());
				String uom = "";
				
				// get uom
				OWLNamedIndividual measuredIn = this.owlwrapper.getSingleObjectProperty(consumptionType, "muo:",
						"measuredIn");
				if (measuredIn != null)
				{
					Set<OWLLiteral> prefSymbol = this.owlwrapper.getSpecificDataPropertyValues(measuredIn, "muo:",
							"prefSymbol");
					
					if (prefSymbol != null && !prefSymbol.isEmpty())
						uom = prefSymbol.iterator().next().getLiteral();
				}
				else
				{
					this.logger.log(LogService.LOG_WARNING, "The " + suffix + "of " + name
							+ " is without unit of measure: is it correct?!");
				}
				
				// set the correct consumption type for the current state
				if (suffix.equalsIgnoreCase("typicalConsumptionValue"))
				{
					powerState.setTypicalConsumption(powerDouble, uom);
				}
				else if (suffix.equalsIgnoreCase("nominalConsumptionValue"))
				{
					powerState.setNominalConsumption(powerDouble, uom);
				}
				else if (suffix.equalsIgnoreCase("actualConsumptionValue"))
				{
					powerState.setActualConsumption(powerDouble, uom);
				}
				
			}
		}
	}
	
}
