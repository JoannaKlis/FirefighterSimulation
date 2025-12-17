package models;

import constants.SimulationConstants;
import java.util.List;

public class IncidentAction {

    private final Incident incident;
    private final List<Car> cars;
    private final boolean falseAlarm;

    private int actionSteps;
    private int returnSteps;
    private Phase phase = Phase.GOING;

    private enum Phase { GOING, ACTION, RETURN, DONE }

    public IncidentAction(Incident incident, List<Car> cars, boolean falseAlarm) {
        this.incident = incident;
        this.cars = cars;
        this.falseAlarm = falseAlarm;

        this.actionSteps = falseAlarm ? 0 : SimulationConstants.getRandomActionSteps();
        this.returnSteps = SimulationConstants.getRandomResponseSteps();
    }

    public void startGoing(int responseSteps) {
        // odstęp między samochodami (ok. 0.4 - 0.6 sekundy przy 25 FPS)
        int delayBetweenCars = 12;

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);

            // kolejny samochód dostaje o 'delayBetweenCars' więcej kroków na dojazd (aby jechały jeden za drugim w wizualizacji)
            int individualResponseSteps = responseSteps + (i * delayBetweenCars);

            car.dispatch(incident.getPosition(), falseAlarm, individualResponseSteps);
        }
    }

    public void update() {
        switch (phase) {
            case GOING -> {
                if (allAtTarget()) {
                    if (falseAlarm) {
                        startReturn();
                    } else {
                        phase = Phase.ACTION;
                    }
                }
            }
            case ACTION -> {
                actionSteps--; // jeden licznik dla całej grupy samochodów, które wyjechały jednocześnie
                if (actionSteps <= 0) {
                    startReturn(); // powrót dla wszystkich z zachowaniem kolumny
                }
            }
            case RETURN -> {
                if (allAtHome()) {
                    phase = Phase.DONE;
                }
            }
        }
    }

    private void startReturn() {
        int delayBetweenCars = 12;

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            // kolejny samochód dostaje o 'delayBetweenCars' więcej kroków na dojazd (aby jechały jeden za drugim w wizualizacji)
            int individualReturnSteps = returnSteps + (i * delayBetweenCars);
            car.initiateReturn(individualReturnSteps);
        }
        phase = Phase.RETURN;
    }

    private boolean allAtTarget() {
        return cars.stream().allMatch(
                c -> c.getCurrentPosition().equals(c.getTargetPosition())
        );
    }

    private boolean allAtHome() {
        return cars.stream().allMatch(
                c -> c.getStatus() == CarStatus.FREE
        );
    }

    public boolean isDone() {
        return phase == Phase.DONE;
    }

    public Incident getIncident() {
        return incident;
    }
}
