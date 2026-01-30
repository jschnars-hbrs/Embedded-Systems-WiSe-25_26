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

// Makro para conversión de colores
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

        // Colores
        int colorBlack   = RGB2COLOR(0, 0, 0);
        int colorWhite   = RGB2COLOR(255, 255, 255);
        int colorMagenta = RGB2COLOR(255, 0, 255); 
        
        // Geometría (Ajustada para Zoom 1)
        int radius = 10;      // Radio reducido para que entre bien
        int gap = 8;          // Espacio vertical
        int colGap = 16;      // Más aire entre columnas
        int groupGap = 40;    // Separación clara entre grupos
        
        // Variables calculadas
        int startX = 0;
        int yBase = 0;

        // Helpers
        void drawFilledCircle(int cx, int cy, int radius, int color);
        void drawCircleOutline(int cx, int cy, int radius, int color);
        void drawBinaryDigit(int x, int yBase, int digit, int rows, int radius, int gap);
        void drawStaticLayout(); 
        void drawDigitNumber(int x, int y, int number); // ¡Recuperamos esta función!
};
#endif
