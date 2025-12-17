package interfaces;

public interface ISubject {
    void addObserver(IObserver observer);
    void notifyIncidentCleared();
}