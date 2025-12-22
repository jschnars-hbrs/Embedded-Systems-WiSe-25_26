public class MinesweeperTerminal extends MinesweeperUI {

    public MinesweeperTerminal() {
        super();
    }
   

    @Override
    public void bekommeEingabe() {
        System.out.println("");
        String eingabe = System.console().readLine();

    }

    @Override
    public void gebeAus() {
        
    }
    
}
