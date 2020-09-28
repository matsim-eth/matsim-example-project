package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class RunTrafficCountEventHandler {

    public static void main(String[] args) throws IOException {

        // load network
        Network network = NetworkUtils.createNetwork();
        MatsimNetworkReader networkReader = new MatsimNetworkReader(network);
        networkReader.readFile("scenarios/siouxfalls-2014/Siouxfalls_network_PT.xml");

        // set up event manager
        EventsManager eventsManager = new EventsManagerImpl();
        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);

        // create handlers
        TrafficCountHandler trafficCountHandler = new TrafficCountHandler(network);

        // add handlers to event manager
        eventsManager.addHandler(trafficCountHandler);

        // read events file
        eventsReader.readFile("scenarios/siouxfalls-2014/simulation_output/ITERS/it.10/10.events.xml.gz");

        // get the travel speeds object from handler
        Map<Id<Link>, Integer> linkCounts = trafficCountHandler.getLinkCounts();

        // write travel speeds to file
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output/traffic_counts.csv")));

        // write header
        String header = String.join(";", new String[] { //
                "link_id", //
                "count", //
        });
        writer.write(header + "\n");
        writer.flush();

        // write all the data
        for (Id<Link> linkId : linkCounts.keySet()) {
            int count = linkCounts.get(linkId);

            String entry = String.join(";", new String[] { //
                    linkId.toString(), //
                    String.valueOf(count), //
            });

            writer.write(entry + "\n");
            writer.flush();
        }

        // close file
        writer.flush();
        writer.close();
    }
}
