package models;

import implementation.Vector2D;
import constants.SimulationConstants;

public class Incident {
    private final IncidentType type;
    private final Vector2D position; // Pozycja zdarzenia (Lat/Lon)

    public Incident(IncidentType type, Vector2D position) {
        this.type = type;
        this.position = position;
    }

    public IncidentType getType() {
        return type;
    }

    public Vector2D getPosition() {
        return position;
    }

    // Określa wymaganą liczbę samochodów na podstawie charakteru zdarzenia (Warunek 7)
    public int getRequiredCars() {
        if (type == IncidentType.PZ) {
            return SimulationConstants.PZ_CAR_COUNT;
        }
        // W przypadku MZ i AF (który jest dysponowany jako MZ)
        return SimulationConstants.MZ_CAR_COUNT;
    }
}