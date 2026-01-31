#ifndef stopwatchviewrings_h_included
#define stopwatchviewrings_h_included

#include "EmbSysLib.h"
#include "ScreenManager.h"
#include "StopUhrTimer.h"

#include <cstdint>

using namespace EmbSysLib::Hw;
using namespace EmbSysLib::Dev;
using namespace EmbSysLib::Ctrl;

/*
 * Helper macro to convert RGB values into a 16-bit color format
 * used by the display hardware.
 */
#define RGB2COLOR(red, green, blue ) \
    ( (((blue )& 0xF8) >> 3)  /* 5 bit,  0.. 4 */\
     |(((green)& 0xFC) << 3)  /* 6 bit,  5..10 */\
     |(((red  )& 0xF8) << 8)) /* 5 bit, 11..15 */

/*
 * StopwatchViewRings
 * Watchface that visualizes a stopwatch using two concentric rings.
 * The inner ring represents seconds, the outer ring represents minutes.
 */
class StopwatchViewRings : public Watchface
{
public:
    StopwatchViewRings(Timer_Mcu *timer,
                       DisplayGraphic *dispGraphic,
                       ScreenGraphic *lcd);

    // Periodic update of the watchface
    void update() override;

    // Called when this watchface becomes active
    void changed_to() override;

    // Handle user button input
    takeActionReturnValues handleButtons(DigitalButton *button1,
                                         DigitalButton *button2,
                                         DigitalButton *button3,
                                         DigitalButton *button_user) override;

private:
    StopUhrTimer myStopwatch;
    DisplayGraphic *dispGraphic {nullptr};
    ScreenGraphic  *lcd {nullptr};

    // State variables for incremental drawing
    int lastSecStep {-1};
    int lastMinStep {-1};

    // Display colors
    int colorBack = RGB2COLOR(0, 0, 0);
    int colorText = RGB2COLOR(0, 255, 255);   // cyan
    int colorBase = RGB2COLOR(0, 255, 255);   // ring outline color

    // Helper functions
    void resetDrawState();
    void formatTime(uint32_t timeMs, char out[16], uint32_t& msOut) const;

    void drawRingOutlineDots(int cx, int cy, int rOuter, int thickness);
    void drawRingArcSolid(int cx, int cy, int rOuter, int thickness,
                          int stepFrom, int stepTo, WORD color);

    static void putPixelSafe(DisplayGraphic& d, int x, int y);
    static int clampi(int v, int lo, int hi);
};

#endif
