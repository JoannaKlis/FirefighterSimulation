package states;

import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;
import java.util.concurrent.ThreadLocalRandom;

public class FreeState implements ICarState {

    // stała określająca, jak daleko od bazy wyjeżdża auto (w stopniach lat/lon)
    private static final double DISPATCH_OFFSET = 0.0001;

    @Override
    public CarStatus getStatus() {
        return CarStatus.FREE;
    }

    @Override
    public void update(Car car) {
        // samochód jest w jednostce, nic się nie dzieje.
    }

    @Override
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm) {
        // zmiana stanu z wolny na w drodze
        car.setTargetPosition(destination);

        // ustawienie pozycji startowej nieco poza JRG, aby samochód faktycznie "wyjechał"
        Vector2D home = car.getHomePosition();
        double offsetX = ThreadLocalRandom.current().nextDouble(-DISPATCH_OFFSET, DISPATCH_OFFSET);
        double offsetY = ThreadLocalRandom.current().nextDouble(-DISPATCH_OFFSET, DISPATCH_OFFSET);

        // upewniamy się, że nie wyjedzie zbyt daleko poza JRG
        double startLat = home.getComponents()[0] + offsetX;
        double startLon = home.getComponents()[1] + offsetY;

        // ustawienie Car.startPosition następuje w Car.dispatch
        car.setCurrentPosition(new Vector2D(startLat, startLon));

        car.setState(new BusyGoingState(isFalseAlarm, true)); // true = jedzie na zdarzenie
    }
}