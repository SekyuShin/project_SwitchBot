/*
 * main Function.
 * Setup Func and Loop Func, Mode Condition according to connection status
 */

#include <SoftwareSerial.h>
#include<String.h>
#include<Servo.h>
#include"queue.h"
#include<avr/wdt.h>
/*
 * Bluetooth Pin Setting
 */
#define BT_RXD 3
#define BT_TXD 4

//디버깅을 위한 옵션
#define SERIALON 0 
#define BTSTATEPIN 5

/*
 * ServoMotor Pin Setting
 */
#define servoPin 2

//연결이 끊어지면 30분 동안 리셋 대기
#define RESET_TIME 1800 // 30분

//연결이 끊어져도 아침 시간동안에는 Switch On을 수행하기 위한 옵션
#define MORNING_TIME 800 

//안드로이드로부터 Default값을 받았다면, 리셋 전에 Switch Off 를 한번 실행하기 위한 변수
bool checkOffBeforeRst = false;
/*
 * To devide Mode
 * Terminal Mode : Terminal Mode is communcates with Android for debugging.
 * Normal Mode : Normal Mode by default
 * Wait Mode : this mode performed after disconnect
 */
enum {TERMINAL, NORMAL, WAIT};
int mainstep = TERMINAL;

void setup() {
  InitTime();
  SerialSetup();
  ServoSetup();
}

void loop()
{
  if(!ReadBT()) { //블루투스로 부터 데이터를 받고, 해당 데이터를 큐에 저장
    mainstep = WAIT;
  } else if(mainstep == WAIT) {
    mainstep = TERMINAL;
    SetSendQueue("TERMINAL MODE ON");
  }
  if(checkTime()) { //run per 1 second
    String tmpStr = GetReceiveQueue(); //큐에 저장된 데이터를 1초 단위로 처리
    switch(mainstep) {
      case TERMINAL:
        WdtReset(); //와치독 리셋
        if(tmpStr != "") TerminalMode(tmpStr);
        break;
      case NORMAL:
        WdtReset();//와치독 리셋
        if(tmpStr != "") NormalMode(tmpStr);
        break;
      case WAIT://리셋 되기 전 대기상태
        WaitMode();
        break;
      default:
        SetSendQueue("MainStepError");
      }
    
      ServoProc(); //서보모터 동작 함수
      RunSendQueue(); //블루투스 송신 큐 동작 함수
  }
}

//기본값을 받기 전에도 기본적인 동작이 수행될 수 있는 Terminal Mode 
void TerminalMode(String str) {
  if(str.length() > 4 && str.substring(0, 4) == "INIT") { //INIT hour:min:sec:posOn:posCommon:posOff:onTime:offTime
    int index1 = str.indexOf(':');
    int index2 = str.indexOf(':', index1 + 1);
    int index3 = str.indexOf(':', index2 + 1);
    int index4 = str.indexOf(':', index3 + 1);
    int index5 = str.indexOf(':', index4 + 1);
    int index6 = str.indexOf(':', index5 + 1);
    int index7 = str.indexOf(':', index6 + 1);
    int index8 = str.length();
    SetDefaultTime(str.substring(4, index1).toInt(),str.substring(index1 + 1, index2).toInt(), str.substring(index2 + 1, index3).toInt(), str.substring(index6 + 1, index7).toInt(), str.substring(index7 + 1, index8).toInt());
    SetDefaultServoPos(str.substring(index3 + 1, index4).toInt(), str.substring(index4 + 1, index5).toInt(), str.substring(index5 + 1, index6).toInt());
    SetOn_CheckComAct();
    mainstep = NORMAL;
    SetSendQueue("NORMAL MODE ON");
  }else {
    if (str == "ON") {
      SetOn_CheckOnAct();
      SetSendQueue("SWITCH ON");
    } else if (str == "OFF") {
      SetOn_CheckOffAct();
      SetSendQueue("SWITCH OFF");
    } else {
      SendServoWrite(str.toInt());
      SetSendQueue("ANGLE TEST : "+str);
    }
  }
}

//기본값을 받은 후에 동작하는 Normal Mode
void NormalMode(String str) {
  if (str.substring(0, 2) == "CL") { //clock 값 재설정
    int index1 = str.indexOf(':');
    int index2 = str.indexOf(':', index1 + 1);
    int index3 = str.length();
    SetTimer(str.substring(2, index1).toInt(), str.substring(index1 + 1, index2).toInt(),str.substring(index2 + 1, index3).toInt());
    SetSendQueue("Clock Setting : "+GetTime());
  } else if (str.substring(0, 5) == "POSON") { //Switch On 하기 위한 모터 위치 설정
    SetPosOn(str.substring(5, str.length()).toInt());
    SetSendQueue("posOn Setting : "+String(GetPosOn()));
  } else if (str.substring(0, 6) == "POSOFF") {//Switch Off 하기 위한 모터 위치 설정
    SetPosOff(str.substring(6, str.length()).toInt());
    SetSendQueue("posOff Setting : "+String(GetPosOff()));
  } else if (str.substring(0, 6) == "POSCOM") {//기본 모터 위치 설정
   SetPosCom(str.substring(6, str.length()).toInt());
   SendServoWrite(GetPosCom());
   SetSendQueue("posCom Setting : "+String(GetPosCom()));
  } else if (str.substring(0, 4) == "SEON") {//아침에 자동 불켜기 기능을 위한 설정
    SetSwitOn(str.substring(4, str.length()).toInt());
    SetSendQueue("SEON Setting : "+String(GetSwitOn()));
  } else if (str.substring(0, 5) == "SEOFF") {//저녁에 자동 불끄기 기능을 위한 설정
    SetSwitOff(str.substring(5, str.length()).toInt());
    SetSendQueue("SEOFF Setting : "+String(GetSwitOff()));
  } else if (str == "ON") {
    SetOn_CheckOnAct();
    SetSendQueue("SWITCH ON");
  } else if (str == "OFF") {
    SetOn_CheckOffAct();
    SetSendQueue("SWITCH OFF");
  } else if(str == "TEST" || str == "TERMINAL"){
    mainstep = TERMINAL;
    SetSendQueue("TERMINAL MODE ON");
  } else {
    SetSendQueue("Error Code :  "+str);
  }
}

void WaitMode() {
#ifdef SERIALON
  Serial.println("WatchDog Reset On");
#endif
  if(GetCheckDefault()) { //안드로이드로부터 받은 기본 값이 존재한다면, 연결이 끊기고 30분동안 리셋 대기를 하지 않기 위한 코드
    checkOffBeforeRst = true;
    CheckAfterSwitOn();
    WdtReset();
  }
}
bool GetCheckOffBeforeRst() {
  return checkOffBeforeRst;
}
int GetCheckMode() {
  return mainstep;
}
void SetCheckOffBeforeRst(bool ch) {
  checkOffBeforeRst = ch;
}
