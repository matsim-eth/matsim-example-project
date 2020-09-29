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

    // we pass our event handler here
    private TrafficCountHandler trafficCountHandler;

    public TrafficCountControlerListener(TrafficCountHandler trafficCountHandler) {
        this.trafficCountHandler = trafficCountHandler;
    }

    @Override
    public void notifyStartup(StartupEvent event) {
        event.getServices().getEvents().addHandler(trafficCountHandler);
    }

    @Override
    public void notifyIterationEnds(IterationEndsEvent event) {

        // get the travel speeds object from handler
        Map<Id<Link>, Integer> linkCounts = trafficCountHandler.getLinkCounts();

        // write travel speeds to file
        int iteration = event.getIteration();
        String outputPath = event.getServices().getControlerIO().getOutputPath();
        String fileName = outputPath + "/ITERS/it." + iteration + "/" + iteration + ".traffic_counts.csv";
        BufferedWriter writer = IOUtils.getBufferedWriter(fileName);

        try {
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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
