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
#include "BouncingWatchFace.h"
#include "BinaryWatchFace.h"
//*******************************************************************
int main(void)
{

DigitalButton Button1(Btn1,taskManager,10,1000);
DigitalButton Button2(Btn2,taskManager,10,1000);
DigitalButton Button3(Btn3,taskManager,10,1000);
DigitalButton UserButton(User,taskManager,10,1000);
ScreenManager screenManager(&Button1, &Button2, &Button3, &UserButton);
BasicWatchface secondwatchface(&timer,&dispGraphic, &lcd) ;
BouncingWatchFace bouncingWatchFace(&timer,&dispGraphic, &lcd);
BinaryWatchFace binaryWatchFace(&timer,&dispGraphic, &lcd);
// add watchfaces to screen manager here
screenManager.addWatchFace(& secondwatchface);
screenManager.addWatchFace(&bouncingWatchFace);
screenManager.addWatchFace(&binaryWatchFace);

// add Screen manger to taskmanager

screenManager.setNextScreen();


  while (1)
  {
      screenManager.update();
    // Main loop does nothing, everything is handled by interrupts
  }

  return 0;
}
