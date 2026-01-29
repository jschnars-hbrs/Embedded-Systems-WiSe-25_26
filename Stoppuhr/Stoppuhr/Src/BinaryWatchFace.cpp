#include "BinaryWatchFace.h"
#include <cstdio> 

// Farbdefinitionen (angepasst an EmbSysLib WORD Format)
#define COL_ON      (WORD)Color::Magenta    // Aktives Bit
#define COL_OFF     (WORD)Color::White      // Inaktives Bit (Rahmen)
#define COL_BG      (WORD)Color::Black      // Hintergrund
#define COL_TEXT    (WORD)Color::White      // Textfarbe

BinaryWatchFace::BinaryWatchFace(StopUhr* sw)
: stopwatch(sw)
{
}

// --- Hilfsmethoden ---

void BinaryWatchFace::drawFilledCircle(ScreenGraphic* dev, int cx, int cy, int radius, WORD color)
{
    // Einfache Iteration zum Füllen des Kreises
    for(int y = -radius; y <= radius; y++) {
        for(int x = -radius; x <= radius; x++) {
            if(x*x + y*y <= radius*radius) {
                dev->drawPixel((WORD)(cx + x), (WORD)(cy + y), color);
            }
        }
    }
}

void BinaryWatchFace::drawCircleOutline(ScreenGraphic* dev, int cx, int cy, int radius, WORD color)
{
    // Bresenham-Algorithmus für Kreisumrisse
    int x = 0;
    int y = radius;
    int d = 3 - 2 * radius;

    while (y >= x) {
        dev->drawPixel((WORD)(cx + x), (WORD)(cy + y), color); dev->drawPixel((WORD)(cx - x), (WORD)(cy + y), color);
        dev->drawPixel((WORD)(cx + x), (WORD)(cy - y), color); dev->drawPixel((WORD)(cx - x), (WORD)(cy - y), color);
        dev->drawPixel((WORD)(cx + y), (WORD)(cy + x), color); dev->drawPixel((WORD)(cx - y), (WORD)(cy + x), color);
        dev->drawPixel((WORD)(cx + y), (WORD)(cy - x), color); dev->drawPixel((WORD)(cx - y), (WORD)(cy - x), color);
        x++;
        if (d > 0) { y--; d = d + 4 * (x - y) + 10; }
        else       { d = d + 4 * x + 6; }
    }
}

void BinaryWatchFace::drawBinaryDigit(ScreenGraphic* dev, int x, int yBase, int digit, int rows, int radius, int gap)
{
    for (int i = 0; i < rows; i++) {
        // Y-Position berechnen (von unten nach oben)
        int y = yBase - i * (2 * radius + gap);
        int bitValue = 1 << i;
        bool isOn = (digit & bitValue) != 0;

        if (isOn) {
            drawFilledCircle(dev, x, y, radius, COL_ON);
        } else {
            // Erst Innenbereich löschen (Hintergrundfarbe), dann Rahmen zeichnen
            drawFilledCircle(dev, x, y, radius, COL_BG);
            drawCircleOutline(dev, x, y, radius, COL_OFF);
        }
    }
}

// --- Hauptlogik ---

void BinaryWatchFace::display(ScreenGraphic* dev)
{
    if (!dev || !stopwatch) return;

    // Zeit von der StopUhr-Logik abrufen (in Millisekunden)
    int timeMs = stopwatch->getPassedTime(); 

    // Geometrie-Konfiguration
    const int wScr = dev->getWidth();
    const int hScr = dev->getHeight();
    const int cx   = wScr / 2;
    const int cy   = hScr / 2;

    int radius   = 20;   
    int gap      = 10;   // Vertikaler Abstand
    int colGap   = 15;   // Abstand zwischen Spalten
    int groupGap = 50;   // Abstand zwischen Gruppen (Min/Sec)
    int scaleWidth = 30; // Platz für die Skala (8-4-2-1) links

    int colW = 2 * radius; 
    int clockWidth = (6 * colW) + (3 * colGap) + (2 * groupGap);
    int totalBlockWidth = clockWidth + scaleWidth;
    
    // Startposition X (zentriert unter Berücksichtigung der Skala)
    int startX = cx - (totalBlockWidth / 2) + scaleWidth + radius; 
    int yBase = cy + 70; 

    // Spaltenpositionen berechnen
    int xMin1 = startX;
    int xMin0 = xMin1 + colW + colGap;
    int xSec1 = xMin0 + colW + groupGap;
    int xSec0 = xSec1 + colW + colGap;
    int xMs1  = xSec0 + colW + groupGap;
    int xMs0  = xMs1 + colW + colGap;

    // Initiale Einrichtung (wird nur einmal ausgeführt)
    if (firstRun) {
        dev->setBackColor(COL_BG);
        dev->clear();
        
        // Schriftart setzen (Annahme: fontFont_16x24 ist global verfügbar)
        // dev->setFont(fontFont_16x24, 1); 
        dev->setTextColor(COL_TEXT);

        // Header zeichnen (MIN, SEC, DEC)
        int yTop = yBase - (4 * (2 * radius + gap)) - 15;
        dev->gotoPixelPos((WORD)(xMin1 + 5), (WORD)yTop); dev->putString("MIN");
        dev->gotoPixelPos((WORD)(xSec1 + 5), (WORD)yTop); dev->putString("SEC");
        dev->gotoPixelPos((WORD)(xMs1 + 5),  (WORD)yTop); dev->putString("DEC");

        // Skala zeichnen (1, 2, 4, 8) am linken Rand
        int xScale = xMin1 - radius - 35;
        for(int i=0; i<4; i++) {
            int yRow = yBase - i * (2 * radius + gap) - 10;
            char numBuf[2];
            sprintf(numBuf, "%d", 1 << i);
            dev->gotoPixelPos((WORD)xScale, (WORD)yRow);
            dev->putString(numBuf);
        }
        
        firstRun = false;
    }

    // Zeitberechnung (Aufteilung in Ziffern)
    uint32_t ms   = (timeMs % 1000) / 10; // Zehntelsekunden (00-99)
    uint32_t sec  = (timeMs / 1000) % 60;
    uint32_t min  = (timeMs / 60000); 

    int minDec = (min / 10) % 10;
    int minUni = min % 10;
    int secDec = sec / 10;
    int secUni = sec % 10;
    int msDec  = ms / 10;
    int msUni  = ms % 10;

    // Binäre Spalten zeichnen
    drawBinaryDigit(dev, xMin1, yBase, minDec, 3, radius, gap);
    drawBinaryDigit(dev, xMin0, yBase, minUni, 4, radius, gap);

    drawBinaryDigit(dev, xSec1, yBase, secDec, 3, radius, gap);
    drawBinaryDigit(dev, xSec0, yBase, secUni, 4, radius, gap);

    drawBinaryDigit(dev, xMs1,  yBase, msDec,  4, radius, gap);
    drawBinaryDigit(dev, xMs0,  yBase, msUni,  4, radius, gap);

    // Bildschirm aktualisieren
    dev->refresh();
}
