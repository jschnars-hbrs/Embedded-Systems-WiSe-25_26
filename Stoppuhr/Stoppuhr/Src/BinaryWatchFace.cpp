/*!
\file   BinaryWatchFace.cpp
*/
#include "BinaryWatchFace.h"
#include <stdio.h> // Wichtig für sprintf

BinaryWatchFace::BinaryWatchFace(Timer_Mcu *timer, DisplayGraphic * dispGraphic, ScreenGraphic * lcd)
: myStoppWatch(timer)
{
    this->dispGraphic = dispGraphic;
    this->lcd = lcd;

    // Berechnung der Startpositionen (Dynamisch basierend auf Radius)
    int wScr = dispGraphic->getWidth();
    int hScr = dispGraphic->getHeight();
    
    int colW = 2 * radius; 
    int scaleWidth = 25; // Platz für Skala links
    
    // Gesamtbreite berechnen: 6 Spalten + Lücken + Skala
    int totalBlockWidth = (6 * colW) + (3 * colGap) + (2 * groupGap) + scaleWidth;

    // Alles zentrieren
    this->startX = (wScr / 2) - (totalBlockWidth / 2) + scaleWidth + radius;
    // Etwas tiefer setzen, damit oben Platz für Text ist
    this->yBase = (hScr / 2) + 50; 
}

void BinaryWatchFace::changed_to() {
    dispGraphic->setBackColor(this->colorBlack);
    dispGraphic->clear();
    drawStaticLayout();
    lcd->refresh();
}

void BinaryWatchFace::update() {
    int timeMs = myStoppWatch.getPassedTime();
    
    uint32_t ms   = (timeMs % 1000) / 10; 
    uint32_t sec  = (timeMs / 1000) % 60;
    uint32_t min  = (timeMs / 60000); 

    int minDec = (min / 10) % 10; int minUni = min % 10;
    int secDec = sec / 10;        int secUni = sec % 10;
    int msDec  = ms / 10;         int msUni  = ms % 10;

    // X-Positionen berechnen
    int colW = 2 * radius;
    int xMin1 = startX;
    int xMin0 = xMin1 + colW + colGap;
    int xSec1 = xMin0 + colW + groupGap;
    int xSec0 = xSec1 + colW + colGap;
    int xMs1  = xSec0 + colW + groupGap;
    int xMs0  = xMs1 + colW + colGap;

    // 1. Binärspalten zeichnen
    drawBinaryDigit(xMin1, yBase, minDec, 3, radius, gap);
    drawBinaryDigit(xMin0, yBase, minUni, 4, radius, gap);

    drawBinaryDigit(xSec1, yBase, secDec, 3, radius, gap);
    drawBinaryDigit(xSec0, yBase, secUni, 4, radius, gap);

    drawBinaryDigit(xMs1,  yBase, msDec,  4, radius, gap);
    drawBinaryDigit(xMs0,  yBase, msUni,  4, radius, gap);

    // 2. Dezimalzahlen unten zeichnen (DAS FEHLTE VORHER)
    int yNum = yBase + radius + 10; // Position unter den Kreisen
    drawDigitNumber(xMin1, yNum, minDec);
    drawDigitNumber(xMin0, yNum, minUni);
    
    drawDigitNumber(xSec1, yNum, secDec);
    drawDigitNumber(xSec0, yNum, secUni);
    
    drawDigitNumber(xMs1, yNum, msDec);
    drawDigitNumber(xMs0, yNum, msUni);

    lcd->refresh();
}

Watchface::takeActionReturnValues BinaryWatchFace::handleButtons(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * button_user) {
    if(button1->getAction() == DigitalButton::ACTIVATED) {
        myStoppWatch.start();
        return NO_ACTION;
    }
    if(button2->getAction() == DigitalButton::ACTIVATED) {
        myStoppWatch.stop();
        return NO_ACTION;
    }
    if(button3->getAction() == DigitalButton::ACTIVATED) {
        myStoppWatch.reset();
        changed_to(); 
        return NO_ACTION;
    }
    if(button_user->getAction() == DigitalButton::ACTIVATED) {
        return NEXT_SCREEN;
    }
    if (button_user->getAction() == DigitalButton::LONG) {
        myStoppWatch.stop();
        return PREVIOUS_SCREEN;
    }
    return NO_ACTION;
}

// --- Grafik-Hilfsmethoden ---

void BinaryWatchFace::drawStaticLayout() {
    dispGraphic->setTextColor(this->colorWhite);
    
    int colW = 2 * radius;
    // Wir berechnen die Startpositionen der Gruppen exakt wie in update()
    int xMinStart = startX;
    int xSecStart = xMinStart + 2*colW + colGap + groupGap;
    int xMsStart  = xSecStart + 2*colW + colGap + groupGap;

    // Header Position (über den Kreisen)
    int yTop = yBase - (4 * (2 * radius + gap)) - 20;

    // Textausgabe (etwas versetzt, damit es mittig über der Gruppe steht)
    dispGraphic->gotoPixelPos(xMinStart, yTop); dispGraphic->putString("MIN");
    dispGraphic->gotoPixelPos(xSecStart, yTop); dispGraphic->putString("SEC");
    dispGraphic->gotoPixelPos(xMsStart,  yTop); dispGraphic->putString("DEC");

    // Skala links (8, 4, 2, 1)
    int xScale = startX - radius - 20;
    for(int i=0; i<4; i++) {
        int yRow = yBase - i * (2 * radius + gap) - 8;
        char numBuf[2];
        sprintf(numBuf, "%d", 1 << i);
        dispGraphic->gotoPixelPos(xScale, yRow);
        dispGraphic->putString(numBuf);
    }
}

// Neue Methode zum Zeichnen der Zahlen unter den Säulen
void BinaryWatchFace::drawDigitNumber(int x, int y, int number) {
    // Kleiner Trick: Hintergrund löschen durch Zeichnen eines schwarzen Rechtecks
    dispGraphic->setPaintColor(this->colorBlack);
    int rectSize = 12; // Größe des Löschbereichs
    for(int j=0; j<rectSize; j++) {
        for(int i=0; i<rectSize; i++) {
            dispGraphic->putPixel(x - 6 + i, y + j);
        }
    }
    
    // Zahl schreiben
    char buf[2];
    sprintf(buf, "%d", number);
    dispGraphic->setTextColor(this->colorWhite);
    dispGraphic->gotoPixelPos(x - 4, y); // Leicht zentrieren
    dispGraphic->putString(buf);
}

void BinaryWatchFace::drawBinaryDigit(int x, int yBase, int digit, int rows, int radius, int gap) {
    for (int i = 0; i < rows; i++) {
        int y = yBase - i * (2 * radius + gap);
        int bitValue = 1 << i;
        bool isOn = (digit & bitValue) != 0;

        if (isOn) {
            drawFilledCircle(x, y, radius, this->colorMagenta);
        } else {
            drawFilledCircle(x, y, radius, this->colorBlack); 
            drawCircleOutline(x, y, radius, this->colorWhite);
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
