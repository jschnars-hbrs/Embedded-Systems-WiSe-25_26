#ifndef STOPUHR_H_INCLUDED
#define STOPUHR_H_INCLUDED
#include "EmbSysLib.h"

class StopUhr
{
public:
    virtual ~StopUhr() = default; //Destruktor


    //abstrakte Funktionen
    virtual DWORD  getPassedTime() = 0; //Zeit kann nicht null sein, daher unsigned
    virtual void start() = 0;
    virtual void stop()  = 0;
    virtual void reset() = 0;
};


#endif /* STOPUHR_H_INCLUDED */
