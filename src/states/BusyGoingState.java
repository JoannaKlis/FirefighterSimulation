package states;

import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;

public class BusyGoingState implements ICarState {
    private int remainingSteps;
    private final boolean goingToIncident;

    private final int totalSteps; // do płynnej animacji

    // konstruktor przyjmuje responseSteps
    public BusyGoingState(int responseSteps, boolean goingToIncident) {
        this.remainingSteps = responseSteps;
        if (this.remainingSteps <= 0) this.remainingSteps = 1;

        this.totalSteps = this.remainingSteps;
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
            car.setCurrentPosition(car.getTargetPosition());
            if (goingToIncident) {
                if (car.isActualFalseAlarm()) {
                    // przy AF samochód wraca natychmiast
                    int returnSteps = SimulationConstants.getRandomResponseSteps();
                    car.initiateReturn(returnSteps);
                } else {
                    car.setState(new BusyActionState());
                }
            } else {
                car.setState(new FreeState());
                car.resetIncidentStatus();
            }
        }
    }

    // metoda do płynnej animacji (interpolacja liniowa)
    private void moveTowardsTarget(Car car, int stepsPassed, int totalSteps) {
        Vector2D start = car.getStartPosition();
        Vector2D target = car.getTargetPosition();

        if (start == null || target == null || totalSteps <= 0) return;

        double ratio = (double) stepsPassed / totalSteps;
        if (ratio >= 1.0) {
            car.setCurrentPosition(target);
            return;
        }

        // standardowa interpolacja
        double latDiff = target.getComponents()[0] - start.getComponents()[0];
        double lonDiff = target.getComponents()[1] - start.getComponents()[1];

        double newLat = start.getComponents()[0] + latDiff * ratio;
        double newLon = start.getComponents()[1] + lonDiff * ratio;

        int carIndex = Integer.parseInt(car.getId().substring(car.getId().lastIndexOf('-') + 1));
        newLat += (carIndex * 0.00005);

        car.setCurrentPosition(new Vector2D(newLat, newLon));
    }

    @Override
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm, int responseSteps) {
        // już zajęty, nie można dysponować
    }
}