

public abstract class MinesweeperUI {

    protected final MinesweeperSpielLogik spielLogik;

    protected MinesweeperUI(MinesweeperSpielLogik spielLogik) {
        this.spielLogik = spielLogik;
    }


    public final void starten() {
        Schwierigkeit level = waehleSchwierigkeit();
        spielLogik.starten(level);

        boolean amLaufen = true;
        while (amLaufen) {
            gebeAus(level);                
            amLaufen = bekommeEingabe(level); // true = weiter, false = beenden
        }

        spielLogik.stoppen();
        System.out.println("Spiel beendet. Score/Zeit: " + spielLogik.gebeScore());
    }

   
    protected abstract boolean bekommeEingabe(Schwierigkeit level);
    
    protected abstract void gebeAus(Schwierigkeit level);

    protected abstract Schwierigkeit waehleSchwierigkeit();
}
