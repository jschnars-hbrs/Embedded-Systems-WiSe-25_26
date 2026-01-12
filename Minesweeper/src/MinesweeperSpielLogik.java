public class MinesweeperSpielLogik {

    private Minenfeld minenfeld;
    private Timer timer;
    private Schwierigkeit level;



    // Statusmeldungn als boolean, um verschiedene Spielzustände abzufragen
    private boolean spielLaeuft;
    private boolean gewonnen;
    private boolean verloren;
    private boolean pausiert;


    public MinesweeperSpielLogik() {
        this.timer = new Timer();
    }

    public void starten(Schwierigkeit level) {
        this.level = level;

        this.minenfeld = new Minenfeld();
        minenfeld.generiere(
                level.getZeilen(),
                level.getSpalten(),
                level.getMinen()
        );

        timer.zuruecksetzen();
        timer.starten();

        spielLaeuft = true;
        gewonnen = false;
        verloren = false;
    }

    public void stoppen() {
        timer.stoppen();
        spielLaeuft = false;
    }
    public boolean istPausiert() {
    return pausiert;
    }

    public void togglePause() {
    if (!spielLaeuft) return;

        if (!pausiert) {
            pausiert = true;
            timer.stoppen();      // Zeit anhalten
        } else {
            pausiert = false;
            timer.starten();      // Zeit weiterlaufen lassen
        }
    }

    public boolean laeuftNoch() {
        return spielLaeuft;
    }

    public boolean istGewonnen() {
        return gewonnen;
    }

    public boolean istVerloren() {
        return verloren;
    }

    public int gebeScore() {
        return timer.getZeit();
    }

    public Zelle gebeFeld(int r, int c) {
        return minenfeld.gebeFeld(r, c);
    }

    public void aufdecken(int r, int c) {
        if (!spielLaeuft || pausiert) return;

        minenfeld.aufdecken(r, c);
        Zelle z = minenfeld.gebeFeld(r, c);

        if (z.gebeIstMineZustand()) {  // Wenn man Zelle aufdeckt und diese eine Mine ist, dann hat man verloren
            verloren = true;
            spielLaeuft = false;
            timer.stoppen();
            return;
        }

        if (pruefeGewonnen()) {         // Wenn alle Flaggen korrekt gesetzt sind und alle übrigen Nicht-Minen-Felder aufgedckt sind, hat man gewonne
            gewonnen = true;
            spielLaeuft = false;
            timer.stoppen();
        }
    }

    public void wechselMarkierung(int r, int c) {
        if (!spielLaeuft || pausiert) return;

        minenfeld.wechselMarkierung(r, c);

    }

    private boolean pruefeGewonnen() {                      //Wenn alle Nicht-Minen-Felder aufgedeckt sind, hat man gewonnen (klassische Regeln, die Flaggen müssen theoretisch gar nicht gesetzt werden)
        for (int r = 0; r < level.getZeilen(); r++) {
            for (int c = 0; c < level.getSpalten(); c++) {
                Zelle z = minenfeld.gebeFeld(r, c);
                if (!z.gebeIstMineZustand() && !z.gebeIstAufgedecktZustand()) {
                    return false;
             }
            }
        }
        return true;
    }
}
