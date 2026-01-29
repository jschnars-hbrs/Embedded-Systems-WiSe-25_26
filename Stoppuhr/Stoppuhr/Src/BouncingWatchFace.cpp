/*!
\file   BouncingWatchFace.cpp
*/
#include "BouncingWatchFace.h"
#include <stdio.h>

BouncingWatchFace::BouncingWatchFace(Timer_Mcu  *timer,DisplayGraphic * dispGraphic, ScreenGraphic * lcd): myStoppWatch(timer)
{
    this->dispGraphic = dispGraphic;
    this->lcd = lcd;
    boundingBoxWidth = dispGraphic->getWidth();
    boundingBoxHeight = dispGraphic->getHeight();
    posX = boundingBoxWidth/2 - bounceObjectWidth/2;    //Centering the bounceObjekt in X
    posY = boundingBoxHeight/2 - bounceObjectHeight/2;  //Centering the bounceObjekt in Y

}

void BouncingWatchFace::changed_to(){

    dispGraphic->setZoom(this->zoomFactor);
    dispGraphic->setBackColor( this->colorBlack);
    this->currentColor -= 1;
    changeColor();
}


void BouncingWatchFace::setUp()
{
    dispGraphic->setZoom(this->zoomFactor);
    dispGraphic->setTextColor( this->colorWhite);
    dispGraphic->setBackColor( this->colorBlack);
    dispGraphic->clear();
    lcd->refresh();
}

void BouncingWatchFace::update()
{

    if(this->setUpStatus == false)
    {
        this->setUpStatus = true;
        this->setUp();
    }


    if (this->getIfRunning())
    {
        this->upDateCurrentTime();
        this->upDateDisplayTime();
        this->upDateAnimation();
    }

    this->upDateDisplay();
}

//Graphics
void BouncingWatchFace::upDateDisplay()
{
    dispGraphic->gotoPixelPos(this->posX, this->posY);
    dispGraphic->putString(this->displaytime);
    lcd->refresh();
}

void BouncingWatchFace::upDateDisplayTime()
{

    this->minuts=int(this->currenttime/60000);
    this->sec=int(this->currenttime/1000)%60;
    this->milliSec=int(this->currenttime)%1000;
    this->displaytime[0] = (this->minuts/10)+48;
    this->displaytime[1] = this->minuts%10+48;
    this->displaytime[3] = this->sec/10+48;
    this->displaytime[4] = this->sec%10+48;
    this->displaytime[6] = this->milliSec/100+48;
    this->displaytime[7] = this->milliSec/10%10+48;
    this->displaytime[8] = this->milliSec%10+48;

}

void BouncingWatchFace::changeColor()
{
    this-> currentColor +=1;
    if (this->currentColor >= 4)
    {
        this->currentColor = 0;
    }

    switch (currentColor)
    {
    case 0:
        dispGraphic->setTextColor( this->colorWhite );
        break;
    case 1:
        dispGraphic->setTextColor( this->colorMagenta);
        break;
    case 2:
        dispGraphic->setTextColor( this->colorCyan );
        break;
    case 3:
        dispGraphic->setTextColor( this->colorYellow);
        break;
    }
    dispGraphic->clear();
}

//Animation
void BouncingWatchFace::upDateAnimation()
{
    if ((this->currenttime-this->lastFrameUpdate)>=1/this->framesPerSecond)
    {
        this->upDatePos();
        this->checkForBounce();
        this->delFrameTraces();
    }
}

void BouncingWatchFace::upDatePos()
{
    this->lastFrameUpdate = this->currenttime;
    this->posXOld = this->posX;
    this->posYOld = this->posY;
    this->posX+=this->speedX;
    this->posY+=this->speedY;
}

void BouncingWatchFace::checkForBounce()
{
    if ((this->posX+this->bounceObjectWidth+this->zoomFactor>= this->boundingBoxWidth)||(this->posX-this->zoomFactor<=0))
    {
        this->speedX *= -1; //reverse direction
        this->changeColor();
    }
    if ((this->posY+this->bounceObjectHeight+this->zoomFactor>=this->boundingBoxHeight)||(this->posY-this->zoomFactor<=0))
    {
        this->speedY *= -1; //reverse direction
        this->changeColor();
    }
}

void BouncingWatchFace::delFrameTraces()
{
    if(this->speedX > 0)
    {
        dispGraphic->gotoPixelPos(this->posXOld-this->zoomFactor, this->posY);
    }
    else
    {
        dispGraphic->gotoPixelPos(this->posX + this->bounceObjectWidth-1, this->posY);
    }
    dispGraphic->putString(" ");
}

//time management
float BouncingWatchFace::getTime()
{
    return this->myStoppWatch.getPassedTime();
}

void BouncingWatchFace::upDateCurrentTime()
{
    this->currenttime=this->getTime();
}

//controls
bool BouncingWatchFace::getIfRunning()
{
    if(this->run == true)
    {
        return true;
    }
    else
    {
        return false;
    }
}

void BouncingWatchFace::resetCurrentTime()
{
    if(this->allowReset==true)
    {
        myStoppWatch.reset();
        this->currenttime = 0;
        this->lastFrameUpdate = 0;
        this->displaytime[0] = '0';
        this->displaytime[1] = '0';
        this->displaytime[3] = '0';
        this->displaytime[4] = '0';
        this->displaytime[6] = '0';
        this->displaytime[7] = '0';
        this->displaytime[8] = '0';
    }
}

BouncingWatchFace::takeActionReturnValues BouncingWatchFace::handleButtons(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * button_user)
{
    if(button1->getAction() == DigitalButton::ACTIVATED) {
        myStoppWatch.start();
        this->run = true;
        this->allowReset = false;
        return NO_ACTION;
    }
    if(button2->getAction() == DigitalButton::ACTIVATED) {
       myStoppWatch.stop();
       this->run = false;
       this->allowReset = true;
        return NO_ACTION;
    }
    if(button3->getAction() == DigitalButton::ACTIVATED) {
       resetCurrentTime();
        return NO_ACTION;
    }
    if(button_user->getAction() == DigitalButton::ACTIVATED) {
        return NEXT_SCREEN;
    }
    if (button_user->getAction() == DigitalButton::LONG)
    {
        myStoppWatch.stop();
        this->run = false;
        this->allowReset = true;
        return PREVIOUS_SCRREEN;
    }

    return NO_ACTION;
}
