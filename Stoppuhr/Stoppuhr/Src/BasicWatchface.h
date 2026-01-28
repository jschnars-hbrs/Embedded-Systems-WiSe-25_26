#ifndef basicwatchface_h_included
#define basicwatchface_h_included
#include "EmbSysLib.h"
#include "ScreenManager.h"
#include "StopUhrTimer.h"

using namespace EmbSysLib::Hw;
using namespace EmbSysLib::Dev;
using namespace EmbSysLib::Ctrl;

class BasicWatchface : public Watchface {
public:
    BasicWatchface(Timer_Mcu  *timer,DisplayGraphic * in_dispGraphic);

    void update() override;
    takeActionReturnValues handleButtons(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * button_user) override;
    private:
    StopUhrTimer stopwatch;
    DisplayGraphic * dispGraphic;
};
#endif
