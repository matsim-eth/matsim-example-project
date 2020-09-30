package org.matsim.matsim_api_examples;

import org.matsim.core.utils.misc.Time;

public class TimeExamples {

    public static void main(String[] args) {

        // MATSim internally uses "seconds after midnight".
        // To convert to and from human-readable time formats, use the Time class:

        String inputString = "07:12:35";
        double outputInSeconds = Time.parseTime("07:12:35");

        System.out.println("Convert from string to seconds.");
        System.out.println("input: " + inputString + ", output: " + outputInSeconds);

        double inputInSeconds = 43200;
        String outputString = Time.writeTime(inputInSeconds);

        System.out.println("Convert from seconds to string.");
        System.out.println("input: " + inputInSeconds + ", output: " + outputString);

    }
}
