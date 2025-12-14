package strategy;

import constants.SimulationConstants;
import implementation.ClosestJRGIterator;
import interfaces.IDispatchStrategy;
import models.Car;
import models.Incident;
import models.JRG;
import java.util.List;

// Strategia: dysponowanie 2 samochodów dla miejscowego zagrożenia
public class MZStrategy implements IDispatchStrategy {
    @Override
    public void executeDispatch(Incident incident, List<JRG> jrgs, boolean isFalseAlarm) {
        int requiredCars = SimulationConstants.MZ_CAR_COUNT;

        ClosestJRGIterator iterator = new ClosestJRGIterator(jrgs, incident.getPosition());

        while (iterator.hasNext()) {
            JRG jrg = iterator.next();

            List<Car> carsToDispatch = jrg.getFreeCars(requiredCars);

            if (carsToDispatch.size() >= requiredCars) {
                // dysponowanie
                for (Car car : carsToDispatch) {
                    // przekazanie do samochodu, czy to jest Fałszywy Alarm
                    car.dispatch(incident.getPosition(), isFalseAlarm);
                }
                return;
            }
        }
    }
}