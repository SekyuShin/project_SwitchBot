/*
 * Timer Functions for Device Time
 */

extern volatile unsigned long timer0_millis; //타이머변수
unsigned long timeVal = 0; //이전시간
unsigned long readTime; //현재타이머시간

//아두이노의 현재 시간 구조체
typedef struct _timer {
  bool checkDefault; //알람 동작을 위한 변수
  int hour, minute, sec;
}TIMER;

//알람 시간과 해당 알람이 수행되었을 때, 한번만 수행하기 위한 구조체
typedef struct _alarm {
  int switOn;
  bool checkSwitOn;
  int switOff;
  bool checkSwitOff;
}ALARM;

TIMER timer;
ALARM alarm;


void InitTime() {
  timer.checkDefault = false;
  timer.hour = 0;
  timer.minute = 0;
  timer.sec = 0;
  alarm.switOn = -1;
  alarm.checkSwitOn = false;
  alarm.switOff = -1;
  alarm.checkSwitOff = false;
}

void SetDefaultTime(int hour, int minute, int sec, int switOn, int switOff) {
  timer.checkDefault = true;
  SetTimer(hour, minute, sec);
  SetSwitOn(switOn);
  SetSwitOff(switOff);
}
void SetSwitOn(int switOn) {
   alarm.switOn = switOn;
}
int GetSwitOn() {
   return alarm.switOn;
}

void SetSwitOff(int switOff) {
   alarm.switOff = switOff;
}
int GetSwitOff() {
   return alarm.switOff;
}
void settingTime() {
  readTime = millis() / 1000;
  if (millis() >= 86400000) { //24시간 단위 초기화
    timer0_millis = 0;
  }
  timeVal = millis();

  timer.sec = readTime % 60;
  timer.minute = (readTime / 60) % 60;
  timer.hour = (readTime / (60 * 60)) % 24;
#ifdef SERIALON
  Serial.println(String(timer.hour)+":"+String(timer.minute)+":"+String(timer.sec));
#endif
  if(GetCheckMode() == 0)SetSendQueue(String(timer.hour)+":"+String(timer.minute)+":"+String(timer.sec));
}
//1초 단위 체크
bool checkTime() {
  if (millis() - timeVal >= 1000) { //1초 단위 출력
    settingTime();
    WdtCountUp();
    if (timer.checkDefault) { //블루투스로 부터 기본값이 세팅되어야 알람 수행
      //1분 단위로 한번만 수행하기 위해서
      if (alarm.switOn / 100 == timer.hour && alarm.switOn % 100 == timer.minute)  { 
        if(!alarm.checkSwitOn) {
          SetOn_CheckOnAct();
#ifdef SERIALON
          Serial.println("alarm SwitOn");
#endif
          if(GetCheckMode() == 0) SetSendQueue("alarm SwitOn");
          alarm.checkSwitOn = true;
        }
      }else if (alarm.switOff / 100 == timer.hour && alarm.switOff % 100 == timer.minute) {
        if(!alarm.checkSwitOff) {
          SetOn_CheckOffAct();
#ifdef SERIALON
          Serial.println("alarm SwitOff");
#endif
          if(GetCheckMode() == 0) SetSendQueue("alarm SwitOff");
          alarm.checkSwitOff = true;
        }
      } else if(alarm.checkSwitOn || alarm.checkSwitOff){
        alarm.checkSwitOn = false;
        alarm.checkSwitOff = false;
      }
    }
    return true;
  }else return false;
}
String GetTime() {
  return String(timer.hour)+":"+String(timer.minute)+":"+String(timer.sec);
}
void SetTimer(int hour, int minute, int sec) {
    timer.hour = hour;
    timer.minute = minute;
    timer.sec = sec;
    timer0_millis = ((long)hour * 3600 + minute * 60 + sec) * 1000;
    //timeVal = millis();
}

//Wait 모드일 때, 연결이 끊어져도 Switch On 알람이 존재할 경우에 Switch On을 수행하고, 아침시간 까지 유지하기 위한 함수
void CheckAfterSwitOn() {
   if(alarm.switOn < timer.hour*100+timer.minute+1 && MORNING_TIME < timer.hour*100+timer.minute+1) {
 #ifdef SERIALON
  Serial.println("CheckAfterSwitOn reset Start");
#endif
    timer.checkDefault = false;
   }
}
bool GetCheckDefault() {
  return timer.checkDefault;
}
