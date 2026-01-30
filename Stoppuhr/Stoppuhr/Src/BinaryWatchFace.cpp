/*!
\file   BinaryWatchFace.cpp
*/
#include "BinaryWatchFace.h"
#include <stdio.h>

BinaryWatchFace::BinaryWatchFace(Timer_Mcu *timer, DisplayGraphic * dispGraphic, ScreenGraphic * lcd)
: myStoppWatch(timer)
{
    this->dispGraphic = dispGraphic;
    this->lcd = lcd;

    // Calculamos la geometría UNA VEZ, centrada
    int wScr = dispGraphic->getWidth();
    int hScr = dispGraphic->getHeight();

    int colW = 2 * radius;
    int scaleWidth = 30; // Espacio para la escala lateral

    // Ancho total del bloque
    int totalBlockWidth = (6 * colW) + (3 * colGap) + (2 * groupGap) + scaleWidth;

    // Centrado X e Y
    this->startX = (wScr / 2) - (totalBlockWidth / 2) + scaleWidth + radius;
    this->yBase = (hScr / 2) + 60; // Bajamos un poco la base para dejar espacio al título
}

void BinaryWatchFace::changed_to() {
    // === FIX CRÍTICO: RESETEAR ESTADO ===
    dispGraphic->setZoom(1);            // Arregla el problema de Tabea
    dispGraphic->setBackColor(colorBlack);
    dispGraphic->setTextColor(colorWhite);
    dispGraphic->setPaintColor(colorBlack); // Resetear paint color

    dispGraphic->clear();
    drawStaticLayout();
    lcd->refresh();
}

void BinaryWatchFace::update() {
    int timeMs = myStoppWatch.getPassedTime();

    // Desglose del tiempo
    uint32_t ms   = (timeMs % 1000) / 10;
    uint32_t sec  = (timeMs / 1000) % 60;
    uint32_t min  = (timeMs / 60000);

    int minDec = (min / 10) % 10; int minUni = min % 10;
    int secDec = sec / 10;        int secUni = sec % 10;
    int msDec  = ms / 10;         int msUni  = ms % 10;

    int colW = 2 * radius;

    // Recalcular posiciones X (deben coincidir con drawStaticLayout)
    int xMin1 = startX;
    int xMin0 = xMin1 + colW + colGap;

    int xSec1 = xMin0 + colW + groupGap;
    int xSec0 = xSec1 + colW + colGap;

    int xMs1  = xSec0 + colW + groupGap;
    int xMs0  = xMs1 + colW + colGap;

    // 1. Dibujar Bolitas
    drawBinaryDigit(xMin1, yBase, minDec, 3, radius, gap);
    drawBinaryDigit(xMin0, yBase, minUni, 4, radius, gap);

    drawBinaryDigit(xSec1, yBase, secDec, 3, radius, gap);
    drawBinaryDigit(xSec0, yBase, secUni, 4, radius, gap);

    drawBinaryDigit(xMs1,  yBase, msDec,  4, radius, gap);
    drawBinaryDigit(xMs0,  yBase, msUni,  4, radius, gap);

    // 2. Dibujar Números (Recuperado)
    int yNum = yBase + radius + 15;
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
        changed_to(); // Forzar redibujado limpio
        return NO_ACTION;
    }
    if(button_user->getAction() == DigitalButton::ACTIVATED) {
        return NEXT_SCREEN;
    }
    if (button_user->getAction() == DigitalButton::LONG) {
        myStoppWatch.stop();
        return PREVIOUS_SCRREEN;
    }
    return NO_ACTION;
}

// --- Helpers ---

void BinaryWatchFace::drawStaticLayout() {
    dispGraphic->setTextColor(colorWhite);
    dispGraphic->setBackColor(colorBlack);

    int colW = 2 * radius;
    int xMinStart = startX;
    int xSecStart = xMinStart + 2*colW + colGap + groupGap;
    int xMsStart  = xSecStart + 2*colW + colGap + groupGap;

    int yTop = yBase - (4 * (2 * radius + gap)) - 25; // Texto bien arriba

    // Ajuste fino (+radius) para centrar texto sobre el par de columnas
    dispGraphic->gotoPixelPos(xMinStart + radius, yTop); dispGraphic->putString("MIN");
    dispGraphic->gotoPixelPos(xSecStart + radius, yTop); dispGraphic->putString("SEC");
    dispGraphic->gotoPixelPos(xMsStart + radius,  yTop); dispGraphic->putString("DEC");

    // Escala lateral
    int xScale = startX - radius - 25;
    for(int i=0; i<4; i++) {
        int yRow = yBase - i * (2 * radius + gap) - 8;
        char numBuf[4];
        sprintf(numBuf, "%d", 1 << i);
        dispGraphic->gotoPixelPos(xScale, yRow);
        dispGraphic->putString(numBuf);
    }
}

void BinaryWatchFace::drawDigitNumber(int x, int y, int number) {
    // Usamos BackColor para que sobrescriba el número anterior limpiamente
    dispGraphic->setBackColor(colorBlack);
    dispGraphic->setTextColor(colorWhite);

    char buf[4];
    sprintf(buf, "%d", number);

    dispGraphic->gotoPixelPos(x - 4, y);
    dispGraphic->putString(buf);
}

void BinaryWatchFace::drawBinaryDigit(int x, int yBase, int digit, int rows, int radius, int gap) {
    for (int i = 0; i < rows; i++) {
        int y = yBase - i * (2 * radius + gap);
        int bitValue = 1 << i;
        bool isOn = (digit & bitValue) != 0;

        if (isOn) {
            drawFilledCircle(x, y, radius, colorMagenta);
        } else {
            drawFilledCircle(x, y, radius, colorBlack);
            drawCircleOutline(x, y, radius, colorWhite);
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
