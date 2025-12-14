package implementation;

import interfaces.IJRGIterator;
import models.JRG;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// konkretny iterator, który sortuje jednostki według odległości Haversine'a
public class ClosestJRGIterator implements IJRGIterator {
    private final List<JRG> sortedJrgs;
    private final Vector2D incidentLocation; // dodanie, aby móc pobrać odległość
    private int position = 0;
    private double lastDistance = -1; // przechowuje dystans dla ostatnio zwróconej JRG

    public ClosestJRGIterator(List<JRG> jrgs, Vector2D incidentLocation) {
        this.incidentLocation = incidentLocation;
        this.sortedJrgs = new ArrayList<>(jrgs);

        // sortowanie JRG po odległości Haversine'a
        Collections.sort(this.sortedJrgs, Comparator.comparingDouble(jrg ->
                jrg.getPosition().distanceKm(incidentLocation)));
    }

    @Override
    public boolean hasNext() {
        return position < sortedJrgs.size();
    }

    @Override
    public JRG next() {
        JRG nextJRG = sortedJrgs.get(position++);
        // aktualizacja ostatniej odległości
        this.lastDistance = nextJRG.getPosition().distanceKm(this.incidentLocation);
        return nextJRG;
    }
}