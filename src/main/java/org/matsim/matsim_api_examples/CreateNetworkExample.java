package org.matsim.matsim_api_examples;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.Arrays;
import java.util.HashSet;

public class CreateNetworkExample {

    public static void main(String[] args) {

        // Create empty network data container
        Network network = NetworkUtils.createNetwork();

        // Get the network factory
        NetworkFactory factory = network.getFactory();

        // Create nodes using the factory (notice we used different ways for creating the node ids and the coordinates)
        Node node0 = factory.createNode(Id.create("0", Node.class), CoordUtils.createCoord(0.0, 0.0)); // first option
        Node node1 = factory.createNode(Id.createNodeId("1"), new Coord(100.0, 100.0)); // second option

        // Create links using the factory
        Link link = factory.createLink(Id.createLinkId("0_1"), node0, node1);

        // Set link attributes (otherwise they will be set to default values)
        link.setFreespeed(50.0 / 3.6); // 50 km/h in m/s
        link.setLength(300); // meter
        link.setCapacity(800); // veh / hour
        link.setNumberOfLanes(1);
        link.setAllowedModes(new HashSet<>(Arrays.asList("car", "bus")));

        // Don't forget to add the created elements to the data container.
        network.addNode(node0);
        network.addNode(node1);
        network.addLink(link);

        // write network to file
        new NetworkWriter(network).write("output/simple_network_example.xml");

    }

}
