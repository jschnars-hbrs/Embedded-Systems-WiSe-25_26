public enum Schwierigkeit {

    LEICHT(8, 8, 10),
    MITTEL(14, 14, 40),
    SCHWER(20, 20, 99);

    private int zeilen;
    private int spalten;
    private int minen;

    Schwierigkeit(int zeilen, int spalten, int minen) {
        this.zeilen = zeilen;
        this.spalten = spalten;
        this.minen = minen;
    }

    public int getZeilen() {
        return zeilen;
    }

    public int getSpalten() {
        return spalten;
    }

    public int getMinen() {
        return minen;
    }
}
