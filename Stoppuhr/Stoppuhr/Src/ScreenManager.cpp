#include "ScreenManager.h"

ScreenManager::ScreenManager(DigitalButton * button1, DigitalButton * button2, DigitalButton * button3, DigitalButton * user)
{
    m_numScreens = 0;
    m_currentScreen = -1;
    m_button1 = button1;
    m_button2 = button2;
    m_button3 = button3;
    m_button_user = user;
    // Possibly reserver space for watchfaces
    m_watchfaces.reserve(5);
}

bool ScreenManager::setScreen(int numScreen)
{
    if (numScreen < 0 || numScreen >= m_numScreens)
    {
        return false;
    }
    m_currentScreen = numScreen;
    m_watchfaces[m_currentScreen]->changed_to();
    return true;
}
bool ScreenManager::setNextScreen()
{
    if (m_numScreens == 0)
    {
        return false;
    }
    m_currentScreen = (m_currentScreen + 1) % m_numScreens;
    m_watchfaces[m_currentScreen]->changed_to();
    return true;
}
bool ScreenManager::setPrevScreen()
{
    if (m_numScreens == 0)
    {
        return false;
    }
    m_currentScreen = (m_currentScreen - 1 + m_numScreens) % m_numScreens;
    m_watchfaces[m_currentScreen]->changed_to();
    return true;
}
void ScreenManager::addWatchFace(Watchface *watchface)
{
    m_watchfaces.push_back(watchface);
    m_numScreens++;
}
bool ScreenManager::removeWatchFace(int numScreen)
{
    if (numScreen < 0 || numScreen >= m_numScreens)
    {
        return false;
    }
    m_watchfaces.erase(numScreen+m_watchfaces.begin());
    m_numScreens--;
    return true;
}

bool ScreenManager::removeWatchFace(Watchface * watchface)
{
    auto it = std::find(m_watchfaces.begin(), m_watchfaces.end(), watchface);
    if (it != m_watchfaces.end())
    {
        m_watchfaces.erase(it);
        m_numScreens--;
        return true;
    }
    return false;
}
void ScreenManager::update(){
    if (m_currentScreen < 0 || m_currentScreen >= m_numScreens)
    {
        return;
    }

    m_watchfaces[m_currentScreen]->update();
    Watchface::takeActionReturnValues action = m_watchfaces[m_currentScreen]->handleButtons(m_button1, m_button2, m_button3, m_button_user);
    switch(action)
    {
        case Watchface::NEXT_SCREEN:
            setNextScreen();

            break;
        case Watchface::PREVIOUS_SCRREEN:
            setPrevScreen();
            break;
        default:
            break;
    }
}
