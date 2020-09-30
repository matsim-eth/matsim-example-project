package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.Vehicle2DriverEventHandler;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.utils.io.IOUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class RunTravelSpeedEventHandler {

    public static void main(String[] args) {

        // load network
        Network network = NetworkUtils.createNetwork();
        MatsimNetworkReader networkReader = new MatsimNetworkReader(network);
        networkReader.readFile("scenarios/siouxfalls-2014/Siouxfalls_network_PT.xml");

        // set up event manager
        EventsManager eventsManager = new EventsManagerImpl();
        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);

        // create handlers
        Vehicle2DriverEventHandler vehicle2DriverEventHandler = new Vehicle2DriverEventHandler();
        TravelSpeedHandler travelSpeedHandler = new TravelSpeedHandler(vehicle2DriverEventHandler, network);

        // add handlers to event manager
        eventsManager.addHandler(vehicle2DriverEventHandler);
        eventsManager.addHandler(travelSpeedHandler);

        // read events file
        eventsReader.readFile("scenarios/siouxfalls-2014/simulation_output/output_events.xml.gz");

        // write output
        // get the hourly speeds counts object from handler
        Map<Id<Link>, double[]> linkSpeedsPerHour = travelSpeedHandler.getLinkSpeedsPerHour();

        // write hourly speeds to different csv file
        BufferedWriter writer = IOUtils.getBufferedWriter("output/hourly_speeds.csv");

        // we need to wrap the writing in a try/catch structure
        try {
            // write header
            String header = String.join(";", new String[] { //
                    "link_id", //
                    "time_bin", //
                    "speed", //
            });
            writer.write(header + "\n");
            writer.flush();

            // write all the data

            // first loop through the links
            for (Id<Link> linkId : linkSpeedsPerHour.keySet()) {

                // get the speeds per hour for each link
                double[] speedsPerHour = linkSpeedsPerHour.get(linkId);

                // loop through the time bins
                for (int timeBin = 0; timeBin < speedsPerHour.length; timeBin++) {

                    // for the time bin, get the speed
                    double speed = speedsPerHour[timeBin];

                    // create the string to write to file
                    String line = String.join(";", new String[] { //
                            linkId.toString(), //
                            String.valueOf(timeBin), //
                            String.valueOf(speed), //
                    });

                    // write line to file
                    writer.write(line + "\n");
                    writer.flush();
                }
            }

            // close file
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
