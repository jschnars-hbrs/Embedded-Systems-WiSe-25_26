public class Main {
    public static void main(String[] args) {
        MinesweeperSpielLogik logik = new MinesweeperSpielLogik();
        MinesweeperUI ui = new MinesweeperGUI(logik);
        ui.starten();
    }
}
