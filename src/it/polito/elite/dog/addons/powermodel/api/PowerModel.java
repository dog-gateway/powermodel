/*
 * Dog - Addons
 * 
 * Copyright (c) 2011-2014 Dario Bonino and Luigi De Russis
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
package it.polito.elite.dog.addons.powermodel.api;

import java.util.Set;

/**
 * The interface defining services offered by the PowerModel bundle of Dog. The
 * PowerModel bundle handles power consumption information modeled as a set of
 * instances of the DogPower ontology, associated to the instances of the DogOnt
 * ontology used by Dog as HouseModel. <br/>
 * The power consumption information represented by means of DogPower instances
 * includes typical, nominal and actual power consumption of every device
 * instance/class.<br/>
 * Offered services reflect this fact by allowing to gather power consumption
 * information for either a single device or for all devices in the house model
 * currently managed by Dog.
 * 
 * @author <a href="mailto:dario.bonino@polito.it">Dario Bonino</a>
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * 
 */
public interface PowerModel
{
	/**
	 * Given the URI of a device provides back the actual power consumption
	 * associated to the device, if available
	 * 
	 * @param deviceURI
	 *            - the URI of the device for which power consumption
	 *            information must be gathered
	 * @param stateName
	 *            - the state value of the device for which power consumption
	 *            information must be gathered
	 * @return a {@link DevicePowerConsumption} object representing the
	 *         device power consumption
	 */
	public DevicePowerConsumption getActualDeviceConsumption(String deviceURI, String stateName);
	
	/**
	 * Given the URI of a device provides back the nominal power consumption
	 * associated to the device, if available
	 * 
	 * @param deviceURI
	 *            - the URI of the device for which power consumption
	 *            information must be gathered
	 * @param stateName
	 *            - the state value of the device for which power consumption
	 *            information must be gathered
	 * @return a {@link DevicePowerConsumption} object representing the
	 *         device power consumption
	 */
	public DevicePowerConsumption getNominalDeviceConsumption(String deviceURI, String stateName);
	
	/**
	 * Given the URI of a device provides back the typical power consumption
	 * associated to the device, if available
	 * 
	 * @param deviceURI
	 *            - the URI of the device for which power consumption
	 *            information must be gathered
	 * @param stateName
	 *            - the state value of the device for which power consumption
	 *            information must be gathered
	 * @return a {@link DevicePowerConsumption} object representing the
	 *         device power consumption
	 */
	public DevicePowerConsumption getTypicalDeviceConsumption(String deviceURI, String stateName);
	
	/**
	 * Given the URI of a device provides back the most accurate power
	 * consumption associated to the device, if available
	 * 
	 * @param deviceURI
	 *            - the URI of the device for which power consumption
	 *            information must be gathered
	 * @param stateName
	 *            - the state value of the device for which power consumption
	 *            information must be gathered
	 * @return a {@link DevicePowerConsumption} object representing the
	 *         device power consumption
	 */
	public DevicePowerConsumption getBestDeviceConsumption(String deviceURI, String stateName);
	
	/**
	 * It provides back the highest power consumption associated to each device,
	 * if available
	 * 
	 * @return a vector of {@link DevicePowerConsumption} objects
	 *         representing the devices power consumption
	 */
	public Set<DevicePowerConsumption> getHighestDeviceConsumptions();
	
}
