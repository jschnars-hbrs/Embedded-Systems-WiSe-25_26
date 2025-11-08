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
                System.out.print(this.spielfeld[zeilenIndex][spaltenIndex].istMine);
                System.out.print(" ");
            }
            System.out.println("");
        }
    }

    private void platziereMinen(int zuVerteilendeMinen, Random Zufallsgenerator){
        int zufaelligeZeile = Zufallsgenerator.nextInt(zeilenAnzahl+1);
        int zufaelligeSpalte = Zufallsgenerator.nextInt(spaltenAnzahl+1);
        if (zuVerteilendeMinen > 0){
            if (this.spielfeld[zufaelligeZeile][zufaelligeSpalte].istMine == false){
                this.spielfeld[zufaelligeZeile][zufaelligeSpalte].istMine = true;
                platziereMinen((zuVerteilendeMinen - 1),Zufallsgenerator);
            }else{
                platziereMinen(zuVerteilendeMinen,Zufallsgenerator);
            }
        }            
    }
    
    public Minenfeld(int zeilenAnzahl, int spaltenAnzahl, int minenAnzahl){

        //Leeres Spifelfeld der Breite "spaltenAnzahl" & der Höhe "zeilenAnzahl" erstellt
        Zelle[][] spielfeld = new Zelle[zeilenAnzahl][spaltenAnzahl];

        for (int zeilenIndex = 0; zeilenIndex < zeilenAnzahl; zeilenIndex++) {
            for (int spaltenIndex = 0; spaltenIndex < spaltenAnzahl; spaltenIndex++) {
                spielfeld[zeilenIndex][spaltenIndex] = new Zelle();
                System.out.print(spielfeld[zeilenIndex][spaltenIndex].angrenzendeMinen); //DEBUG Print
            }
            System.out.println(""); //DEBUG Print
        }
        this.zeilenAnzahl = zeilenAnzahl;
        this.spaltenAnzahl = spaltenAnzahl;
        this.spielfeld = spielfeld;
        this.minenAnzahl = minenAnzahl;

        //Platziere zufällig Minen
        Random Zufallsgenerator= new Random();
        platziereMinen(this.minenAnzahl, Zufallsgenerator);
        printSpielfeld();

        /* 
        for (int minenIndex = 0; minenIndex < minenAnzahl; minenIndex++){
            int zufaelligeZeile = Zufallsgenerator.nextInt(zeilenAnzahl+1);
            int zufaelligeSpalte = Zufallsgenerator.nextInt(spaltenAnzahl+1);
                  
            
        }
            */
        
    }


//
  //  }
    //aufdecken(zeilenNummer: int, spaltenNummer: int): Zelle.istMine
    //wechselMarkierung(zeilenNummer: int, spaltenNummer: int)
    //gebeFeld(zeilenNummer, spaltenNummer): Zelle




}
