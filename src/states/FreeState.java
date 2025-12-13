package states;

import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;

public class FreeState implements ICarState {
    @Override
    public CarStatus getStatus() {
        return CarStatus.FREE;
    }

    @Override
    public void update(Car car) {
        // Samochód jest w jednostce, nic się nie dzieje.
    }

    @Override
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm) {
        // WZmiana stanu z wolny na w drodze
        car.setTargetPosition(destination);
        car.setState(new BusyGoingState(isFalseAlarm, true)); // true = jedzie na zdarzenie
        System.out.printf("[%s] Rozpoczęto dysponowanie (dojazd). Stan BUSY_GOING.\n", car.getId());
    }
}