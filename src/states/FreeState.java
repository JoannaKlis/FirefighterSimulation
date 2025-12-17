package states;

import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;

public class FreeState implements ICarState {

    private static final double DISPATCH_OFFSET = 0.0003;

    @Override
    public CarStatus getStatus() {
        return CarStatus.FREE;
    }

    @Override
    public void update(Car car) {
        // samochód jest w jednostce, nic się nie dzieje.
    }

    @Override
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm, int responseSteps) {
        // tylko w stanie FREE możemy zmienić stan
        Vector2D home = car.getHomePosition();
        String carId = car.getId();
        int carIndex = Integer.parseInt(carId.substring(carId.lastIndexOf('-') + 1));

        // Rozmieszczenie samochodów w prostym wzorze wokół JRG
        double offsetX = 0;
        double offsetY = 0;

        double stepOffset = DISPATCH_OFFSET * (carIndex % 5 == 0 ? 0.5 : 1.0);

        switch (carIndex % 5) {
            case 1:
                offsetX = stepOffset;
                offsetY = 0;
                break;
            case 2:
                offsetX = 0;
                offsetY = stepOffset;
                break;
            case 3:
                offsetX = -stepOffset;
                offsetY = 0;
                break;
            case 4:
                offsetX = 0;
                offsetY = -stepOffset;
                break;
            case 0: // 5
                offsetX = stepOffset;
                offsetY = stepOffset;
                break;
        }

        double startLat = home.getComponents()[0] + offsetX;
        double startLon = home.getComponents()[1] + offsetY;

        // ustawienie bieżącej pozycji na lekko przesuniętą pozycję startową
        car.setCurrentPosition(new Vector2D(startLat, startLon));

        // zmiana stanu
        car.setState(new BusyGoingState(responseSteps, true)); // true = jedzie na zdarzenie
    }
}