#ifndef basicwatchface_h_included
#define basicwatchface_h_included
#include "EmbSysLib.h"
#include "ScreenManager.h"
#include "StopUhrTimer.h"

using namespace EmbSysLib::Hw;
using namespace EmbSysLib::Dev;
using namespace EmbSysLib::Ctrl;

#define RGB2COLOR(red, green, blue ) \
    ( (((blue )& 0xF8) >> 3)  /* 5 bit,  0.. 4 */\
     |(((green)& 0xFC) << 3)  /* 6 bit,  5..10 */\
     |(((red  )& 0xF8) << 8)) /* 5 bit, 11..15 */


class BasicWatchface : public Watchface
{
public:
    BasicWatchface(Timer_Mcu *timer, DisplayGraphic *in_dispGraphic, ScreenGraphic *in_lcd);
    void changed_to();
    void update();
    takeActionReturnValues handleButtons(DigitalButton *button1, DigitalButton *button2, DigitalButton *button3, DigitalButton *button_user);

private:
    StopUhrTimer stopwatch;
    DisplayGraphic *dispGraphic;
    ScreenGraphic *lcd;
};
#endif
