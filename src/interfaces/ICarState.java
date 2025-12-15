package interfaces;
import implementation.Vector2D;
import models.Car;
import models.CarStatus;

// Kontrakt dla stanów samochodów
public interface ICarState {
    CarStatus getStatus();
    void update(Car car);
    // ZMIANA: Metoda dispatch musi być w interfejsie ICarState
    void dispatch(Car car, Vector2D destination, boolean isFalseAlarm, int responseSteps); // Zaczyna wyjazd
}