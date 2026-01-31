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
    posX = boundingBoxWidth/2 - bounceObjectWidth/2;    //Das bounceObjekt (Uhrzeit) wird in X-Richtung zentriert
    posY = boundingBoxHeight/2 - bounceObjectHeight/2;  //Das bounceObjekt (Uhrzeit) wird in Y-Richtung zentriert

}

void BouncingWatchFace::changed_to(){

    dispGraphic->setZoom(this->zoomFactor);
    dispGraphic->setBackColor( this->colorBlack);
    this->currentColor -= 1;        //Die aktuelle Textfarbe muss auf die davor gesetzt werden,
    changeColor();                  //damit changeColor() nicht die nächste, sondern die Farbe auswählt, 
}                                   //die aktiv war, als das Face verlassen wurde


void BouncingWatchFace::setUp()    //Wird einmalig beim ersten Aufruf von BouncingWatchFace aufgerufen
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
void BouncingWatchFace::upDateDisplay() //Gibt unabhängig davon, ob die Stoppuhr angehalten ist oder nicht, 
{                                       //die aktuelle Zeit auf der aktuellen Position auf
    dispGraphic->gotoPixelPos(this->posX, this->posY);
    dispGraphic->putString(this->displaytime);
    lcd->refresh();
}

void BouncingWatchFace::upDateDisplayTime() //Berechnet die aktuellen Minuten, Sekunden und Millisekunden 
{                                           //und formartiert sie zusammen zu einem String

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

void BouncingWatchFace::changeColor() //Wechsel die Textfarbe von weiß auf magenta, von magenta auf cyan,
{                                     //von cyan auf gelb und von gelb wieder auf weiß
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
void BouncingWatchFace::upDateAnimation() //Animiert die Bewegung der Uhrzeit, wenn die Stoppuhr läuft.
{                                         //Wie oft die Animation aktulisiert wird ist abhängig von framesPerSecond,
    if ((this->currenttime-this->lastFrameUpdate)>=1/this->framesPerSecond) //die wie bei den klassischen Animationen 24 Bilder pro Sekunde betragen.
    {
        this->upDatePos();
        this->checkForBounce();
        this->delFrameTraces();
    }
}

void BouncingWatchFace::upDatePos() //Aktualisiert die Position der Animation, abhängig von der eingestellten Geschwindigkeit
{                                   //und merkt sich den Zeitpunkt der Aktualisierung und die 
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
        this->speedX *= -1; //Richtungswechsel in X-Richtung
        this->changeColor();
    }
    if ((this->posY+this->bounceObjectHeight+this->zoomFactor>=this->boundingBoxHeight)||(this->posY-this->zoomFactor<=0))
    {
        this->speedY *= -1; //Richtungswechsel in Y-Richtung
        this->changeColor();
    }
}

void BouncingWatchFace::delFrameTraces()    //Überschreibt die Spuren des vorherigen Frames mit einem Leerzeichen. 
{                                           //Das geht deutlich schneller, als jedesmal den kompletten Bildschirm zu refreshen.
    if(this->speedX > 0)                
    {                                       //Bewegt sich die Uhrzeit nach rechts, wird vor der Uhrzeit ein Leerzeichen geschrieben.
        dispGraphic->gotoPixelPos(this->posXOld-this->zoomFactor, this->posY);
    }
    else
    {                                       //Bewegt sich die Uhrzeit nach links, wird hinter der Uhrzeit ein Leerzeichen geschrieben.
        dispGraphic->gotoPixelPos(this->posX + this->bounceObjectWidth-1, this->posY);
    }
    dispGraphic->putString(" ");
}

//time management
float BouncingWatchFace::getTime()          //Holt sich die aktuelle Zeit von der StopUhrTimer-Klasse
{
    return this->myStoppWatch.getPassedTime();
}

void BouncingWatchFace::upDateCurrentTime() //Holt sich die aktuelle Zeit von der Methode getTime()
{                                           //Überrest aus einer vorherigen Version dieser Klasse, bei dem 
    this->currenttime=this->getTime();      //hier noch der Zeitunterschied von der angehaltenen Zeit und der des Timers
}                                           //rausgerechnet wurde. Diese Aufgabe wird jetzt von der StopUhrTimer-Klasse übernommen.

//controls
bool BouncingWatchFace::getIfRunning()  //Getter-Methode für die Variabel "run".
{                                       //Gibt an ob die Stoppuhr läuft oder angehalten wurde.
    if(this->run == true)
    {
        return true;
    }
    else
    {
        return false;
    }
}

void BouncingWatchFace::resetCurrentTime() //Setzt die ausgegebene Zeit zurück auf 0.
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
