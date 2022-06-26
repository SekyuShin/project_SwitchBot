

int wdtCount = 0;

void WdtReset() {
  wdtCount = 0;
}
void WdtCountUp() {
  if(++wdtCount>RESET_TIME) {
#ifdef SERIALON
  Serial.println("WatchDog Reset On");
#endif
    wdt_enable(WDTO_15MS);
    delay(15);
  } else if(GetCheckOffBeforeRst() && (wdtCount > RESET_TIME-3)) {
    SetOn_CheckOffAct();
    SetCheckOffBeforeRst(false);
  }
}
