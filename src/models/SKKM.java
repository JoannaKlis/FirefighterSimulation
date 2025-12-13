package models;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.IDispatchStrategy;
import implementation.ClosestJRGIterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import strategy.MZStrategy;
import strategy.PZStrategy;

public class SKKM {
    private final List<JRG> jrgs;
    private Vector2D lastIncidentPosition = null; // do wizualizacji miejsca ostatniego zdarzenia

    public SKKM(List<JRG> jrgs) {
        this.jrgs = jrgs;
    }

    // Tworzy losowe zgłoszenie w obszarze zdarzeń
    public Incident receiveCall() {
        double lat = ThreadLocalRandom.current().nextDouble(AreaConstants.INCIDENT_MIN_LATITUDE, AreaConstants.INCIDENT_MAX_LATITUDE);
        double lon = ThreadLocalRandom.current().nextDouble(AreaConstants.INCIDENT_MIN_LONGITUDE, AreaConstants.INCIDENT_MAX_LONGITUDE);
        Vector2D position = new Vector2D(lat, lon);

        this.lastIncidentPosition = position;

        double rand = ThreadLocalRandom.current().nextDouble();
        IncidentType type;

        // losowanie charakteru zdarzenia (PZ vs MZ, 30% vs 70%)
        if (rand < SimulationConstants.PZ_PROBABILITY) {
            type = IncidentType.PZ;
        } else {
            type = IncidentType.MZ;
        }

        // losowanie, czy zdarzenie okaże się Alarmem Fałszywym (AF, 5%)
        boolean isAF = ThreadLocalRandom.current().nextDouble() < SimulationConstants.AF_PROBABILITY;

        if (isAF) {
            System.out.println("--- Nowe zgłoszenie: " + type + " -> Alarm Fałszywy (AF, 5%) ---");
            return new Incident(IncidentType.AF, position);
        }

        System.out.printf("--- Nowe zgłoszenie: %s (%.5f, %.5f) ---\n", type, lat, lon);
        return new Incident(type, position);
    }

    // Wzorzec Strategia: Wybór i wykonanie dysponowania (Warunek 7)
    public void handleIncident(Incident incident) {

        IDispatchStrategy strategy;

        if (incident.getType() == IncidentType.PZ) {
            strategy = new PZStrategy();
        } else { // Obejmuje MZ i AF
            strategy = new MZStrategy();
        }

        strategy.executeDispatch(incident, this.jrgs);
    }

    // Tworzenie iteratora (Wzorzec Iterator)
    public ClosestJRGIterator createClosestIterator(Incident incident) {
        return new ClosestJRGIterator(jrgs, incident.getPosition());
    }

    public Vector2D getLastIncidentPosition() {
        return lastIncidentPosition;
    }
}