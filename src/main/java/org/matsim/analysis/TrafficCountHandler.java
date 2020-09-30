package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import java.util.HashMap;
import java.util.Map;

public class TrafficCountHandler implements LinkEnterEventHandler {

    // network
    private Network network;

    // map to keep track of the number of agents on each link
    private Map<Id<Link>, Integer> linkCounts = new HashMap<>();
    private Map<Id<Link>, int[]> linkCountsPerHour = new HashMap<>();

    // we pass the network to the class constructor, because we need it to initialize our maps
    public TrafficCountHandler(Network network) {

        // assign network
        this.network = network;

        // for each link in network, set the initial values to our maps
        for (Id<Link> linkId : network.getLinks().keySet()) {

            // set an initial value of zero
            linkCounts.put(linkId, 0);

            // we create a integer array of length 30 since we have 30 hours in the simulation
            // int[30] creates an array of length 30 with all zeros
            linkCountsPerHour.put(linkId, new int[30]);
        }
    }

    // the reset method is called at each iteration before the mobsim
    // it is important to reset any data containers you might want to reset here
    // here, we want the counts to be reset to zero before we add them up for the new iteration
    @Override
    public void reset(int iteration) {

        // for each link in network, reset the initial values to our maps
        for (Id<Link> linkId : network.getLinks().keySet()) {

            // reset an initial value of zero
            linkCounts.put(linkId, 0);

            // reset the integer arrays
            linkCountsPerHour.put(linkId, new int[30]);
        }
    }

    // we need to implement the event handler here
    // i.e. what we want to happen each time a LinkEnterEvent is triggered
    @Override
    public void handleEvent(LinkEnterEvent event) {

        // get link where the event takes place
        Id<Link> linkId = event.getLinkId();

        // get time and time bin of the event
        double time = event.getTime();
        int timeBin = getTimeBin(time);

        // increase the total count on the link by 1
        int previousCount = linkCounts.get(linkId);
        linkCounts.put(linkId, previousCount + 1);

        // increase the count for that time bin by 1
        int previousCountPerHour = linkCountsPerHour.get(linkId)[timeBin];
        linkCountsPerHour.get(linkId)[timeBin] = previousCountPerHour + 1;
    }

    // method to get the total link counts
    public Map<Id<Link>, Integer> getLinkCounts() {
        return linkCounts;
    }

    // method to get the link counts per hour
    public Map<Id<Link>, int[]> getLinkCountsPerHour() {
        return linkCountsPerHour;
    }

    // detailed method for computing the time bin given the event time in seconds
    private int getTimeBin(double time) {

        // we first divide the time by 3600, which gives us the time bin as a decimal
        // e.g. time = 1800 -> timeBinDecimal = 1800 / 3600 = 0.5
        double timeBinDecimal = time / 3600;

        // then, we take the floor of this number, to get the integer part
        // e.g. timeBinDecimal = 0.5 -> timeBinInteger = 0, i.e. we are in the first time bin
        double timeBinInteger = Math.floor(timeBinDecimal);

        // then, we return the minimum value between this value and 29, cast as an integer
        // we take the min for the rare case where the time = 108000, i.e. exactly 30 hours
        // in this case, timeBinInteger would become 30, but the since of our integer array is 30,
        // which mean the bins range from 0 to 29
        // leaving the bin as 30 would cause the code to attempt to access an array element outside the size of the array
        return (int) Math.min(timeBinInteger, 29);
    }

}
