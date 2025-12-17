package models;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.IDispatchStrategy;
import interfaces.IObserver;
import interfaces.ISubject;
import strategy.MZStrategy;
import strategy.PZStrategy;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SKKM implements ISubject {
    private final List<JRG> jrgs;
    private final List<IncidentAction> activeActions = new ArrayList<>();
    private final Queue<Incident> waitingIncidents = new LinkedList<>();
    private final List<IObserver> observers = new ArrayList<>();

    public SKKM(List<JRG> jrgs) {
        this.jrgs = jrgs;
    }

    // dodanie zgłoszenia do kolejki
    public void receiveCall() {
        double lat = ThreadLocalRandom.current().nextDouble(AreaConstants.INCIDENT_MIN_LATITUDE, AreaConstants.INCIDENT_MAX_LATITUDE);
        double lon = ThreadLocalRandom.current().nextDouble(AreaConstants.INCIDENT_MIN_LONGITUDE, AreaConstants.INCIDENT_MAX_LONGITUDE);

        IncidentType reportedType = ThreadLocalRandom.current().nextDouble() < SimulationConstants.PZ_PROBABILITY ? IncidentType.PZ : IncidentType.MZ;
        boolean isFalseAlarm = ThreadLocalRandom.current().nextDouble() < SimulationConstants.AF_PROBABILITY;

        Incident incident = new Incident(isFalseAlarm ? IncidentType.AF : reportedType, new Vector2D(lat, lon));
        incident.setVisualizedType(reportedType);

        waitingIncidents.add(incident);
        notifyIncidentReported(incident);
    }

    public void update() {
        // obsługa zdarzenia z kolejki
        Iterator<Incident> queueIt = waitingIncidents.iterator();
        while (queueIt.hasNext()) {
            Incident incident = queueIt.next();
            if (tryDispatch(incident)) {
                queueIt.remove();
            }
        }

        // aktualizacja trwającej akcji
        Iterator<IncidentAction> actionIt = activeActions.iterator();
        while (actionIt.hasNext()) {
            IncidentAction action = actionIt.next();
            action.update();
            if (action.isDone()) {
                actionIt.remove();
                notifyIncidentCleared();
            }
        }
    }

    private boolean tryDispatch(Incident incident) {
        IDispatchStrategy strategy = (incident.getVisualizedType() == IncidentType.PZ) ? new PZStrategy() : new MZStrategy();
        List<Car> dispatchedCars = strategy.selectCars(incident, jrgs);

        if (!dispatchedCars.isEmpty()) {
            IncidentAction action = new IncidentAction(incident, dispatchedCars, incident.getType() == IncidentType.AF);
            action.startGoing(SimulationConstants.getRandomResponseSteps());
            activeActions.add(action);
            return true;
        }
        return false; // brak wolnych samochodów, zostaje w kolejce
    }

    // obserwator i gettery dostosowane do list
    public List<IncidentAction> getActiveActions() { return activeActions; }
    public Queue<Incident> getWaitingIncidents() { return waitingIncidents; }

    @Override public void addObserver(IObserver o) { observers.add(o); }
    private void notifyIncidentReported(Incident i) { observers.forEach(o -> o.onIncidentReported(i)); }
    @Override public void notifyIncidentCleared() { observers.forEach(IObserver::onIncidentCleared); }
}