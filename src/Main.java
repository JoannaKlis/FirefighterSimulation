import simulation.SimulationPanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Symulacja Dysponowania JRG w Krakowie");

            SimulationPanel simulationPanel = new SimulationPanel();
            frame.add(simulationPanel);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocation(200, 50);
            frame.setResizable(true);
            frame.setVisible(true);

            // ustawienie rozmiaru
            frame.pack();
            frame.setSize(simulationPanel.getPreferredSize().width + 20, simulationPanel.getPreferredSize().height + 50);
        });
    }
}