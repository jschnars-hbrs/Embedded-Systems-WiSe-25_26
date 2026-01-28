/*!
\file   BouncingWatchFace.cpp
*/
#include "BouncingWatchFace.h"

void BouncingWatchFace::setUp()
{
    dispGraphic.setZoom(this->zoomFactor);
    dispGraphic.setTextColor( Color::White );
    dispGraphic.setBackColor( Color::Black );
    dispGraphic.clear();
    lcd.refresh();
}

void BouncingWatchFace::update()
{

    if(this->setUpStatus == false)
    {
        this->setUpStatus = true;
        this->setUp();
    }

    /*
    if (this->getIfRunning())
    {
        this->upDateCurrentTime();
        this->upDateDisplayTime();
        this->upDateAnimation();
    }
        */
    this->upDateDisplay();

    /*
    LD1.set( Btn1.get() );
    LD2.set( Btn2.get() );

    this->checkStartStoppBTN();
    this->checkResetBTN();*/

}

void BouncingWatchFace::upDateDisplay()
{
    dispGraphic.gotoPixelPos(this->posX, this->posY);
    dispGraphic.putString(this->displaytime);
    lcd.refresh();
}