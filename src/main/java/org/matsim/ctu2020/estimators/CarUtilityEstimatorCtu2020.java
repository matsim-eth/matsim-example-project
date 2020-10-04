/**
 * 
 */
package org.matsim.ctu2020.estimators;

import java.util.List;

import org.eqasim.core.simulation.mode_choice.utilities.estimators.CarUtilityEstimator;
import org.eqasim.core.simulation.mode_choice.utilities.estimators.EstimatorUtils;
import org.eqasim.core.simulation.mode_choice.utilities.predictors.CarPredictor;
import org.eqasim.core.simulation.mode_choice.utilities.predictors.PersonPredictor;
import org.eqasim.core.simulation.mode_choice.utilities.variables.CarVariables;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.ctu2020.parameters.ModeParametersCtu2020;

import com.google.inject.Inject;

/**
 * @author stefanopenazzi
 *
 */
public class CarUtilityEstimatorCtu2020 extends CarUtilityEstimator { 
	private final ModeParametersCtu2020 parameters;
	
	private final CarPredictor carPredictor;

	// the constructor must support the annotation @Inject in order to construct its
	// parameters using Guice. This makes ModeParametersCtu2020 available by its @Provider
	// defined in Ctu2020ModeChoiceModule
	@Inject
	public CarUtilityEstimatorCtu2020(ModeParametersCtu2020 parameters, PersonPredictor personPredictor,
			CarPredictor carPredictor) { 
		super(parameters, carPredictor);
		this.carPredictor = carPredictor;
		this.parameters = parameters;
		
	}

	protected double estimateTravelTime(CarVariables variables_car) {
		return parameters.sfCar.beta * variables_car.travelTime_min;
	}

	@Override
	protected double estimateMonetaryCostUtility(CarVariables variables) {
		return EstimatorUtils.interaction(variables.euclideanDistance_km, parameters.referenceEuclideanDistance_km,
				parameters.lambdaCostEuclideanDistance) * variables.cost_MU;
	}

	@Override
	public double estimateUtility(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
		CarVariables variables_car = carPredictor.predict(person, trip, elements);

		double utility = 0.0;

		utility += estimateConstantUtility();
		utility += (estimateTravelTime(variables_car) + estimateMonetaryCostUtility(variables_car))
				* (parameters.betaCost_u_MU /10) * parameters.betaCost_u_MU;

		return utility;
	}
}
