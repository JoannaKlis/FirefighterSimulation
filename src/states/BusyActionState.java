package states;

import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;

public class BusyActionState implements ICarState {

    public BusyActionState() {
    }

    @Override
    public CarStatus getStatus() {
        return CarStatus.BUSY_ACTION;
    }

    @Override
    public void update(Car car) {
        // czeka, aż IncidentAction wywoła car.initiateReturn()
    }

    @Override
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm, int responseSteps) {
        // zajęty
    }
}