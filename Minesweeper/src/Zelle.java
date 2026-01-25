//Autorin: Tabea Barteldrees
public class Zelle {

    private boolean istMine = false;
    private boolean istAufgedeckt = false;
    private boolean istMarkiert = false;
    private int angrenzendeMinen = 0;

    public boolean gebeIstMineZustand(){
        return this.istMine;
    }

    public void setzeMine(){
        if (this.istMine == true){
            throw new ArithmeticException("Auf dem Feld befindet sich bereits eine Mine.");
        }
        this.istMine = true;
    }


    public boolean gebeIstAufgedecktZustand(){
        return this.istAufgedeckt;
    }

    public void deckeAuf(){
        if(this.istMarkiert == false){
            this.istAufgedeckt = true;
        }
    }


    public boolean gebeIstMarkiertZustand(){
        return this.istMarkiert;
    }

    public void wechselMarkierung(){
        if(!this.istAufgedeckt){
            this.istMarkiert = !this.istMarkiert;
        }
    }

    public int gebeAnzahlAngrenzenderMinen(){
        return this.angrenzendeMinen;
    }

    public void zaehleAngerenzendeMinenHochUmEins(){
        if(this.angrenzendeMinen >= 8)
        {
            throw new ArithmeticException("Die Anzahl angrenzender Minen kann nicht größer 8 sein.");
        }
            this.angrenzendeMinen += 1;
        
    }
}
    
