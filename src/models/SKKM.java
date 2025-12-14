package models;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.IDispatchStrategy;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import strategy.MZStrategy;
import strategy.PZStrategy;

public class SKKM {
    private final List<JRG> jrgs;
    private Incident lastReportedIncident = null;
    private IncidentType lastVisualizedIncidentType = null;

    // pole do śledzenia statusu zdarzenia
    private boolean incidentResolved = true;

    public SKKM(List<JRG> jrgs) {
        this.jrgs = jrgs;
    }

    // losowe zgłoszenie w obszarze zdarzeń
    public Incident receiveCall() {
        this.incidentResolved = false;

        double lat = ThreadLocalRandom.current().nextDouble(AreaConstants.INCIDENT_MIN_LATITUDE, AreaConstants.INCIDENT_MAX_LATITUDE);
        double lon = ThreadLocalRandom.current().nextDouble(AreaConstants.INCIDENT_MIN_LONGITUDE, AreaConstants.INCIDENT_MAX_LONGITUDE);
        Vector2D position = new Vector2D(lat, lon);

        IncidentType reportedType;
        double rand = ThreadLocalRandom.current().nextDouble();

        // logika PZ (30%) lub MZ (70%)
        if (rand < SimulationConstants.PZ_PROBABILITY) {
            reportedType = IncidentType.PZ;
        } else {
            reportedType = IncidentType.MZ;
        }

        this.lastVisualizedIncidentType = reportedType;

        // niezależne losowanie Fałszywego Alarmu (AF, 5%)
        boolean isAF = ThreadLocalRandom.current().nextDouble() < SimulationConstants.AF_PROBABILITY;

        Incident newIncident;
        if (isAF) {
            newIncident = new Incident(IncidentType.AF, position);
            // zmiana koloru na ten w legendzie
        } else {
            newIncident = new Incident(reportedType, position);
            // kolor zgłoszenia zostaje oryginalny
        }

        this.lastReportedIncident = newIncident;
        return newIncident;
    }

    // Strategia: wybór i wykonanie dysponowania
    public void handleIncident(Incident incident) {
        IDispatchStrategy strategy;
        IncidentType typeToDispatch = this.lastVisualizedIncidentType;

        if (typeToDispatch == IncidentType.PZ) {
            strategy = new PZStrategy();
        } else {
            strategy = new MZStrategy();
        }

        // przekazanie do strategii, czy jest to AF, aby samochody miały informację o rzeczywistym statusie zdarzenia
        boolean isFalseAlarm = incident.getType() == IncidentType.AF;

        // wywołanie dispatch ( aby przekazywać isFalseAlarm)
        strategy.executeDispatch(incident, this.jrgs, isFalseAlarm);
    }

    // metoda do ustawiania wizualizacji AF po dotarciu samochodów
    public void visualizeRealStatus() {
        if (this.lastReportedIncident == null) return;

        // tutaj ta zmiana kolorów (SimulationPanel.updateSimulation)
        if (this.lastVisualizedIncidentType != this.lastReportedIncident.getType()) {
            this.lastVisualizedIncidentType = this.lastReportedIncident.getType();
        }
    }

    public void setIncidentResolved(boolean resolved) {
        this.incidentResolved = resolved;
        if (resolved) {
            this.lastReportedIncident = null; // ukrycie zdarzenia
            this.lastVisualizedIncidentType = null;
        }
    }

    public Incident getLastReportedIncident() {
        return lastReportedIncident;
    }

    public IncidentType getLastVisualizedIncidentType() {
        return lastVisualizedIncidentType;
    }

    public boolean isIncidentResolved() {
        return incidentResolved;
    }
}