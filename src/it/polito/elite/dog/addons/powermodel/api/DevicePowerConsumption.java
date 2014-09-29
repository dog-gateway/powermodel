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

import java.util.Calendar;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Power;

/**
 * A utility class for representing the power consumption of a given device. It
 * carries information about the device for which data is represented, in form
 * of URI, and it reports the power consumption value (nominal, typical, actual
 * or measured) as a {@link Measure} object with a value and a unit of measure
 * (in the International System). <br/>
 * The power consumption information carried by
 * an instance of {@link DevicePowerConsumption} is time stamped with a
 * {@link Calendar} representing the instant in which data has been gathered.
 * 
 * @author <a href="mailto:dario.bonino@polito.it">Dario Bonino</a>
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * 
 */
public class DevicePowerConsumption
{
	// the URI of the device for which this class represents the power consumption information
	private String deviceURI;

	// the power consumption information associated to the device
	private DecimalMeasure<Power> consumption;

	// the instant in which the power consumption measure has been gathered
	private Calendar latestUpdate;
	
	/**
	 * Constructor
	 * 
	 * @param deviceURI
	 *            the name of the device to which the consumption is related
	 * @param consumption
	 *            one of the (power) consumptions of a certain device state
	 */
	public DevicePowerConsumption(String deviceURI, DecimalMeasure<Power> consumption)
	{
		this.deviceURI = deviceURI;
		this.consumption = consumption;
	}
	
	/**
	 * Gets the device associated to the power consumption information modeled
	 * by an instance of this class
	 * 
	 * @return the deviceURI ({@link String}) of the device whose power
	 *         consumption is represented.
	 */
	public String getDeviceURI()
	{
		return deviceURI;
	}
	
	/**
	 * Sets the URI of the device associated to the power consumption
	 * information represented by this class.
	 * 
	 * @param deviceURI
	 *            the deviceURI to set
	 */
	public void setDeviceURI(String deviceURI)
	{
		this.deviceURI = deviceURI;
	}
	
	/**
	 * Gets the power consumption value as a {@link Measure}&lt;{@link Power}
	 * &gt; object
	 * 
	 * @return the consumption information ({@link Measure}&lt;{@link Power}
	 *         &gt;);
	 */
	public DecimalMeasure<Power> getConsumption()
	{
		return consumption;
	}
	
	/**
	 * Sets the power consumption value represented by this object
	 * 
	 * @param consumption
	 *            - the consumption to set ({@link Measure}&lt;{@link Power}
	 *            &gt;)
	 */
	public void setConsumption(DecimalMeasure<Power> consumption)
	{
		this.consumption = consumption;
	}
	
	/**
	 * Gets the instant in which the measure has been gathered
	 * 
	 * @return the latestUpdate as a {@link Calendar}
	 */
	public Calendar getLatestUpdate()
	{
		return latestUpdate;
	}
	
	/**
	 * Sets the instant in which the power consumption represented by this
	 * object has been gathered
	 * 
	 * @param latestUpdate
	 *            - the instant in which the power consumption has been gathered
	 *            as a {@link Calendar}.
	 */
	public void setLatestUpdate(Calendar latestUpdate)
	{
		this.latestUpdate = latestUpdate;
	}
	
}
