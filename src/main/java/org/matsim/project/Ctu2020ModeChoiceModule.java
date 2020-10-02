/**
 * 
 */
package org.matsim.project;

import java.io.IOException;

import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.mode_choice.AbstractEqasimExtension;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.matsim.core.config.CommandLine.ConfigurationException;

import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * @author stefanopenazzi
 *
 */
public class Ctu2020ModeChoiceModule extends AbstractEqasimExtension {

	@Override
	protected void installEqasimExtension() {
		bindUtilityEstimator("CarEstimatorCtu2020").to(CarUtilityEstimatorCtu2020.class);
		bind(ModeParameters.class).to(ModeParametersCtu2020.class);
		
	}
	
	@Provides
	@Singleton
	public ModeParametersCtu2020 provideModeChoiceParameters(EqasimConfigGroup config) throws IOException, ConfigurationException {
		ModeParametersCtu2020 parameters = new ModeParametersCtu2020();
		
		return parameters;
	}

}
