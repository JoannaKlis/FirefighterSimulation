package states;

import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;

// Stan: Samochód Zajęty (W trakcie działań na miejscu zdarzenia)
public class BusyActionState implements ICarState {
    private int remainingSteps;

    // Konstruktor dla Czasu Działania (5-25s w krokach)
    public BusyActionState() {
        this.remainingSteps = SimulationConstants.getRandomActionSteps();
    }

    @Override
    public CarStatus getStatus() {
        return CarStatus.BUSY_ACTION;
    }

    @Override
    public void update(Car car) {
        remainingSteps--;

        if (remainingSteps <= 0) {
            // Koniec akcji, samochód zaczyna wracać do jednostki
            car.setTargetPosition(car.getHomePosition());
            car.setState(new BusyGoingState(false, false)); // false = powrót
            System.out.printf("[%s] Koniec akcji. Zaczyna powrót (BUSY_GOING).\n", car.getId());
        }
    }

    @Override
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm) {
        // Zajęty, nie można dysponować
    }
}