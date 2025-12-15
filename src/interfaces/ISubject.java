package interfaces;

public interface ISubject {
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyIncidentReported();
    void notifyIncidentCleared();
}