## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

##Diagramm (Plantuml-Version):

```plantuml
@startuml
' --- Configuración para un look más limpio ---
skinparam classAttributeIconSize 0
skinparam packageStyle rectangle

' --- Paquete del MODELO ---
package "Modell" {
    
    interface Beobachtbar {
        + hinzufuegen(Beobachter)
        + entfernen(Beobachter)
        + benachrichtigen()
    }
    
    class Spiel implements Beobachtbar {
        - status: SpielStatus
        - verbleibendeMinen: int
        - timer: Timer
        - brett: Brett
        + starten()
        + aufdecken(x,y)
        + markierungUmschalten(x,y)
        + siegUberpruefen()
    }

    class Brett {
        - zellen: Zelle[][]
        - zeilen: int
        - spalten: int
        - minen: int
        + initialisieren()
        + minenPlatzieren()
        + zelleHolen(x,y)
    }

    class Zelle {
        - isMine: boolean
        - istAufgedeckt: boolean
        - istMarkiert: boolean
        - angrenzendeMinen: int
        - position: Position
        + aufdecken()
        + markierungUmschalten()
    }

    class Timer {
        - startZeit: long
        - lauf: boolean
        + starten()
        + stoppen()
        + getVerstricheneZeit()
    }
    
    class Position {
        - zeile: int
        - spalte: int
    }

    class HighScoreManager {
        - highScores: HighScore[]
        + ladeScores(): void
        + speichereScore(String, float): void
    }
    
    class HighScore {
        - name: String
        - score: float
    }
    
    enum Schwierigkeitsgrad {
        EASY
        MEDIUM
        HARD
    }
    
    enum SpielStatus {
        SPIELEND
        GEWONNEN
        VERLOREN
    }
}

' --- Paquete del CONTROLADOR ---
package "Kontroller" {
    class SpielSteuerung {
        - spiel: Spiel
        - view: MinesweeperView
        + clicLink(x,y)
        + clicRecht(x,y)
        + neuesSpiel(Schwierigkeitsgrad)
    }
}

' --- Paquete de la VISTA ---
package "Aussehen" {

    interface Beobachter {
        + aktualisieren()
    }

    abstract class MinesweeperView implements Beobachter {
        + {abstract} bekommeEingabe(): void
        + {abstract} gebeAus(): void
    }

    class MinesweeperGUIView extends MinesweeperView {
        - spielFenster: SpielFenster
        + aktualisieren()
        + bekommeEingabe()
        + gebeAus()
    }

    
    class SpielFenster {
        - brettPanel: BrettPanel
        - infoPanel: InfoPanel
        + uiInitialisieren()
    }
    
    class BrettPanel {
        - zellenButtons: ZellenButton[][]
        + aktualisieren()
    }
    
    class InfoPanel {
        - minesLabel: JLabel
        - zeitLabel: JLabel
    }
    
    class ZellenButton {
        - zelle: Zelle
    }
}

' --- RELACIONES ENTRE PAQUETES Y CLASES ---

' Relaciones del Modelo
Spiel "1" *-- "1" Brett
Spiel "1" *-- "1" Timer
Brett "1" *-- "n" Zelle
Zelle "1" -- "1" Position
Spiel --> HighScoreManager
HighScoreManager "1" *-- "n" HighScore

' Relaciones del Controlador
SpielSteuerung ..> Spiel : benutz
SpielSteuerung ..> MinesweeperView : benutz

' Relaciones de la Vista
MinesweeperGUIView "1" *-- "1" SpielFenster
SpielFenster "1" *-- "1" BrettPanel
SpielFenster "1" *-- "1" InfoPanel
BrettPanel "1" *-- "n" ZellenButton
ZellenButton ..> Zelle : reprasentiert

' Relaciones del Patrón Observador
Spiel ..> MinesweeperView : benachrichtigen
@enduml
```



## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
