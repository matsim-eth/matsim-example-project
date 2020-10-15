/**
 * 
 */
package org.matsim.project;

import org.eqasim.core.analysis.DistanceUnit;
import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.EqasimConfigurator;
import org.eqasim.core.simulation.analysis.EqasimAnalysisModule;
import org.eqasim.core.simulation.mode_choice.EqasimModeChoiceModule;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.ctu2020.Ctu2020ModeChoiceModule;

/**
 * @author stefanopenazzi
 *
 */
public class RunMatsimEqasimDefaultEsimators {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws ConfigurationException {
		
		// first thing first check in the pom.xml file if eqasim is imported as a dependency
				// if not follow this link 
				//https://github.com/eqasim-org/eqasim-java
				
				// necessary command line args can be set up in eclipse defining a New Configuration 
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
				  eqasimConfig.setEstimator("car", "CarUtilityEstimator");
				 

				Controler controller = new Controler(scenario);
				EqasimConfigurator.configureController(controller);
				
		        controller.addOverridingModule(new EqasimModeChoiceModule());
				controller.addOverridingModule(new EqasimAnalysisModule());
				
				
				controller.run();
			}
			

}
