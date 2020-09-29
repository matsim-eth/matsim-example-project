package org.matsim.api;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

public class CoordUtilsExamples {

    public static void main(String[] args) {

        // Create some coordinates
        Coord coord1 = new Coord(0.0, 0.0);
        Coord coord2 = new Coord(10.0, 10.0);

        // Add them
        Coord coord3 = CoordUtils.plus(coord1, coord2);

        // Subtract them
        Coord coord4 = CoordUtils.minus(coord1, coord2);

        // Get distance between coordinates
        double distance = CoordUtils.calcEuclideanDistance(coord1, coord2);
    }
}
