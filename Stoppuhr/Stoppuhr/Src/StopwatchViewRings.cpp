#include "StopwatchViewRings.h"

#include <cmath>
#include <cstring>

//#include "C:\Users\julia\Documents\GitHub\Embedded-Systems-WiSe-25_26\Stoppuhr\Stoppuhr\Src\Resource\Font\Font_16x24.h" - Pfad direkt angegeben, sonst hat es nicht funktioniert
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif


namespace
{
  // ===== Visual =====
  constexpr int RING_GAP = 20;
  constexpr int RING_THICKNESS = 40;
  constexpr int DOT_SIZE = 1;

  // Progreso sólido continuo
  constexpr int FILL_STEPS = 1440;

  // Anillo vacío punteado
  constexpr int OUTLINE_DOTS = 180;

  // Random simple (sin rand())
  static uint32_t g_rng = 0x12345678u;
  static inline uint32_t rng_next()
  {
    g_rng = g_rng * 1664525u + 1013904223u;
    return g_rng;
  }

  static const WORD kFillPalette[] = {
    (WORD)RGB2COLOR(255,0,0),    // red
    (WORD)RGB2COLOR(0,255,0),    // green
    (WORD)RGB2COLOR(0,0,255),    // blue
    (WORD)RGB2COLOR(255,255,0),  // yellow
    (WORD)RGB2COLOR(255,0,255),  // magenta
    (WORD)RGB2COLOR(255,255,255) // white
  };
  constexpr int kFillPaletteN = (int)(sizeof(kFillPalette) / sizeof(kFillPalette[0]));

  static inline WORD pickRandomFillColor()
  {
    return kFillPalette[rng_next() % (uint32_t)kFillPaletteN];
  }
}

int StopwatchViewRings::clampi(int v, int lo, int hi)
{
  if(v < lo) return lo;
  if(v > hi) return hi;
  return v;
}

void StopwatchViewRings::putPixelSafe(DisplayGraphic& d, int x, int y)
{
  if(x < 0 || y < 0) return;
  if(x >= (int)d.getWidth())  return;
  if(y >= (int)d.getHeight()) return;
  d.putPixel((WORD)x, (WORD)y);
}

StopwatchViewRings::StopwatchViewRings(Timer_Mcu *timer,
                                       DisplayGraphic *dispGraphic,
                                       ScreenGraphic *lcd)
: myStopwatch(timer)
{
  this->dispGraphic = dispGraphic;
  this->lcd = lcd;

  // Config visual
  this->dispGraphic->setZoom(1);
  this->dispGraphic->setTextColor(colorText);
  this->dispGraphic->setBackColor(colorBack);
  this->dispGraphic->setPaintColor(colorBack);

  this->dispGraphic->clear();
  this->lcd->refresh();

  resetDrawState();
}

void StopwatchViewRings::resetDrawState()
{
  lastSecStep = -1;
  lastMinStep = -1;
}

// MM:SS en out y milisegundos (0..999) en msOut
void StopwatchViewRings::formatTime(uint32_t timeMs, char out[16], uint32_t& msOut) const
{
  const uint32_t ms = timeMs % 1000U;
  msOut = ms;

  const uint32_t secTotal = timeMs / 1000U;
  const uint32_t sec = secTotal % 60U;

  uint32_t min = secTotal / 60U;
  if(min > 99U) min = 99U;

  out[0] = char('0' + (min / 10U));
  out[1] = char('0' + (min % 10U));
  out[2] = ':';
  out[3] = char('0' + (sec / 10U));
  out[4] = char('0' + (sec % 10U));
  out[5] = '\0';
}

