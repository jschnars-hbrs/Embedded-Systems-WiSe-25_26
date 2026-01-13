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
//*******************************************************************
int main(void)
{
  lcd.printf( 0, 0, __DATE__ "," __TIME__ );
  lcd.printf( 1, 0, "Stopuhr Demo" );
  lcd.refresh();

  StopUhrTimer stopuhr( &timer );

  bool lastBtn1State = false;
  bool running = false;  // Eigene Variable für den Zustand

  while(1)
  {
    bool btn1Pressed = Btn1.get();

    // Flankenerkennung Btn1: Start/Stop
    if( btn1Pressed && !lastBtn1State )
    {
      if( !running )
      {
        stopuhr.start();
        running = true;
        LD1.set( true );
      }
      else
      {
        stopuhr.stop();
        running = false;
        LD1.set( false );
      }
    }
    lastBtn1State = btn1Pressed;

    // Reset mit Btn2
    if( Btn2.get() )
    {
      stopuhr.reset();
      running = false;
      LD1.set( false );
    }

    // Zeit anzeigen
    DWORD timeUs = stopuhr.getPassedTimeuS();
    DWORD timeMs = stopuhr.getPassedTime();

    lcd.printf( 2, 0, "Zeit: %lu us   ", timeUs );
    lcd.printf( 3, 0, "Zeit: %lu ms   ", timeMs );
    lcd.printf( 4, 0, "Zeit: %.3f s   ", timeUs / 1000000.0f );
    lcd.printf( 5, 0, "Running: %d", running );  // Debug-Ausgabe
    lcd.printf( 6, 0, "Btn1: Start/Stop" );
    lcd.printf( 7, 0, "Btn2: Reset" );
    lcd.printf( 8, 0, "Cycle: %lu", timer.getCycleTime() );

    lcd.refresh();
  }
}
