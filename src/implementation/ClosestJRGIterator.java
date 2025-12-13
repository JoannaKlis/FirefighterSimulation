package implementation;

import interfaces.IJRGIterator;
import models.JRG;
import implementation.Vector2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

// Konkretny Iterator, który sortuje jednostki według odległości Haversine'a (Warunek 3, 11)
public class ClosestJRGIterator implements IJRGIterator {
    private final List<JRG> sortedJrgs;
    private int position = 0;
    private double lastDistance = -1; // Przechowuje dystans dla ostatnio zwróconej JRG

    public ClosestJRGIterator(List<JRG> jrgs, Vector2D incidentLocation) {
        this.sortedJrgs = new ArrayList<>(jrgs);

        // Sortowanie JRG po odległości Haversine'a
        Collections.sort(this.sortedJrgs, Comparator.comparingDouble(jrg ->
                calculateHaversineDistance(jrg.getPosition(), incidentLocation)));
    }

    // Użycie formuły Haversine'a do obliczenia odległości w kilometrach
    public static double calculateHaversineDistance(Vector2D p1, Vector2D p2) {
        final int R = 6371; // Promień Ziemi w kilometrach

        // szerokość (lat) na składowej X, długość (lon) na składowej Y
        double lat1 = p1.getComponents()[0];
        double lon1 = p1.getComponents()[1];
        double lat2 = p2.getComponents()[0];
        double lon2 = p2.getComponents()[1];

        // konwersja stopni na radiany
        double latRad1 = Math.toRadians(lat1);
        double latRad2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // Formuła Haversine'a
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(latRad1) * Math.cos(latRad2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // odległość w kilometrach
    }

    @Override
    public boolean hasNext() {
        return position < sortedJrgs.size();
    }

    @Override
    public JRG next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Brak kolejnych jednostek JRG.");
        }
        JRG nextJRG = sortedJrgs.get(position++);
        // aktualizacja ostatniej odległości
        this.lastDistance = calculateHaversineDistance(nextJRG.getPosition(), sortedJrgs.get(0).getPosition());
        return nextJRG;
    }

    // metoda do pobrania dystansu dla ostatnio zwróconej JRG
    public double getDistanceToLastJRG() {
        return lastDistance;
    }
}