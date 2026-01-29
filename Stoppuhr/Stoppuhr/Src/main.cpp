//*******************************************************************
/*!
\file   main.cpp
\author Thomas Breuer
\date   20.09.2022
\brief  Template
*/
//*******************************************************************
#include <stdio.h>
#include "EmbSysLib.h"
//-------------------------------------------------------------------
#include "ReportHandler.h"
#include "config.h"
#include "StopUhrTimer.h"
#include "ScreenManager.h"
#include "BasicWatchface.h"
//*******************************************************************
int main(void)
{
Timer_Mcu  timer2( Timer_Mcu::TIM_9, 10000L/*us*/ );

TaskManager taskManagerScrreen( timer2 );

DigitalButton Button1(Btn1,taskManager,10,1000);
DigitalButton Button2(Btn2,taskManager,10,1000);
DigitalButton Button3(Btn3,taskManager,10,1000);
DigitalButton UserButton(User,taskManager,10,1000);
ScreenManager screenManager(&Button1, &Button2, &Button3, &UserButton);
BasicWatchface secondwatchface(&timer,&dispGraphic, &lcd) ;
// add watchfaces to screen manager here
screenManager.addWatchFace(& secondwatchface);


// add Screen manger to taskmanager

taskManagerScrreen.add(&screenManager);


  while (1)
  {
    // Main loop does nothing, everything is handled by interrupts
  }

  return 0;
}
