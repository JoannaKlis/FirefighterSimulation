import constants.AreaConstants;
import simulation.SimulationPanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Określenie zakresu geograficznego do wizualizacji (VIS_...)
            double latRange = AreaConstants.VIS_MAX_LATITUDE - AreaConstants.VIS_MIN_LATITUDE;
            double lonRange = AreaConstants.VIS_MAX_LONGITUDE - AreaConstants.VIS_MIN_LONGITUDE;

            // Obliczenie wymiarów okna w pikselach
            int widthPixels = (int) (lonRange * AreaConstants.PIXELS_PER_DEGREE);
            int heightPixels = (int) (latRange * AreaConstants.PIXELS_PER_DEGREE);

            JFrame frame = new JFrame("Symulacja Dysponowania JRG Kraków (Wzorce Projektowe)");

            SimulationPanel simulationPanel = new SimulationPanel();
            frame.add(simulationPanel);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setVisible(true);

            // Ustawienie rozmiaru
            frame.pack();
            frame.setSize(simulationPanel.getPreferredSize().width + 20, simulationPanel.getPreferredSize().height + 50);
        });
    }
}