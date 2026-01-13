#include "StopUhrTimer.h"

StopUhrTimer::StopUhrTimer(Timer* timer) //Konsturktor Definition - Initialisierung der Variablen und des Timers
    : m_timer(timer)
    , m_startTime(0)
    , m_accumulatedTime(0)
    , m_running(false)
{
}

void StopUhrTimer::start()
{
    if (!m_running && m_timer != nullptr) {
        m_startTime = m_timer->getCycleTime();
        m_running = true;
    }
}

void StopUhrTimer::stop()
{
    if (m_running && m_timer != nullptr) {
        DWORD currentTime = m_timer->getCycleTime();
        m_accumulatedTime += (currentTime - m_startTime);
        m_running = false;
    }
}

void StopUhrTimer::reset()
{
    m_startTime = 0;
    m_accumulatedTime = 0;
    m_running = false;
}

DWORD StopUhrTimer::getPassedTime()
{
    // Gibt die Zeit in Millisekunden zurück
    return getPassedTimeuS() / 1000;
}

DWORD StopUhrTimer::getPassedTimeuS()
{
    if (m_timer == nullptr) {
        return 0;
    }

    if (m_running) {
        DWORD currentTime = m_timer->getCycleTime();
        return (m_accumulatedTime + (currentTime - m_startTime));
    }

    return (m_accumulatedTime);
}
