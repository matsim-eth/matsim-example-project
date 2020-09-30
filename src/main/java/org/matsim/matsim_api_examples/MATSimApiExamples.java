package org.matsim.matsim_api_examples;

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

        // Config examples

        // 1) Default config
        // Create a default config object using ConfigUtils
        Config configDefault = ConfigUtils.createConfig();

        // Write out the default config to file (open it and see what it looks like)
        new ConfigWriter(configDefault, ConfigWriter.Verbosity.all).write("output/default_config.xml");

        // Config provides access to the different groups and parameters
        // This means you can basically modify anything in the config using Java
        // For example, we can specify the path to the network and population plans files
        configDefault.network().setInputFile("scenarios/siouxfalls-2014/Siouxfalls_network_PT.xml");
        configDefault.plans().setInputFile("scenarios/siouxfalls-2014/Siouxfalls_population.xml.gz");

        // Write out the now modified config to file (open it to see what changed)
        new ConfigWriter(configDefault, ConfigWriter.Verbosity.all).write("output/modified_default_config.xml");

        // 2) Config from config file
        // Load config from file using ConfigUtils
        Config configLoaded = ConfigUtils.loadConfig("scenarios/siouxfalls-2014/config_default.xml");

        // Again, we can change anything we want in the config
        // Here we will set the last iteration to 1 (originally it was 5) so this example runs faster :)
        // We will also write the simulation output to another location so the code does not complain
        configLoaded.controler().setLastIteration(1);
        configLoaded.controler().setOutputDirectory("output/siouxfalls-2014/simulation_output_1_iteration");


        // Scenario examples

        // Create an empty scenario (empty data container)
        System.out.println("Creating empty scenario.");
        Scenario scenarioEmpty = ScenarioUtils.createScenario(configLoaded);

        // Scenario provides access to data containers
        Network emptyNetwork = scenarioEmpty.getNetwork();
        Population emptyPopulation = scenarioEmpty.getPopulation();
        ActivityFacilities emptyActivityFacilities = scenarioEmpty.getActivityFacilities();
        TransitSchedule emptyTransitSchedule = scenarioEmpty.getTransitSchedule();

        // We will prove here that these data containers are in fact empty
        System.out.println("Number of links in empty network: " + emptyNetwork.getLinks().size());
        System.out.println("Number of persons in empty population: " + emptyPopulation.getPersons().size());
        System.out.println("Number of facilities in empty facilities: " + emptyActivityFacilities.getFacilities().size());
        System.out.println("Number of transit lines in empty transit schedule: " + emptyTransitSchedule.getTransitLines().size());

        // Now we will create the scenario by loading the files from the config
        System.out.println("Loading scenario from files.");
        Scenario scenario = ScenarioUtils.loadScenario(configLoaded);

        // We will prove here that these data containers are NOT empty
        System.out.println("Number of links in loaded network: " + scenario.getNetwork().getLinks().size());
        System.out.println("Number of persons in loaded population: " + scenario.getPopulation().getPersons().size());
        System.out.println("Number of facilities in loaded facilities: " + scenario.getActivityFacilities().getFacilities().size());
        System.out.println("Number of transit lines in loaded transit schedule: " + scenario.getTransitSchedule().getTransitLines().size());

        // By default, a Scenario is immutable.
        // Its data containers cannot be replaced.
        // But the content of data containers can be modified.
        // We will show this in other examples later.


        // Controler examples

        // Create a controler
        Controler controler = new Controler(scenario);

        // Controler provides access to important data structures.
        Config controlerConfig = controler.getConfig();
        Scenario controlerScenario = controler.getScenario();
        EventsManager controlerEventsManager = controler.getEvents(); // this is important for handling events during a simulation

        // Run the simulation
        controler.run();

    }

}
