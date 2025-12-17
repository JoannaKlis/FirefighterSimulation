package strategy;

import constants.SimulationConstants;
import implementation.ClosestJRGIterator;
import interfaces.IDispatchStrategy;
import models.Car;
import models.Incident;
import models.JRG;

import java.util.ArrayList;
import java.util.List;

public class PZStrategy implements IDispatchStrategy {

    @Override
    public List<Car> selectCars(Incident incident, List<JRG> jrgs) {
        int requiredCars = SimulationConstants.PZ_CAR_COUNT;
        List<Car> selectedCars = new ArrayList<>();

        // iterator posortowany od najbliższej JRG
        ClosestJRGIterator it = new ClosestJRGIterator(jrgs, incident.getPosition());

        while (it.hasNext() && selectedCars.size() < requiredCars) {
            JRG jrg = it.next();
            int stillNeeded = requiredCars - selectedCars.size();

            // pobieramy tyle wolnych aut, ile jest dostępnych w tej jednostce (ale nie więcej niż nam brakuje)
            List<Car> availableInJrg = jrg.getFreeCars(5); // pobierz listę wszystkich wolnych

            for (int i = 0; i < availableInJrg.size() && selectedCars.size() < requiredCars; i++) {
                selectedCars.add(availableInJrg.get(i));
            }
        }

        // zwracamy listę tylko jeśli udało się zebrać pełny skład
        if (selectedCars.size() == requiredCars) {
            return selectedCars;
        } else {
            return List.of(); // jeśli brakuje aut w całym mieście, czekamy w kolejce
        }
    }
}