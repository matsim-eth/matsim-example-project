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
import org.matsim.ctu2020.Ctu2020ModeChoiceModule;



/**
 * @author nagel
 *
 */
public class RunMatsimEqasimCustomCarEstimator{

	static public void main(String[] args) throws ConfigurationException {
		
		// first thing first check in the pom.xml file if eqasim is imported as a dependency
		// if not follow this link 
		//https://github.com/eqasim-org/eqasim-java
		
		// necessary command line args can be set up in eclipse defining a New Configuration 
		//e.g. --config-path home/.../config.xml
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("config-path") //
				.allowPrefixes("mode-parameter", "cost-parameter") //
				.build();

		//load the config file using the path specify as an argument in the command line +
		//the eqasim configurator. 
		Config config = ConfigUtils.loadConfig(cmd.getOptionStrict("config-path"),
				EqasimConfigurator.getConfigGroups());
		
		
		EqasimConfigGroup.get(config).setTripAnalysisInterval(5);
		EqasimConfigGroup.get(config).setDistanceUnit(DistanceUnit.foot);
		cmd.applyConfiguration(config);

		// create the scenario
		Scenario scenario = ScenarioUtils.createScenario(config);
		
		
		EqasimConfigurator.configureScenario(scenario);
		
		ScenarioUtils.loadScenario(scenario);
		
		EqasimConfigurator.adjustScenario(scenario);

		EqasimConfigGroup eqasimConfig = (EqasimConfigGroup) config.getModules().get(EqasimConfigGroup.GROUP_NAME);
		
		// All the estimators of the utility function components are defined as the 
		// default that can be found into
		// org.eqasim.core.simulation.mode_choice.utilities.estimators
		// but the CarEstimatorCtu2020 that has been replaced by a custom version
		eqasimConfig.setEstimator("walk", "WalkUtilityEstimator");
		eqasimConfig.setEstimator("bike", "BikeUtilityEstimator");
		eqasimConfig.setEstimator("pt", "PtUtilityEstimator");
		eqasimConfig.setEstimator("car", "CarEstimatorCtu2020"); // custom estimator
		 
        // create the controler
		Controler controller = new Controler(scenario);
		EqasimConfigurator.configureController(controller);
		
		
        controller.addOverridingModule(new EqasimModeChoiceModule());
        // this module replace the default bind of eqasim in order to use the custom estimator CarEstimatorCtu2020
        controller.addOverridingModule(new Ctu2020ModeChoiceModule());
        
		controller.addOverridingModule(new EqasimAnalysisModule());
		
		controller.run();
	}
	
}
