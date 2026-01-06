import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;


public class HighScoreManager {
    // Hier werden die Highscore-Objekte für jede Schwierigkeit gehalte
    // warscheinlich wäre es hier Sinnvoll die Objecte nur dann zu laden wenn sie
    // gebraucht werden
    private HighScore highScoreLeicht;
    private HighScore highScoreMittel;
    private HighScore highScoreSchwer;

    HighScoreManager() {
        // Initialisiere die Highscore-Objekte
        highScoreLeicht = new HighScore("scores/HighScores_Leicht.txt");
        highScoreMittel = new HighScore("scores/HighScores_Mittel.txt");
        highScoreSchwer = new HighScore("scores/HighScores_Schwer.txt");
        // theoretisch könnten die Highscore-Klassen auch mit IO Buffern übergeben
        // werden um später flexibler zu sein dies ist aber erstmal nicht vorgesehen

    }

    public Score[] ladeScore(Schwierigkeit Schwierigkeit) {
        // Lade die Highscores aus einer Datei basierend auf der Schwierigkeit
        switch (Schwierigkeit) {
            case LEICHT:
                // Lade highScoreLeicht
                return highScoreLeicht.ladeScore();

            case MITTEL:
                // Lade highScoreMittel
                return highScoreMittel.ladeScore();

            case SCHWER:
                // Lade highScoreSchwer
                return highScoreSchwer.ladeScore();

            default:
                return highScoreLeicht.ladeScore();
        }
    }

    public void updateScore(Schwierigkeit schwierigkeit, String playerName, float scoreValue) {
        // Aktualisiere die Highscore-Liste basierend auf der Schwierigkeit
        switch (schwierigkeit) {
            case LEICHT:
                highScoreLeicht.updateScore(playerName, scoreValue);
                break;

            case MITTEL:
                highScoreMittel.updateScore(playerName, scoreValue);
                break;

            case SCHWER:
                highScoreSchwer.updateScore(playerName, scoreValue);
                break;

            default:
                highScoreLeicht.updateScore(playerName, scoreValue);
                break;
        }
    }
}

class HighScore {
    private final int MAX_SCORES = 5;// Maximale Anzahl der Highscores sollte nicht zu groß sein um OutOfMemory zu
                                     // vermeiden. Das limmit liegt wahrscheinlich bei 500_000 Scores
    private Score[] highScores;
    private Path dateiPfad;

    HighScore(String dateiPfad) {
        this.dateiPfad = Path.of(dateiPfad);
        this.highScores = new Score[MAX_SCORES];
        ladeAusDatei();
    }

    private void ladeAusDatei() {
        // Lade die Highscores aus der Datei
        try {
            String fileContent = Files.readString(dateiPfad);
            String[] lines = fileContent.split("\n");// Teile den Inhalt in Zeilen auf gibt auch die ranfolge der Scores
                                                     // vor
            if (lines.length < MAX_SCORES) {
                // TODO Schreibe in den log das die Scores erweitert werden müssen
            } else if (lines.length > MAX_SCORES) {
                // TODO Schreibe in den log das die Scores gekürzt werden müssen
            }
            for (int i = 0; i < MAX_SCORES; i++) {
                String[] parts = lines[i].split(":");// Versuche die Zeile in Name und Score zu teilen
                if (parts.length != 2) {
                    // TODO Schreibe in den log das die Zeile i fehlerhaft ist
                    highScores[i].Name = "---";
                    highScores[i].Score = 0;
                } else {
                    try {
                        // Versuche den Score zu parsen
                        highScores[i].Name = parts[0];
                        highScores[i].Score = Float.parseFloat(parts[1]);
                    } catch (NumberFormatException e) {
                        // TODO Schreibe in den log das die Zeile i fehlerhaft ist
                        highScores[i].Name = "---";
                        highScores[i].Score = 0;
                    }
                }
            }

        } catch (IOException | OutOfMemoryError e) {
            for (int i = 0; i < MAX_SCORES; i++) {
                // Initialisiere mit Standardwerten, wenn die Datei nicht existiert oder ein
                // Fehler auftritt Fängt explizit nicht alle Fehler ab nur IO bezogene
                highScores[i].Name = "---";
                highScores[i].Score = 0;
            }
        }

    }

    private void speichereInDatei() {
        // Speichere die Highscores in der Datei

        if (dateiPfad.getParent() != null) {
            // Sicherstellen, dass das Parent-Verzeichnis existiert
            if (!Files.isDirectory(dateiPfad.getParent())) {
                try {
                    // Erstelle das Verzeichnis falls es nicht existiert
                    Files.createDirectories(dateiPfad.getParent());
                } catch (IOException e) {
                    // TODO Schreibe in den log das der Score Ordner nicht erstellt werden konnte
                }
            }
        }

        StringBuilder fileContent = new StringBuilder();
        for (int i = 0; i < MAX_SCORES; i++) {
            fileContent.append(highScores[i].Name).append(":").append(highScores[i].Score).append("\n");
        }
        try {
            Files.writeString(dateiPfad, fileContent.toString());
        } catch (IOException e) {
            // TODO Schreibe in den log das die Datei nicht gespeichert werden konnte
            // hier sollte keine outofmemory exception auftreten da das array auf max size
            // begrenzt ist
        }
    }

    public Score[] ladeScore() {
        // Gib eine kopie der Highscore-Liste zurück um ungewollte veränderungen zu
        // vermeiden
        // Hier könnte auch eine aktualisierung der Liste aus der Datei erfolgen
        return highScores.clone();
    }

    public void updateScore(String playerName, float scoreValue) {
        // Füge neuen Score hinzu und sortiere die Highscore-Liste
        for (int i = 0; i < MAX_SCORES; i++) {
            if (scoreValue < highScores[i].Score || highScores[i].Score == 0) {
                // Neuer Highscore gefunden, verschiebe die restlichen Scores nach unten
                for (int j = MAX_SCORES - 1; j > i; j--) {
                    highScores[j] = highScores[j - 1];
                }
                // Füge den neuen Score ein
                highScores[i] = new Score();
                highScores[i].Name = playerName;
                highScores[i].Score = scoreValue;
                break;
            }
        }
        // Speichere die aktualisierte Highscore-Liste in der Datei
        speichereInDatei();
    }
}