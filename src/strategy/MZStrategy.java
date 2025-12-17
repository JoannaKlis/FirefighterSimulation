package strategy;

import constants.SimulationConstants;
import implementation.ClosestJRGIterator;
import interfaces.IDispatchStrategy;
import models.Car;
import models.Incident;
import models.JRG;

import java.util.ArrayList;
import java.util.List;

public class MZStrategy implements IDispatchStrategy {

    @Override
    public List<Car> selectCars(Incident incident, List<JRG> jrgs) {
        int requiredCars = SimulationConstants.MZ_CAR_COUNT;
        List<Car> selectedCars = new ArrayList<>();

        ClosestJRGIterator it = new ClosestJRGIterator(jrgs, incident.getPosition());

        while (it.hasNext() && selectedCars.size() < requiredCars) {
            JRG jrg = it.next();
            List<Car> availableInJrg = jrg.getFreeCars(5);

            for (int i = 0; i < availableInJrg.size() && selectedCars.size() < requiredCars; i++) {
                selectedCars.add(availableInJrg.get(i));
            }
        }

        if (selectedCars.size() == requiredCars) {
            return selectedCars;
        } else {
            return List.of();
        }
    }
}