/**
 * 
 */
package org.matsim.ctu2020.parameters;

import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;

/**
 * @author stefanopenazzi
 *
 */
public class ModeParametersCtu2020 extends ModeParameters{

	public class Ctu2020CarParameters {
		public double beta = 0.0;
	}
	
	public final Ctu2020CarParameters sfCar = new Ctu2020CarParameters();
	

}
