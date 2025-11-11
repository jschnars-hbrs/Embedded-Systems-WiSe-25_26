public class Main {
 
  public static void main(String[] args) {
    try {
         Minenfeld Testfeld = new Minenfeld(10, 10, 101);
  
    } catch (ArithmeticException e) {
      System.out.println("Es wurde versucht ein Spielfeld mit mehr Minen zu generieren als auf dem Spielfeld Platz haben.");
    }
   
  }
}
