public class Main {
    public static void main(String[] args) {
        MinesweeperSpielLogik logik = new MinesweeperSpielLogik();
        MinesweeperUI ui = new MinesweeperTerminal(logik);
        ui.starten();
    }
}