// Anillo vacío: puntos en borde interior y exterior
void StopwatchViewRings::drawRingOutlineDots(int cx, int cy, int rOuter, int thickness)
{
  const float a0 = -(float)M_PI / 2.0f;
  const float da = 2.0f * (float)M_PI / (float)OUTLINE_DOTS;

  const int rIn = rOuter - thickness;

  for(int i = 0; i < OUTLINE_DOTS; ++i)
  {
    const float a = a0 + da * (float)i;
    const float ca = std::cos(a);
    const float sa = std::sin(a);

    const int xIn  = cx + (int)std::lround((float)rIn    * ca);
    const int yIn  = cy + (int)std::lround((float)rIn    * sa);
    const int xOut = cx + (int)std::lround((float)rOuter * ca);
    const int yOut = cy + (int)std::lround((float)rOuter * sa);

    for(int dy=-DOT_SIZE; dy<=DOT_SIZE; ++dy)
    {
      for(int dx=-DOT_SIZE; dx<=DOT_SIZE; ++dx)
      {
        putPixelSafe(*dispGraphic, xIn  + dx, yIn  + dy);
        putPixelSafe(*dispGraphic, xOut + dx, yOut + dy);
      }
    }
  }
}

// Progreso: arco sólido (rellena rIn..rOut para cada ángulo)
void StopwatchViewRings::drawRingArcSolid(int cx, int cy, int rOuter, int thickness,
                                         int stepFrom, int stepTo, WORD color)
{
  if(stepTo <= stepFrom) return;

  if(stepFrom < 0) stepFrom = 0;
  if(stepTo > FILL_STEPS) stepTo = FILL_STEPS;

  const int rIn = rOuter - thickness;

  dispGraphic->setPaintColor(color);

  for(int s = stepFrom; s < stepTo; ++s)
  {
    const float a = -(float)M_PI/2.0f + (2.0f*(float)M_PI) * ((float)s / (float)FILL_STEPS);
    const float ca = std::cos(a);
    const float sa = std::sin(a);

    for(int r = rIn; r <= rOuter; ++r)
    {
      const int x = cx + (int)std::lround((float)r * ca);
      const int y = cy + (int)std::lround((float)r * sa);
      putPixelSafe(*dispGraphic, x, y);
    }
  }
}

void StopwatchViewRings::changed_to()
{
  dispGraphic->setZoom(1);
  dispGraphic->setTextColor(colorText);
  dispGraphic->setBackColor(colorBack);
  dispGraphic->setPaintColor(colorBack);
  dispGraphic->clear();
  lcd->refresh();

  resetDrawState();
}

