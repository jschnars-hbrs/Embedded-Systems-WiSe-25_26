#include "BasicWatchface.h"
#include <stdio.h>

BasicWatchface::BasicWatchface(Timer_Mcu  *timer,DisplayGraphic * in_dispGraphic,ScreenGraphic * in_lcd): stopwatch(timer)
{
      dispGraphic=in_dispGraphic;
      lcd=in_lcd;
      //dispGraphic->setTextColor( Color::White    );
      //dispGraphic->setBackColor( Color::DarkBlue );
      }
void BasicWatchface::update()
{
    char txt[1000]="Hello World";
    dispGraphic->clear();
    // Update display with stopwatch time
    DWORD time = stopwatch.getPassedTime();
    // Code to update the display goes here
    printf(txt,"%02lu:%02lu.%03lu", (time / 60000), (time / 1000) % 60, time % 1000);
    dispGraphic->gotoPixelPos(0,0);
    dispGraphic->putString(txt);
    lcd->refresh();

}

BasicWatchface::takeActionReturnValues BasicWatchface::handleButtons(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * button_user)
{
    if(button1->getAction() == DigitalButton::ACTIVATED) {
        stopwatch.start();
        return NO_ACTION;
    }
    if(button2->getAction() == DigitalButton::ACTIVATED) {
       stopwatch.stop();
        return NO_ACTION;
    }
    if(button3->getAction() == DigitalButton::ACTIVATED) {
       stopwatch.reset();
        return NO_ACTION;
    }
    if(button_user->getAction() == DigitalButton::ACTIVATED) {
        return NEXT_SCREEN;
    }
    if (button_user->getAction() == DigitalButton::LONG)
    {
        return PREVIOUS_SCRREEN;
    }

    return NO_ACTION;
}
