public class Timer {

    private long startZeit;     // Zeitpunkt (in ms), an dem der Timer gestartet wurde
    private long laufZeit;      // Bereits verstrichene Zeit in Millisekunden
    private TimerStatus status; // Aktueller Zustand des Timers

    // Konstruktor: initialisiert den Timer
    public Timer() {
        this.laufZeit = 0;
        this.status = TimerStatus.neugestartet;
    }

    // Startet oder setzt den Timer fort, falls er aktuell nicht läuft
    public void starten() {
        if (status != TimerStatus.laufend) {
            startZeit = System.currentTimeMillis(); // aktueller Systemzeitpunkt
            status = TimerStatus.laufend;
        }
    }

    // Stoppt den Timer und speichert die bisher verstrichene Zeit
    public void stoppen() {
        if (status == TimerStatus.laufend) {
            laufZeit += System.currentTimeMillis() - startZeit;
            status = TimerStatus.gestoppt;
        }
    }

    // Setzt den Timer vollständig zurück. Die gespeicherte Zeit wird auf 0 gesetzt
    public void zuruecksetzen() {
        laufZeit = 0;
        status = TimerStatus.neugestartet;
    }

    // Gibt die aktuell verstrichene Zeit in Sekunden zurück
    public int getZeit() {
        if (status == TimerStatus.laufend) {
            long jetzt = System.currentTimeMillis();
            return (int) ((laufZeit + (jetzt - startZeit)) / 1000);
        }
        return (int) (laufZeit / 1000);
    }

    // Gibt den aktuellen Status des Timers zurück
    public TimerStatus getStatus() {
        return status;
    }
}
