/*!
\file   BouncingWatchFace.h
*/
#ifndef bouncingwatchface_h_included
#define bouncingwatchface_h_included

#include "EmbSysLib.h"
#include "ScreenManager.h"
#include "StopUhrTimer.h"
//#include "Color.h"
using namespace EmbSysLib::Hw;
using namespace EmbSysLib::Dev;
using namespace EmbSysLib::Ctrl;

#define RGB2COLOR(red, green, blue ) \
    ( (((blue )& 0xF8) >> 3)  /* 5 bit,  0.. 4 */\
     |(((green)& 0xFC) << 3)  /* 6 bit,  5..10 */\
     |(((red  )& 0xF8) << 8)) /* 5 bit, 11..15 */

#include "StopUhrTimer.h"
class BouncingWatchFace : public Watchface {

    public:
        BouncingWatchFace(Timer_Mcu  *timer,DisplayGraphic * dispGraphic, ScreenGraphic * lcd);
        void update() override;
        takeActionReturnValues handleButtons(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * button_user) override;

    private:
        //Time management
        StopUhrTimer myStoppWatch;
        float currenttime = 0;
        int milliSec = 0;
        int sec = 0;
        int minuts = 0;

        //Graphics
        int colorBlack = RGB2COLOR(   0,    0,   0 );
        int colorWhite = RGB2COLOR(   255,    255,   255 );
        int colorYellow = RGB2COLOR( 255,  255,   0 );
        int colorCyan = RGB2COLOR(   0,  255, 255 );
        int colorMagenta = RGB2COLOR( 255,    0, 255 );
        DisplayGraphic * dispGraphic;
        ScreenGraphic * lcd;
        WORD boundingBoxWidth =432;
        WORD boundingBoxHeight=54;

        int zoomFactor = 6;

        char displaytime[10] = "00:00:000";
        int symbolCount = 9;    // 7*"0" + 2*":"
        int lineCount = 1;
        int symbolHeight = 9;   //in pixel
        int symbolWidth = 8;    //in pixel

        //Animation
        int bounceObjectWidth = symbolWidth*zoomFactor*symbolCount;
        int bounceObjectHeight = symbolHeight*zoomFactor*lineCount;

        int posX;    //Centering the bounceObjekt in X
        int posY;  //Centering the bounceObjekt in Y
        int posXOld;
        int posYOld;
        float speedX = 4;
        float speedY = 2;

        int framesPerSecond = 24;
        float lastFrameUpdate;
        int currentColor = 0;

        //controls
        bool setUpStatus = false;
        bool run = false;
        bool allowReset = true;




        //Functions
        void setUp();
        void upDateDisplay();

        //Graphics
        void upDateDisplayTime();
        void changeColor();

        //Animations
        void upDateAnimation();
        void upDatePos();
        void checkForBounce();
        void delFrameTraces();

        //Time management
        float getTime();
        void upDateCurrentTime();

        //controls
        bool getIfRunning();
        void resetCurrentTime();

};
#endif
