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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class RunTravelSpeedEventHandler {

    public static void main(String[] args) throws IOException {

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
        eventsReader.readFile("scenarios/siouxfalls-2014/simulation_output/ITERS/it.10/10.events.xml.gz");

        // get the travel speeds object from handler
        Map<Id<Link>, Double> travelSpeeds = travelSpeedHandler.getLinkSpeeds();

        // write travel speeds to file
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output/travel_speeds.csv")));

        // write header
        String header = String.join(";", new String[] { //
                "link_id", //
                "travel_speed", //
        });
        writer.write(header + "\n");
        writer.flush();

        // write all the data
        for (Id<Link> linkId : travelSpeeds.keySet()) {
            double speed = travelSpeeds.get(linkId);

            String entry = String.join(";", new String[] { //
                    linkId.toString(), //
                    String.valueOf(speed), //
            });

            writer.write(entry + "\n");
            writer.flush();
        }

        // close file
        writer.flush();
        writer.close();
    }
}
