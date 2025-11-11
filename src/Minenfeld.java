//Autorin: Tabea Barteldrees
import java.util.Random;

public class Minenfeld {

    Zelle[][] spielfeld;
    int zeilenAnzahl;
    int spaltenAnzahl;
    int minenAnzahl;

    //DEBUG Methode
    private void printSpielfeld(){
         for (int zeilenIndex = 0; zeilenIndex < this.zeilenAnzahl; zeilenIndex++) {
            for (int spaltenIndex = 0; spaltenIndex < this.spaltenAnzahl; spaltenIndex++) {
                if (this.spielfeld[zeilenIndex][spaltenIndex].gebeIstMineZustand()==true) {
                    System.out.print("X");
                }else{
                    System.out.print(this.spielfeld[zeilenIndex][spaltenIndex].gebeAnzahlAngrenzenderMinen());
                }
                System.out.print(" ");
            }
            System.out.println("");
        }
    }
    
    //private Methoden    
    private void platziereMinen(int zuVerteilendeMinen, Random Zufallsgenerator){
        int zufaelligeZeile = Zufallsgenerator.nextInt(zeilenAnzahl);
        int zufaelligeSpalte = Zufallsgenerator.nextInt(spaltenAnzahl);
        if (zuVerteilendeMinen > 0){
            try {
                this.spielfeld[zufaelligeZeile][zufaelligeSpalte].setzeMine();
                platziereMinen((zuVerteilendeMinen - 1),Zufallsgenerator);
            } catch (ArithmeticException e) {
                platziereMinen(zuVerteilendeMinen,Zufallsgenerator);
            }
        }            
    }

    private void zaehleMinen(){
        for (int zeilenIndex = 0; zeilenIndex < this.zeilenAnzahl; zeilenIndex++)  {
            for (int spaltenIndex = 0; spaltenIndex < this.spaltenAnzahl; spaltenIndex++){
                if(this.spielfeld[zeilenIndex][spaltenIndex].gebeIstMineZustand() == true){
                    zaehleAngernzendeFelderUmEinsHoch(zeilenIndex, spaltenIndex);
                }
            }
            
        }
    }
    //zaehleAngerenzendeMinenHochUmEins();
    private  void zaehleAngernzendeFelderUmEinsHoch(int minenZeile, int minenSpalte){
        int indexStart;

        //Zellen eine Zeile über der Mine hochzählen:
        if(minenZeile > 0){ //Mine liegt NICHT am oberen Rand 
            if(minenSpalte > 0){ //Mine liegt NICHT am linken Rand 
                indexStart = minenSpalte-1;
            }else{ //Mine liegt am linken Rand 
                indexStart = 0;
            }
            for(int Index = indexStart; Index <= minenSpalte+1 && Index < this.spaltenAnzahl; Index++){
                this.spielfeld[minenZeile-1][Index].zaehleAngerenzendeMinenHochUmEins();
            }
        }

        //Zelle links der Mine hochzählen
        if(minenSpalte > 0){ //Mine liegt NICHT am linken Rand 
                this.spielfeld[minenZeile][minenSpalte-1].zaehleAngerenzendeMinenHochUmEins();
            }

        //Zelle rechts der Mine hochzählen
        if (minenSpalte < this.spaltenAnzahl-1){ //Mine liegt NICHT am rechten Rand 
                this.spielfeld[minenZeile][minenSpalte+1].zaehleAngerenzendeMinenHochUmEins();
            }

        //Zellen eine Zeile unter der Mine hochzählen:
        if (minenZeile < this.zeilenAnzahl-1){ //Mine liegt NICHT am unteren Rand 
            if(minenSpalte > 0){ //Mine liegt NICHT am linken Rand 
                indexStart = minenSpalte-1;
            }else{ //Mine liegt am linken Rand 
                indexStart = 0;
            }
            int indexStopp;
            if(minenSpalte+1 >= this.spaltenAnzahl){
                indexStopp = this.spaltenAnzahl;
            }
            else{
                indexStopp = minenSpalte+2;
            }
            for(int Index = indexStart; Index < indexStopp; Index++){
                this.spielfeld[minenZeile+1][Index].zaehleAngerenzendeMinenHochUmEins();
            }
        }
    }

    public Minenfeld(int zeilenAnzahl, int spaltenAnzahl, int minenAnzahl){
        
        this.zeilenAnzahl = zeilenAnzahl;
        this.spaltenAnzahl = spaltenAnzahl;
        this.minenAnzahl = minenAnzahl;

        if(this.minenAnzahl > this.zeilenAnzahl*this.spaltenAnzahl){
            throw new ArithmeticException("Die Anzahl der Minen darf die Anzahl der Felder des Speifeld nicht überschreiten.");
        }

        //Leeres Spifelfeld der Breite "spaltenAnzahl" & der Höhe "zeilenAnzahl" erstellt
        this.spielfeld = new Zelle[this.zeilenAnzahl][this.spaltenAnzahl];

        for (int zeilenIndex = 0; zeilenIndex < zeilenAnzahl; zeilenIndex++) {
            for (int spaltenIndex = 0; spaltenIndex < spaltenAnzahl; spaltenIndex++) {
                this.spielfeld[zeilenIndex][spaltenIndex] = new Zelle();
            }
        }

        //Platziere zufällig Minen
        Random Zufallsgenerator = new Random();
        platziereMinen(this.minenAnzahl, Zufallsgenerator);
        zaehleMinen();
        printSpielfeld(); //DEBUG Print
        
    }

    //aufdecken(zeilenNummer: int, spaltenNummer: int): Zelle.istMine
    //wechselMarkierung(zeilenNummer: int, spaltenNummer: int)
    //gebeFeld(zeilenNummer, spaltenNummer): Zelle
}
