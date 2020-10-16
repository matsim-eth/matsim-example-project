package org.matsim.eqasim;

import java.io.IOException;

import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.EqasimConfigurator;
import org.eqasim.core.simulation.analysis.EqasimAnalysisModule;
import org.eqasim.core.simulation.mode_choice.AbstractEqasimExtension;
import org.eqasim.core.simulation.mode_choice.EqasimModeChoiceModule;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contribs.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

import com.google.inject.Provides;
import com.google.inject.Singleton;

public class RunDefaultEqasim {

	public static void main(String[] args) throws ConfigurationException {

		// first thing first check in the pom.xml file if eqasim is imported as a
		// dependency
		// if not follow this link
		// https://github.com/eqasim-org/eqasim-java

		// necessary command line args can be set up in eclipse defining a New
		// Configuration
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("config-path") //
				.allowPrefixes("mode-parameter", "cost-parameter") //
				.build();

		Config config = ConfigUtils.loadConfig(cmd.getOptionStrict("config-path"),
				EqasimConfigurator.getConfigGroups());

		// EqasimConfigGroup.get(config).setTripAnalysisInterval(5);
		// EqasimConfigGroup.get(config).setDistanceUnit(DistanceUnit.meter);
		cmd.applyConfiguration(config);
		EqasimConfigGroup eqasimConfig = (EqasimConfigGroup) config.getModules().get(EqasimConfigGroup.GROUP_NAME);
		DiscreteModeChoiceConfigGroup dmcConfig = (DiscreteModeChoiceConfigGroup)config.getModules().get(DiscreteModeChoiceConfigGroup.GROUP_NAME);
		dmcConfig.setTourEstimator("Cumulative");
		dmcConfig.setTripEstimator("EqasimUtilityEstimator");
		// Set up modes

		eqasimConfig.setEstimator(TransportMode.car, EqasimModeChoiceModule.CAR_ESTIMATOR_NAME);
		eqasimConfig.setEstimator(TransportMode.pt, EqasimModeChoiceModule.PT_ESTIMATOR_NAME);
		eqasimConfig.setEstimator(TransportMode.bike, EqasimModeChoiceModule.BIKE_ESTIMATOR_NAME);
		
		//advanced version
		//eqasimConfig.setEstimator(TransportMode.bike, "BikeEstimatorCtu2020");
		eqasimConfig.setEstimator(TransportMode.walk, EqasimModeChoiceModule.WALK_ESTIMATOR_NAME);
		
		eqasimConfig.setCostModel(TransportMode.car, "ZeroCostModel");
		eqasimConfig.setCostModel(TransportMode.pt, "ZeroCostModel");

		Scenario scenario = ScenarioUtils.createScenario(config);

		EqasimConfigurator.configureScenario(scenario);

		ScenarioUtils.loadScenario(scenario);

		EqasimConfigurator.adjustScenario(scenario);		

		Controler controller = new Controler(scenario);
		EqasimConfigurator.configureController(controller);
		// we need to setup mode choice model used
		controller.addOverridingModule(new EqasimModeChoiceModule());
// how to make changes example 
		/*
		 * controller.addOverridingModule(new AbstractEqasimExtension() {
		 * 
		 * @Override protected void installEqasimExtension() {
		 * 
		 * bindUtilityEstimator("BikeEstimatorCtu2020").to(CTUBikeEstimator.class);
		 * bind(ModeParameters.class).to(CTUModeParameters.class); }
		 * 
		 * @Provides
		 * 
		 * @Singleton public CTUModeParameters
		 * provideModeChoiceParameters(EqasimConfigGroup config) throws IOException,
		 * ConfigurationException { CTUModeParameters parameters = new
		 * CTUModeParameters(); //change the default parameters
		 * //parameters.buildDefault(); return parameters; } });
		 */

		controller.addOverridingModule(new EqasimAnalysisModule());

		controller.run();
	}

}
