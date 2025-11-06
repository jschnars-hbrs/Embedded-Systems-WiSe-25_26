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
        this.minenfeld = new Minenfeld();
        this.timer = new Timer();
    }
    // --- ÖFFENTLICHE METHODEN (API) ---
    
    public void starten(Schwierigkeit level) {
      /** 
       * Diese drei Zahlen (Zeilen, Spalten, Minen) hängen direkt von der Schwierigkeitsstufe ab, 
       * die du in Schwierigkeit-Block definierst. 
       */

        // Wir verwenden die Werte des empfangenen „Levels”.
        this.minenfeld.generieren(
            level.getZeilen(), 
            level.getSpalten(), 
            level.getMinen()
        );
        
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
