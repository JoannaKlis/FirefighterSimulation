package states;

import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.ICarState;
import models.Car;
import models.CarStatus;

// Stan: samochód zajęty (w trakcie działań na miejscu zdarzenia)
public class BusyActionState implements ICarState {
    private int remainingSteps;

    // konstruktor dla czasu działania (5-25s w krokach)
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
            // koniec akcji, samochód zaczyna wracać do jednostki
            car.setTargetPosition(car.getHomePosition());

            // zmiana stanu na powrót (dispatch wywoła nowy BusyGoingState)
            car.dispatch(car.getHomePosition(), false);
        }
    }

    @Override
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm) {
        // zajęty, nie można dysponować
    }
}