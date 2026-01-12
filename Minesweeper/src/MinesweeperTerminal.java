import java.util.Scanner;

public class MinesweeperTerminal extends MinesweeperUI {

    private final Scanner scanner = new Scanner(System.in);
    private final HighScoreManager highScoreManager = new HighScoreManager();

    public MinesweeperTerminal(MinesweeperSpielLogik spielLogik) {
        super(spielLogik);
    }

    @Override
    protected Schwierigkeit waehleSchwierigkeit() {
        while (true) {
            System.out.println("Schwierigkeit wählen: 1=leicht, 2=mittel, 3=schwer");
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1": return Schwierigkeit.LEICHT;
                case "2": return Schwierigkeit.MITTEL;
                case "3": return Schwierigkeit.SCHWER;
                case "42": return Schwierigkeit.TEST;
                default:
                    System.out.println("Ungültige Eingabe. Bitte 1, 2 oder 3 eingeben.");
            }
        }
    }

    @Override
    protected void gebeAus(Schwierigkeit level) {
        int zeilen = level.getZeilen();
        int spalten = level.getSpalten();
        int flaggenZaehler = 0;            //Es wird gezählt wie viele Flaggen gesetzt sind und in flaggenZaehler gespeichert, um die Anzahl für den Spieler zu printen


        for (int r = 0; r < zeilen; r++) {
            for (int c = 0; c < spalten; c++) {
                if (spielLogik.gebeFeld(r, c).gebeIstMarkiertZustand()) {
                    flaggenZaehler++;
                }
            }
        }

        System.out.println();
        System.out.println("Zeit/Score: " + spielLogik.gebeScore() + "s");  //Aktuelle Zeit wird am Anfang jedes Spielzugs angezeigt

        if (spielLogik.istPausiert()) {
            System.out.println("STATUS: PAUSIERT (drücke 'p' zum Fortsetzen)");
        }

        System.out.println();
        System.out.println("Flaggen gesetzt: " + flaggenZaehler + "/" + level.getMinen());  //Aktuelle Flaggenanzahl wird am Anfang angezeigt
        System.out.println();

        System.out.print("    ");                           //Offset, damit die Spaltennummern korrekt über den Spalten angezeigt werden
        for (int c = 0; c < spalten; c++) {
            System.out.printf("%2d ", c);
        }
        System.out.println();

        System.out.print("     ");                          //Offset, damit die Trennsymbole "-" zwischen Spalten udn Spielfeld angezeigt werden
        for (int c = 0; c < spalten; c++) {
            System.out.print("─  ");                        // Trennsymbole "-" anzeigen
        }
        System.out.println();

        for (int r = 0; r < zeilen; r++) {
            System.out.printf("%2d |", r);                   //Zeilenzahl und Trennsymbol "|" wird angezeigt
            for (int c = 0; c < spalten; c++) {
                Zelle z = spielLogik.gebeFeld(r, c);
                System.out.print(" " + symbolFuerZelle(z) + " ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Befehle aufdecken, Flagge setzen, Pause und Spiel beenden: a <spalte> <zeile>  |  f <spalte> <zeile>  | p | q"); //Befehle, die akzeptiert werden
    }

    private String symbolFuerZelle(Zelle z) {         //Bei symbolFuerZelle kann man bestimmen was für eine Zelle mit Mine, Leere Zelle, geflaggte Zelle angezeigt werden soll
        if (z.gebeIstAufgedecktZustand()) {
            if (z.gebeIstMineZustand()) return "*";
            int n = z.gebeAnzahlAngrenzenderMinen();
            return (n == 0) ? "·" : Integer.toString(n);            //ASCII Code 250 "middle dot"
        } else {
            if (z.gebeIstMarkiertZustand()) return "ƒ";
            return "■";                                     //ASCII Code 254 "Black square"
        }
    }

    @Override
    protected boolean bekommeEingabe(Schwierigkeit level) {
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            
            if (line.equalsIgnoreCase("q")) {   // Beenden ist immer erlaubt, auch währned der Pause
                return false;
            }

            
            if (line.equalsIgnoreCase("p")) {   // Pause aufrufen
                spielLogik.togglePause();
                return true; 
            }

            
            if (spielLogik.istPausiert()) { // Wenn pausiert: keine Aktionen außer p/q
                System.out.println("Spiel ist pausiert. Drücke 'p' zum Fortsetzen oder 'q' zum Beenden.");
                continue;
            }

            String[] parts = line.split("\\s+");
            if (parts.length != 3) {
                System.out.println("Falsche Eingabe bitte Format beachten. Beispiel: 'a 3 5' zum Aufdecken von Spalte 3 und Zeile 5  oder  'f 3 5' zum Flagge setzen auf Spalte 3 und Zeile 5 oder 'p' zum pausieren oder 'q' zum beenden des Spiels.");
                continue;
            }

            String cmd = parts[0].toLowerCase();    // 1. Teil des Befehls a / f / p / q
            Integer c = tryParseInt(parts[1]);      // 2. Teil des Befehls Spalt
            Integer r = tryParseInt(parts[2]);      // 3. Teil des Befehls Zeile

            if (r == null || c == null) {
                System.out.println("Zeile und Spalte müssen eine Zahln sein.");
                continue;
            }

            if (!istImFeld(level, r, c)) {
                System.out.println("Ungültige Koordinate (außerhalb des Feldes).");
                continue;
            }

            switch (cmd) {
                case "a":
                    spielLogik.aufdecken(r, c);
                    return spielLogik.laeuftNoch(); // wenn verloren/gewonnen -> false

                case "f":
                    spielLogik.wechselMarkierung(r, c);
                    return spielLogik.laeuftNoch();

                default:
                    System.out.println("Unbekannter Befehl. Nutze a zum aufdecken, f zum Flagge setzen, p zum pausieren und q um das Spiel zu beenden.");
            }
        }
    }

    private boolean istImFeld(Schwierigkeit level, int r, int c) {
        return r >= 0 && r < level.getZeilen() && c >= 0 && c < level.getSpalten();  //Prüft, ob die Koordinate im Feld vorhanden ist oder außerhalb des Bereichs liegt
    }

    private Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    protected void wennGewonnen(Schwierigkeit level) {
        handleHighscoreNachSieg(level);
    }

    private void handleHighscoreNachSieg(Schwierigkeit level) {
        float score = spielLogik.gebeScore(); // Zeit in Sekunden als Score

        System.out.print("Möchtest du deinen Highscore speichern? (j/n) > ");
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("j") || input.equals("ja")) {
            String name = "";
            while (name.trim().isEmpty()) {
                System.out.print("Name eingeben > ");
                name = scanner.nextLine().trim();
                if (name.trim().isEmpty()) {
                    System.out.println("Name darf nicht leer sein.");
                }
            }

            highScoreManager.updateScore(level, name, score);
            System.out.println("Highscore wurde gespeichert!");
        }

        zeigeTop5(level);
    }

    private void zeigeTop5(Schwierigkeit level) {
        Score[] scores = highScoreManager.ladeScore(level);

        System.out.println();
        System.out.println("Top 5 Highscores (" + level + ")");
        System.out.println("----------------------------");

        for (int i = 0; i < scores.length; i++) {
            Score s = scores[i];
            String name = (s == null || s.Name == null || s.Name.trim().isEmpty()) ? "---" : s.Name;
            float value = (s == null) ? 0 : s.Score;

            System.out.printf("%d) %-15s  %6.0fs%n", (i + 1), name, value);
        }

        System.out.println();
    }
}
