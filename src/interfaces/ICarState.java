package interfaces;
import implementation.Vector2D;
import models.Car;
import models.CarStatus;

// Kontrakt dla stanów samochodów
public interface ICarState {
    CarStatus getStatus();
    void update(Car car);
    void dispatch(Car car, Vector2D destination, boolean isFalseAlarm); // Zaczyna wyjazd
}