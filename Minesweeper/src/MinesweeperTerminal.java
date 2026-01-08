import java.util.Scanner;



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
                case "1": return Schwierigkeit.TEST;
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
        int flagcount = 0;

        for (int r = 0; r < level.getZeilen(); r++) {                       //Es wird gezählt wie viele Flaggen gesetzt sind und in flagcount gespeichert.
            for (int c = 0; c < level.getSpalten(); c++) {
                if (spielLogik.gebeFeld(r, c).gebeIstMarkiertZustand()) {
                flagcount++;
                }
        }
    }


        System.out.println();
        System.out.println("Zeit/Score: " + spielLogik.gebeScore() + "s"); //Aktuelle Zeit wird am Anfang jedes Spielzugs angezeigt
        System.out.println();
        System.out.println("Flaggen gesetzt: " + flagcount + "/" + level.getMinen()); //Aktuelle Flaggenanzahl wird am Anfang angezeigt
        System.out.println();

      
        System.out.print("    ");               //Offset, damit die Spaltennummern korrekt über den Spalten angezeigt werden
        for (int c = 0; c < spalten; c++) {
           System.out.printf("%2d ", c);        //Spaltennummern anzeigen
        }
        System.out.println();
        System.out.print("     ");              //Offset, damit die Trennsymbole "-" zwischen Spalten udn Spielfeld angezeigt werden
        for (int c = 0; c < spalten; c++) {     // Trennsymbole "-" anzeigen
            
            System.out.printf("-  ",c);
            
        }
        System.out.println();
        
   
        for (int r = 0; r < zeilen; r++) {
            System.out.printf("%2d |", r);          //Zeilenzahl und Trennsymbol "|" wird angezeigt
            for (int c = 0; c < spalten; c++) {
        
                Zelle z = spielLogik.gebeFeld(r, c);
                System.out.print(" " + symbolFuerZelle(z) + " ");  
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Befehle aufdecken, Flagge setzen und Spiel beenden: a <spalte> <zeile>  |  f <spalte> <zeile>  |  q"); //Befehle, die akzeptiert werden
    }

    private String symbolFuerZelle(Zelle z) {   //Bei symbolFuerZelle kann man bestimmen was für eine Zelle mit Mine, Leere Zelle, geflaggte Zelle angezeigt werden soll
    
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
                System.out.println("Falsche Eingabe bitte Format beachten. Beispiel: 'a 3 5' zum Aufdecken von Spalte 3 und Zeile 5  oder  'f 3 5' zum Flagge setzen auf Spalte 3 und Zeile 5 oder 'q' zum beenden des Spiels.");
                continue;
            }

            String cmd = parts[0].toLowerCase(); // 1. Teil des Befehls a / f
            Integer c = tryParseInt(parts[1]);  // 2. Teil des Befehls Spalt
            Integer r = tryParseInt(parts[2]);  // 3. Teil des Befehls Zeile

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
                    if (spielLogik.gebeFeld(r, c).gebeIstMineZustand()) {
                        gebeAus(level);
                        return false; // verloren
                    }

                    
                    if (checkWinUndBeendeWennNoetig(level)) {
                        return false; // gewonnen
                    }
                
                    return true;
                    
                case "f":
                    spielLogik.wechselMarkierung(r, c);
                     
                    if (checkWinUndBeendeWennNoetig(level)) {
                        return false; // gewonnen
                    }
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


    private boolean checkWinUndBeendeWennNoetig(Schwierigkeit level) {
    int flagcount = 0;
    int correctFlags = 0;
    boolean alleFelderAufgedeckt = true;

    for (int a = 0; a < level.getZeilen(); a++) {
        for (int b = 0; b < level.getSpalten(); b++) {
            Zelle z = spielLogik.gebeFeld(a, b);

            if (z.gebeIstMarkiertZustand()) {
                flagcount++;
                if (z.gebeIstMineZustand()) {
                    correctFlags++;
                }
            }

            // alle sonstigen Felder müssen aufgedeckt sein
            if (!z.gebeIstMineZustand() && !z.gebeIstAufgedecktZustand()) {
                alleFelderAufgedeckt = false;
            }
        }
    }

    boolean gewonnen = (flagcount == level.getMinen())      //Wenn alle Flaggen gesetzt wurden, diese korrekt auf Minen sind und alle Sonstigen Felder aufgedeckt sind, ist das Spiel vorbei und man hat gewonnen
            && (correctFlags == level.getMinen())
            && alleFelderAufgedeckt;

    if (gewonnen) {
        gebeAus(level);
        return true; 
    }else{
        return false;
    }

    
}

}
