
import javax.swing.*;

import HighScoreManager;
import MinesweeperSpielLogik;
import MinesweeperUI;
import Schwierigkeit;
import Score;
import Zelle;

import java.awt.*;
import java.awt.event.*;

/**
 * Grafische Benutzeroberfläche für Minesweeper.
 * Erbt von MinesweeperUI und implementiert die abstrakten Methoden.
 * 
 * Maus-Interaktion:
 * - Linksklick: Feld aufdecken
 * - Rechtsklick: Flagge setzen/entfernen
 */
public class MinesweeperGUI_old_ohne_textures extends MinesweeperUI {

    // ==================== KONSTANTEN FÜR EINFACHE ANPASSUNG ====================
    
    /** Größe einer Zelle in Pixeln */
    private static final int ZELLEN_GROESSE = 32;
    
    /** Schriftgröße für Zahlen in den Zellen */
    private static final int SCHRIFT_GROESSE = 18;
    
    /** Timer-Intervall für Aktualisierung in Millisekunden */
    private static final int TIMER_INTERVALL = 100;
    
    // ==================== FARBEN - HIER KANNST DU SPÄTER TEXTUREN EINFÜGEN ====================
    
    /** Farbe für verdeckte Zellen */
    private static final Color FARBE_VERDECKT = new Color(170, 170, 170);
    
    /** Farbe für verdeckte Zellen (Hover-Effekt) */
    private static final Color FARBE_VERDECKT_HOVER = new Color(190, 190, 190);
    
    /** Farbe für aufgedeckte Zellen */
    private static final Color FARBE_AUFGEDECKT = new Color(220, 220, 220);
    
    /** Farbe für Minen */
    private static final Color FARBE_MINE = new Color(255, 80, 80);
    
    /** Farbe für Flaggen */
    private static final Color FARBE_FLAGGE = new Color(255, 200, 0);
    
    /** Rahmenfarbe */
    private static final Color FARBE_RAHMEN = new Color(128, 128, 128);
    
    /** Farben für die Zahlen 1-8 */
    private static final Color[] ZAHLEN_FARBEN = {
        null,                           // 0 - wird nicht angezeigt
        new Color(0, 0, 255),           // 1 - Blau
        new Color(0, 128, 0),           // 2 - Grün
        new Color(255, 0, 0),           // 3 - Rot
        new Color(0, 0, 128),           // 4 - Dunkelblau
        new Color(128, 0, 0),           // 5 - Dunkelrot
        new Color(0, 128, 128),         // 6 - Türkis
        new Color(0, 0, 0),             // 7 - Schwarz
        new Color(128, 128, 128)        // 8 - Grau
    };
    
    // ==================== GUI KOMPONENTEN ====================
    
    private JFrame frame;
    private SpielPanel spielPanel;
    private JLabel zeitLabel;
    private JLabel flaggenLabel;
    private JLabel statusLabel;
    private JButton neuesSpielButton;
    private JButton pauseButton;
    
    private final HighScoreManager highScoreManager = new HighScoreManager();
    
    // ==================== SPIELZUSTAND ====================
    
    private Schwierigkeit aktuellesSchwierigkeit;
    private javax.swing.Timer updateTimer;
    private volatile boolean warteAufEingabe = false;
    private volatile boolean eingabeErhalten = false;
    private volatile boolean spielBeendet = false;
    private int hoverZeile = -1;
    private int hoverSpalte = -1;

    // ==================== KONSTRUKTOR ====================
    
    public MinesweeperGUI_old_ohne_textures(MinesweeperSpielLogik spielLogik) {
        super(spielLogik);
    }

    // ==================== ÜBERSCHRIEBENE METHODEN VON MinesweeperUI ====================

    @Override
    protected Schwierigkeit waehleSchwierigkeit() {
        // Dialog zur Schwierigkeitsauswahl
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
        // GUI initialisieren falls noch nicht geschehen
        if (frame == null) {
            initialisiereGUI(level);
        }
        
        aktuellesSchwierigkeit = level;
        
        // Labels aktualisieren
        aktualisiereAnzeige();
        
        // Spielfeld neu zeichnen
        if (spielPanel != null) {
            spielPanel.repaint();
        }
    }

