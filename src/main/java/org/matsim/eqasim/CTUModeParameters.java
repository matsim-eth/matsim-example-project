package org.matsim.eqasim;

import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;

public class CTUModeParameters extends ModeParameters {

	public class Ctu2020CarParameters {
		public double beta = -10.0;
	}

	public final Ctu2020CarParameters ctuCar = new Ctu2020CarParameters();

	public CTUModeParameters buildDefault() {
		CTUModeParameters parameters = new CTUModeParameters();

		// Cost
		parameters.betaCost_u_MU = -0.206;


		return parameters;
	}
}
