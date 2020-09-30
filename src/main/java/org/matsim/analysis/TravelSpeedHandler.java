package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.events.algorithms.Vehicle2DriverEventHandler;

import java.util.HashMap;
import java.util.Map;


public class TravelSpeedHandler implements LinkEnterEventHandler, LinkLeaveEventHandler, PersonArrivalEventHandler {

    // handler to keep track of the driver of each vehicle
    private Vehicle2DriverEventHandler vehicle2DriverEventHandler;

    // network
    private Network network;

    // map to keep track of the enter time on a link for each agent
    private Map<Id<Person>, Double> enterTimes = new HashMap<>();

    // map to keep track of the number of agents on each link per hour
    private Map<Id<Link>, int[]> linkCountsPerHour = new HashMap<>();

    // map to keep track of the cumulative speed on each link per hour
    private Map<Id<Link>, double[]> cumulativeLinkSpeedsPerHour = new HashMap<>();

    // we pass the network to the class constructor, because we need it to initialize our maps
    // also, we need the Vehicle2DriverEventHandler to track the driver of each vehicle
    public TravelSpeedHandler(Vehicle2DriverEventHandler vehicle2DriverEventHandler, Network network) {
        this.vehicle2DriverEventHandler = vehicle2DriverEventHandler;
        this.network = network;

        // loop through all links in the network
        for (Id<Link> linkId : network.getLinks().keySet()) {

            // initialize each array for each link
            linkCountsPerHour.put(linkId, new int[30]);
            cumulativeLinkSpeedsPerHour.put(linkId, new double[30]);
        }
    }

    @Override
    public void reset(int iteration) {

        // clear the contents of the enterTimes map
        enterTimes.clear();

        // loop through all links in the network
        for (Id<Link> linkId : network.getLinks().keySet()) {

            // reset the initial values
            linkCountsPerHour.put(linkId, new int[30]);
            cumulativeLinkSpeedsPerHour.put(linkId, new double[30]);
        }
    }

    // what happens when an agent enters a link
    @Override
    public void handleEvent(LinkEnterEvent event) {

        // get the person and link id
        Id<Person> personId = vehicle2DriverEventHandler.getDriverOfVehicle(event.getVehicleId());

        // get time the event
        double time = event.getTime();

        // record the time the agent entered the link
        enterTimes.put(personId, time);

    }

    // what happens when the agent exits a link
    @Override
    public void handleEvent(LinkLeaveEvent event) {
        Id<Person> personId = vehicle2DriverEventHandler.getDriverOfVehicle(event.getVehicleId());
        Id<Link> linkId = event.getLinkId();

        // The first time an agent enters the network, a VehicleEntersTrafficEvent (not LinkEnterEvent) is triggered.
        // Since we only listen to LinkEnterEvents and LinkExitEvents,
        // the agent will not be in the enterTimes map after the VehicleEntersTrafficEvent.
        // Therefore, we first need to check whether the agent is in the map before proceeding any further.

        if (enterTimes.containsKey(personId)) {

            // retrieve the enter time previously stored (it must be on the same link)
            // this remove method removes the entry from the map and returns it
            double enterTime = enterTimes.remove(personId);

            // get the time bin of the enter time
            int timeBin = getTimeBin(enterTime);

            // increase the count at the enter time bin by 1
            int previousCountPerHour = linkCountsPerHour.get(linkId)[timeBin];
            linkCountsPerHour.get(linkId)[timeBin] = previousCountPerHour + 1;

            // get the exit time from the event
            double exitTime = event.getTime();

            // compute the travel speed
            double travelTime = exitTime - enterTime;
            double travelSpeed = network.getLinks().get(linkId).getLength() / travelTime;

            // add to cumulative travel speed at the enter time bin
            double previousCumulativeSpeedPerHour = cumulativeLinkSpeedsPerHour.get(linkId)[timeBin];
            cumulativeLinkSpeedsPerHour.get(linkId)[timeBin] = previousCumulativeSpeedPerHour + travelSpeed;
        }

    }

    // when the agent arrives at there destination, we want to remove them from the enterTimes map
    @Override
    public void handleEvent(PersonArrivalEvent event) {
        // get the person id
        Id<Person> personId = event.getPersonId();

        // remove agent from map
        enterTimes.remove(personId);
    }

    // we compute the average link speeds per hour here
    public Map<Id<Link>, double[]> getLinkSpeedsPerHour() {

        // create a new map to store the average link speeds (so we do not override our other maps)
        Map<Id<Link>, double[]> linkSpeedsPerHour = new HashMap<>();

        // loop through all links in the network
        for ( Link link : network.getLinks().values() ) {

            // get link id
            Id<Link> linkId = link.getId();

            // get the corresponding link traffic counts
            int[] counts = linkCountsPerHour.get(linkId);

            // put an empty array in the linkSpeedsPerHour map if not already there
            linkSpeedsPerHour.putIfAbsent(linkId, new double[counts.length]);

            // loop through the time bins
            for (int timeBin = 0; timeBin < counts.length; timeBin++) {

                // get the count for that time bin
                int count = counts[timeBin];

                // check if we have any counts
                if (count == 0) {

                    // if no traffic count, set average link speed to freespeed
                    linkSpeedsPerHour.get(linkId)[timeBin] = link.getFreespeed();

                } else {

                    // otherwise, compute average speed
                    double averageSpeed = cumulativeLinkSpeedsPerHour.get(linkId)[timeBin] / count;
                    linkSpeedsPerHour.get(linkId)[timeBin] = averageSpeed;
                }

            }
        }

        // return the average link speeds
        return linkSpeedsPerHour;
    }

    // method for computing the time bin given the event time in seconds
    private int getTimeBin(double time) {
        return (int) Math.min(Math.floor(time / 3600), 29);
    }
}
