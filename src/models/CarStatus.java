package models;

import java.awt.Color;

public enum CarStatus {
    FREE(Color.GREEN), // Wolny
    BUSY_GOING(Color.ORANGE), // W drodze na miejsce/powrót
    BUSY_ACTION(Color.RED); // W trakcie działań

    private final Color color;

    CarStatus(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}