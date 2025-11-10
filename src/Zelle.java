//Autorin: Tabea Barteldrees
public class Zelle {
    /*
     TODO: Alle Variabeln auf private setzen und get set Methoden hinzugügen.
     Zugriff auf Zellen auch in Minenfeld ändern.
     */

    private boolean istMine = false;
    private boolean istAufgedeckt = false;
    private boolean istMarkiert = false;
    private int angrenzendeMinen = 0;

    public boolean gebeIstMineZustand(){
        return this.istMine;
    }

    public void setzeMine(){
        this.istMine = true;
    }


    public boolean gebeIstAufgedecktZustand(){
        return this.istAufgedeckt;
    }

    public void deckeAuf(){
        this.istAufgedeckt = true;
    }


    public boolean gebeIstMarkiertZustand(){
        return this.istMarkiert;
    }

    public void wechselMarkierung(){
        if (this.gebeIstMarkiertZustand() == false) {
            this.istMarkiert = true;
        }else{
            this.istMarkiert = false;
        }
    }

    public int gebeAnzahlAngrenzenderMinen(){
        return this.angrenzendeMinen;
    }

    public void zaehleAngerenzendeMinenHochUmEins(){
        this.angrenzendeMinen += 1;
    }
}
    
