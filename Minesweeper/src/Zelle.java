//Autorin: Tabea Barteldrees
public class Zelle {

    private boolean istMine = false;
    private boolean istAufgedeckt = false;
    private boolean istMarkiert = false;
    private int angrenzendeMinen = 0;

    //Zugrifffunktionen von istMine
    public boolean gebeIstMineZustand(){
        return this.istMine;
    }

    public void setzeMine(){
        if (this.istMine == true){// Wenn bereits eine Mine platziert wurde, darf keine weitere platziert werden.
            throw new ArithmeticException("Auf dem Feld befindet sich bereits eine Mine.");
        }
        this.istMine = true;
    }

    //Zugrifffunktionen von istAufgedeckt
    public boolean gebeIstAufgedecktZustand(){
        return this.istAufgedeckt;
    }

    public void deckeAuf(){
        if(this.istMarkiert == false){
            this.istAufgedeckt = true;
        }
    }

    //Zugrifffunktionen von istMarkiert
    public boolean gebeIstMarkiertZustand(){
        return this.istMarkiert;
    }

    public void wechselMarkierung(){
        if(!this.istAufgedeckt){    //Nur verdeckte Zellen dürfen markiert werden
            this.istMarkiert = !this.istMarkiert;
        }
    }

    //Zugrifffunktionen von angrenzendeMinen
    public int gebeAnzahlAngrenzenderMinen(){
        return this.angrenzendeMinen;
    }

    public void zaehleAngerenzendeMinenHochUmEins(){
        if(this.angrenzendeMinen >= 8)  //Es können nicht mehr als 8 Zellen mit Minen an einer Zelle angrenzen
        {
            throw new ArithmeticException("Die Anzahl angrenzender Minen kann nicht größer 8 sein.");
        }
            this.angrenzendeMinen += 1;
        
    }
}
    
