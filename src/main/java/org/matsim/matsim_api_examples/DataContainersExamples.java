package org.matsim.matsim_api_examples;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.ControlerUtils;
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

import java.util.Arrays;
import java.util.HashSet;

public class DataContainersExamples {

    public static void main(String[] args) {

        // Create the data containers using the scenario
        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
        Network network = scenario.getNetwork();
        Population population = scenario.getPopulation();
        ActivityFacilities facilities = scenario.getActivityFacilities();
        TransitSchedule transitSchedule = scenario.getTransitSchedule();

        // or using *Utils-classes to create them
        Network network2 = NetworkUtils.createNetwork();
        Population population2 = PopulationUtils.createPopulation(config);
        ActivityFacilities facilities2 = FacilitiesUtils.createActivityFacilities();
        TransitSchedule schedule = new TransitScheduleFactoryImpl().createTransitSchedule();  // exception from the *Utils-pattern

        // read in data containers
        new MatsimNetworkReader(network).readFile("/path/to/input/network.xml");
        new PopulationReader(scenario).readFile("/path/to/input/population.xml");
        new MatsimFacilitiesReader(scenario).readFile("/path/to/input/facilities.xml");
        new TransitScheduleReader(scenario).readFile("/path/to/input/transitSchedule.xml");

        // write out the data to xml
        new NetworkWriter(network).write("/path/to/output/network.xml");
        new PopulationWriter(population).write("/path/to/output/population.xml");
        new FacilitiesWriter(facilities).write("/path/to/output/facilities.xml");
        new TransitScheduleWriter(transitSchedule).writeFile("/path/to/output/transitSchedule.xml");

        // IDs
        // Elements in data containers have Ids for identification.

        // These IDs have a type:
        Id<Link> linkId;
        Id<Node> nodeId;
        Id<Person> personId;

        // The type must be specified when creating an Id:
        linkId = Id.create("link", Link.class);
        nodeId = Id.create("node", Node.class);
        personId = Id.create("person", Person.class);

        // Alternatively, some id type have specific creation methods
        Id<Link> linkId2 = Id.createLinkId("link2");
        Id<Node> nodeId2 = Id.createNodeId("node2");
        Id<Person> personId2 = Id.createPersonId("person2");

        // Factories
        // Use Factories to modify create new elements for data containers.
        // Each data container provides its own factory.

        // Network example
        NetworkFactory networkFactory = network.getFactory();
        Node node1 = networkFactory.createNode(Id.createNodeId("1"), new Coord(0,0));
        Node node2 = networkFactory.createNode(Id.createNodeId("2"), new Coord(10,10));

        Link link = networkFactory.createLink(Id.createLinkId("1"), node1, node2);
        link.setFreespeed(50.0 / 3.6); // 50 km/h in m/s
        link.setLength(300); // meter
        link.setCapacity(800); // veh / hour
        link.setNumberOfLanes(1);
        link.setAllowedModes(new HashSet<>(Arrays.asList("car", "bus")));

        // Don't forget to add the created elements to the data container.
        network.addNode(node1);
        network.addNode(node2);
        network.addLink(link);

        // Population example
        PopulationFactory populationFactory = population.getFactory();

        // Create a person and its plan
        Person person = populationFactory.createPerson(Id.createPersonId("person"));
        Plan plan = populationFactory.createPlan();

        // Create activities and legs
        Activity act1 = populationFactory.createActivityFromCoord("home", new Coord(150, 250));
        act1.setEndTime(8 * 3600);
        Leg leg = populationFactory.createLeg("car");
        Activity act2 = populationFactory.createActivityFromCoord("work", new Coord(750, 650));

        // Add activities and legs to plan
        plan.addActivity(act1);
        plan.addLeg(leg);
        plan.addActivity(act2);

        // Add plan to person
        person.addPlan(plan);

        // Add person to population
        population.addPerson(person);

        // Attributes
        // Attributes can be stored within the objects
        person.getAttributes().putAttribute("age", 39);
        int age = (Integer) person.getAttributes().getAttribute("age");

    }
}
