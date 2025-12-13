package models;

import implementation.Vector2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JRG {
    private final String name;
    private final Vector2D position; // pozycja stacjonowania JRG
    private final List<Car> cars;

    public JRG(String name, Vector2D position) {
        this.name = name;
        this.position = position;
        this.cars = new ArrayList<>();
        // 5 samochodów na jednostkę
        for (int i = 1; i <= 5; i++) {
            cars.add(new Car(name + "-" + i, position));
        }
    }

    // pobiera wymaganą liczbę wolnych samochodów (Warunek 9)
    public List<Car> getFreeCars(int requiredCount) {
        return cars.stream()
                .filter(car -> car.getStatus() == CarStatus.FREE)
                .limit(requiredCount)
                .collect(Collectors.toList());
    }

    public List<Car> getAllCars() { return cars; }
    public Vector2D getPosition() { return position; }
    public String getName() { return name; }

    // aktualizacja stanu wszystkich samochodów w jednostce
    public void updateCars() {
        for (Car car : cars) {
            car.update();
        }
    }
}