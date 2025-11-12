//Autorin: Tabea Barteldrees
import java.util.Random;

public class Minenfeld {

    Zelle[][] spielfeld;
    int zeilenAnzahl;
    int spaltenAnzahl;
    int minenAnzahl;

    Zelle[][] testfeld;


    

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

    private  void erstelleTestfeld(){
        this.testfeld = new Zelle[6][6];

        for (int zeilenIndex = 0; zeilenIndex < 6; zeilenIndex++) {
            for (int spaltenIndex = 0; spaltenIndex < 6; spaltenIndex++) {
                this.testfeld[zeilenIndex][spaltenIndex] = new Zelle();
            }
        }

        //platziere Minen
        this.testfeld[2][2].setzeMine();
        this.testfeld[4][3].setzeMine();
        this.testfeld[5][4].setzeMine();

        this.testfeld[1][1].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[1][2].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[1][3].zaehleAngerenzendeMinenHochUmEins();

        this.testfeld[2][1].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[2][3].zaehleAngerenzendeMinenHochUmEins();

        this.testfeld[3][1].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[3][2].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[3][2].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[3][3].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[3][3].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[3][4].zaehleAngerenzendeMinenHochUmEins();

        this.testfeld[4][2].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[4][4].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[4][4].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[4][5].zaehleAngerenzendeMinenHochUmEins();

        this.testfeld[5][2].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[5][3].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[5][3].zaehleAngerenzendeMinenHochUmEins();
        this.testfeld[5][5].zaehleAngerenzendeMinenHochUmEins();


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

        /* 
        erstelleTestfeld(); //DEBUG Print
        this.spaltenAnzahl=6;
        this.zeilenAnzahl=6;
        this.spielfeld = this.testfeld;
        printSpielfeld(); //DEBUG Print
        
        if(aufdecken(5, 4) == true){
            System.out.println("Mine getroffen");
        }
        */
    }

    
    public boolean aufdecken(int zeilenNummer, int spaltenNummer){
         
        if(this.spielfeld[zeilenNummer][spaltenNummer].gebeIstMarkiertZustand() == true){//Ist die Zelle markiert & darf nicht aufgedeckt werden?
            return false;
        }else if(this.spielfeld[zeilenNummer][spaltenNummer].gebeIstMineZustand() == true){ //Liegt dort eine Mine?
            this.spielfeld[zeilenNummer][spaltenNummer].deckeAuf();
            return true;
        }else if(this.spielfeld[zeilenNummer][spaltenNummer].gebeAnzahlAngrenzenderMinen() > 0){ //Zelle mit Zahl?
            this.spielfeld[zeilenNummer][spaltenNummer].deckeAuf();
            return false;
        }else{
            //TODO Add Breath-first search
            return false;
        }
        
    }
    //wechselMarkierung(zeilenNummer: int, spaltenNummer: int)
    //gebeFeld(zeilenNummer, spaltenNummer): Zelle
}
