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
        for (Car car : cars) {
            car.dispatch(incident.getPosition(), falseAlarm, responseSteps);
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
                actionSteps--;
                if (actionSteps <= 0) {
                    startReturn();
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
        for (Car car : cars) {
            car.initiateReturn(returnSteps);
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
}
