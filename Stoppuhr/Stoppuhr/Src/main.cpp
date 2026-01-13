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

  bool lastBtnState = false;

  while(1)
  {
    bool btnPressed = Btn1.get();

    // Flankenerkennung: Start/Stop bei Tastendruck
    if( btnPressed && !lastBtnState )
    {
      if( stopuhr.getPassedTimeuS() == 0 || !LD1.get() )
      {
        stopuhr.start();
        LD1.set( true );   // LED an = läuft
      }
      else
      {
        stopuhr.stop();
        LD1.set( false );  // LED aus = gestoppt
      }
    }
    lastBtnState = btnPressed;

    // Reset mit Btn2
    if( Btn2.get() )
    {
      stopuhr.reset();
      LD1.set( false );
    }

    // Zeit anzeigen
    DWORD timeUs = stopuhr.getPassedTimeuS();
    DWORD timeMs = stopuhr.getPassedTime();

    lcd.printf( 2, 0, "Zeit: %lu us   ", timeUs );
    lcd.printf( 3, 0, "Zeit: %lu ms   ", timeMs );
    lcd.printf( 4, 0, "Zeit: %.3f s   ", timeUs / 1000000.0f );

    lcd.printf( 6, 0, "Btn1: Start/Stop" );
    lcd.printf( 7, 0, "Btn2: Reset" );

    lcd.refresh();
  }
}
