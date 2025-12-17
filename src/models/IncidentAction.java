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
        // Odstęp między samochodami (ok. 0.4 - 0.6 sekundy przy 25 FPS)
        int delayBetweenCars = 12;

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);

            // Każdy kolejny samochód dostaje o 'delayBetweenCars' więcej kroków na dojazd.
            // Powoduje to, że jadą w szyku jeden za drugim.
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
                        // Opcjonalnie: upewnij się, że actionSteps jest wylosowane raz dla całej akcji
                        // (To już robisz w konstruktorze IncidentAction)
                    }
                }
            }
            case ACTION -> {
                actionSteps--; // Ten licznik bije dla całej grupy jednocześnie
                if (actionSteps <= 0) {
                    startReturn(); // Wywołuje powrót dla wszystkich z zachowaniem kolumny
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
        int delayBetweenCars = 12; // ok. 0.6 sekundy odstępu między autami (15 kroków / 25 fps)

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            // Każdy kolejny samochód ma o 'delayBetweenCars' więcej kroków do celu
            // dzięki temu nie najeżdżają na siebie i wracają w szyku
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