    @Override
    protected boolean bekommeEingabe(Schwierigkeit level) {
        // Warte auf Mausklick
        warteAufEingabe = true;
        eingabeErhalten = false;
        
        // Warte bis eine Eingabe erfolgt ist
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
        // Timer stoppen
        if (updateTimer != null) {
            updateTimer.stop();
        }
        
        statusLabel.setText("🎉 GEWONNEN!");
        statusLabel.setForeground(new Color(0, 150, 0));
        
        // Highscore-Dialog
        handleHighscoreNachSieg(level);
    }
    
    // ==================== GUI INITIALISIERUNG ====================

    private void initialisiereGUI(Schwierigkeit level) {
        // Frame erstellen
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(5, 5));
        
        // Oberes Panel mit Infos
        JPanel infoPanel = erstelleInfoPanel();
        frame.add(infoPanel, BorderLayout.NORTH);
        
        // Spielfeld-Panel
        spielPanel = new SpielPanel(level);
        frame.add(spielPanel, BorderLayout.CENTER);
        
        // Unteres Panel mit Buttons
        JPanel buttonPanel = erstelleButtonPanel();
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        // Frame anpassen und anzeigen
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Update-Timer starten
        starteUpdateTimer();
        
        // Window-Listener für Schließen
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
        
        // Zeit-Anzeige
        zeitLabel = new JLabel("⏱ Zeit: 0s", SwingConstants.CENTER);
        zeitLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        zeitLabel.setForeground(Color.WHITE);
        panel.add(zeitLabel);
        
        // Status-Anzeige
        statusLabel = new JLabel("Spiel läuft...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        panel.add(statusLabel);
        
        // Flaggen-Anzeige
        flaggenLabel = new JLabel("🚩 0/0", SwingConstants.CENTER);
        flaggenLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        flaggenLabel.setForeground(Color.WHITE);
        panel.add(flaggenLabel);
        
        return panel;
    }
    
