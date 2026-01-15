public abstract class MinesweeperUI {

    protected final MinesweeperSpielLogik spielLogik;

    protected MinesweeperUI(MinesweeperSpielLogik spielLogik) {
        this.spielLogik = spielLogik;
    }

    public void starten() {
        Schwierigkeit level = waehleSchwierigkeit();
        spielLogik.starten(level);

        while (spielLogik.laeuftNoch()) {
            System.out.print("\033[H\033[2J"); // Spielfeld wird immer neu geprinted, aber durch den Befehl sieht es aus, als würde es sich aktualisieren
            System.out.flush();
            gebeAus(level);

            if (!bekommeEingabe(level)) {
                break;
            }
        }

        System.out.print("\033[H\033[2J");  // Spielfeld wird immer neu geprinted, aber durch den Befehl sieht es aus, als würde es sich aktualisieren
        System.out.flush();
        gebeAus(level);

        if (spielLogik.istGewonnen()) {
            System.out.println("Gewonnen! Alle Minen wurden gefunden!");
            wennGewonnen(level);
        } else if (spielLogik.istVerloren()) {
            System.out.println("BOOM! Du hast eine Mine getroffen.");
        }

        System.out.println("Spiel beendet. Score/Zeit: " + spielLogik.gebeScore());
    }

    
    
    protected abstract void wennGewonnen(Schwierigkeit level); // Entscheidet GUI/Terminal selbst wie sie alles nach dem Win Handeln mit HighScore Manager usw.
    protected abstract boolean bekommeEingabe(Schwierigkeit level);
    protected abstract void gebeAus(Schwierigkeit level);
    protected abstract Schwierigkeit waehleSchwierigkeit();
}
