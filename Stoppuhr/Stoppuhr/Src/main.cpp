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
/*
  LED:
    LD1  red   PJ13
    LD2 green  PJ5

  Button:
    Btn1       PF8
    Btn2       PF9
    Btn3       PA6
    User       PA0

*/
//*******************************************************************

//*******************************************************************
class App : public Timer::Task
{
	public:
    //---------------------------------------------------------------
    App( Timer *timer )
    {
      T = timer->getCycleTime()*1E-6;
      timer->add( this );
    }

    //---------------------------------------------------------------
    void update()
    {
      cnt++;
    }

    //---------------------------------------------------------------
    float getTime()
    {
      return( (float)cnt*T );
    }

	private:
    //---------------------------------------------------------------
    DWORD cnt = 0;
    float T;
};

//*******************************************************************
int main(void)
{
  lcd.printf( 0, 0, __DATE__ "," __TIME__ );
  lcd.printf( 1, 0, "Hello world!" );
  lcd.refresh();

  App app( &timer );

  while(1)
  {
	  lcd.printf( 2, 0, "Time: %6.3f sec",app.getTime() );

	  LD1.set( Btn1.get() );
	  LD2.set( Btn2.get() );

	  lcd.printf( 4, 0, "User:%d", User.get() );
	  lcd.printf( 5, 0, "Btn1:%d", Btn1.get() );
	  lcd.printf( 6, 0, "Btn2:%d", Btn2.get() );
	  lcd.printf( 7, 0, "Btn3:%d", Btn3.get() );
	  lcd.refresh();
  }
}
