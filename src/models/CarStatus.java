package models;

import java.awt.Color;

public enum CarStatus {
    FREE(Color.GREEN), // Wolny (Warunek 8, 11)
    BUSY_GOING(Color.ORANGE), // W drodze na miejsce/powrót (Warunek 8)
    BUSY_ACTION(Color.RED); // W trakcie działań (Warunek 8)

    private final Color color;

    CarStatus(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}