package implementation;

import interfaces.IVector;

public class Vector2D implements IVector {
    protected double x; // szerokość geograficzna (Latitude)
    protected double y; // długość geograficzna (Longitude)

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // formuła Haversine'a do obliczenia odległości w kilometrach
    public double distanceKm(Vector2D p2) {
        final int R = 6371; // promień Ziemi w kilometrach

        double lat1 = this.x;
        double lon1 = this.y;
        double lat2 = p2.x;
        double lon2 = p2.y;

        // konwersja stopni na radiany
        double latRad1 = Math.toRadians(lat1);
        double latRad2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // formuła Haversine'a
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(latRad1) * Math.cos(latRad2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // odległość w kilometrach
    }

    @Override
    public double[] getComponents() {
        return new double[]{x, y};
    }
}