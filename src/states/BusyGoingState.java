package states;

import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;

// Stan: samochód zajęty (w drodze na akcję lub w drodze powrotnej)
public class BusyGoingState implements ICarState {
    private int remainingSteps;
    private final boolean isFalseAlarmReported;
    private final boolean goingToIncident;

    private final int totalSteps; // do płynnej animacji

    // konstruktor
    public BusyGoingState(boolean isFalseAlarmReported, boolean goingToIncident) {
        this.remainingSteps = SimulationConstants.getRandomResponseSteps();
        if (this.remainingSteps == 0) this.remainingSteps = 1;

        this.totalSteps = this.remainingSteps;
        this.isFalseAlarmReported = isFalseAlarmReported;
        this.goingToIncident = goingToIncident;
    }

    @Override
    public CarStatus getStatus() {
        return CarStatus.BUSY_GOING;
    }

    @Override
    public void update(Car car) {
        int stepsPassed = totalSteps - remainingSteps;

        // płynne przemieszczenie
        moveTowardsTarget(car, stepsPassed, totalSteps);

        remainingSteps--;

        if (remainingSteps <= 0) {
            // samochód dotarł do celu
            car.setCurrentPosition(car.getTargetPosition());

            if (goingToIncident) {
                // dojechał na miejsce zdarzenia
                if (car.isActualFalseAlarm()) {
                    // AF - natychmiast wraca
                    car.setTargetPosition(car.getHomePosition());

                    // false dla isFalseAlarm, bo w drodze powrotnej nie ma znaczenia
                    car.dispatch(car.getHomePosition(), false);
                } else {
                    // prawdziwe zdarzenie - zaczyna akcję
                    car.setState(new BusyActionState());
                }
            } else {
                // dojechał do jednostki (powrót)
                car.setState(new FreeState());
                car.resetIncidentStatus(); // reset statusu AF po powrocie
            }
        }
    }

    // metoda do płynnej animacji (interpolacja liniowa)
    private void moveTowardsTarget(Car car, int stepsPassed, int totalSteps) {
        Vector2D start = car.getStartPosition();
        Vector2D target = car.getTargetPosition();

        if (start == null || target == null || totalSteps <= 0) return;

        double ratio = (double) stepsPassed / totalSteps;

        // jeśli ratio >= 1.0, oznacza to, że samochód powinien już być w miejscu docelowym
        if (ratio >= 1.0) {
            car.setCurrentPosition(target);
            return;
        }

        double latDiff = target.getComponents()[0] - start.getComponents()[0];
        double lonDiff = target.getComponents()[1] - start.getComponents()[1];

        // liniowa interpolacja: P_nowe = P_start + (P_cel - P_start) * ratio
        double newLat = start.getComponents()[0] + latDiff * ratio;
        double newLon = start.getComponents()[1] + lonDiff * ratio;

        car.setCurrentPosition(new Vector2D(newLat, newLon));
    }

    @Override
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm) {
        // już zajęty, nie można dysponować
    }
}