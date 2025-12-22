public class Main {
 
  public static void main(String[] args) {
    Minenfeld Testfeld = new Minenfeld();
    if(Testfeld.generiere(5, 10, 7)){
      System.out.println("Minefeld erfolgreich erstellt.");
    }else{
      System.out.println("Fehlgeschlagen Minenfeld zu erstellen");
    }
  }
}
