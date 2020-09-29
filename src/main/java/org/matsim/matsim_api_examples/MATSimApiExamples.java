package org.matsim.api;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

public class MATSimApiExamples {

    public static void main(String[] args) {

        // Config

        // Create or load config object
        Config config1 = ConfigUtils.createConfig();
        Config config2 = ConfigUtils.loadConfig("/path/to/input_config.xml");

        // Create a default config and write it out
        Config config = ConfigUtils.createConfig();
        new ConfigWriter(config, ConfigWriter.Verbosity.all).write("/path/to/output_config.xml");

        // Config provides access to the different groups and parameters
        config.controler().setLastIteration(10);
        int eventsInterval = config.controler().getWriteEventsInterval();

        config.network().setInputFile("/path/to/network.xml");

        // Scenario

        // Create a scenario
        Scenario scenario1 = ScenarioUtils.createScenario(config);

        // Create and load a scenario
        Scenario scenario2 = ScenarioUtils.loadScenario(config);

        // Scenario provides access to data containers
        Network network = scenario2.getNetwork();
        Population population = scenario2.getPopulation();
        ActivityFacilities facilities = scenario2.getActivityFacilities();
        TransitSchedule schedule = scenario2.getTransitSchedule();

        // By default, a Scenario is immutable.
        // Its data containers cannot be replaced.
        // But the content of data containers can be modified.

        // Controler

        // Create a controler
        Controler controler = new Controler(scenario2);

        // Controler provides access to important data structures.
        Config config3 = controler.getConfig();
        Scenario scenario3 = controler.getScenario();
        EventsManager eventsManager = controler.getEvents();
        Integer iteration = controler.getIterationNumber();

        // Run the simulation
        controler.run();

    }

}
