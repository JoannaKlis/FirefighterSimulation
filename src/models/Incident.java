package models;

import implementation.Vector2D;
import constants.SimulationConstants;

public class Incident {
    private final IncidentType type; // Rzeczywisty (PZ, MZ, AF)
    private IncidentType visualizedType; // Zgłoszony (PZ, MZ)
    private final Vector2D position;

    public Incident(IncidentType type, Vector2D position) {
        this.type = type;
        this.position = position;
    }

    // Ten getter jest potrzebny dla SimulationPanel
    public IncidentType getType() {
        return type;
    }

    public IncidentType getVisualizedType() {
        return visualizedType;
    }

    public void setVisualizedType(IncidentType visualizedType) {
        this.visualizedType = visualizedType;
    }

    public Vector2D getPosition() {
        return position;
    }

    public int getRequiredCars() {
        if (type == IncidentType.PZ) return SimulationConstants.PZ_CAR_COUNT;
        return SimulationConstants.MZ_CAR_COUNT;
    }
}