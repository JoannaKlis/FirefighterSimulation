package constants;

import java.util.concurrent.ThreadLocalRandom;

public class SimulationConstants {
    // STAŁE CZASU SYMULACJI (25 kroków to 1 sekunda)
    public static final int STEPS_PER_SECOND = 25;
    public static final int SIMULATION_DELAY_MS = 1000 / STEPS_PER_SECOND;

    // LOGIKA ZGŁOSZEŃ
    public static final double PZ_PROBABILITY = 0.30; // Pożar 30%, Miejscowe zagrożenie 70% bo (1-PZ)
    public static final double AF_PROBABILITY = 0.05; // Fałszywy alarm 5%

    public static final int PZ_CAR_COUNT = 3; // Wymagane samochody na PZ
    public static final int MZ_CAR_COUNT = 2; // Wymagane samochody na MZ

    // NOWE ZGŁOSZENIE (CO 3 SEKUNDY)
    public static final int CALL_INTERVAL_STEPS = 3 * STEPS_PER_SECOND;

    // CZAS TRWANIA AKCJI I DOJAZDU (w sekundach, Warunek 11)
    public static final int MIN_RESPONSE_TIME_S = 0;
    public static final int MAX_RESPONSE_TIME_S = 3; // Dojazd/powrót 0-3s
    public static final int MIN_ACTION_TIME_S = 5;
    public static final int MAX_ACTION_TIME_S = 25; // Akcja 5-25s

    // LOSOWY CZAS (0-3s) w krokach symulacji
    public static int getRandomResponseSteps() {
        int minSteps = MIN_RESPONSE_TIME_S * STEPS_PER_SECOND;
        int maxSteps = MAX_RESPONSE_TIME_S * STEPS_PER_SECOND;
        return ThreadLocalRandom.current().nextInt(minSteps, maxSteps + 1);
    }

    // LOSOWY CZAS (5-25s) w krokach symulacji
    public static int getRandomActionSteps() {
        int minSteps = MIN_ACTION_TIME_S * STEPS_PER_SECOND;
        int maxSteps = MAX_ACTION_TIME_S * STEPS_PER_SECOND;
        return ThreadLocalRandom.current().nextInt(minSteps, maxSteps + 1);
    }
}