void StopwatchViewRings::update()
{
  const uint32_t timeMs = (uint32_t)myStopwatch.getPassedTime();

  const int w  = (int)dispGraphic->getWidth();
  const int h  = (int)dispGraphic->getHeight();
  const int cx = w / 2;
  const int cy = h / 2;
  const int minSide = (w < h) ? w : h;

  const int thOuter = RING_THICKNESS;
  const int thInner = RING_THICKNESS;
  const int gap     = RING_GAP;

  const int rOuter = (minSide / 2) - 10;
  const int rInner = rOuter - gap - thOuter;

  const float sec01 = (float)(timeMs % 60000U) / 60000.0f;
  const int secStep = (int)(sec01 * (float)FILL_STEPS);

  const float min01 = (float)(timeMs % 3600000U) / 3600000.0f;
  const int minStep = (int)(min01 * (float)FILL_STEPS);

  const WORD BLACK = (WORD)colorBack;
  const WORD BASE  = (WORD)colorBase;

  static WORD secFillColor = (WORD)RGB2COLOR(0,255,0);
  static WORD minFillColor = (WORD)RGB2COLOR(255,255,0);

  if(lastSecStep < 0 || lastMinStep < 0)
  {
    secFillColor = pickRandomFillColor();
    minFillColor = pickRandomFillColor();

    dispGraphic->setPaintColor(BLACK);
    dispGraphic->clear();

    dispGraphic->setPaintColor(BASE);
    drawRingOutlineDots(cx, cy, rOuter, thOuter);
    drawRingOutlineDots(cx, cy, rInner, thInner);

    lastSecStep = 0;
    lastMinStep = 0;
  }

  const bool secWrapped = (lastSecStep > (FILL_STEPS - 6) && secStep < 6);
  const bool minWrapped = (lastMinStep > (FILL_STEPS - 6) && minStep < 6);

  if(secWrapped)
  {
    secFillColor = pickRandomFillColor();

    drawRingArcSolid(cx, cy, rInner, thInner, 0, FILL_STEPS, BLACK);
    dispGraphic->setPaintColor(BASE);
    drawRingOutlineDots(cx, cy, rInner, thInner);
    lastSecStep = 0;
  }

  if(minWrapped)
  {
    minFillColor = pickRandomFillColor();

    drawRingArcSolid(cx, cy, rOuter, thOuter, 0, FILL_STEPS, BLACK);
    dispGraphic->setPaintColor(BASE);
    drawRingOutlineDots(cx, cy, rOuter, thOuter);
    lastMinStep = 0;
  }

  if(secStep >= lastSecStep)
    drawRingArcSolid(cx, cy, rInner, thInner, lastSecStep, secStep, secFillColor);

  if(minStep >= lastMinStep)
    drawRingArcSolid(cx, cy, rOuter, thOuter, lastMinStep, minStep, minFillColor);

  lastSecStep = secStep;
  lastMinStep = minStep;

  // ===== Texto MM:SS + mmm =====
  char buf[16];
  uint32_t msVal = 0;
  formatTime(timeMs, buf, msVal);
  const int len = (int)std::strlen(buf);

  int zoom = 3;
  int charW = 16 * zoom;
  int charH = 24 * zoom;
  int totalW = len * charW;

  if(totalW > w)
  {
    zoom = 2;
    charW = 16 * zoom;
    charH = 24 * zoom;
    totalW = len * charW;
  }

  dispGraphic->setFont(fontFont_16x24, zoom);
  dispGraphic->setTextColor((WORD)colorText);

  int xText = cx - totalW / 2;
  int yText = cy - charH / 2;

  xText = clampi(xText, 0, w - 1);
  yText = clampi(yText, 0, h - 1);

  dispGraphic->gotoPixelPos((WORD)xText, (WORD)yText);
  dispGraphic->putString(buf);
  dispGraphic->gotoPixelPos((WORD)(xText+1), (WORD)yText);
  dispGraphic->putString(buf);

  // --- mmm debajo ---
  char msBuf[8];
  msBuf[0] = char('0' + (msVal / 100U));
  msBuf[1] = char('0' + ((msVal / 10U) % 10U));
  msBuf[2] = char('0' + (msVal % 10U));
  msBuf[3] = '\0';

  const int zoomMs = (zoom > 1) ? (zoom - 1) : 1;
  const int charWms = 16 * zoomMs;
  const int totalWms = 3 * charWms;

  //dispGraphic->setFont(fontFont_16x24, zoomMs);
  dispGraphic->setTextColor((WORD)colorText);

  int xMs = cx - totalWms / 2;
  int yMs = yText + charH + (2 * zoom);

  xMs = clampi(xMs, 0, w - 1);
  yMs = clampi(yMs, 0, h - 1);

  dispGraphic->gotoPixelPos((WORD)xMs, (WORD)yMs);
  dispGraphic->putString(msBuf);
  dispGraphic->gotoPixelPos((WORD)(xMs+1), (WORD)yMs);
  dispGraphic->putString(msBuf);

  lcd->refresh();
}

StopwatchViewRings::takeActionReturnValues
StopwatchViewRings::handleButtons(DigitalButton *button1,
                                  DigitalButton *button2,
                                  DigitalButton *button3,
                                  DigitalButton *button_user)
{
  if(button1->getAction() == DigitalButton::ACTIVATED)
  {
    myStopwatch.start();
    return NO_ACTION;
  }
  if(button2->getAction() == DigitalButton::ACTIVATED)
  {
    myStopwatch.stop();
    return NO_ACTION;
  }
  if(button3->getAction() == DigitalButton::ACTIVATED)
  {
    myStopwatch.reset();
    dispGraphic->clear();
    lcd->refresh();
    resetDrawState();
    return NO_ACTION;
  }
  if(button_user->getAction() == DigitalButton::ACTIVATED)
  {
    dispGraphic->clear();
    lcd->refresh();
    resetDrawState();
    return NEXT_SCREEN;
  }
  if(button_user->getAction() == DigitalButton::LONG)
  {
    dispGraphic->clear();
    lcd->refresh();
    resetDrawState();
    return PREVIOUS_SCRREEN;
  }

  return NO_ACTION;
}
