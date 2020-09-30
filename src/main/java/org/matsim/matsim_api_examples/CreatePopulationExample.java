package org.matsim.matsim_api_examples;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;

public class CreatePopulationExample {

    public static void main(String[] args) {

        // Create an empty population data container
        Population population = PopulationUtils.createPopulation(ConfigUtils.createConfig());

        // Get the population factory
        PopulationFactory populationFactory = population.getFactory();

        // Create a person and its plan using the factory
        Id<Person> personId = Id.createPersonId("person");
        Person person = populationFactory.createPerson(personId);
        Plan plan = populationFactory.createPlan();

        // Create activities and legs to add to the plan using the factory
        Activity act1 = populationFactory.createActivityFromCoord("home", new Coord(150, 250));
        act1.setEndTime(8 * 3600);
        Leg leg = populationFactory.createLeg("car");
        Activity act2 = populationFactory.createActivityFromCoord("work", new Coord(750, 650));

        // Add activities and legs to plan (do not forget!)
        plan.addActivity(act1);
        plan.addLeg(leg);
        plan.addActivity(act2);

        // Add plan to person (do not forget!)
        person.addPlan(plan);

        // We can also add attributes to the person
        person.getAttributes().putAttribute("age", 39);

        // And we can access them later if needed
        int age = (Integer) person.getAttributes().getAttribute("age");
        System.out.println("person age: " + age);

        // Add person to population (do not forget!)
        population.addPerson(person);

        // Write out this simple population to file (see what you created)
        new PopulationWriter(population).write("output/simple_population_example.xml");

    }

}
