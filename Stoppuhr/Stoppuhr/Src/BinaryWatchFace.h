#pragma once

#include "ScreenGraphic.h"
#include "StopUhr.h"
#include "WatchFace.h" // Passe diesen Include an den tatsächlichen Dateinamen an (z.B. AbstractWatchFace.h)

class BinaryWatchFace : public WatchFace
{
public:
    // Konstruktor: Erwartet einen Zeiger auf die StopUhr-Logik (Dependency Injection)
    BinaryWatchFace(StopUhr* stopwatch);

    // Implementierung der abstrakten Methode aus WatchFace
    void display(ScreenGraphic* dev) override;

    // Button-Handler (muss implementiert werden, auch wenn leer)
    // Passe die Signatur an, falls das Interface anders definiert ist
    // T* handleButtons(Button* btn1, Button* btn2, Button* btn3) override;

private:
    StopUhr* stopwatch;
    bool firstRun {true};

    // Private Hilfsmethoden zum Zeichnen
    void drawFilledCircle(ScreenGraphic* dev, int cx, int cy, int radius, WORD color);
    void drawCircleOutline(ScreenGraphic* dev, int cx, int cy, int radius, WORD color);
    
    // Zeichnet eine vertikale Bit-Spalte (für eine Dezimalziffer)
    void drawBinaryDigit(ScreenGraphic* dev, int x, int yBase, int digit, int rows, int radius, int gap);
};
