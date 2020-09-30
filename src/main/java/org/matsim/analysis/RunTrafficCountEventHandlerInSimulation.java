package org.matsim.analysis;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

public class RunTrafficCountEventHandlerInSimulation {

    // this is an example run script to process events during the MATSim simulation
    public static void main(String[] args) {

        // Load config
        Config config = ConfigUtils.loadConfig("scenarios/siouxfalls-2014/config_default.xml");

        // Set a different output path
        config.controler().setOutputDirectory("output/siouxfalls-2014/simulation_output");

        // Load scenario
        Scenario scenario = ScenarioUtils.loadScenario(config);

        // create controler
        Controler controler = new Controler(scenario);

        // create event handler
        Network network = scenario.getNetwork(); // we get the network from the scenario
        TrafficCountHandler trafficCountHandler = new TrafficCountHandler(network);

        // create controler listener
        TrafficCountControlerListener trafficCountControlerListener = new TrafficCountControlerListener(trafficCountHandler);

        // add controler listener to controler
        controler.addControlerListener(trafficCountControlerListener);

        // run simulation
        controler.run();

    }
}
