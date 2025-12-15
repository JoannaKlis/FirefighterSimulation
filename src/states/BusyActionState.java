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
            // koniec akcji, samochód zaczyna wracać do jednostki (krok 6)

            // ZMIANA: Losujemy czas powrotu (0-3s)
            int returnSteps = SimulationConstants.getRandomResponseSteps();

            // ZMIANA: Inicjacja powrotu
            car.initiateReturn(returnSteps);
        }
    }

    @Override
    // ZMIANA: Dostosowanie sygnatury do ICarState
    public void dispatch(Car car, Vector2D destination, boolean isFalseAlarm, int responseSteps) {
        // zajęty, nie można dysponować
    }
}