public class Main {
 
  public static void main(String[] args) {
    try {
         Minenfeld Testfeld = new Minenfeld(20, 20, 45);
  
    } catch (ArithmeticException e) {
      System.out.println("Es wurde versucht ein Spielfeld mit mehr Minen zu generieren als auf dem Spielfeld Platz haben.");
    }
   
  }
}
