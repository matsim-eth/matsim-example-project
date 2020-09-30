package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.utils.io.IOUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class RunTrafficCountEventHandler {

    // this is an example run script to read and process events that have been outputted after a MATSim simulation
    public static void main(String[] args) {

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
        eventsReader.readFile("scenarios/siouxfalls-2014/simulation_output/output_events.xml.gz");

        // write output

        // 1) total counts
        // get the link counts object from handler
        Map<Id<Link>, Integer> linkCounts = trafficCountHandler.getLinkCounts();

        // write counts to csv file
        BufferedWriter writer1 = IOUtils.getBufferedWriter("output/counts.csv");

        try {
            // write header
            String header = String.join(";", new String[] { //
                    "link_id", //
                    "count", //
            });
            writer1.write(header + "\n");
            writer1.flush();

            // write all the data

            // loop through the links
            for (Id<Link> linkId : linkCounts.keySet()) {

                // get the count for that link
                int count = linkCounts.get(linkId);

                // create the string to write to file
                String line = String.join(";", new String[] { //
                        linkId.toString(), //
                        String.valueOf(count), //
                });

                // write line to file
                writer1.write(line + "\n");
                writer1.flush();
            }

            // close file
            writer1.flush();
            writer1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2) hourly counts
        // get the hourly link counts object from handler
        Map<Id<Link>, int[]> linkCountsPerHour = trafficCountHandler.getLinkCountsPerHour();

        // write hourly counts to different csv file
        BufferedWriter writer2 = IOUtils.getBufferedWriter("output/hourly_counts.csv");

        // we need to wrap the writing in a try/catch structure
        try {
            // write header
            String header = String.join(";", new String[] { //
                    "link_id", //
                    "time_bin", //
                    "count", //
            });
            writer2.write(header + "\n");
            writer2.flush();

            // write all the data

            // first loop through the links
            for (Id<Link> linkId : linkCountsPerHour.keySet()) {

                // get the counts per hour for each link
                int[] countsPerHour = linkCountsPerHour.get(linkId);

                // loop through the time bins
                for (int timeBin = 0; timeBin < countsPerHour.length; timeBin++) {

                    // for the time bin, get the count
                    int count = countsPerHour[timeBin];

                    // create the string to write to file
                    String line = String.join(";", new String[] { //
                            linkId.toString(), //
                            String.valueOf(timeBin), //
                            String.valueOf(count), //
                    });

                    // write line to file
                    writer2.write(line + "\n");
                    writer2.flush();
                }
            }

            // close file
            writer2.flush();
            writer2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
