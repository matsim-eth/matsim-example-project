package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleEntersTrafficEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleLeavesTrafficEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.events.algorithms.Vehicle2DriverEventHandler;

import java.util.HashMap;
import java.util.Map;


public class TravelSpeedHandler implements LinkEnterEventHandler, LinkLeaveEventHandler,
        VehicleEntersTrafficEventHandler, VehicleLeavesTrafficEventHandler {

    // handler to keep track of the driver of each vehicle
    private Vehicle2DriverEventHandler vehicle2DriverEventHandler;

    // network
    private Network network;

    // map to keep track of the enter time on a link for each agent
    private Map<Id<Person>, Double> enterTimes = new HashMap<>();

    // map to keep track of the number of agents on each link
    private Map<Id<Link>, Integer> linkCounts = new HashMap<>();

    // map to keep track of the average speed on each link
    private Map<Id<Link>, Double> linkSpeeds = new HashMap<>();


    public TravelSpeedHandler(Vehicle2DriverEventHandler vehicle2DriverEventHandler, Network network) {
        this.vehicle2DriverEventHandler = vehicle2DriverEventHandler;
        this.network = network;

        for (Id<Link> linkId : network.getLinks().keySet()) {
            linkCounts.put(linkId, 0);
            linkSpeeds.put(linkId, 0.0);
        }

    }

    @Override
    public void reset(int iteration) {
        enterTimes.clear();
        for (Id<Link> linkId : network.getLinks().keySet()) {
            linkCounts.put(linkId, 0);
            linkSpeeds.put(linkId, 0.0);
        }
    }

    @Override
    public void handleEvent(LinkEnterEvent event) {
        Id<Person> personId = vehicle2DriverEventHandler.getDriverOfVehicle(event.getVehicleId());
        Id<Link> linkId = event.getLinkId();

        // record the time the agent entered the link
        enterTimes.put(personId, event.getTime());

        // increase the count on the link by 1
        int previousCount = linkCounts.get(linkId);
        linkCounts.put(linkId, previousCount + 1);
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {
        Id<Person> personId = vehicle2DriverEventHandler.getDriverOfVehicle(event.getVehicleId());
        Id<Link> linkId = event.getLinkId();

        // retrieve the enter time previously stored (it must be on the same link)
        double enterTime = enterTimes.remove(personId);

        // get the exit time
        double exitTime = event.getTime();

        // compute the travel speed
        double travelTime = exitTime - enterTime;
        double travelSpeed = network.getLinks().get(linkId).getLength() / travelTime;

        // add travel speed
        double previousTotal = linkSpeeds.get(linkId);
        linkSpeeds.put(linkId, previousTotal + travelSpeed);
    }

    @Override
    public void handleEvent(VehicleEntersTrafficEvent event) {
        Id<Person> personId = event.getPersonId();
        Id<Link> linkId = event.getLinkId();

        // record the time the agent entered the link
        enterTimes.put(personId, event.getTime());

        // increase the count on the link by 1
        int previousCount = linkCounts.get(linkId);
        linkCounts.put(linkId, previousCount + 1);
    }

    @Override
    public void handleEvent(VehicleLeavesTrafficEvent event) {
        Id<Person> personId = event.getPersonId();
        Id<Link> linkId = event.getLinkId();

        // retrieve the enter time previously stored (it must be on the same link)
        double enterTime = enterTimes.remove(personId);

        // get the exit time
        double exitTime = event.getTime();

        // compute the travel speed
        double travelTime = exitTime - enterTime;
        double travelSpeed = network.getLinks().get(linkId).getLength() / travelTime;

        // add travel speed
        double previousTotal = linkSpeeds.get(linkId);
        linkSpeeds.put(linkId, previousTotal + travelSpeed);
    }

    public Map<Id<Link>, Double> getLinkSpeeds() {
        for ( Id<Link> linkId : linkSpeeds.keySet() ) {

            // get the correspond link traffic count
            double count = linkCounts.get(linkId);

            // if no traffic count, set link speed to freespeed
            // otherwise, compute average speed
            if (count == 0) {
                linkSpeeds.put(linkId, network.getLinks().get(linkId).getFreespeed());
            } else {
                double totalSpeed = linkSpeeds.get(linkId);
                linkSpeeds.put(linkId, totalSpeed / count);
            }
        }

        return linkSpeeds;
    }
}
