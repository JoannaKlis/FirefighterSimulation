package implementation;

import interfaces.IVector;

public class Vector2D implements IVector {
    protected double x; // Używane jako Latitude (szerokość)
    protected double y; // Używane jako Longitude (długość)

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double abs() {
        // Obliczanie długości wektora (niezbyt sensowne dla Lat/Lon, ale wymagane przez IVector)
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public double cdot(IVector param) {
        double[] c = param.getComponents();
        if (c.length < 2) {
            throw new IllegalArgumentException("Wektor do iloczynu skalarnego musi mieć co najmniej 2 wymiary.");
        }
        // iloczyn skalarny dla 2D: x1*x2 + y1*y2
        return this.x * c[0] + this.y * c[1];
    }

    @Override
    public double[] getComponents() {
        return new double[]{x, y};
    }
}