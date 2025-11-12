import Schwierigkeit; 
import Minenfeld;
import Timer;
import Zelle;

public class MinesweeperSpielLogik {
    // --- ATTRIBUTE ---
    private Minenfeld minenfeld;
    private Timer timer;
  
    // --- KONSTRUKTOR ---
    public MinesweeperSpielLogik() {
        this.minenfeld = null;
        this.timer = new Timer();
    }
    // --- ÖFFENTLICHE METHODEN (API) ---

    public void starten(Schwierigkeit level) {
        
        // 1. Wir bekommen die Zahlen aus Juliáns Objekt (Schwierigkeit).
        int zeilen = level.getZeilen();
        int spalten = level.getSpalten();
        int minen = level.getMinen();

        // 2. WECHSEL! Wir rufen den BAUHERRN von Tabea an (nicht generieren).
        this.minenfeld = new Minenfeld(zeilen, spalten, minen);
        //Timmer
        this.timer.starten();
    }
    
    public void stoppen() {
        this.timer.stoppen();
    }

    public void aufdecken(int zeilenNummer, int spaltenNummer) {
        this.minenfeld.aufdecken(zeilenNummer, spaltenNummer);
    }

    public void wechselMarkierung(int zeilenNummer, int spaltenNummer) {
        this.minenfeld.wechselMarkierung(zeilenNummer, spaltenNummer);
    }

    public Zelle gebeFeld(int zeilenNummer, int spaltenNummer) {
        return this.minenfeld.gebeFeld(zeilenNummer, spaltenNummer);
    }

    public int gebeScore() {
        return this.timer.gebeZeit(); 
    }
}
