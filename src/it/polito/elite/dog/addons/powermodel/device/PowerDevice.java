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
package it.polito.elite.dog.addons.powermodel.device;

import java.util.HashSet;
import java.util.Set;

/**
 * The class PowerDevice defines and stores all the information needed to handle
 * the typical, nominal and actual consumptions related to the devices present
 * in Dog. It uses the @link{PowerState} class to store the information about
 * the consumptions for each states.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * 
 */
public class PowerDevice
{
	// the device name
	private String deviceURI;
	
	// the set for storing the consumptions for each device states
	private Set<PowerState> stateConsumptions;
	
	/**
	 * Base constructor, to be used when the states information were not yet
	 * available. It also initializes the set used to store the states and
	 * consumptions information.
	 * 
	 * @param deviceURI
	 *            a {@link String} that contains the device name
	 */
	public PowerDevice(String deviceURI)
	{
		this.deviceURI = deviceURI;
		this.stateConsumptions = new HashSet<PowerState>();
	}
	
	/**
	 * Complete constructor for DogPowerDevice.
	 * 
	 * @param deviceURI
	 *            the name of the device
	 * @param stateConsumptions
	 *            the set that contains all the states and consumptions for
	 *            deviceURI
	 */
	public PowerDevice(String deviceURI, Set<PowerState> stateConsumptions)
	{
		this.deviceURI = deviceURI;
		this.stateConsumptions = stateConsumptions;
	}
	
	/**
	 * getter for retrieving the device name
	 */
	public String getDeviceURI()
	{
		return this.deviceURI;
	}
	
	/**
	 * setter for the device name
	 * 
	 * @param deviceURI
	 *            the device URI
	 */
	public void setDeviceURI(String deviceURI)
	{
		this.deviceURI = deviceURI;
	}
	
	/**
	 * getter for retrieving the complete set of DogPowerStates (information
	 * about states and related consumptions)
	 */
	public Set<PowerState> getStateConsumptions()
	{
		return this.stateConsumptions;
	}
	
	/**
	 * setter for the set containing the DogPowerStates value
	 * 
	 * @param stateConsumptions
	 *            the set containing the DogPowerStates (information about
	 *            states and related consumptions)
	 */
	public void setStateConsumptions(Set<PowerState> stateConsumptions)
	{
		this.stateConsumptions = stateConsumptions;
	}
	
	/**
	 * This method let to add a single DogPowerState to the inner set of the
	 * DogPowerDevice
	 * 
	 * @param consumption
	 *            the DogPowerState to add
	 */
	public void addSinglePowerState(PowerState consumption)
	{
		this.stateConsumptions.add(consumption);
	}
}
