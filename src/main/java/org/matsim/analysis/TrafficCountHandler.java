package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleEntersTrafficEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import java.util.HashMap;
import java.util.Map;

public class TrafficCountHandler implements LinkEnterEventHandler, VehicleEntersTrafficEventHandler {

    // network
    private Network network;

    // map to keep track of the number of agents on each link
    private Map<Id<Link>, Integer> linkCounts = new HashMap<>();


    public TrafficCountHandler(Network network) {
        this.network = network;

        for (Id<Link> linkId : network.getLinks().keySet()) {
            linkCounts.put(linkId, 0);
        }

    }

    @Override
    public void reset(int iteration) {
        for (Id<Link> linkId : network.getLinks().keySet()) {
            linkCounts.put(linkId, 0);
        }
    }

    @Override
    public void handleEvent(LinkEnterEvent event) {
        Id<Link> linkId = event.getLinkId();

        // increase the count on the link by 1
        int previousCount = linkCounts.get(linkId);
        linkCounts.put(linkId, previousCount + 1);
    }

    @Override
    public void handleEvent(VehicleEntersTrafficEvent event) {
        Id<Link> linkId = event.getLinkId();

        // increase the count on the link by 1
        int previousCount = linkCounts.get(linkId);
        linkCounts.put(linkId, previousCount + 1);
    }


    public Map<Id<Link>, Integer> getLinkCounts() {
        return linkCounts;
    }

}
