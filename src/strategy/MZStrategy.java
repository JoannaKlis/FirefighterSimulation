package strategy;

import constants.SimulationConstants;
import implementation.ClosestJRGIterator;
import interfaces.IDispatchStrategy;
import models.Car;
import models.Incident;
import models.JRG;
import java.util.List;

// Wzorzec Strategia: Dysponowanie 2 samochodów dla Miejscowego Zagrożenia/Alarmu Fałszywego
public class MZStrategy implements IDispatchStrategy {
    @Override
    public void executeDispatch(Incident incident, List<JRG> jrgs) {
        int requiredCars = SimulationConstants.MZ_CAR_COUNT;

        // Użycie Iteratora do znalezienia najbliższej dostępnej jednostki
        ClosestJRGIterator iterator = new ClosestJRGIterator(jrgs, incident.getPosition());

        while (iterator.hasNext()) {
            JRG jrg = iterator.next();

            // Dysponowanie tylko z jednostek JRG-1..7
            if (jrg.getName().startsWith("JRG-") && jrg.getName().length() <= 5) {

                List<Car> carsToDispatch = jrg.getFreeCars(requiredCars);

                if (carsToDispatch.size() >= requiredCars) {
                    // Dysponowanie
                    boolean isFalseAlarm = incident.getType() == models.IncidentType.AF;

                    for (Car car : carsToDispatch) {
                        car.dispatch(incident.getPosition(), isFalseAlarm);
                    }

                    System.out.printf("[DISPATCH] MZ/AF: Zadysponowano %d samochody z %s. Dystans: %.2f km.\n",
                            requiredCars, jrg.getName(), ClosestJRGIterator.calculateHaversineDistance(jrg.getPosition(), incident.getPosition()));
                    return;
                }
            }
        }

        System.out.println("[DISPATCH] MZ/AF: Brak dostępnych 2 samochodów w żadnej jednostce JRG-1..7!");
    }
}