package simulation;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import models.Car;
import models.Incident;
import models.JRG;
import models.SKKM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SimulationPanel extends JPanel implements ActionListener {
    private final SKKM skkm;
    private final List<JRG> jrgs;
    private int stepCounter;
    private final Timer timer;

    public SimulationPanel() {
        this.stepCounter = 0;
        this.jrgs = initializeJRG();
        this.skkm = new SKKM(this.jrgs);

        // GUI setup
        this.setLayout(new BorderLayout());
        this.setBackground(Color.DARK_GRAY);

        // Ustawienie preferowanego rozmiaru na podstawie granic WIZUALIZACJI
        double latRange = AreaConstants.VIS_MAX_LATITUDE - AreaConstants.VIS_MIN_LATITUDE;
        double lonRange = AreaConstants.VIS_MAX_LONGITUDE - AreaConstants.VIS_MIN_LONGITUDE;
        int widthPixels = (int) (lonRange * AreaConstants.PIXELS_PER_DEGREE);
        int heightPixels = (int) (latRange * AreaConstants.PIXELS_PER_DEGREE);

        this.setPreferredSize(new Dimension(widthPixels, heightPixels));

        timer = new Timer(SimulationConstants.SIMULATION_DELAY_MS, this);
        timer.start();
    }

    private List<JRG> initializeJRG() {
        List<JRG> list = new ArrayList<>();

        // 7 JRG podlegających SKKM (Warunek 5, 7)
        list.add(new JRG("JRG-1", new Vector2D(50.0599424, 19.9432412)));
        list.add(new JRG("JRG-2", new Vector2D(50.0335189, 19.9358363)));
        list.add(new JRG("JRG-3", new Vector2D(50.0755229, 19.8875822)));
        list.add(new JRG("JRG-4", new Vector2D(50.0375876, 20.0057190)));
        list.add(new JRG("JRG-5", new Vector2D(50.0921839, 19.9217896)));
        list.add(new JRG("JRG-6", new Vector2D(50.0159353, 20.0156738)));
        list.add(new JRG("JRG-7", new Vector2D(50.0941205, 19.9773860)));

        // Dodatkowe jednostki (wizualizacja)
        list.add(new JRG("JRG-SA", new Vector2D(50.0769598, 20.0338662)));
        list.add(new JRG("JRG-SKA", new Vector2D(49.9721807, 19.7960337)));
        list.add(new JRG("LSP-BAL", new Vector2D(50.0782553, 19.7862538)));

        return list;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateSimulation();
        repaint();
    }

    private void updateSimulation() {
        stepCounter++;

        // 1. Odbieranie i obsługa zgłoszeń
        if (stepCounter % SimulationConstants.CALL_INTERVAL_STEPS == 0) {
            Incident incident = skkm.receiveCall();
            skkm.handleIncident(incident);
        }

        // 2. Aktualizacja stanu samochodów (Wzorzec Stan)
        for (JRG jrg : jrgs) {
            jrg.updateCars();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Zakres wizualizacji
        double latRange = AreaConstants.VIS_MAX_LATITUDE - AreaConstants.VIS_MIN_LATITUDE;
        double lonRange = AreaConstants.VIS_MAX_LONGITUDE - AreaConstants.VIS_MIN_LONGITUDE;

        // Rysowanie JRG i Car
        for (JRG jrg : jrgs) {
            Vector2D jrgPos = jrg.getPosition();

            // Konwersja z Lat/Lon na piksele (Y jest odwrócone)
            int jrgX = (int) ((jrgPos.getComponents()[1] - AreaConstants.VIS_MIN_LONGITUDE) * panelWidth / lonRange);
            int jrgY = (int) ((AreaConstants.VIS_MAX_LATITUDE - jrgPos.getComponents()[0]) * panelHeight / latRange);

            // Rysowanie bazy JRG
            g2d.setColor(Color.WHITE.darker());
            g2d.fillOval(jrgX - 5, jrgY - 5, 10, 10);
            g2d.drawString(jrg.getName(), jrgX + 12, jrgY + 5);

            // Rysowanie samochodów
            for (Car car : jrg.getAllCars()) {
                Vector2D pos = car.getCurrentPosition();

                int xPixel = (int) ((pos.getComponents()[1] - AreaConstants.VIS_MIN_LONGITUDE) * panelWidth / lonRange);
                int yPixel = (int) ((AreaConstants.VIS_MAX_LATITUDE - pos.getComponents()[0]) * panelHeight / latRange);

                int diameter = 6;
                int radius = diameter / 2;

                g2d.setColor(car.getStatus().getColor());
                g2d.fillOval(xPixel - radius, yPixel - radius, diameter, diameter);
            }
        }

        // drawStatus(g2d);
    }
}