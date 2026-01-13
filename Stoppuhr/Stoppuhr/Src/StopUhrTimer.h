#ifndef STOPUHRTIMER_H_INCLUDED
#define STOPUHRTIMER_H_INCLUDED
#include "EmbSysLib.h"
#include <cstdint>
#include "StopUhr.h"

using namespace EmbSysLib::Hw; //dann kennt man Timer usw.

class StopUhrTimer : public StopUhr {   //StopUhrTimer erbt von StopUhr
public:

    explicit StopUhrTimer(Timer* timer); //Konstruktor, nimmt Timer erntgegen

    ~StopUhrTimer() override = default; //Destruktor

    DWORD getPassedTimeuS();

    // Implementierung der StopUhr-Interface-Methoden
    DWORD getPassedTime() override;
    void start() override;
    void stop() override;
    void reset() override;

private:
    Timer* m_timer;           // Pointer auf das Timer-Objekt
    DWORD m_startTime;        // Startzeitpunkt
    DWORD m_accumulatedTime;  // Akkumulierte Zeit (für Pause/Resume)
    bool m_running;           // Hilfsvariable ob die Stoppuhr läuft
};

#endif /* STOPUHRTIMER_H_INCLUDED */
