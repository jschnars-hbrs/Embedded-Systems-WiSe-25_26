
#include "BinaryWatchFace.h"
#include <stdio.h>

// Konstruktor: Initialisiert Timer und Grafik-Pointer
BinaryWatchFace::BinaryWatchFace(Timer_Mcu *timer, DisplayGraphic * dispGraphic, ScreenGraphic * lcd)
: myStoppWatch(timer)
{
    this->dispGraphic = dispGraphic;
    this->lcd = lcd;

    // Berechnung der Startpositionen für die Zentrierung
    int wScr = dispGraphic->getWidth();
    int hScr = dispGraphic->getHeight();
    
    int colW = 2 * radius; 
    int scaleWidth = 30; // Platz für Skala links
    int totalBlockWidth = (6 * colW) + (3 * colGap) + (2 * groupGap) + scaleWidth;

    this->startX = (wScr / 2) - (totalBlockWidth / 2) + scaleWidth + radius;
    this->yBase = (hScr / 2) + 70;
}

// Wird aufgerufen, wenn zu diesem WatchFace gewechselt wird
void BinaryWatchFace::changed_to() {
    dispGraphic->setBackColor(this->colorBlack);
    dispGraphic->clear();
    
    // Statische Elemente (Text, Skala) neu zeichnen
    drawStaticLayout();
    lcd->refresh();
}

// Hauptschleife: Aktualisiert Zeit und Anzeige
void BinaryWatchFace::update() {
    // Zeit holen
    int timeMs = myStoppWatch.getPassedTime();
    
    uint32_t ms   = (timeMs % 1000) / 10; // 00-99
    uint32_t sec  = (timeMs / 1000) % 60;
    uint32_t min  = (timeMs / 60000); 

    // Ziffern berechnen
    int minDec = (min / 10) % 10;
    int minUni = min % 10;
    int secDec = sec / 10;
    int secUni = sec % 10;
    int msDec  = ms / 10;
    int msUni  = ms % 10;

    // X-Positionen berechnen
    int colW = 2 * radius;
    int xMin1 = startX;
    int xMin0 = xMin1 + colW + colGap;
    int xSec1 = xMin0 + colW + groupGap;
    int xSec0 = xSec1 + colW + colGap;
    int xMs1  = xSec0 + colW + groupGap;
    int xMs0  = xMs1 + colW + colGap;

    // Binärspalten zeichnen
    drawBinaryDigit(xMin1, yBase, minDec, 3, radius, gap);
    drawBinaryDigit(xMin0, yBase, minUni, 4, radius, gap);

    drawBinaryDigit(xSec1, yBase, secDec, 3, radius, gap);
    drawBinaryDigit(xSec0, yBase, secUni, 4, radius, gap);

    drawBinaryDigit(xMs1,  yBase, msDec,  4, radius, gap);
    drawBinaryDigit(xMs0,  yBase, msUni,  4, radius, gap);

    lcd->refresh();
}

// Button-Steuerung (identisch zur Logik der Partnerin)
Watchface::takeActionReturnValues BinaryWatchFace::handleButtons(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * button_user) {
    
    // Button 1: Start
    if(button1->getAction() == DigitalButton::ACTIVATED) {
        myStoppWatch.start();
        return NO_ACTION;
    }
    // Button 2: Stopp
    if(button2->getAction() == DigitalButton::ACTIVATED) {
        myStoppWatch.stop();
        return NO_ACTION;
    }
    // Button 3: Reset
    if(button3->getAction() == DigitalButton::ACTIVATED) {
        myStoppWatch.reset();
        changed_to(); // Erzwingt Neuzeichnen des Hintergrunds
        return NO_ACTION;
    }
    // User Button: Navigation
    if(button_user->getAction() == DigitalButton::ACTIVATED) {
        return NEXT_SCREEN;
    }
    if (button_user->getAction() == DigitalButton::LONG) {
        myStoppWatch.stop(); // Sicherheitshalber stoppen
        return PREVIOUS_SCREEN; // Achte auf Schreibweise im Enum (Screen vs Scrreen)
    }

    return NO_ACTION;
}

// --- Grafik-Hilfsmethoden ---

void BinaryWatchFace::drawStaticLayout() {
    // Farben setzen
    dispGraphic->setTextColor(this->colorWhite);
    
    // Header Position berechnen
    int yTop = yBase - (4 * (2 * radius + gap)) - 15;
    int colW = 2 * radius;
    
    // Header Text
    // Hinweis: Hier werden einfache Positionsschätzungen verwendet
    dispGraphic->gotoPixelPos(startX, yTop); dispGraphic->putString("MIN");
    dispGraphic->gotoPixelPos(startX + 2*colW + groupGap, yTop); dispGraphic->putString("SEC");
    dispGraphic->gotoPixelPos(startX + 4*colW + 2*groupGap, yTop); dispGraphic->putString("DEC");

    // Skala links (8, 4, 2, 1)
    int xScale = startX - radius - 30;
    for(int i=0; i<4; i++) {
        int yRow = yBase - i * (2 * radius + gap) - 10;
        char numBuf[2];
        sprintf(numBuf, "%d", 1 << i);
        dispGraphic->gotoPixelPos(xScale, yRow);
        dispGraphic->putString(numBuf);
    }
}

void BinaryWatchFace::drawBinaryDigit(int x, int yBase, int digit, int rows, int radius, int gap) {
    for (int i = 0; i < rows; i++) {
        int y = yBase - i * (2 * radius + gap);
        int bitValue = 1 << i;
        bool isOn = (digit & bitValue) != 0;

        if (isOn) {
            drawFilledCircle(x, y, radius, this->colorMagenta);
        } else {
            drawFilledCircle(x, y, radius, this->colorBlack); // Innen löschen
            drawCircleOutline(x, y, radius, this->colorWhite); // Rahmen
        }
    }
}

void BinaryWatchFace::drawFilledCircle(int cx, int cy, int radius, int color) {
    dispGraphic->setPaintColor(color);
    for(int y = -radius; y <= radius; y++) {
        for(int x = -radius; x <= radius; x++) {
            if(x*x + y*y <= radius*radius) {
                dispGraphic->putPixel(cx + x, cy + y);
            }
        }
    }
}

void BinaryWatchFace::drawCircleOutline(int cx, int cy, int radius, int color) {
    dispGraphic->setPaintColor(color);
    int x = 0; int y = radius; int d = 3 - 2 * radius;
    while (y >= x) {
        dispGraphic->putPixel(cx + x, cy + y); dispGraphic->putPixel(cx - x, cy + y);
        dispGraphic->putPixel(cx + x, cy - y); dispGraphic->putPixel(cx - x, cy - y);
        dispGraphic->putPixel(cx + y, cy + x); dispGraphic->putPixel(cx - y, cy + x);
        dispGraphic->putPixel(cx + y, cy - x); dispGraphic->putPixel(cx - y, cy - x);
        x++;
        if (d > 0) { y--; d = d + 4 * (x - y) + 10; } else { d = d + 4 * x + 6; }
    }
}
