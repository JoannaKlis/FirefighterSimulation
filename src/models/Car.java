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
        this.id = id;
        this.homePosition = homePosition;
        this.currentPosition = homePosition;
        this.currentState = new FreeState();
        this.startPosition = homePosition; // początkowo jest w jednostce
        this.isActualFalseAlarm = false; // domyślnie
    }

    public void update() { currentState.update(this); }

    // wywołanie do stanu
    public void dispatch(Vector2D destination, boolean isFalseAlarm, int responseSteps) {
        // ustawienie informacji o celu i AF
        this.setTargetPosition(destination);
        this.isActualFalseAlarm = isFalseAlarm;

        // aktualna pozycja jako punkt startowy
        this.startPosition = this.currentPosition;

        // tylko FreeState faktycznie przełączy się na BusyGoingState
        currentState.dispatch(this, destination, isFalseAlarm, responseSteps);
    }

    // ruchu powrotny samochodów
    public void initiateReturn(int responseSteps) {
        this.startPosition = this.currentPosition;
        this.setTargetPosition(this.homePosition);
        // przejście bezpośrednio do stanu powrotu (false = nie jedzie na zdarzenie)
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