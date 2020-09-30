package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.io.IOUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class TrafficCountControlerListener implements StartupListener, IterationEndsListener {

    // we need the event handler inside the class to be able to access the results
    private TrafficCountHandler trafficCountHandler;

    // we pass our event handler through the class constructor
    public TrafficCountControlerListener(TrafficCountHandler trafficCountHandler) {
        this.trafficCountHandler = trafficCountHandler;
    }

    // this is where we pass our event handler to the events manager
    @Override
    public void notifyStartup(StartupEvent event) {
        event.getServices().getEvents().addHandler(trafficCountHandler);
    }

    // here we implement what we want to happen at the end of each iteration
    @Override
    public void notifyIterationEnds(IterationEndsEvent event) {

        // get the iteration number from the event
        int iteration = event.getIteration();

        // get the output path for the simulation
        String outputPath = event.getServices().getControlerIO().getOutputPath();

        // write output (note that this is basically the same code as in the RunTrafficCountEventHandler)

        // 1) total counts
        // get the link counts object from handler
        Map<Id<Link>, Integer> linkCounts = trafficCountHandler.getLinkCounts();

        // write counts to csv file in the simulation output folder
        String fileName1 = outputPath + "/ITERS/it." + iteration + "/" + iteration + ".counts.csv";
        BufferedWriter writer1 = IOUtils.getBufferedWriter(fileName1);

        // we need to wrap the writing in a try/catch structure
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

        // write counts to csv file in the simulation output folder
        String fileName2 = outputPath + "/ITERS/it." + iteration + "/" + iteration + ".hourly_counts.csv";
        BufferedWriter writer2 = IOUtils.getBufferedWriter(fileName2);

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
