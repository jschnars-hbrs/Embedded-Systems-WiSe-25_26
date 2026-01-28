#ifndef SCREENMANAGER_H_INCLUDED
#define SCREENMANAGER_H_INCLUDED

#include "EmbSysLib.h"
using namespace EmbSysLib::Hw;
using namespace EmbSysLib::Dev;
using namespace EmbSysLib::Ctrl;
using namespace EmbSysLib::Mod;
#include <vector>
#include <algorithm>
class Watchface{
public:
    enum takeActionReturnValues {NO_ACTION, NEXT_SCREEN, PREVIOUS_SCRREEN};
    virtual void update()=0;
    virtual takeActionReturnValues handleButtons(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * button_user)=0;
};

class ScreenManager: public TaskManager::Task {
public:
    ScreenManager(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * user);
    bool setScreen(int numScreen);
    bool setNextScreen();
    bool setPrevScreen();
    void addWatchFace(Watchface *watchface);
    bool removeWatchFace(int numScreen);
    bool removeWatchFace(Watchface * watchface);
    void update();
private:

    int m_numScreens;
    int m_currentScreen;
    std::vector<Watchface *> m_watchfaces;
    DigitalButton * m_button1;
    DigitalButton * m_button2;
    DigitalButton * m_button3;
    DigitalButton * m_button_user;
};


#endif
