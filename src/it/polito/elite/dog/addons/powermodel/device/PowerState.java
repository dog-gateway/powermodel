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

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Power;

/**
 * The class PowerState defines and stores all the information needed to handle
 * the typical, nominal and actual consumptions related to a precise device
 * state in Dog. It is especially useful when used in conjunction with the class
 * 
 * @link{PowerDevice for the PowerModel bundle.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * 
 */
public class PowerState
{
	// the state name for which the consumption is present
	private String stateName;
	
	// the three types of consumptions
	private DecimalMeasure<Power> typicalConsumption;
	private DecimalMeasure<Power> nominalConsumption;
	private DecimalMeasure<Power> actualConsumption;
	
	// three boolean variables to quickly know if the state has a specified
	// consumption or not
	private boolean hasTypicalConsumption;
	private boolean hasNominalConsumption;
	private boolean hasActualConsumption;
	
	/**
	 * Base constructor, to be used when the consumption information were not
	 * yet available.
	 * 
	 * @param stateName
	 *            a {@link String} that contains the state name
	 */
	public PowerState(String stateName)
	{
		this.stateName = stateName;
		
		// init boolean to false, since no consumption insert has been done
		// (right now)
		hasTypicalConsumption = false;
		hasActualConsumption = false;
		hasNominalConsumption = false;
	}
	
	/**
	 * Complete constructor for DogPowerState.
	 * 
	 * @param stateName
	 *            a {@link String} that contains the state name
	 * @param typicalConsumption
	 *            a {@link DecimalMeasure} that stores the typical consumption
	 *            for the declared state
	 * @param nominalConsumption
	 *            a {@link DecimalMeasure} that stores the nominal consumption
	 *            for the declared state
	 * @param actualConsumption
	 *            a {@link DecimalMeasure} that stores the actual consumption
	 *            for the declared state
	 */
	public PowerState(String stateName, DecimalMeasure<Power> typicalConsumption,
			DecimalMeasure<Power> nominalConsumption, DecimalMeasure<Power> actualConsumption)
	{
		this.stateName = stateName;
		
		// typical consumption
		this.typicalConsumption = typicalConsumption;
		if (this.typicalConsumption != null)
			hasTypicalConsumption = true;
		else
			hasTypicalConsumption = false;
		
		// actual consumption
		this.actualConsumption = actualConsumption;
		if (this.actualConsumption != null)
			hasActualConsumption = true;
		else
			hasActualConsumption = false;
		
		// nominal consumption
		this.nominalConsumption = nominalConsumption;
		if (this.nominalConsumption != null)
			hasNominalConsumption = true;
		else
			hasNominalConsumption = false;
	}
	
	/**
	 * Getter for retrieving the state name
	 */
	public String getStateName()
	{
		return this.stateName;
	}
	
	/**
	 * Getter for retrieving the typical consumptions as a
	 * {@link DecimalMeasure}
	 */
	public DecimalMeasure<Power> getTypicalConsumption()
	{
		return this.typicalConsumption;
	}
	
	/**
	 * Getter for retrieving the nominal consumptions as a
	 * {@link DecimalMeasure}
	 */
	public DecimalMeasure<Power> getNominalConsumption()
	{
		return this.nominalConsumption;
	}
	
	/**
	 * Getter for retrieving the actual consumptions as a {@link DecimalMeasure}
	 */
	public DecimalMeasure<Power> getActualConsumption()
	{
		return this.actualConsumption;
	}
	
	/**
	 * Setter for storing the typical consumptions
	 * 
	 * @param typicalConsumption
	 *            a {@link DecimalMeasure}
	 */
	public void setTypicalConsumption(DecimalMeasure<Power> typicalConsumption)
	{
		this.typicalConsumption = typicalConsumption;
		this.hasTypicalConsumption = true;
	}
	
	/**
	 * Setter for creating and storing the typical consumptions
	 * 
	 * @param value
	 *            a {@link Double} representing the value of the typical power
	 *            consumption
	 * @param UnitOfMeasure
	 *            a {@link String} representing the current unit of measure
	 */
	public void setTypicalConsumption(Double value, String UnitOfMeasure)
	{
		this.typicalConsumption = DecimalMeasure.valueOf(value + " " + UnitOfMeasure);
		this.hasTypicalConsumption = true;
	}
	
	/**
	 * Setter for storing the nominal consumptions
	 * 
	 * @param nominalConsumption
	 *            a {@link DecimalMeasure}
	 */
	public void setNominalConsumption(DecimalMeasure<Power> nominalConsumption)
	{
		this.nominalConsumption = nominalConsumption;
		this.hasNominalConsumption = true;
	}
	
	/**
	 * Setter for creating and storing the nominal consumptions
	 * 
	 * @param value
	 *            a {@link Double} representing the value of the nominal power
	 *            consumption
	 * @param UnitOfMeasure
	 *            a {@link String} representing the current unit of measure
	 */
	public void setNominalConsumption(Double value, String UnitOfMeasure)
	{
		this.nominalConsumption = DecimalMeasure.valueOf(value + " " + UnitOfMeasure);
		this.hasNominalConsumption = true;
	}
	
	/**
	 * Setter for storing the actual consumptions
	 * 
	 * @param actualConsumption
	 *            a {@link DecimalMeasure}
	 */
	public void setActualConsumption(DecimalMeasure<Power> actualConsumption)
	{
		this.actualConsumption = actualConsumption;
		this.hasActualConsumption = true;
	}
	
	/**
	 * Setter for creating and storing the actual consumptions
	 * 
	 * @param value
	 *            a {@link Double} representing the value of the actual power
	 *            consumption
	 * @param UnitOfMeasure
	 *            a {@link String} representing the current unit of measure
	 */
	public void setActualConsumption(Double value, String UnitOfMeasure)
	{
		this.actualConsumption = DecimalMeasure.valueOf(value + " " + UnitOfMeasure);
		this.hasActualConsumption = true;
	}
	
	/**
	 * @return the hasTypicalConsumption
	 */
	public boolean hasTypicalConsumption()
	{
		return hasTypicalConsumption;
	}
	
	/**
	 * @return the hasNominalConsumption
	 */
	public boolean hasNominalConsumption()
	{
		return hasNominalConsumption;
	}
	
	/**
	 * @return the hasActualConsumption
	 */
	public boolean hasActualConsumption()
	{
		return hasActualConsumption;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean equals = false;
		
		if (obj instanceof PowerState)
		{
			PowerState other = (PowerState) obj;
			equals = (this.stateName.equals(other.stateName));
		}
		return equals;
	}
	
}
