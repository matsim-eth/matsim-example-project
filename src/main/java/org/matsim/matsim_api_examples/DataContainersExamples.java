package org.matsim.matsim_api_examples;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.FacilitiesUtils;
import org.matsim.facilities.FacilitiesWriter;
import org.matsim.facilities.MatsimFacilitiesReader;
import org.matsim.pt.transitSchedule.TransitScheduleFactoryImpl;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.transitSchedule.api.TransitScheduleWriter;
import org.matsim.vehicles.MatsimVehicleReader;
import org.matsim.vehicles.MatsimVehicleWriter;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.Vehicles;

public class DataContainersExamples {

    public static void main(String[] args) {

        // There are two ways to create EMPTY data containers

        // 1) Create the empty data containers using the scenario

        // create an empty scenario
        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);

        // create the empty data containers
        System.out.println("Creating empty data containers using scenario.");
        Network emptyNetworkFromScenario = scenario.getNetwork();
        Population emptyPopulationFromScenario = scenario.getPopulation();
        ActivityFacilities emptyFacilitiesFromScenario = scenario.getActivityFacilities();
        Vehicles emptyVehiclesFromScenario = scenario.getVehicles();
        TransitSchedule emptyTransitScheduleFromScenario = scenario.getTransitSchedule();

        // We will prove here that these data containers are in fact empty
        System.out.println("Number of links in empty network: " + emptyNetworkFromScenario.getLinks().size());
        System.out.println("Number of persons in empty population: " + emptyPopulationFromScenario.getPersons().size());
        System.out.println("Number of facilities in empty facilities container: " + emptyFacilitiesFromScenario.getFacilities().size());
        System.out.println("Number of vehicles in empty vehicles container: " + emptyVehiclesFromScenario.getVehicles().size());
        System.out.println("Number of transit lines in empty transit schedule: " + emptyTransitScheduleFromScenario.getTransitLines().size());

        // 2) Create them using *Utils-classes
        System.out.println("Creating empty data containers using *Utils-classes.");
        Network emptyNetworkFromUtils = NetworkUtils.createNetwork();
        Population emptyPopulationFromUtils = PopulationUtils.createPopulation(config);
        ActivityFacilities emptyFacilitiesFromUtils = FacilitiesUtils.createActivityFacilities();
        Vehicles emptyVehiclesFromUtils = VehicleUtils.createVehiclesContainer();
        TransitSchedule emptyTransitScheduleFromUtils = new TransitScheduleFactoryImpl().createTransitSchedule();  // exception from the *Utils-pattern

        // Again, we will prove here that these data containers are in fact empty
        System.out.println("Number of links in empty network: " + emptyNetworkFromUtils.getLinks().size());
        System.out.println("Number of persons in empty population: " + emptyPopulationFromUtils.getPersons().size());
        System.out.println("Number of facilities in empty facilities container: " + emptyFacilitiesFromUtils.getFacilities().size());
        System.out.println("Number of vehicles in empty vehicles container: " + emptyVehiclesFromUtils.getVehicles().size());
        System.out.println("Number of transit lines in empty transit schedule: " + emptyTransitScheduleFromUtils.getTransitLines().size());


        // These various Utils classes also contain many helper functions
        // We suggest you look through them to see what is available


        // Otherwise, we can load the different data containers from file
        // For each data container, the exact logic for doing this is slightly different

        // Network
        // 1) create empty network
        Network network = NetworkUtils.createNetwork();
        // 2) pass it to network reader and then read in the file
        new MatsimNetworkReader(network).readFile("scenarios/siouxfalls-2014/Siouxfalls_network_PT.xml");

        // Population
        // 1) create an empty (we can use the one we created previously)
        // 2) pass the scenario to population reader and then read in the file
        new PopulationReader(scenario).readFile("scenarios/siouxfalls-2014/Siouxfalls_population.xml.gz");
        // 3) extract the population from the scenario
        Population population = scenario.getPopulation();

        // Facilities (same logic as population)
        new MatsimFacilitiesReader(scenario).readFile("scenarios/siouxfalls-2014/Siouxfalls_facilities.xml.gz");
        ActivityFacilities facilities = scenario.getActivityFacilities();

        // TransitSchedule (same logic as population)
        new TransitScheduleReader(scenario).readFile("scenarios/siouxfalls-2014/Siouxfalls_transitSchedule.xml");
        TransitSchedule transitSchedule = scenario.getTransitSchedule();

        // Vehicles (same logic as network, more or less)
        Vehicles vehicles = VehicleUtils.createVehiclesContainer();
        new MatsimVehicleReader.VehicleReader(vehicles).readFile("scenarios/siouxfalls-2014/Siouxfalls_vehicles.xml");

        // Now, we will prove here that these data containers are NOT empty!
        System.out.println("Number of links in loaded network: " + network.getLinks().size());
        System.out.println("Number of persons in loaded population: " + population.getPersons().size());
        System.out.println("Number of facilities in loaded facilities container: " + facilities.getFacilities().size());
        System.out.println("Number of vehicles in loaded vehicles container: " + vehicles.getVehicles().size());
        System.out.println("Number of transit lines in loaded transit schedule: " + transitSchedule.getTransitLines().size());


        // We can also write these containers out to file
        new NetworkWriter(network).write("output/network.xml");
        new PopulationWriter(population).write("output/population.xml");
        new FacilitiesWriter(facilities).write("output/facilities.xml");
        new TransitScheduleWriter(transitSchedule).writeFile("output/transitSchedule.xml");
        new MatsimVehicleWriter(vehicles).writeFile("output/transitSchedule.xml");


        // IDs
        // Elements in data containers have Ids for identification.

        // These IDs have a type which must be specified when creating an Id:
        Id<Link> linkId1 = Id.create("link", Link.class);
        Id<Node> nodeId1 = Id.create("node", Node.class);
        Id<Person> personId1 = Id.create("person", Person.class);

        // Alternatively, some id type have specific creation methods
        Id<Link> linkId2 = Id.createLinkId("link2");
        Id<Node> nodeId2 = Id.createNodeId("node2");
        Id<Person> personId2 = Id.createPersonId("person2");


        // Factories
        // Use Factories to modify create new elements for data containers.
        // Each data container provides its own factory.
        // More detailed use of these factories can be seen in the CreatePopulationExample or CreateNetworkExample

    }
}
