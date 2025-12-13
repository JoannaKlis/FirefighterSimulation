package interfaces;
import models.Incident;

// Repozytorium JRG, odpowiedzialne za tworzenie iteratorów
public interface IJRGRepository {
    // Fabryka tworząca konkretny iterator (posortowany po odległości)
    IJRGIterator createClosestIterator(Incident incident);
}