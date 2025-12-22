public class Timer {

    private long startZeit;      // Marca el momento exacto en que el timer comenzó a contar
    private long laufZeit;       // Tiempo acumulado en milisegundos
    private TimerStatus status;  // Estado actual del timer

    public Timer() {
        this.laufZeit = 0;
        this.status = TimerStatus.neugestartet;
    }

    // Inicia o reanuda el timer
    public void starten() {
        if (status != TimerStatus.laufend) {
            startZeit = System.currentTimeMillis(); // Momento actual
            status = TimerStatus.laufend;
        }
    }

    // Detiene el timer y guarda el tiempo acumulado
    public void stoppen() {
        if (status == TimerStatus.laufend) {
            laufZeit += System.currentTimeMillis() - startZeit;
            status = TimerStatus.gestoppt;
        }
    }

    // Reinicia completamente el contador a 0
    public void zuruecksetzen() {
        laufZeit = 0;
        status = TimerStatus.neugestartet;
    }

    // Devuelve el tiempo transcurrido en segundos
    public int getZeit() {
        if (status == TimerStatus.laufend) {
            long jetzt = System.currentTimeMillis();
            return (int)((laufZeit + (jetzt - startZeit)) / 1000); // pasa ms → s
        }
        return (int)(laufZeit / 1000);
    }

    // Devuelve el estado actual del timer (necesario según el diagrama)
    public TimerStatus getStatus() {
        return status;
    }
}
