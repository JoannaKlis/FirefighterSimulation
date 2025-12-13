package simulation;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import models.*;

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
        List<JRG> allJrgs = initializeJRG();
        this.jrgs = allJrgs;
        // SKKM nadzoruje tylko JRG-1 do JRG-7
        this.skkm = new SKKM(getJrgsForSKKM(allJrgs));

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

    // Helper do wyodrębnienia JRG-1 do JRG-7 dla SKKM
    private List<JRG> getJrgsForSKKM(List<JRG> allJrgs) {
        List<JRG> skkmJrgs = new ArrayList<>();
        for (JRG jrg : allJrgs) {
            if (jrg.getName().startsWith("JRG-") && jrg.getName().length() <= 5) {
                skkmJrgs.add(jrg);
            }
        }
        return skkmJrgs;
    }

    private List<JRG> initializeJRG() {
        List<JRG> list = new ArrayList<>();

        // 7 JRG podlegających SKKM
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

        // odbieranie i obsługa zgłoszeń
        if (stepCounter % SimulationConstants.CALL_INTERVAL_STEPS == 0) {
            Incident incident = skkm.receiveCall();
            skkm.handleIncident(incident);
        }

        // aktualizacja stanu samochodów (Wzorzec Stan)
        for (JRG jrg : jrgs) {
            jrg.updateCars();
        }
    }

    // zamiana lat/lon na piksele
    private Point geoToPixel(double lat, double lon, int panelWidth, int panelHeight) {
        double latRange = AreaConstants.VIS_MAX_LATITUDE - AreaConstants.VIS_MIN_LATITUDE;
        double lonRange = AreaConstants.VIS_MAX_LONGITUDE - AreaConstants.VIS_MIN_LONGITUDE;

        int xPixel = (int) ((lon - AreaConstants.VIS_MIN_LONGITUDE) * panelWidth / lonRange);
        int yPixel = (int) ((AreaConstants.VIS_MAX_LATITUDE - lat) * panelHeight / latRange);
        return new Point(xPixel, yPixel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // rysowanie ramki obszaru zdarzeń
        drawIncidentAreaFrame(g2d, panelWidth, panelHeight);

        //rysowanie ostatniego miejsca zdarzenia
        drawLastIncidentLocation(g2d, panelWidth, panelHeight);

        // rysowanie JRG, pojemności i samochodów
        for (JRG jrg : jrgs) {
            drawJRG(g2d, jrg, panelWidth, panelHeight);
        }
    }

    private void drawIncidentAreaFrame(Graphics2D g2d, int panelWidth, int panelHeight) {
        // konwersja narożników INCIDENT_AREA na piksele
        Point nw = geoToPixel(AreaConstants.INCIDENT_MAX_LATITUDE, AreaConstants.INCIDENT_MIN_LONGITUDE, panelWidth, panelHeight);
        Point se = geoToPixel(AreaConstants.INCIDENT_MIN_LATITUDE, AreaConstants.INCIDENT_MAX_LONGITUDE, panelWidth, panelHeight);

        int width = se.x - nw.x;
        int height = se.y - nw.y;

        g2d.setColor(new Color(255, 165, 0, 80)); // Półprzezroczysty pomarańczowy dla tła
        g2d.fillRect(nw.x, nw.y, width, height);

        g2d.setColor(Color.YELLOW); // Żółta ramka
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(nw.x, nw.y, width, height);
        g2d.setStroke(new BasicStroke(1));
    }

    private void drawLastIncidentLocation(Graphics2D g2d, int panelWidth, int panelHeight) {
        Vector2D pos = skkm.getLastIncidentPosition();
        if (pos != null) {
            Point p = geoToPixel(pos.getComponents()[0], pos.getComponents()[1], panelWidth, panelHeight);

            g2d.setColor(Color.RED);
            g2d.fillOval(p.x - 5, p.y - 5, 10, 10);

            // Rysowanie 'X' na punkcie
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(p.x - 5, p.y - 5, p.x + 5, p.y + 5);
            g2d.drawLine(p.x - 5, p.y + 5, p.x + 5, p.y - 5);
            g2d.setStroke(new BasicStroke(1));
        }
    }

    private void drawJRG(Graphics2D g2d, JRG jrg, int panelWidth, int panelHeight) {
        Vector2D jrgPos = jrg.getPosition();
        Point p = geoToPixel(jrgPos.getComponents()[0], jrgPos.getComponents()[1], panelWidth, panelHeight);

        final int BAR_WIDTH = 50;
        final int BAR_HEIGHT = 10;
        final int TOTAL_CARS = 5;
        int freeCars = jrg.getFreeCars(TOTAL_CARS).size();

        int barX = p.x - BAR_WIDTH / 2;
        int barY = p.y - BAR_HEIGHT - 5;

        // Rysowanie Nazwy JRG
        g2d.setColor(Color.WHITE);
        g2d.drawString(jrg.getName(), barX, barY - 5);

        // rysowanie pojemności jednostki
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(barX, barY, BAR_WIDTH, BAR_HEIGHT);

        // wypełnienie (wolne samochody - zielony)
        int filledWidth = (BAR_WIDTH / TOTAL_CARS) * freeCars;
        g2d.setColor(CarStatus.FREE.getColor().darker());
        g2d.fillRect(barX, barY, filledWidth, BAR_HEIGHT);

        // linia podziału (5 części)
        g2d.setColor(Color.BLACK);
        g2d.drawRect(barX, barY, BAR_WIDTH, BAR_HEIGHT);
        for (int i = 1; i < TOTAL_CARS; i++) {
            int lineX = barX + (BAR_WIDTH / TOTAL_CARS) * i;
            g2d.drawLine(lineX, barY, lineX, barY + BAR_HEIGHT);
        }

        // rysowanie samochodów
        for (Car car : jrg.getAllCars()) {
            // Rysujemy tylko, jeśli samochód jest poza bazą (nie jest FREE)
            if (car.getStatus() != CarStatus.FREE) {
                Vector2D pos = car.getCurrentPosition();
                Point carP = geoToPixel(pos.getComponents()[0], pos.getComponents()[1], panelWidth, panelHeight);

                int diameter = 6;
                int radius = diameter / 2;

                g2d.setColor(car.getStatus().getColor()); // Kolor (Orange/Red) zależy od Statusu
                g2d.fillOval(carP.x - radius, carP.y - radius, diameter, diameter);
            }
        }
    }
}