/*!
\file   BinaryWatchFace.h
*/
#ifndef binarywatchface_h_included
#define binarywatchface_h_included

#include "EmbSysLib.h"
#include "ScreenManager.h"
#include "StopUhrTimer.h"

using namespace EmbSysLib::Hw;
using namespace EmbSysLib::Dev;
using namespace EmbSysLib::Ctrl;

// Makro für Farbkonvertierung
#define RGB2COLOR(red, green, blue ) \
    ( (((blue )& 0xF8) >> 3)  /* 5 bit,  0.. 4 */\
     |(((green)& 0xFC) << 3)  /* 6 bit,  5..10 */\
     |(((red  )& 0xF8) << 8)) /* 5 bit, 11..15 */

class BinaryWatchFace : public Watchface {

    public:
        BinaryWatchFace(Timer_Mcu *timer, DisplayGraphic * dispGraphic, ScreenGraphic * lcd);
        
        void update() override;
        void changed_to() override;
        takeActionReturnValues handleButtons(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * button_user) override;

    private:
        StopUhrTimer myStoppWatch;
        DisplayGraphic * dispGraphic;
        ScreenGraphic * lcd;

        // Farben
        int colorBlack   = RGB2COLOR(0, 0, 0);
        int colorWhite   = RGB2COLOR(255, 255, 255);
        int colorMagenta = RGB2COLOR(255, 0, 255); 
        int colorDark    = RGB2COLOR(50, 50, 50); 

        // Geometrie-Parameter (VERKLEINERT)
        int radius = 10;      // Kleinerer Radius (war 20)
        int gap = 6;          // Abstand vertikal
        int colGap = 8;       // Abstand zwischen Spalten
        int groupGap = 35;    // Abstand zwischen Gruppen
        int startX = 0;
        int yBase = 0;

        // Hilfsmethoden
        void drawFilledCircle(int cx, int cy, int radius, int color);
        void drawCircleOutline(int cx, int cy, int radius, int color);
        void drawBinaryDigit(int x, int yBase, int digit, int rows, int radius, int gap);
        void drawStaticLayout(); 
        
        // Helper um Dezimalzahlen zu zeichnen
        void drawDigitNumber(int x, int y, int number);

        bool isSetup = false;
};
#endif
