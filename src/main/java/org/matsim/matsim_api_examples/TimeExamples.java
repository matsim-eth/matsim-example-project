package org.matsim.api;

import org.matsim.core.utils.misc.Time;

public class TimeExamples {

    public static void main(String[] args) {

        // MATSim internally uses "seconds after midnight".
        // To convert to and from human-readable time formats, use the Time class:

        double seconds = Time.parseTime("07:12:35");
        String time = Time.writeTime(43200);

    }
}
