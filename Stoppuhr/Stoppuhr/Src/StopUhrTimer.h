#ifndef STOPUHRTIMER_H_INCLUDED
#define STOPUHRTIMER_H_INCLUDED

#include <cstdint>
#include "StopUhr.h"
#include "Timer.h"

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
