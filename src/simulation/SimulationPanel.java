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

    // stałe do rysowania legendy
    private static final int LEGEND_X = 10;
    private static final int LEGEND_Y_START = 40;
    private static final int LEGEND_LINE_HEIGHT = 14;
    private static final int LEGEND_FONT_SIZE = 11;

    public SimulationPanel() {
        this.stepCounter = 0;
        List<JRG> allJrgs = initializeJRG();
        this.jrgs = allJrgs;
        this.skkm = new SKKM(allJrgs);

        // GUI setup
        this.setLayout(new BorderLayout());
        this.setBackground(Color.DARK_GRAY);

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

        // współrzędne jednostrek RG
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
        boolean incidentHasActiveCars = false; // Będziemy sprawdzać, czy jakiekolwiek auto jest poza bazą

        // odbieranie i obsługa zgłoszeń
        if (stepCounter % SimulationConstants.CALL_INTERVAL_STEPS == 0) {
            Incident incident = skkm.receiveCall();
            skkm.handleIncident(incident);
        }

        // aktualizacja stanu samochodów (Wzorzec Stan)
        for (JRG jrg : jrgs) {
            for (Car car : jrg.getAllCars()) {
                car.update();

                // sprawdzenie, czy samochód jest w drodze lub w akcji
                if (car.getStatus() != CarStatus.FREE) {
                    // Wystarczy, że jakikolwiek samochód jest zajęty, aby zdarzenie było AKTYWNE
                    incidentHasActiveCars = true;
                }

                // logika zmiany wizualizacji zdarzenia na AF/Prawdziwe
                // Zmiana powinna nastąpić, gdy samochód DOJECHAŁ na miejsce zdarzenia
                // Sprawdzamy, czy samochód JEST na pozycji celu i cel to NIE JEST jego jednostka
                if (car.getCurrentPosition().equals(car.getTargetPosition()) &&
                        !car.getTargetPosition().equals(car.getHomePosition()) &&
                        (car.getStatus() == CarStatus.BUSY_ACTION || car.getStatus() == CarStatus.BUSY_GOING)) {

                    // Zdarzenie zostało zweryfikowane po dojechaniu
                    skkm.visualizeRealStatus();
                }
            }
        }

        // ikonka zdarzenia ma być widoczna do czasu, aż wszystkie samochody wrócą do jednostki.
        // Jeśli jest zgłoszenie, ale ŻADEN samochód nie jest aktywny, oznacza to, że akcja się zakończyła
        if (skkm.getLastReportedIncident() != null && !incidentHasActiveCars) {
            skkm.setIncidentResolved(true);
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

        // rysowanie timera
        drawTimer(g2d);
        // rysowanie legendy
        drawLegend(g2d);

        // rysowanie ramki obszaru zdarzeń
        drawIncidentAreaFrame(g2d, panelWidth, panelHeight);

        // rysowanie miejsca zdarzenia tylko, gdy jest aktywne
        if (!skkm.isIncidentResolved()) {
            drawLastIncidentLocation(g2d, panelWidth, panelHeight);
        }

        // rysowanie JRG, pojemności i samochodów
        for (JRG jrg : jrgs) {
            drawJRG(g2d, jrg, panelWidth, panelHeight);
        }
    }

    private void drawTimer(Graphics2D g2d) {
        int seconds = stepCounter / SimulationConstants.STEPS_PER_SECOND;
        int steps = stepCounter % SimulationConstants.STEPS_PER_SECOND;

        String timeString = String.format("Czas symulacji: %d s",seconds);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.drawString(timeString, LEGEND_X, 20);
    }

    private void drawLegend(Graphics2D g2d) {
        g2d.setFont(new Font("SansSerif", Font.PLAIN, LEGEND_FONT_SIZE));
        g2d.setColor(Color.WHITE);

        int y = LEGEND_Y_START;

        g2d.drawString("LEGENDA:", LEGEND_X, y);
        y += LEGEND_LINE_HEIGHT;

        // pojazdy
        drawCarStatus(g2d, LEGEND_X, y, CarStatus.FREE, "Wolny");
        y += LEGEND_LINE_HEIGHT;
        drawCarStatus(g2d, LEGEND_X, y, CarStatus.BUSY_GOING, "W drodze");
        y += LEGEND_LINE_HEIGHT;
        drawCarStatus(g2d, LEGEND_X, y, CarStatus.BUSY_ACTION, "W akcji");
        y += LEGEND_LINE_HEIGHT;

        // Zdarzenia
        g2d.drawString("Zdarzenia:", LEGEND_X, y);
        y += LEGEND_LINE_HEIGHT;

        drawIncidentShape(g2d, IncidentType.PZ, LEGEND_X, y, "PZ - Prawdziwy", Color.GREEN);
        y += LEGEND_LINE_HEIGHT;
        drawIncidentShape(g2d, IncidentType.PZ, LEGEND_X, y, "PZ - Fałszywy (AF)"); // Przykład fałszywego z czerwoną obwódką
        y += LEGEND_LINE_HEIGHT;
        drawIncidentShape(g2d, IncidentType.MZ, LEGEND_X, y, "MZ - Prawdziwy", Color.GREEN);
        y += LEGEND_LINE_HEIGHT;
        drawIncidentShape(g2d, IncidentType.MZ, LEGEND_X, y, "MZ - Fałszywy (AF)"); // Przykład fałszywego z czerwoną obwódką
    }

    private void drawCarStatus(Graphics2D g2d, int x, int y, CarStatus status, String label) {
        final int SIZE = 5;
        g2d.setColor(status.getColor());
        g2d.fillOval(x, y - SIZE/2 - 2, SIZE, SIZE);
        g2d.setColor(Color.WHITE);
        g2d.drawString(label, x + SIZE + 5, y);
    }

    // opcjonalny kolor obwódki do legendy
    private void drawIncidentShape(Graphics2D g2d, IncidentType type, int x, int y, String label, Color outlineColor) {
        final int SIZE = 6;
        int shapeX = x + SIZE/2;
        int shapeY = y - SIZE/2 - 2;

        g2d.setStroke(new BasicStroke(1));

        Color fillColor = Color.RED;
        Shape shape = null;

        // Określamy kształt na podstawie typu WIZUALIZOWANEGO
        // W legendzie używamy PZ/MZ do rysowania, AF jest tylko do opisania
        IncidentType actualType = type == IncidentType.AF ? IncidentType.MZ : type;

        switch (actualType) {
            case PZ:
                fillColor = Color.ORANGE;
                int[] xPZ = {shapeX - SIZE, shapeX + SIZE, shapeX};
                int[] yPZ = {shapeY + SIZE, shapeY + SIZE, shapeY - SIZE};
                shape = new Polygon(xPZ, yPZ, 3);
                break;
            case MZ:
                fillColor = Color.PINK;
                shape = new Rectangle(shapeX - SIZE / 2, shapeY - SIZE / 2, SIZE, SIZE);
                break;
        }

        if (shape != null) {
            g2d.setColor(fillColor);
            g2d.fill(shape);

            // rysowanie obwódki, jeśli podano
            if (outlineColor != null) {
                g2d.setColor(outlineColor);
                g2d.draw(shape);
            } else {
                g2d.setColor(Color.RED);
                g2d.draw(shape);
            }
        }

        g2d.setColor(Color.WHITE);
        g2d.drawString(label, x + SIZE * 2 + 5, y);
    }

    private void drawIncidentShape(Graphics2D g2d, IncidentType type, int x, int y, String label) {
        drawIncidentShape(g2d, type, x, y, label, null);
    }

    private void drawIncidentAreaFrame(Graphics2D g2d, int panelWidth, int panelHeight) {
        Point nw = geoToPixel(AreaConstants.INCIDENT_MAX_LATITUDE, AreaConstants.INCIDENT_MIN_LONGITUDE, panelWidth, panelHeight);
        Point se = geoToPixel(AreaConstants.INCIDENT_MIN_LATITUDE, AreaConstants.INCIDENT_MAX_LONGITUDE, panelWidth, panelHeight);

        int width = se.x - nw.x;
        int height = se.y - nw.y;

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(nw.x, nw.y, width, height);
        g2d.setStroke(new BasicStroke(1));
    }

    // rysowanie miejsca zdarzenia z obwódką na podstawie statusu (prawdziwy/fałszywy)
    private void drawLastIncidentLocation(Graphics2D g2d, int panelWidth, int panelHeight) {
        Incident reportedIncident = skkm.getLastReportedIncident();
        IncidentType visualizedType = skkm.getLastVisualizedIncidentType();

        // sprawdzamy, czy w ogóle jest zdarzenie do wizualizacji
        if (reportedIncident == null || visualizedType == null) return;

        Vector2D pos = reportedIncident.getPosition();
        Point p = geoToPixel(pos.getComponents()[0], pos.getComponents()[1], panelWidth, panelHeight);

        final int SIZE = 8;
        int x = p.x;
        int y = p.y;

        // wizualizacja fałszywych alarmów
        Color outlineColor = Color.RED;
        if (visualizedType == reportedIncident.getType()) {
            // Prawdziwe Zdarzenie (PZ lub MZ): Zielona obwódka
            outlineColor = Color.GREEN;
        }

        g2d.setStroke(new BasicStroke(3)); // grubsza obwódka dla zdarzenia
        Shape shape = null;

        // Użycie typu do wizualizacji, ale unikamy przypadku AF w switch (aby nie rysować X)
        IncidentType shapeType = visualizedType;

        // Jeśli AF został ujawniony, używamy kształtu MZ (różowy kwadrat) jako domyślnego
        if (shapeType == IncidentType.AF) {
            shapeType = IncidentType.MZ;
        }

        switch (shapeType) {
            case PZ:
                g2d.setColor(Color.ORANGE);
                int[] xPZ = {x - SIZE, x + SIZE, x};
                int[] yPZ = {y + SIZE, y + SIZE, y - SIZE};
                shape = new Polygon(xPZ, yPZ, 3);
                break;
            case MZ:
                g2d.setColor(Color.PINK);
                shape = new Rectangle(x - SIZE, y - SIZE, 2 * SIZE, 2 * SIZE);
                break;
        }

        if (shape != null) {
            // rysowanie wypełnienia (dla PZ/MZ)
            g2d.fill(shape);
            // rysowanie obwódki (krawędzi)
            g2d.setColor(outlineColor);
            g2d.draw(shape);
        }

        g2d.setStroke(new BasicStroke(1));
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

        // rysowanie Nazwy JRG
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
            // rysujemy tylko te, które są w ruchu (BusyGoing) lub w akcji (BusyAction)
            if (car.getStatus() != CarStatus.FREE) {
                Vector2D pos = car.getCurrentPosition();
                Point carP = geoToPixel(pos.getComponents()[0], pos.getComponents()[1], panelWidth, panelHeight);

                int diameter = 5;
                int radius = diameter / 2;

                g2d.setColor(car.getStatus().getColor());
                g2d.fillOval(carP.x - radius, carP.y - radius, diameter, diameter);
            }
        }
    }
}