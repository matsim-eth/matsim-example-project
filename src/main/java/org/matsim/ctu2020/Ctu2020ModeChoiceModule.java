/**
 * 
 */
package org.matsim.ctu2020;

import java.io.IOException;

import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.mode_choice.AbstractEqasimExtension;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.ctu2020.estimators.CarUtilityEstimatorCtu2020;
import org.matsim.ctu2020.parameters.ModeParametersCtu2020;

import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * @author stefanopenazzi
 *
 */
public class Ctu2020ModeChoiceModule extends AbstractEqasimExtension {

	@Override
	protected void installEqasimExtension() {
		// bindUtilityEstimator is a method in AbstractEqasimExtension that make possible add
		// to a mapbinder objects of type UtilityEstimator with a specific key, in this case CarEstimatorCtu2020
		
		bindUtilityEstimator("CarEstimatorCtu2020").to(CarUtilityEstimatorCtu2020.class);
		bind(ModeParameters.class).to(ModeParametersCtu2020.class);
		
	}
	
	//@Provides is used as a factory for a new object. Every time a new object of 
	// type ModeParametersCtu2020 is required as parameter in a constructor implementing @Inject
	//this object will be created by using the following method
	
	@Provides
	@Singleton
	public ModeParametersCtu2020 provideModeChoiceParameters(EqasimConfigGroup config) throws IOException, ConfigurationException {
		ModeParametersCtu2020 parameters = new ModeParametersCtu2020();
		
		return parameters;
	}

}
