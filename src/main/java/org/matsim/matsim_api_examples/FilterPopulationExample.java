package org.matsim.api;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;

public class FilterPopulationExample {

    public static void main(String[] args) {

        // create population data container
        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);

        // read original population file
        new PopulationReader(scenario).readFile("scenarios/siouxfalls-2014/Siouxfalls_population.xml.gz");
        Population originalPopulation = scenario.getPopulation();

        // filter females and add to new filtered population object
        Population filteredPopulation = PopulationUtils.createPopulation(config);

        for (Person person : originalPopulation.getPersons().values()) {
            System.out.println(person.getAttributes().getAttribute("sex").toString());
            if (person.getAttributes().getAttribute("sex").toString().equals("f")) {
                filteredPopulation.addPerson(person);
            }
        }

        // write out new population
        new PopulationWriter(filteredPopulation).write("output/filtered_population.xml");

    }
}
