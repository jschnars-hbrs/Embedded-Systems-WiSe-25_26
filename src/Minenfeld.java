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
                System.out.print(this.spielfeld[zeilenIndex][spaltenIndex].gebeIstMineZustand());
                System.out.print(" ");
            }
            System.out.println("");
        }
    }

    private void platziereMinen(int zuVerteilendeMinen, Random Zufallsgenerator){
        int zufaelligeZeile = Zufallsgenerator.nextInt(zeilenAnzahl);
        int zufaelligeSpalte = Zufallsgenerator.nextInt(spaltenAnzahl);
        if (zuVerteilendeMinen > 0){
            if (this.spielfeld[zufaelligeZeile][zufaelligeSpalte].gebeIstMineZustand() == false){
                this.spielfeld[zufaelligeZeile][zufaelligeSpalte].setzeMine();
                platziereMinen((zuVerteilendeMinen - 1),Zufallsgenerator);
            }else{
                platziereMinen(zuVerteilendeMinen,Zufallsgenerator);
            }
        }            
    }
    
    public Minenfeld(int zeilenAnzahl, int spaltenAnzahl, int minenAnzahl){
        
        this.zeilenAnzahl = zeilenAnzahl;
        this.spaltenAnzahl = spaltenAnzahl;
        this.minenAnzahl = minenAnzahl;

        //Leeres Spifelfeld der Breite "spaltenAnzahl" & der Höhe "zeilenAnzahl" erstellt
        this.spielfeld = new Zelle[this.zeilenAnzahl][this.spaltenAnzahl];

        for (int zeilenIndex = 0; zeilenIndex < zeilenAnzahl; zeilenIndex++) {
            for (int spaltenIndex = 0; spaltenIndex < spaltenAnzahl; spaltenIndex++) {
                this.spielfeld[zeilenIndex][spaltenIndex] = new Zelle();
                System.out.print(this.spielfeld[zeilenIndex][spaltenIndex].gebeAnzahlAngrenzenderMinen()); //DEBUG Print
            }
            System.out.println(""); //DEBUG Print
        }

        //Platziere zufällig Minen
        Random Zufallsgenerator = new Random();
        platziereMinen(this.minenAnzahl, Zufallsgenerator);
        printSpielfeld(); //DEBUG Print


        
    }


//
  //  }
    //aufdecken(zeilenNummer: int, spaltenNummer: int): Zelle.istMine
    //wechselMarkierung(zeilenNummer: int, spaltenNummer: int)
    //gebeFeld(zeilenNummer, spaltenNummer): Zelle




}
