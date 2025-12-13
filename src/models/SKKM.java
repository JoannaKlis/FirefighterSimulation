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

    public SKKM(List<JRG> jrgs) {
        this.jrgs = jrgs;
    }

    // Tworzy losowe zgłoszenie w obszarze INCIDENT_... (Warunek 6)
    public Incident receiveCall() {
        double lat = ThreadLocalRandom.current().nextDouble(AreaConstants.INCIDENT_MIN_LATITUDE, AreaConstants.INCIDENT_MAX_LATITUDE);
        double lon = ThreadLocalRandom.current().nextDouble(AreaConstants.INCIDENT_MIN_LONGITUDE, AreaConstants.INCIDENT_MAX_LONGITUDE);
        Vector2D position = new Vector2D(lat, lon);

        double rand = ThreadLocalRandom.current().nextDouble();
        IncidentType type;

        // Losowanie charakteru zdarzenia (PZ vs MZ, 30% vs 70%)
        if (rand < SimulationConstants.PZ_PROBABILITY) {
            type = IncidentType.PZ;
        } else {
            type = IncidentType.MZ;
        }

        // Losowanie, czy zdarzenie okaże się Alarmem Fałszywym (AF, 5%)
        boolean isAF = ThreadLocalRandom.current().nextDouble() < SimulationConstants.AF_PROBABILITY;

        // Zgłoszenie: PZ lub MZ. Jeśli AF, nadpisujemy typ.
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

        // Wybór strategii na podstawie typu zgłoszenia
        if (incident.getType() == IncidentType.PZ) {
            strategy = new PZStrategy();
        } else { // Obejmuje MZ i AF (który jest dysponowany jako MZ, Warunek 7)
            strategy = new MZStrategy();
        }

        strategy.executeDispatch(incident, this.jrgs);
    }

    // Tworzenie iteratora (Wzorzec Iterator)
    public ClosestJRGIterator createClosestIterator(Incident incident) {
        return new ClosestJRGIterator(jrgs, incident.getPosition());
    }
}