/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.project;

import org.eqasim.core.analysis.DistanceUnit;
import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.EqasimConfigurator;
import org.eqasim.core.simulation.analysis.EqasimAnalysisModule;
import org.eqasim.core.simulation.mode_choice.EqasimModeChoiceModule;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;



/**
 * @author nagel
 *
 */
public class RunMatsim{

	static public void main(String[] args) throws ConfigurationException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("config-path") //
				.allowPrefixes("mode-parameter", "cost-parameter") //
				.build();

		Config config = ConfigUtils.loadConfig(cmd.getOptionStrict("config-path"),
				EqasimConfigurator.getConfigGroups());
		
		
		EqasimConfigGroup.get(config).setTripAnalysisInterval(5);
		EqasimConfigGroup.get(config).setDistanceUnit(DistanceUnit.foot);
		cmd.applyConfiguration(config);

		Scenario scenario = ScenarioUtils.createScenario(config);
		
		
		EqasimConfigurator.configureScenario(scenario);
		
		ScenarioUtils.loadScenario(scenario);
		
		EqasimConfigurator.adjustScenario(scenario);

		EqasimConfigGroup eqasimConfig = (EqasimConfigGroup) config.getModules().get(EqasimConfigGroup.GROUP_NAME);
		
		  eqasimConfig.setEstimator("walk", "WalkUtilityEstimator");
		  eqasimConfig.setEstimator("bike", "BikeUtilityEstimator");
		  eqasimConfig.setEstimator("pt", "PtUtilityEstimator");
		  eqasimConfig.setEstimator("car", "CarEstimatorCtu2020");
		 

		Controler controller = new Controler(scenario);
		EqasimConfigurator.configureController(controller);
		
        controller.addOverridingModule(new EqasimModeChoiceModule());
        controller.addOverridingModule(new Ctu2020ModeChoiceModule());
		controller.addOverridingModule(new EqasimAnalysisModule());
		// controller.addOverridingModule(new CalibrationModule());
		
		controller.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
            	bind(ModeParameters.class);
            	}
         });
		
		
		controller.run();
	}
	
}
