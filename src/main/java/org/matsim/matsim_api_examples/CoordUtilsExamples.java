package org.matsim.matsim_api_examples;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

public class CoordUtilsExamples {

    public static void main(String[] args) {

        // CoordUtils provides many methods for manipulating coordinates

        // Create some coordinates
        Coord coord1 = CoordUtils.createCoord(0.0, 0.0);
        Coord coord2 = CoordUtils.createCoord(10.0, 10.0);

        System.out.println("coord1: " + coord1.toString());
        System.out.println("coord2: " + coord2.toString());

        // Add them
        Coord coordSum = CoordUtils.plus(coord1, coord2);
        System.out.println("result of adding coordinates: " + coordSum.toString());

        // Subtract them
        Coord coordDifference = CoordUtils.minus(coord1, coord2);
        System.out.println("result of subtracting coordinates: " + coordDifference.toString());

        // Get distance between coordinates
        double distance = CoordUtils.calcEuclideanDistance(coord1, coord2);
        System.out.println("distance between coordinates: " + distance);

    }
}
