package org.matsim.api;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Arrays;
import java.util.HashSet;

public class CreateNetworkExample {

    public static void main(String[] args) {

        // create network data container and factory
        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
        Network network = scenario.getNetwork();
        NetworkFactory factory = network.getFactory();

        // create nodes
        Node node0 = factory.createNode(Id.createNodeId("0"), new Coord(0.0, 0.0));
        Node node1 = factory.createNode(Id.createNodeId("1"), new Coord(1000.0, 0.0));

        // add to network
        network.addNode(node0);
        network.addNode(node1);

        // create links
        Link link = factory.createLink(Id.createLinkId("0_1"), node0, node1);
        link.setCapacity(1800.0);
        link.setLength(1000.0);
        link.setFreespeed(50.0 / 3.6);
        link.setNumberOfLanes(1);
        link.setAllowedModes(new HashSet<>(Arrays.asList("car", "bus")));

        // add to network
        network.addLink(link);

        // write network to file
        new NetworkWriter(network).write("output/network.xml");
    }

}
