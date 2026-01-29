/*!
\file   BouncingWatchFace.h
*/

#include "config.h"

#include "StopUhrTimer.h"
class BouncingWatchFace : public WatchFace 
{

    public:
        BouncingWatchFace(Timer *timer): myStoppWatch(timer)
    {
    }
    void update();

    private:
        //Time management
        StopUhrTimer myStoppWatch;
        float currenttime = 0;

        //Graphics
        WORD boundingBoxWidth = dispGraphic.getWidth();
        WORD boundingBoxHeight = dispGraphic.getHeight();

        int zoomFactor = 6;

        char displaytime[10] = "00:00:000";
        int symbolCount = 9;    // 7*"0" + 2*":"
        int lineCount = 1;
        int symbolHeight = 9;   //in pixel
        int symbolWidth = 8;    //in pixel

        //Animation
        int bounceObjectWidth = symbolWidth*zoomFactor*symbolCount;
        int bounceObjectHeight = symbolHeight*zoomFactor*lineCount;

        int posX = boundingBoxWidth/2 - bounceObjectWidth/2;    //Centering the bounceObjekt in X
        int posY = boundingBoxHeight/2 - bounceObjectHeight/2;  //Centering the bounceObjekt in Y
        int posXOld;
        int posYOld;
        float speedX = 4;
        float speedY = 2;

        int framesPerSecond = 24;
        float lastFrameUpdate;
        int currentColor = 0;

        //controls
        bool setUpStatus = false;
        bool allowReset = true;

        //Functions
        void setUp();
        void upDateDisplay();

};
