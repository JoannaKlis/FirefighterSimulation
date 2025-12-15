package strategy;

import constants.SimulationConstants;
import implementation.ClosestJRGIterator;
import interfaces.IDispatchStrategy;
import models.Car;
import models.Incident;
import models.JRG;

import java.util.List;

public class PZStrategy implements IDispatchStrategy {

    @Override
    public List<Car> selectCars(Incident incident, List<JRG> jrgs) {

        int requiredCars = SimulationConstants.PZ_CAR_COUNT;
        ClosestJRGIterator it =
                new ClosestJRGIterator(jrgs, incident.getPosition());

        while (it.hasNext()) {
            JRG jrg = it.next();
            List<Car> freeCars = jrg.getFreeCars(requiredCars);

            if (freeCars.size() >= requiredCars) {
                return freeCars;
            }
        }
        return List.of();
    }
}