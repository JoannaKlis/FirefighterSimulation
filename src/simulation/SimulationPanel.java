package simulation;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.IObserver;
import models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SimulationPanel extends JPanel
        implements ActionListener, IObserver {

    private final SKKM skkm;
    private final List<JRG> jrgs;

    private int stepCounter;
    private final Timer timer;

    // legenda
    private static final int LEGEND_X = 10;
    private static final int LEGEND_Y_START = 40;
    private static final int LEGEND_LINE = 14;

    public SimulationPanel() {

        stepCounter = 0;
        jrgs = initializeJRG();

        skkm = new SKKM(jrgs);
        skkm.addObserver(this); // obserwator

        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());

        double latRange = AreaConstants.VIS_MAX_LATITUDE - AreaConstants.VIS_MIN_LATITUDE;
        double lonRange = AreaConstants.VIS_MAX_LONGITUDE - AreaConstants.VIS_MIN_LONGITUDE;

        setPreferredSize(new Dimension(
                (int) (lonRange * AreaConstants.PIXELS_PER_DEGREE),
                (int) (latRange * AreaConstants.PIXELS_PER_DEGREE)
        ));

        timer = new Timer(SimulationConstants.SIMULATION_DELAY_MS, this);
        timer.start();
    }

    // obserwator
    @Override
    public void onIncidentReported(Incident incident) {
    }

    @Override
    public void onIncidentCleared() {
    }

    // JRG
    private List<JRG> initializeJRG() {

        List<JRG> list = new ArrayList<>();

        list.add(new JRG("JRG-1", new Vector2D(50.0599424, 19.9432412)));
        list.add(new JRG("JRG-2", new Vector2D(50.0335189, 19.9358363)));
        list.add(new JRG("JRG-3", new Vector2D(50.0755229, 19.8875822)));
        list.add(new JRG("JRG-4", new Vector2D(50.0375876, 20.0057190)));
        list.add(new JRG("JRG-5", new Vector2D(50.0921839, 19.9217896)));
        list.add(new JRG("JRG-6", new Vector2D(50.0159353, 20.0156738)));
        list.add(new JRG("JRG-7", new Vector2D(50.0941205, 19.9773860)));
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

        if (stepCounter % SimulationConstants.CALL_INTERVAL_STEPS == 0) {
            skkm.receiveCall();
        }

        for (JRG jrg : jrgs) {
            jrg.updateCars();
        }
        skkm.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();

        drawTimer(g2);
        drawLegend(g2);
        drawIncidentArea(g2, w, h);

        // rysowanie wszystkich oczekujących i trwających zdarzeń
        for (Incident inc : skkm.getWaitingIncidents()) {
            drawIncident(g2, inc, inc.getVisualizedType(), w, h);
        }
        for (IncidentAction action : skkm.getActiveActions()) {
            drawIncident(g2, action.getIncident(), action.getIncident().getVisualizedType(), w, h);
        }

        for (JRG jrg : jrgs) {
            drawJRG(g2, jrg, w, h);
        }
    }

    // timer
    private void drawTimer(Graphics2D g) {

        int seconds = stepCounter / SimulationConstants.STEPS_PER_SECOND;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("Czas symulacji: " + seconds + " s", LEGEND_X, 20);
    }

    // rysowanie legendy
    private void drawLegend(Graphics2D g) {

        int y = LEGEND_Y_START;
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(Color.WHITE);

        g.drawString("LEGENDA:", LEGEND_X, y);
        y += LEGEND_LINE;

        drawLegendDot(g, CarStatus.FREE.getColor(), "Wolny", y);
        y += LEGEND_LINE;
        drawLegendDot(g, CarStatus.BUSY_GOING.getColor(), "W drodze", y);
        y += LEGEND_LINE;
        drawLegendDot(g, CarStatus.BUSY_ACTION.getColor(), "W akcji", y);
        y += LEGEND_LINE * 2;

        g.drawString("Zdarzenia:", LEGEND_X, y);
        y += LEGEND_LINE;

        // PZ prawdziwe
        drawLegendPZ(g, Color.GREEN, y);
        g.setColor(Color.WHITE);
        g.drawString("Pożar (PZ)", LEGEND_X + 14, y + 6);
        y += LEGEND_LINE;

        // PZ fałszywy alarm
        drawLegendPZ(g, Color.RED, y);
        g.setColor(Color.WHITE);
        g.drawString("Pożar – FA", LEGEND_X + 14, y + 6);
        y += LEGEND_LINE;

        // MZ prawdziwe
        drawLegendMZ(g, Color.GREEN, y);
        g.setColor(Color.WHITE);
        g.drawString("Miejscowe zagrożenie (MZ)", LEGEND_X + 14, y + 6);
        y += LEGEND_LINE;

        // MZ fałszywy alarm
        drawLegendMZ(g, Color.RED, y);
        g.setColor(Color.WHITE);
        g.drawString("MZ – FA", LEGEND_X + 14, y + 6);
    }

    private void drawLegendPZ(Graphics2D g, Color borderColor, int y) {

        g.setStroke(new BasicStroke(2));
        g.setColor(Color.ORANGE);

        Polygon p = new Polygon(
                new int[]{LEGEND_X, LEGEND_X + 6, LEGEND_X + 3},
                new int[]{y + 6, y + 6, y},
                3
        );

        g.fill(p);
        g.setColor(borderColor);
        g.draw(p);
    }

    private void drawLegendMZ(Graphics2D g, Color borderColor, int y) {

        g.setStroke(new BasicStroke(2));
        g.setColor(Color.PINK);
        g.fillRect(LEGEND_X, y, 6, 6);

        g.setColor(borderColor);
        g.drawRect(LEGEND_X, y, 6, 6);
    }


    private void drawLegendDot(Graphics2D g, Color c, String label, int y) {
        g.setColor(c);
        g.fillOval(LEGEND_X, y - 6, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString(label, LEGEND_X + 12, y);
    }

    // obszar zdarzeń
    private void drawIncidentArea(Graphics2D g, int w, int h) {

        Point nw = geoToPixel(
                AreaConstants.INCIDENT_MAX_LATITUDE,
                AreaConstants.INCIDENT_MIN_LONGITUDE,
                w, h
        );
        Point se = geoToPixel(
                AreaConstants.INCIDENT_MIN_LATITUDE,
                AreaConstants.INCIDENT_MAX_LONGITUDE,
                w, h
        );

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRect(nw.x, nw.y, se.x - nw.x, se.y - nw.y);
    }

    // zdarzenie
    private void drawIncident(Graphics2D g, Incident incident,
                              IncidentType visualType, int w, int h) {

        Vector2D p = incident.getPosition();
        Point pt = geoToPixel(p.getComponents()[0], p.getComponents()[1], w, h);

        g.setStroke(new BasicStroke(3));
        Shape shape;

        if (visualType == IncidentType.PZ) {
            g.setColor(Color.ORANGE);
            shape = new Polygon(
                    new int[]{pt.x - 8, pt.x + 8, pt.x},
                    new int[]{pt.y + 8, pt.y + 8, pt.y - 8}, 3
            );
        } else {
            g.setColor(Color.PINK);
            shape = new Rectangle(pt.x - 8, pt.y - 8, 16, 16);
        }

        g.fill(shape);
        g.setColor(incident.getType() == visualType ? Color.GREEN : Color.RED);
        g.draw(shape);
    }

    // pasek zasobów JRG
    private void drawJRG(Graphics2D g, JRG jrg, int w, int h) {

        Vector2D pos = jrg.getPosition();
        Point p = geoToPixel(pos.getComponents()[0], pos.getComponents()[1], w, h);

        final int BAR_W = 50;
        final int BAR_H = 8;
        final int TOTAL = 5;

        int free = jrg.getFreeCars(TOTAL).size();
        int bx = p.x - BAR_W / 2;
        int by = p.y - 18;

        g.setColor(Color.WHITE);
        g.drawString(jrg.getName(), bx, by - 4);

        g.setColor(Color.GRAY);
        g.fillRect(bx, by, BAR_W, BAR_H);

        g.setColor(CarStatus.FREE.getColor().darker());
        g.fillRect(bx, by, BAR_W * free / TOTAL, BAR_H);

        g.setColor(Color.BLACK);
        g.drawRect(bx, by, BAR_W, BAR_H);

        // linia podziału (5 części)
        g.setColor(Color.GRAY);
        g.drawRect(bx, by, BAR_W, BAR_H);
        for (int i = 1; i < TOTAL; i++) {
            int lineX = bx + (BAR_W / TOTAL) * i;
            g.drawLine(lineX, by, lineX, by + BAR_H);
        }

        for (Car car : jrg.getAllCars()) {
            if (car.getStatus() != CarStatus.FREE) {
                Vector2D cp = car.getCurrentPosition();
                Point cpt = geoToPixel(cp.getComponents()[0], cp.getComponents()[1], w, h);
                g.setColor(car.getStatus().getColor());
                g.fillOval(cpt.x - 3, cpt.y - 3, 6, 6);
            }
        }
    }

    private Point geoToPixel(double lat, double lon, int w, int h) {

        double latRange = AreaConstants.VIS_MAX_LATITUDE - AreaConstants.VIS_MIN_LATITUDE;
        double lonRange = AreaConstants.VIS_MAX_LONGITUDE - AreaConstants.VIS_MIN_LONGITUDE;

        return new Point(
                (int) ((lon - AreaConstants.VIS_MIN_LONGITUDE) * w / lonRange),
                (int) ((AreaConstants.VIS_MAX_LATITUDE - lat) * h / latRange)
        );
    }
}
