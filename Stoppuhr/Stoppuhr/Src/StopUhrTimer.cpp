#include "EmbSysLib.h"
#include "StopUhrTimer.h"

using namespace EmbSysLib::Hw;

StopUhrTimer::StopUhrTimer(Timer* timer)
    : m_cycleTime(timer->getCycleTime())
    , m_counter(0)
    , m_running(false)
{
    timer->add(this);
}
void StopUhrTimer::update()
{
    if(m_running) {
        m_counter++;
    }
}

void StopUhrTimer::start()
{
    m_running = true;
}

void StopUhrTimer::stop()
{
    m_running = false;
}

void StopUhrTimer::reset()
{
    m_counter = 0;
    m_running = false;
}

DWORD StopUhrTimer::getPassedTime()
{
    return getPassedTimeuS() / 1000;
}

DWORD StopUhrTimer::getPassedTimeuS()
{
    return m_counter * m_cycleTime;
}
