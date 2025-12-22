import java.util.Scanner;
import Schwierigkeit;


public class MinesweeperTerminal extends MinesweeperUI {

    private final Scanner scanner = new Scanner(System.in);

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
                default:
                    System.out.println("Ungültige Eingabe. Bitte 1, 2 oder 3 eingeben.");
            }
        }
    }

    @Override
    protected void gebeAus(Schwierigkeit level) {
        int zeilen = level.getZeilen();
        int spalten = level.getSpalten(); 

        System.out.println();
        System.out.println("Zeit/Score: " + spielLogik.gebeScore());
        System.out.println();

      
        System.out.print("    ");
        for (int c = 0; c < spalten; c++) {
            System.out.printf("%2d ", c);
        }
        System.out.println();

   
        for (int r = 0; r < zeilen; r++) {
            System.out.printf("%2d |", r);
            for (int c = 0; c < spalten; c++) {
                Zelle z = spielLogik.gebeFeld(r, c);
                System.out.print(" " + symbolFuerZelle(z) + " ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Befehle aufdecken, Flagge setzen und Spiel beenden: a <zeile> <spalte>  |  f <zeile> <spalte>  |  q");
    }

    private String symbolFuerZelle(Zelle z) {
    
        if (z.gebeIstAufgedecktZustand()) {
            if (z.gebeIstMineZustand()) return "*";
            int n = z.gebeAnzahlAngrenzenderMinen();
            return (n == 0) ? "." : Integer.toString(n);
        } else {
            if (z.gebeIstMarkiertZustand()) return "F";
            return "#";
        }
    }

    @Override
    protected boolean bekommeEingabe(Schwierigkeit level) {
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("q")) {
                return false;
            }

            String[] parts = line.split("\\s+");
            if (parts.length != 3) {
                System.out.println("Format: a 3 5  |  f 3 5  |  q");
                continue;
            }

            String cmd = parts[0].toLowerCase(); // 1. Teil des Befehls a / f
            Integer r = tryParseInt(parts[1]);  // 2. Teil des Befehls Zeile
            Integer c = tryParseInt(parts[2]);  // 3. Teil des Befehls Spalt

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
                    return true; 
                case "f":
                    spielLogik.wechselMarkierung(r, c);
                    return true;
                default:
                    System.out.println("Unbekannter Befehl. Nutze a zum aufdecken, f zum Flagge setzen und q um das Spiel zu beenden.");
            }
        }
    }

    private boolean istImFeld(Schwierigkeit level, int r, int c) {
        return r >= 0 && r < level.getZeilen() && c >= 0 && c < level.getSpalten();
    }

    private Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
