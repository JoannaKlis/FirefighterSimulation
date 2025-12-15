package interfaces;

import models.Incident;

public interface IObserver {
    void onIncidentReported(Incident incident);
    void onIncidentCleared();
}