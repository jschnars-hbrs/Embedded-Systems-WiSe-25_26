
public class MinesweeperSpielLogik {

    private Minenfeld minenfeld;
    private Timer timer;

    public MinesweeperSpielLogik() {

        this.minenfeld = null;
        this.timer = new Timer();
    }

    public void starten(Schwierigkeit level) {
        

        int zeilen = level.getZeilen();
        int spalten = level.getSpalten();
        int minen = level.getMinen();


        this.minenfeld = new Minenfeld();
        boolean exito = this.minenfeld.generiere(zeilen, spalten, minen);
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
        return this.timer.getZeit(); 
    }
}
