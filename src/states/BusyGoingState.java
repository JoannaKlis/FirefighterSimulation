package states;

import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;

// Stan: Samochód Zajęty (W drodze na akcję lub w drodze powrotnej)
public class BusyGoingState implements ICarState {
    private int remainingSteps;
    private final boolean isFalseAlarm;
    private final boolean goingToIncident; // true: dojazd do zdarzenia, false: powrót do JRG

    // Konstruktor
    public BusyGoingState(boolean isFalseAlarm, boolean goingToIncident) {
        // Czas dojazdu/powrotu (0-3s w krokach)
        this.remainingSteps = SimulationConstants.getRandomResponseSteps();
        // Zapewnienie, że czas jest > 0, aby uniknąć błędów w moveTowardsTarget
        if (this.remainingSteps == 0) this.remainingSteps = 1;

        this.isFalseAlarm = isFalseAlarm;
        this.goingToIncident = goingToIncident;
    }

    @Override
    public CarStatus getStatus() {
        return CarStatus.BUSY_GOING;
    }

    @Override
    public void update(Car car) {
        // Symulacja ruchu
        moveTowardsTarget(car, remainingSteps);

        remainingSteps--;

        if (remainingSteps <= 0) {
            if (goingToIncident) {
                // Dojechał na miejsce
                car.setCurrentPosition(car.getTargetPosition());

                if (isFalseAlarm) {
                    // Alarm Fałszywy - natychmiast wraca
                    car.setTargetPosition(car.getHomePosition());
                    car.setState(new BusyGoingState(true, false)); // false = powrót
                    System.out.printf("[%s] Dojechał (AF). Zaczyna powrót.\n", car.getId());
                } else {
                    // Prawdziwe zdarzenie - zaczyna akcję
                    car.setState(new BusyActionState());
                    System.out.printf("[%s] Dojechał. Zaczyna akcję (BUSY_ACTION).\n", car.getId());
                }
            } else {
                // Dojechał do jednostki (Powrót)
                car.setCurrentPosition(car.getHomePosition());
                car.setState(new FreeState()); // Powrót i zmiana stanu na wolny (Warunek 11)
                System.out.printf("[%s] Powrócił do bazy. Stan FREE.\n", car.getId());
            }
        }
    }

    // Uproszczona symulacja ruchu (przesunięcie w kierunku celu)
    private void moveTowardsTarget(Car car, int stepsRemaining) {
        Vector2D current = car.getCurrentPosition();
        Vector2D target = car.getTargetPosition();

        if (current == null || target == null || stepsRemaining <= 0) return;

        double latDiff = target.getComponents()[0] - current.getComponents()[0];
        double lonDiff = target.getComponents()[1] - current.getComponents()[1];

        // Całkowita liczba kroków podróży (stepsRemaining + krok wykonany)
        int totalSteps = SimulationConstants.MAX_RESPONSE_TIME_S * SimulationConstants.STEPS_PER_SECOND;

        // Przesunięcie na jeden krok: (Cała odległość / Całkowita liczba kroków, jeśli ruch jest liniowy)
        double stepLat = latDiff / totalSteps;
        double stepLon = lonDiff / totalSteps;

        double newLat = current.getComponents()[0] + stepLat;
        double newLon = current.getComponents()[1] + stepLon;

        car.setCurrentPosition(new Vector2D(newLat, newLon));
    }

    @Override
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm) {
        // Już zajęty, nie można dysponować
    }
}