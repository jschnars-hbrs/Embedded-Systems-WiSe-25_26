public class Main {
    public static void main(String[] args) {
        MinesweeperSpielLogik logik = new MinesweeperSpielLogik();
        MinesweeperUI ui = new MinesweeperGUI(logik);               // Grafisches User-Interface
        //MinesweeperUI ui = new MinesweeperTerminal(logik);        // User Interface im Terminal
        ui.starten();
    }
}
