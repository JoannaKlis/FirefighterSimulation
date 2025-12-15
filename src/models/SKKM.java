package models;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.IDispatchStrategy;
import strategy.MZStrategy;
import strategy.PZStrategy;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SKKM {

    private final List<JRG> jrgs;

    private Incident lastReportedIncident;
    private IncidentType lastVisualizedIncidentType;

    private IncidentAction activeAction;

    public SKKM(List<JRG> jrgs) {
        this.jrgs = jrgs;
    }

    // =======================
    // ODBIÓR ZGŁOSZENIA
    // =======================
    public Incident receiveCall() {

        double lat = ThreadLocalRandom.current().nextDouble(
                AreaConstants.INCIDENT_MIN_LATITUDE,
                AreaConstants.INCIDENT_MAX_LATITUDE
        );
        double lon = ThreadLocalRandom.current().nextDouble(
                AreaConstants.INCIDENT_MIN_LONGITUDE,
                AreaConstants.INCIDENT_MAX_LONGITUDE
        );

        Vector2D pos = new Vector2D(lat, lon);

        IncidentType reportedType =
                ThreadLocalRandom.current().nextDouble() < SimulationConstants.PZ_PROBABILITY
                        ? IncidentType.PZ
                        : IncidentType.MZ;

        boolean isFalseAlarm =
                ThreadLocalRandom.current().nextDouble() < SimulationConstants.AF_PROBABILITY;

        Incident incident = new Incident(
                isFalseAlarm ? IncidentType.AF : reportedType,
                pos
        );

        this.lastReportedIncident = incident;
        this.lastVisualizedIncidentType = reportedType;

        return incident;
    }

    // =======================
    // DYSPONOWANIE
    // =======================
    public void handleIncident(Incident incident) {

        IDispatchStrategy strategy =
                lastVisualizedIncidentType == IncidentType.PZ
                        ? new PZStrategy()
                        : new MZStrategy();

        boolean isFalseAlarm = incident.getType() == IncidentType.AF;

        List<Car> dispatchedCars =
                strategy.selectCars(incident, jrgs);

        if (dispatchedCars.isEmpty()) return;

        int responseSteps = SimulationConstants.getRandomResponseSteps();

        activeAction = new IncidentAction(
                incident,
                dispatchedCars,
                isFalseAlarm
        );

        activeAction.startGoing(responseSteps);
    }

    // =======================
    // AKTUALIZACJA AKCJI
    // =======================
    public void update() {
        if (activeAction == null) return;

        activeAction.update();

        if (activeAction.isDone()) {
            activeAction = null;
            lastReportedIncident = null;
            lastVisualizedIncidentType = null;
        }
    }

    // =======================
    // GETTERY
    // =======================
    public boolean hasActiveAction() {
        return activeAction != null;
    }

    public Incident getLastReportedIncident() {
        return lastReportedIncident;
    }

    public IncidentType getLastVisualizedIncidentType() {
        return lastVisualizedIncidentType;
    }
}