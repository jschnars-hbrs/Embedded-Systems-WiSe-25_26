public enum Schwierigkeit {
    TEST(4, 4, 1),
    LEICHT(9, 9, 10),
    MITTEL(16, 16, 40),
    SCHWER(16, 30, 99);

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
