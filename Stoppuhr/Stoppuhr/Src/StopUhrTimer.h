#ifndef STOPUHRTIMER_H_INCLUDED
#define STOPUHRTIMER_H_INCLUDED
#include "EmbSysLib.h"
#include <cstdint>
#include "StopUhr.h"

using namespace EmbSysLib::Hw;  // namespace wichtig für DWORD z.B.

class StopUhrTimer : public StopUhr, public Timer::Task {
public:
    explicit StopUhrTimer(Timer* timer);
    ~StopUhrTimer() override = default;

    void update() override;  // Wird vom Timer aufgerufen

    DWORD getPassedTimeuS();
    DWORD getPassedTime() override;
    void start() override;
    void stop() override;
    void reset() override;

private:
    DWORD m_cycleTime;      // Zykluszeit in µs
    DWORD m_counter;        // Zählt die Zyklen
    bool m_running;
};

#endif
