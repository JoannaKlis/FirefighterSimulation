package models;

import implementation.Vector2D;
import interfaces.ICarState;
import states.FreeState;

public class Car {
    private final String id;
    private final Vector2D homePosition; // pozycja JRG
    private ICarState currentState;
    private Vector2D currentPosition;
    private Vector2D targetPosition;

    // przechowuje pozycję, z której samochód rozpoczął ostatni ruch
    private Vector2D startPosition;

    // określa, czy to jest fałszywy alarm
    private boolean isActualFalseAlarm;

    public Car(String id, Vector2D homePosition) {
// ... (pozostała część konstruktora bez zmian)
        this.id = id;
        this.homePosition = homePosition;
        this.currentPosition = homePosition;
        this.currentState = new FreeState();
        this.startPosition = homePosition; // początkowo jest w jednostce
        this.isActualFalseAlarm = false; // domyślnie
    }

    public void update() { currentState.update(this); }

    // ZMIANA: Przywrócenie metody dispatch, która deleguje wywołanie do stanu
    public void dispatch(Vector2D destination, boolean isFalseAlarm, int responseSteps) {
        // Ustawienie informacji o celu i FA
        this.setTargetPosition(destination);
        this.isActualFalseAlarm = isFalseAlarm;

        // Ustawienie aktualnej pozycji jako punktu startowego
        // (to jest kluczowe dla interpolacji w BusyGoingState,
        // a FreeState nadpisze ją na pozycję wyjazdową)
        this.startPosition = this.currentPosition;

        // Delegowanie wywołania do aktualnego stanu.
        // Tylko FreeState faktycznie przełączy się na BusyGoingState.
        currentState.dispatch(this, destination, isFalseAlarm, responseSteps);
    }

    // ZMIANA: Nowa, uproszczona metoda, która obsługuje inicjację ruchu powrotnego
    public void initiateReturn(int responseSteps) {
        this.startPosition = this.currentPosition;
        this.setTargetPosition(this.homePosition);
        // Przechodzimy bezpośrednio do stanu powrotu (false = nie jedzie na zdarzenie)
        this.setState(new states.BusyGoingState(responseSteps, false));
    }

    // zmiana stanu
    public void setState(ICarState newState) {
        this.currentState = newState;
    }

    public CarStatus getStatus() { return currentState.getStatus(); }
    public Vector2D getHomePosition() { return homePosition; }
    public Vector2D getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Vector2D currentPosition) { this.currentPosition = currentPosition; }
    public Vector2D getTargetPosition() { return targetPosition; }
    public void setTargetPosition(Vector2D targetPosition) { this.targetPosition = targetPosition; }
    public String getId() { return id; }

    // getter dla nowej pozycji startowej
    public Vector2D getStartPosition() { return startPosition; }

    // getter dla statusu FA
    public boolean isActualFalseAlarm() { return isActualFalseAlarm; }

    // używane do resetowania statusu fałszywego alarmu po powrocie do JRG
    public void resetIncidentStatus() { this.isActualFalseAlarm = false; }
}