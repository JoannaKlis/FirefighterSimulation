package states;

import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;

// Stan: samochód zajęty (w drodze na akcję lub w drodze powrotnej)
public class BusyGoingState implements ICarState {
    private int remainingSteps;
    private final boolean goingToIncident;

    private final int totalSteps; // do płynnej animacji

    // ZMIANA: Konstruktor przyjmuje responseSteps
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

        // fragment w update() klasy BusyGoingState
        if (remainingSteps <= 0) {
            car.setCurrentPosition(car.getTargetPosition());
            if (goingToIncident) {
                if (car.isActualFalseAlarm()) {
                    // Przy FA pozwalamy mu wrócić od razu, ale IncidentAction
                    // i tak zazwyczaj obsłuży to przez startReturn()
                    int returnSteps = SimulationConstants.getRandomResponseSteps();
                    car.initiateReturn(returnSteps);
                } else {
                    // Przejście w stan akcji i czekanie na rozkaz z IncidentAction
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

        // Standardowa interpolacja
        double latDiff = target.getComponents()[0] - start.getComponents()[0];
        double lonDiff = target.getComponents()[1] - start.getComponents()[1];

        double newLat = start.getComponents()[0] + latDiff * ratio;
        double newLon = start.getComponents()[1] + lonDiff * ratio;

        // DODATEK: Lekkie przesunięcie wizualne zależne od ID samochodu,
        // żeby kropki nie jechały idealnie po tej samej linii (opcjonalnie)
        int carIndex = Integer.parseInt(car.getId().substring(car.getId().lastIndexOf('-') + 1));
        newLat += (carIndex * 0.00005);

        car.setCurrentPosition(new Vector2D(newLat, newLon));
    }

    @Override
    // ZMIANA: Dostosowanie sygnatury do ICarState
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm, int responseSteps) {
        // już zajęty, nie można dysponować
    }
}