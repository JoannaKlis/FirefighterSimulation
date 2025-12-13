package models;

import implementation.Vector2D;
import interfaces.ICarState;
import states.FreeState;

public class Car {
    private final String id;
    private final Vector2D homePosition; // Pozycja JRG
    private ICarState currentState;
    private Vector2D currentPosition;
    private Vector2D targetPosition;

    public Car(String id, Vector2D homePosition) {
        this.id = id;
        this.homePosition = homePosition;
        this.currentPosition = homePosition;
        this.currentState = new FreeState();
    }

    // Delegacja do stanu (Warunek 8)
    public void update() { currentState.update(this); }
    public void dispatch(Vector2D destination, boolean isFalseAlarm) {
        currentState.dispatch(this, destination, isFalseAlarm);
    }

    // Zmiana stanu (dostępna tylko dla obiektów stanu)
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
}