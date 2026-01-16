import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MinesweeperGUI extends MinesweeperUI {

    private static final int ZELLEN_GROESSE = 64;
    private static final int SCHRIFT_GROESSE = 36;
    private static final int TIMER_INTERVALL = 100;

    // =============================================================================
    // TEXTUR-PFADE - Hier einfach den Pfad zu deinen PNG-Dateien eintragen
    // Wenn ein Pfad null oder leer ist, wird die Standard-Grafik verwendet
    // =============================================================================
    private static final String TEXTUR_VERDECKT = "textures/verdeckt_normal_1.png";           // z.B. "textures/verdeckt.png"
    private static final String TEXTUR_VERDECKT_HOVER = "textures/verdeckt_hover1.png";     // z.B. "textures/verdeckt_hover.png"
    private static final String TEXTUR_AUFGEDECKT = "textures/aufgedeckt1.png";         // z.B. "textures/aufgedeckt.png"
    private static final String TEXTUR_MINE = "textures/mine_1.png";               // z.B. "textures/mine.png"
    private static final String TEXTUR_FLAGGE = "textures/flagge1.png";             // z.B. "textures/flagge.png"
    private static final String TEXTUR_ZAHL_1 = "textures/zahl_1_1.png";             // z.B. "textures/zahl_1.png"
    private static final String TEXTUR_ZAHL_2 = "textures/zahl_2_2.png";             // z.B. "textures/zahl_2.png"
    private static final String TEXTUR_ZAHL_3 = "textures/zahl_3_1.png";             // z.B. "textures/zahl_3.png"
    private static final String TEXTUR_ZAHL_4 = "textures/zahl_4_1.png";             // z.B. "textures/zahl_4.png"
    private static final String TEXTUR_ZAHL_5 = "textures/zahl_5_1.png";             // z.B. "textures/zahl_5.png"
    private static final String TEXTUR_ZAHL_6 = "textures/zahl_6_1.png";             // z.B. "textures/zahl_6.png"
    private static final String TEXTUR_ZAHL_7 = "textures/zahl_7_1.png";             // z.B. "textures/zahl_7.png"
    private static final String TEXTUR_ZAHL_8 = "textures/zahl_8_1.png";             // z.B. "textures/zahl_8.png"
    private static final String TEXTUR_HINTERGRUND = "textures/hintergrund_1.png";        // z.B. "textures/hintergrund.png"
    // =============================================================================

    private static final Color FARBE_VERDECKT = new Color(170, 170, 170);
    private static final Color FARBE_VERDECKT_HOVER = new Color(190, 190, 190);
    private static final Color FARBE_AUFGEDECKT = new Color(220, 220, 220);
    private static final Color FARBE_MINE = new Color(255, 80, 80);
    private static final Color FARBE_FLAGGE = new Color(255, 200, 0);
    private static final Color FARBE_RAHMEN = new Color(128, 128, 128);

    private static final Color[] ZAHLEN_FARBEN = {
        null,
        new Color(0, 0, 255),
        new Color(0, 128, 0),
        new Color(255, 0, 0),
        new Color(0, 0, 128),
        new Color(128, 0, 0),
        new Color(0, 128, 128),
        new Color(0, 0, 0),
        new Color(128, 128, 128)
    };

    private BufferedImage bildVerdeckt;
    private BufferedImage bildVerdecktHover;
    private BufferedImage bildAufgedeckt;
    private BufferedImage bildMine;
    private BufferedImage bildFlagge;
    private BufferedImage[] bildZahlen = new BufferedImage[9];
    private BufferedImage bildHintergrund;

    private static JFrame aktuelleInstanz = null;

    private JFrame frame;
    private SpielPanel spielPanel;
    private JLabel zeitLabel;
    private JLabel flaggenLabel;
    private JLabel statusLabel;
    private JButton pauseButton;

    private final HighScoreManager highScoreManager = new HighScoreManager();

    private Schwierigkeit aktuellesSchwierigkeit;
    private javax.swing.Timer updateTimer;
    private volatile boolean warteAufEingabe = false;
    private volatile boolean eingabeErhalten = false;
    private volatile boolean spielBeendet = false;
    private int hoverZeile = -1;
    private int hoverSpalte = -1;

    public MinesweeperGUI(MinesweeperSpielLogik spielLogik) {
        super(spielLogik);
        ladeTexturen();
    }

    private void ladeTexturen() {
        bildVerdeckt = ladeBild(TEXTUR_VERDECKT);
        bildVerdecktHover = ladeBild(TEXTUR_VERDECKT_HOVER);
        bildAufgedeckt = ladeBild(TEXTUR_AUFGEDECKT);
        bildMine = ladeBild(TEXTUR_MINE);
        bildFlagge = ladeBild(TEXTUR_FLAGGE);
        bildHintergrund = ladeBild(TEXTUR_HINTERGRUND);

        String[] zahlPfade = {null, TEXTUR_ZAHL_1, TEXTUR_ZAHL_2, TEXTUR_ZAHL_3, 
                              TEXTUR_ZAHL_4, TEXTUR_ZAHL_5, TEXTUR_ZAHL_6, 
                              TEXTUR_ZAHL_7, TEXTUR_ZAHL_8};
        for (int i = 1; i <= 8; i++) {
            bildZahlen[i] = ladeBild(zahlPfade[i]);
        }
    }

    private BufferedImage ladeBild(String pfad) {
        if (pfad == null || pfad.trim().isEmpty()) {
            return null;
        }
        try {
            return ImageIO.read(new File(pfad));
        } catch (Exception e) {
            System.out.println("Konnte Textur nicht laden: " + pfad);
            return null;
        }
    }

    @Override
    public void starten() {
        SwingUtilities.invokeLater(() -> {
            Schwierigkeit level = waehleSchwierigkeit();
            spielLogik.starten(level);
            spielBeendet = false;

            gebeAus(level);

            new Thread(() -> {
                while (spielLogik.laeuftNoch() && !spielBeendet) {
                    SwingUtilities.invokeLater(() -> gebeAus(aktuellesSchwierigkeit));

                    if (!bekommeEingabe(level)) {
                        break;
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    gebeAus(level);

                    if (spielLogik.istGewonnen()) {
                        wennGewonnen(level);
                    } else if (spielLogik.istVerloren()) {
                        zeigeVerloren(level);
                    }
                });
            }).start();
        });
    }

    @Override
    protected Schwierigkeit waehleSchwierigkeit() {
        String[] optionen = {"Leicht (8x8, 10 Minen)", "Mittel (14x14, 40 Minen)", "Schwer (20x20, 99 Minen)","Test (4x4, 1 Mine)"};

        int auswahl = JOptionPane.showOptionDialog(
            null,
            "Wähle die Schwierigkeit:",
            "Minesweeper - Schwierigkeit",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            optionen,
            optionen[0]
        );

        switch (auswahl) {
            case 0: return Schwierigkeit.LEICHT;
            case 1: return Schwierigkeit.MITTEL;
            case 2: return Schwierigkeit.SCHWER;
            case 3: return Schwierigkeit.TEST;
            default: return Schwierigkeit.LEICHT;
        }
    }

    @Override
    protected void gebeAus(Schwierigkeit level) {
        if (frame == null) {
            initialisiereGUI(level);
        }

        aktuellesSchwierigkeit = level;
        aktualisiereAnzeige();

        if (spielPanel != null) {
            spielPanel.repaint();
        }
    }

    @Override
    protected boolean bekommeEingabe(Schwierigkeit level) {
        warteAufEingabe = true;
        eingabeErhalten = false;

        while (!eingabeErhalten && !spielBeendet) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        warteAufEingabe = false;

        if (spielBeendet) {
            return false;
        }

        return spielLogik.laeuftNoch();
    }

    @Override
    protected void wennGewonnen(Schwierigkeit level) {
        if (updateTimer != null) {
            updateTimer.stop();
        }

        statusLabel.setText("GEWONNEN!");
        statusLabel.setForeground(new Color(0, 150, 0));

        handleHighscoreNachSieg(level);
    }

    private void zeigeVerloren(Schwierigkeit level) {
        if (updateTimer != null) {
            updateTimer.stop();
        }

        statusLabel.setText("VERLOREN!");
        statusLabel.setForeground(Color.RED);

        for (int r = 0; r < level.getZeilen(); r++) {
            for (int c = 0; c < level.getSpalten(); c++) {
                Zelle z = spielLogik.gebeFeld(r, c);
                if (z.gebeIstMineZustand()) {
                    z.deckeAuf();
                }
            }
        }
        spielPanel.repaint();

        JOptionPane.showMessageDialog(
            frame,
            "BOOM! Du hast eine Mine getroffen.\nZeit: " + spielLogik.gebeScore() + " Sekunden",
            "Verloren",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void initialisiereGUI(Schwierigkeit level) {
        if (aktuelleInstanz != null) {
            aktuelleInstanz.dispose();
        }

        frame = new JFrame("Minesweeper");
        aktuelleInstanz = frame;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(5, 5));

        JPanel infoPanel = erstelleInfoPanel();
        frame.add(infoPanel, BorderLayout.NORTH);

        spielPanel = new SpielPanel(level);
        frame.add(spielPanel, BorderLayout.CENTER);

        JPanel buttonPanel = erstelleButtonPanel();
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        starteUpdateTimer();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                spielBeendet = true;
                if (updateTimer != null) {
                    updateTimer.stop();
                }
            }
        });
    }

    private JPanel erstelleInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panel.setBackground(new Color(50, 50, 50));

        zeitLabel = new JLabel("Zeit: 0s", SwingConstants.CENTER);
        zeitLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        zeitLabel.setForeground(Color.WHITE);
        panel.add(zeitLabel);

        statusLabel = new JLabel("Spiel laeuft...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        panel.add(statusLabel);

        flaggenLabel = new JLabel("Flaggen: 0/0", SwingConstants.CENTER);
        flaggenLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        flaggenLabel.setForeground(Color.WHITE);
        panel.add(flaggenLabel);

        return panel;
    }

    private JPanel erstelleButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(70, 70, 70));

        JButton neuesSpielButton = new JButton("Neues Spiel");
        neuesSpielButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        neuesSpielButton.setFocusPainted(false);
        neuesSpielButton.addActionListener(e -> starteNeuesSpiel());
        panel.add(neuesSpielButton);

        pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        pauseButton.setFocusPainted(false);
        pauseButton.addActionListener(e -> togglePause());
        panel.add(pauseButton);

        JButton highscoreButton = new JButton("Highscores");
        highscoreButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        highscoreButton.setFocusPainted(false);
        highscoreButton.addActionListener(e -> zeigeHighscores());
        panel.add(highscoreButton);

        return panel;
    }

    private class SpielPanel extends JPanel {

        private final int zeilen;
        private final int spalten;

        public SpielPanel(Schwierigkeit level) {
            this.zeilen = level.getZeilen();
            this.spalten = level.getSpalten();

            int breite = spalten * ZELLEN_GROESSE;
            int hoehe = zeilen * ZELLEN_GROESSE;

            setPreferredSize(new Dimension(breite, hoehe));
            setBackground(FARBE_RAHMEN);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMausKlick(e);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverZeile = -1;
                    hoverSpalte = -1;
                    repaint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int neueSpalte = e.getX() / ZELLEN_GROESSE;
                    int neueZeile = e.getY() / ZELLEN_GROESSE;

                    if (neueZeile != hoverZeile || neueSpalte != hoverSpalte) {
                        hoverZeile = neueZeile;
                        hoverSpalte = neueSpalte;
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (bildHintergrund != null) {
                g2d.drawImage(bildHintergrund, 0, 0, getWidth(), getHeight(), null);
            }

            if (spielLogik.istPausiert()) {
                zeichnePauseScreen(g2d);
                return;
            }

            for (int zeile = 0; zeile < zeilen; zeile++) {
                for (int spalte = 0; spalte < spalten; spalte++) {
                    zeichneZelle(g2d, zeile, spalte);
                }
            }
        }

        private void zeichneZelle(Graphics2D g2d, int zeile, int spalte) {
            int x = spalte * ZELLEN_GROESSE;
            int y = zeile * ZELLEN_GROESSE;
            int groesse = ZELLEN_GROESSE - 2;

            Zelle zelle = spielLogik.gebeFeld(zeile, spalte);

            if (zelle.gebeIstAufgedecktZustand()) {
                if (zelle.gebeIstMineZustand()) {
                    if (bildMine != null) {
                        g2d.drawImage(bildMine, x + 1, y + 1, groesse, groesse, null);
                    } else {
                        zeichneHintergrund(g2d, x, y, groesse, FARBE_MINE);
                        zeichneMine(g2d, x, y, groesse);
                    }
                } else {
                    int anzahl = zelle.gebeAnzahlAngrenzenderMinen();
                    if (anzahl > 0 && bildZahlen[anzahl] != null) {
                        g2d.drawImage(bildZahlen[anzahl], x + 1, y + 1, groesse, groesse, null);
                    } else {
                        if (bildAufgedeckt != null) {
                            g2d.drawImage(bildAufgedeckt, x + 1, y + 1, groesse, groesse, null);
                        } else {
                            zeichneHintergrund(g2d, x, y, groesse, FARBE_AUFGEDECKT);
                        }
                        if (anzahl > 0) {
                            zeichneZahl(g2d, x, y, groesse, anzahl);
                        }
                    }
                }
            } else if (zelle.gebeIstMarkiertZustand()) {
                if (bildFlagge != null) {
                    g2d.drawImage(bildFlagge, x + 1, y + 1, groesse, groesse, null);
                } else {
                    zeichneHintergrund(g2d, x, y, groesse, FARBE_VERDECKT);
                    zeichneFlagge(g2d, x, y, groesse);
                }
            } else {
                boolean hover = (zeile == hoverZeile && spalte == hoverSpalte);
                if (hover && bildVerdecktHover != null) {
                    g2d.drawImage(bildVerdecktHover, x + 1, y + 1, groesse, groesse, null);
                } else if (!hover && bildVerdeckt != null) {
                    g2d.drawImage(bildVerdeckt, x + 1, y + 1, groesse, groesse, null);
                } else if (hover && bildVerdeckt != null) {
                    g2d.drawImage(bildVerdeckt, x + 1, y + 1, groesse, groesse, null);
                } else {
                    zeichneHintergrund(g2d, x, y, groesse, hover ? FARBE_VERDECKT_HOVER : FARBE_VERDECKT);
                    zeichneVerdeckteZelle(g2d, x, y, groesse);
                }
            }
        }

        private void zeichneHintergrund(Graphics2D g2d, int x, int y, int groesse, Color farbe) {
            g2d.setColor(farbe);
            g2d.fillRoundRect(x + 1, y + 1, groesse, groesse, 4, 4);
            g2d.setColor(FARBE_RAHMEN);
            g2d.drawRoundRect(x + 1, y + 1, groesse, groesse, 4, 4);
        }

        private void zeichneMine(Graphics2D g2d, int x, int y, int groesse) {
            int zentrum = groesse / 2;
            int radius = groesse / 4;

            g2d.setColor(Color.BLACK);
            g2d.fillOval(x + zentrum - radius + 1, y + zentrum - radius + 1, radius * 2, radius * 2);

            g2d.setStroke(new BasicStroke(2));
            int cx = x + zentrum + 1;
            int cy = y + zentrum + 1;
            for (int i = 0; i < 8; i++) {
                double winkel = i * Math.PI / 4;
                int x1 = (int) (cx + radius * Math.cos(winkel));
                int y1 = (int) (cy + radius * Math.sin(winkel));
                int x2 = (int) (cx + (radius + 4) * Math.cos(winkel));
                int y2 = (int) (cy + (radius + 4) * Math.sin(winkel));
                g2d.drawLine(x1, y1, x2, y2);
            }

            g2d.setColor(Color.WHITE);
            g2d.fillOval(cx - radius / 2, cy - radius / 2, 4, 4);
        }

        private void zeichneZahl(Graphics2D g2d, int x, int y, int groesse, int zahl) {
            g2d.setFont(new Font("SansSerif", Font.BOLD, SCHRIFT_GROESSE));
            g2d.setColor(ZAHLEN_FARBEN[zahl]);

            String text = String.valueOf(zahl);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (groesse - fm.stringWidth(text)) / 2 + 1;
            int textY = y + (groesse + fm.getAscent() - fm.getDescent()) / 2 + 1;

            g2d.drawString(text, textX, textY);
        }

        private void zeichneFlagge(Graphics2D g2d, int x, int y, int groesse) {
            int flaggenX = x + groesse / 4 + 1;
            int flaggenY = y + groesse / 5 + 1;
            int flaggenBreite = groesse / 2;
            int flaggenHoehe = groesse / 3;

            g2d.setColor(new Color(80, 50, 20));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(flaggenX, flaggenY, flaggenX, y + groesse - 5);

            int[] xPunkte = {flaggenX, flaggenX + flaggenBreite, flaggenX};
            int[] yPunkte = {flaggenY, flaggenY + flaggenHoehe / 2, flaggenY + flaggenHoehe};
            g2d.setColor(FARBE_FLAGGE);
            g2d.fillPolygon(xPunkte, yPunkte, 3);
            g2d.setColor(new Color(200, 150, 0));
            g2d.drawPolygon(xPunkte, yPunkte, 3);
        }

        private void zeichneVerdeckteZelle(Graphics2D g2d, int x, int y, int groesse) {
            g2d.setColor(new Color(210, 210, 210));
            g2d.drawLine(x + 2, y + 2, x + groesse - 1, y + 2);
            g2d.drawLine(x + 2, y + 2, x + 2, y + groesse - 1);

            g2d.setColor(new Color(100, 100, 100));
            g2d.drawLine(x + groesse - 1, y + 2, x + groesse - 1, y + groesse - 1);
            g2d.drawLine(x + 2, y + groesse - 1, x + groesse - 1, y + groesse - 1);
        }

        private void zeichnePauseScreen(Graphics2D g2d) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 36));
            String text = "PAUSIERT";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            int textY = getHeight() / 2;
            g2d.drawString(text, textX, textY);

            g2d.setFont(new Font("SansSerif", Font.PLAIN, 16));
            String hinweis = "Klicke auf 'Fortsetzen' um weiterzuspielen";
            fm = g2d.getFontMetrics();
            textX = (getWidth() - fm.stringWidth(hinweis)) / 2;
            g2d.drawString(hinweis, textX, textY + 40);
        }
    }

    private void handleMausKlick(MouseEvent e) {
        if (!warteAufEingabe || spielLogik.istPausiert()) {
            return;
        }

        int spalte = e.getX() / ZELLEN_GROESSE;
        int zeile = e.getY() / ZELLEN_GROESSE;

        if (!istImFeld(zeile, spalte)) {
            return;
        }

        Zelle zelle = spielLogik.gebeFeld(zeile, spalte);

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (!zelle.gebeIstMarkiertZustand() && !zelle.gebeIstAufgedecktZustand()) {
                spielLogik.aufdecken(zeile, spalte);
                eingabeErhalten = true;
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (!zelle.gebeIstAufgedecktZustand()) {
                spielLogik.wechselMarkierung(zeile, spalte);
                eingabeErhalten = true;
            }
        }

        aktualisiereAnzeige();
        spielPanel.repaint();
    }

    private boolean istImFeld(int zeile, int spalte) {
        if (aktuellesSchwierigkeit == null) return false;
        return zeile >= 0 && zeile < aktuellesSchwierigkeit.getZeilen()
            && spalte >= 0 && spalte < aktuellesSchwierigkeit.getSpalten();
    }

    private void starteUpdateTimer() {
        updateTimer = new javax.swing.Timer(TIMER_INTERVALL, e -> {
            if (spielLogik.laeuftNoch()) {
                aktualisiereAnzeige();
            }
        });
        updateTimer.start();
    }

    private void aktualisiereAnzeige() {
        if (zeitLabel == null || flaggenLabel == null || statusLabel == null) return;

        zeitLabel.setText("Zeit: " + spielLogik.gebeScore() + "s");

        int flaggenZaehler = 0;
        if (aktuellesSchwierigkeit != null) {
            for (int r = 0; r < aktuellesSchwierigkeit.getZeilen(); r++) {
                for (int c = 0; c < aktuellesSchwierigkeit.getSpalten(); c++) {
                    if (spielLogik.gebeFeld(r, c).gebeIstMarkiertZustand()) {
                        flaggenZaehler++;
                    }
                }
            }
            flaggenLabel.setText("Flaggen: " + flaggenZaehler + "/" + aktuellesSchwierigkeit.getMinen());
        }

        if (spielLogik.istPausiert()) {
            statusLabel.setText("PAUSIERT");
            statusLabel.setForeground(Color.YELLOW);
            pauseButton.setText("Fortsetzen");
        } else if (spielLogik.istVerloren()) {
            statusLabel.setText("VERLOREN!");
            statusLabel.setForeground(Color.RED);
            if (updateTimer != null) updateTimer.stop();
        } else if (spielLogik.istGewonnen()) {
            statusLabel.setText("GEWONNEN!");
            statusLabel.setForeground(new Color(0, 200, 0));
            if (updateTimer != null) updateTimer.stop();
        } else {
            statusLabel.setText("Spiel laeuft...");
            statusLabel.setForeground(Color.WHITE);
            pauseButton.setText("Pause");
        }
    }

    private void togglePause() {
        if (!spielLogik.laeuftNoch()) return;

        spielLogik.togglePause();
        aktualisiereAnzeige();
        spielPanel.repaint();

        if (warteAufEingabe) {
            eingabeErhalten = true;
        }
    }

    private void starteNeuesSpiel() {
        int antwort = JOptionPane.showConfirmDialog(
            frame,
            "Möchtest du wirklich ein neues Spiel starten?",
            "Neues Spiel",
            JOptionPane.YES_NO_OPTION
        );

        if (antwort == JOptionPane.YES_OPTION) {
            spielBeendet = true;
            eingabeErhalten = true;

            if (updateTimer != null) updateTimer.stop();
            frame.dispose();
            frame = null;
            spielPanel = null;

            SwingUtilities.invokeLater(() -> {
                MinesweeperGUI neueGUI = new MinesweeperGUI(new MinesweeperSpielLogik());
                neueGUI.starten();
            });
        }
    }

    private void zeigeHighscores() {
        StringBuilder sb = new StringBuilder();

        for (Schwierigkeit level : new Schwierigkeit[]{Schwierigkeit.LEICHT, Schwierigkeit.MITTEL, Schwierigkeit.SCHWER}) {
            sb.append("\n").append(level).append("\n");
            sb.append("----------------------------\n");

            Score[] scores = highScoreManager.ladeScore(level);
            for (int i = 0; i < scores.length; i++) {
                Score s = scores[i];
                String name = (s == null || s.Name == null || s.Name.trim().isEmpty()) ? "---" : s.Name;
                float value = (s == null) ? 0 : s.Score;
                sb.append(String.format("%d. %-15s %6.0fs\n", (i + 1), name, value));
            }
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 400));

        JOptionPane.showMessageDialog(
            frame,
            scrollPane,
            "Highscores",
            JOptionPane.PLAIN_MESSAGE
        );
    }

    private void handleHighscoreNachSieg(Schwierigkeit level) {
        float score = spielLogik.gebeScore();

        int antwort = JOptionPane.showConfirmDialog(
            frame,
            "Du hast gewonnen!\nZeit: " + score + " Sekunden\n\nMöchtest du deinen Score speichern?",

            "Gewonnen!",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (antwort == JOptionPane.YES_OPTION) {
            String name = JOptionPane.showInputDialog(
                frame,
                "Gib deinen Namen ein:",
                "Name eingeben",
                JOptionPane.PLAIN_MESSAGE
            );

            if (name != null && !name.trim().isEmpty()) {
                highScoreManager.updateScore(level, name.trim(), score);
                JOptionPane.showMessageDialog(
                    frame,
                    "Highscore gespeichert!",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }

        zeigeHighscores();
    }
}