    private JPanel erstelleButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(70, 70, 70));
        
        // Neues Spiel Button
        neuesSpielButton = new JButton("🔄 Neues Spiel");
        neuesSpielButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        neuesSpielButton.setFocusPainted(false);
        neuesSpielButton.addActionListener(e -> starteNeuesSpiel());
        panel.add(neuesSpielButton);
        
        // Pause Button
        pauseButton = new JButton("⏸ Pause");
        pauseButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        pauseButton.setFocusPainted(false);
        pauseButton.addActionListener(e -> togglePause());
        panel.add(pauseButton);
        
        // Highscores Button
        JButton highscoreButton = new JButton("🏆 Highscores");
        highscoreButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        highscoreButton.setFocusPainted(false);
        highscoreButton.addActionListener(e -> zeigeHighscores());
        panel.add(highscoreButton);
        
        return panel;
    }
    
    // ==================== SPIELFELD PANEL ====================
    
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
            
            // Maus-Listener hinzufügen
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
            
            // Maus-Bewegung für Hover-Effekt
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
            
            // Wenn pausiert, zeige Pause-Bildschirm
            if (spielLogik.istPausiert()) {
                zeichnePauseScreen(g2d);
                return;
            }
            
            // Alle Zellen zeichnen
            for (int zeile = 0; zeile < zeilen; zeile++) {
                for (int spalte = 0; spalte < spalten; spalte++) {
                    zeichneZelle(g2d, zeile, spalte);
                }
            }
        }
        
        private void zeichneZelle(Graphics2D g2d, int zeile, int spalte) {
            int x = spalte * ZELLEN_GROESSE;
            int y = zeile * ZELLEN_GROESSE;
            int groesse = ZELLEN_GROESSE - 2; // 2 Pixel Abstand zwischen Zellen
            
            Zelle zelle = spielLogik.gebeFeld(zeile, spalte);
            
            // Hintergrundfarbe bestimmen
            Color hintergrund;
            if (zelle.gebeIstAufgedecktZustand()) {
                if (zelle.gebeIstMineZustand()) {
                    hintergrund = FARBE_MINE;
                } else {
                    hintergrund = FARBE_AUFGEDECKT;
                }
            } else {
                // Hover-Effekt für verdeckte Zellen
                if (zeile == hoverZeile && spalte == hoverSpalte) {
                    hintergrund = FARBE_VERDECKT_HOVER;
                } else {
                    hintergrund = FARBE_VERDECKT;
                }
            }
            
            // Zelle zeichnen
            g2d.setColor(hintergrund);
            g2d.fillRoundRect(x + 1, y + 1, groesse, groesse, 4, 4);
            
            // Rahmen zeichnen
            g2d.setColor(FARBE_RAHMEN);
            g2d.drawRoundRect(x + 1, y + 1, groesse, groesse, 4, 4);
            
            // Inhalt zeichnen
            if (zelle.gebeIstAufgedecktZustand()) {
                if (zelle.gebeIstMineZustand()) {
                    // ==================== HIER KANNST DU SPÄTER EINE BOMBEN-TEXTUR EINFÜGEN ====================
                    zeichneMine(g2d, x, y, groesse);
                } else {
                    int anzahl = zelle.gebeAnzahlAngrenzenderMinen();
                    if (anzahl > 0) {
                        // ==================== HIER KANNST DU SPÄTER ZAHLEN-TEXTUREN EINFÜGEN ====================
                        zeichneZahl(g2d, x, y, groesse, anzahl);
                    }
                    // Leere Zelle - nichts zeichnen
                }
            } else if (zelle.gebeIstMarkiertZustand()) {
                // ==================== HIER KANNST DU SPÄTER EINE FLAGGEN-TEXTUR EINFÜGEN ====================
                zeichneFlagge(g2d, x, y, groesse);
            } else {
                // ==================== HIER KANNST DU SPÄTER EINE VERDECKT-TEXTUR EINFÜGEN ====================
                // Verdeckte Zelle - 3D-Effekt
                zeichneVerdeckteZelle(g2d, x, y, groesse);
            }
        }
        
        // ==================== ZEICHENMETHODEN - HIER TEXTUREN ERSETZEN ====================
        
        /**
         * Zeichnet eine Mine. 
         * HIER KANNST DU SPÄTER EIN BILD LADEN UND ZEICHNEN:
         * Image bombenBild = ...;
         * g2d.drawImage(bombenBild, x+1, y+1, groesse, groesse, null);
         */
        private void zeichneMine(Graphics2D g2d, int x, int y, int groesse) {
            int zentrum = groesse / 2;
            int radius = groesse / 4;
            
            g2d.setColor(Color.BLACK);
            g2d.fillOval(x + zentrum - radius + 1, y + zentrum - radius + 1, radius * 2, radius * 2);
            
            // Stacheln
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
            
            // Glanzpunkt
            g2d.setColor(Color.WHITE);
            g2d.fillOval(cx - radius / 2, cy - radius / 2, 4, 4);
        }
        
        /**
         * Zeichnet eine Zahl.
         * HIER KANNST DU SPÄTER EIN BILD FÜR JEDE ZAHL LADEN.
         */
        private void zeichneZahl(Graphics2D g2d, int x, int y, int groesse, int zahl) {
            g2d.setFont(new Font("SansSerif", Font.BOLD, SCHRIFT_GROESSE));
            g2d.setColor(ZAHLEN_FARBEN[zahl]);
            
            String text = String.valueOf(zahl);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (groesse - fm.stringWidth(text)) / 2 + 1;
            int textY = y + (groesse + fm.getAscent() - fm.getDescent()) / 2 + 1;
            
            g2d.drawString(text, textX, textY);
        }
        
        /**
         * Zeichnet eine Flagge.
         * HIER KANNST DU SPÄTER EIN FLAGGEN-BILD LADEN.
         */
        private void zeichneFlagge(Graphics2D g2d, int x, int y, int groesse) {
            int flaggenX = x + groesse / 4 + 1;
            int flaggenY = y + groesse / 5 + 1;
            int flaggenBreite = groesse / 2;
            int flaggenHoehe = groesse / 3;
            
            // Fahnenstange
            g2d.setColor(new Color(80, 50, 20));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(flaggenX, flaggenY, flaggenX, y + groesse - 5);
            
            // Flagge
            int[] xPunkte = {flaggenX, flaggenX + flaggenBreite, flaggenX};
            int[] yPunkte = {flaggenY, flaggenY + flaggenHoehe / 2, flaggenY + flaggenHoehe};
            g2d.setColor(FARBE_FLAGGE);
            g2d.fillPolygon(xPunkte, yPunkte, 3);
            g2d.setColor(new Color(200, 150, 0));
            g2d.drawPolygon(xPunkte, yPunkte, 3);
        }
        
        /**
         * Zeichnet eine verdeckte Zelle mit 3D-Effekt.
         * HIER KANNST DU SPÄTER EIN BILD FÜR VERDECKTE ZELLEN LADEN.
         */
        private void zeichneVerdeckteZelle(Graphics2D g2d, int x, int y, int groesse) {
            // 3D-Effekt: helle Kante oben/links, dunkle Kante unten/rechts
            g2d.setColor(new Color(210, 210, 210));
            g2d.drawLine(x + 2, y + 2, x + groesse - 1, y + 2);
            g2d.drawLine(x + 2, y + 2, x + 2, y + groesse - 1);
            
            g2d.setColor(new Color(100, 100, 100));
            g2d.drawLine(x + groesse - 1, y + 2, x + groesse - 1, y + groesse - 1);
            g2d.drawLine(x + 2, y + groesse - 1, x + groesse - 1, y + groesse - 1);
        }
        
        private void zeichnePauseScreen(Graphics2D g2d) {
            // Halbtransparenter Hintergrund
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Pause-Text
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
    
    // ==================== MAUS-INTERAKTION ====================
    
    private void handleMausKlick(MouseEvent e) {
        if (!warteAufEingabe || spielLogik.istPausiert()) {
            return;
        }
        
        int spalte = e.getX() / ZELLEN_GROESSE;
        int zeile = e.getY() / ZELLEN_GROESSE;
        
        // Prüfen ob Koordinaten im Feld liegen
        if (!istImFeld(zeile, spalte)) {
            return;
        }
        
        // Linksklick = Aufdecken, Rechtsklick = Flagge
        if (SwingUtilities.isLeftMouseButton(e)) {
            spielLogik.aufdecken(zeile, spalte);
            eingabeErhalten = true;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            spielLogik.wechselMarkierung(zeile, spalte);
            eingabeErhalten = true;
        }
        
        // Anzeige aktualisieren
        aktualisiereAnzeige();
        spielPanel.repaint();
    }
    
    private boolean istImFeld(int zeile, int spalte) {
        if (aktuellesSchwierigkeit == null) return false;
        return zeile >= 0 && zeile < aktuellesSchwierigkeit.getZeilen() 
            && spalte >= 0 && spalte < aktuellesSchwierigkeit.getSpalten();
    }
    
    // ==================== AKTUALISIERUNG ====================
    
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
        
        // Zeit aktualisieren
        zeitLabel.setText("⏱ Zeit: " + spielLogik.gebeScore() + "s");
        
        // Flaggen zählen
        int flaggenZaehler = 0;
        if (aktuellesSchwierigkeit != null) {
            for (int r = 0; r < aktuellesSchwierigkeit.getZeilen(); r++) {
                for (int c = 0; c < aktuellesSchwierigkeit.getSpalten(); c++) {
                    if (spielLogik.gebeFeld(r, c).gebeIstMarkiertZustand()) {
                        flaggenZaehler++;
                    }
                }
            }
            flaggenLabel.setText("🚩 " + flaggenZaehler + "/" + aktuellesSchwierigkeit.getMinen());
        }
        
        // Status aktualisieren
        if (spielLogik.istPausiert()) {
            statusLabel.setText("⏸ PAUSIERT");
            statusLabel.setForeground(Color.YELLOW);
            pauseButton.setText("▶ Fortsetzen");
        } else if (spielLogik.istVerloren()) {
            statusLabel.setText("💥 VERLOREN!");
            statusLabel.setForeground(Color.RED);
            if (updateTimer != null) updateTimer.stop();
        } else if (spielLogik.istGewonnen()) {
            statusLabel.setText("🎉 GEWONNEN!");
            statusLabel.setForeground(new Color(0, 200, 0));
            if (updateTimer != null) updateTimer.stop();
        } else {
            statusLabel.setText("Spiel läuft...");
            statusLabel.setForeground(Color.WHITE);
            pauseButton.setText("⏸ Pause");
        }
    }
    
    // ==================== BUTTON-AKTIONEN ====================
    
    private void togglePause() {
        if (!spielLogik.laeuftNoch()) return;
        
        spielLogik.togglePause();
        aktualisiereAnzeige();
        spielPanel.repaint();
        
        // Eingabe signalisieren damit die Spielschleife weiterläuft
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
            
            // Fenster schließen und neu starten
            if (updateTimer != null) updateTimer.stop();
            frame.dispose();
            frame = null;
            spielPanel = null;
            
            // Neues Spiel in neuem Thread starten
            SwingUtilities.invokeLater(() -> {
                MinesweeperGUI neueGUI = new MinesweeperGUI(new MinesweeperSpielLogik());
                neueGUI.starten();
            });
        }
    }
    
    private void zeigeHighscores() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════\n");
        
        for (Schwierigkeit level : new Schwierigkeit[]{Schwierigkeit.LEICHT, Schwierigkeit.MITTEL, Schwierigkeit.SCHWER}) {
            sb.append("\n🏆 ").append(level).append("\n");
            sb.append("───────────────────────────────────\n");
            
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
        textArea.setBackground(new Color(40, 40, 40));
        textArea.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 400));
        
        JOptionPane.showMessageDialog(
            frame,
            scrollPane,
            "Highscores",
            JOptionPane.PLAIN_MESSAGE
        );
    }
    
    // ==================== HIGHSCORE NACH SIEG ====================
    
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
        
        // Highscores anzeigen
        zeigeHighscores();
    }
    
    // ==================== ÜBERSCHRIEBENE STARTEN-METHODE ====================
    
    /**
     * Überschreibt die starten()-Methode der Basisklasse für GUI-spezifisches Verhalten.
     */
    @Override
    public final void starten() {
        // GUI muss im Event Dispatch Thread laufen
        SwingUtilities.invokeLater(() -> {
            Schwierigkeit level = waehleSchwierigkeit();
            spielLogik.starten(level);
            spielBeendet = false;
            
            // GUI initialisieren
            gebeAus(level);
            
            // Spielschleife in separatem Thread
            new Thread(() -> {
                while (spielLogik.laeuftNoch() && !spielBeendet) {
                    SwingUtilities.invokeLater(() -> gebeAus(level));
                    
                    if (!bekommeEingabe(level)) {
                        break;
                    }
                }
                
                // Spielende
                SwingUtilities.invokeLater(() -> {
                    gebeAus(level);
                    
                    if (spielLogik.istGewonnen()) {
                        wennGewonnen(level);
                    } else if (spielLogik.istVerloren()) {
                        if (updateTimer != null) updateTimer.stop();
                        statusLabel.setText("💥 VERLOREN!");
                        statusLabel.setForeground(Color.RED);
                        
                        // Alle Minen aufdecken
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
                });
            }).start();
        });
    }
}