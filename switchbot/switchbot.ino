#include <SoftwareSerial.h> // 0,1번핀 제외하고 Serial 통신을 하기 위해 선언
#include<String.h>
#include<Servo.h>
#include<avr/wdt.h>

#define SERIALON 1

//블루투스
#define BT_RXD 3
#define BT_TXD 4
SoftwareSerial mySerial(BT_RXD, BT_TXD); 
int bluetoothStatePin = 5;
bool bluetoothState = 0;
bool checkInit = false;
//서보모터
int servoPin = 2;
int pos = 0; //test
int posOn = 10;
int posCommon = 50;
int posOff = 80;
bool checkPosOn = false;
bool checkPosOff = false;
bool checkPosCommon = true;

Servo myservo;

//리셋핀
//int resetPin = 6;
int wdtCount = 0; //왓치독 카운트

//시간
extern volatile unsigned long timer0_millis; //타이머변수
unsigned long timeVal = 0; //이전시간
unsigned long readTime; //현재타이머시간
int hour, min, sec;

int onTimeSetting = 500;
int offTimeSetting = 2300;
bool checkAlarm = false;
void setup()
{
#ifdef SERIALON
  Serial.begin(9600); // 통신 속도 9600bps로 PC와 시리얼 통신 시작
#endif
  myservo.attach(servoPin);
  myservo.write(posCommon);
  pinMode(bluetoothStatePin, INPUT);
  mySerial.begin(9600); // 통신 속도 9600bps로 블루투스 시리얼 통신 시작

  //digitalWrite(resetPin,1);
  //pinMode(resetPin,OUTPUT);

}

void loop()
{
  //  if (mySerial.available()) {
  //    Serial.write(mySerial.read());  //블루투스측 내용을 시리얼모니터에 출력
  //  }
  //  if (Serial.available()) {
  //    mySerial.write(Serial.read());  //시리얼 모니터 내용을 블루추스 측에 WRITE
  //  }
  if (mySerial.available() > 0) { // mySerial 핀에 입력이 들어오면, 바이트단위로 읽어서 PC로 출력
    String readstr = mySerial.readStringUntil('\n');
    bluetoothCommand(readstr);
  }
  if (millis() - timeVal >= 1000) { //1초 단위 출력
    settingTime();
    bluetoothState = digitalRead(bluetoothStatePin);
    if (bluetoothState != 1) {
#ifdef SERIALON
      Serial.print("bluetooth fail :");
      Serial.println(bluetoothState);
#endif
      //digitalWrite(resetPin,0); //방어코드 요망 (와치독 이용해보자) https://m.blog.naver.com/PostView.nhn?blogId=simjk98&logNo=221663797431&proxyReferer=https:%2F%2Fwww.google.com%2F
      if (++wdtCount >= 3600) {
        wdt_enable(WDTO_15MS);
        delay(15);
      }
    } else {
      wdtCount = 0;
      if (checkInit) {
        //settingTime();
        if (onTimeSetting / 100 == hour && onTimeSetting % 100 == min)  {
          sendAlarm("onTimeSetting");
        }
        else if (offTimeSetting / 100 == hour && offTimeSetting % 100 == min) {
          sendAlarm("offTimeSetting");
        }
        else{
          checkAlarm = false;
        }
        checkPos();
      }else{
#ifdef SERIALON
      Serial.print("not init Setting :");
      Serial.println(checkInit);
#endif
      mySerial.print("not init Setting :");
      mySerial.println(checkInit);
      }
    }
  }
}

void checkPos() {
  if (!checkPosCommon) {
    if (checkPosOn) {
      checkPosOn = false;
      myservo.write(posOn);
    } else if (checkPosOff) {
      checkPosOff = false;
      myservo.write(posOff);
    } else {
      checkPosCommon = true;
      myservo.write(posCommon);
    }
  }
}

void sendAlarm(String str) {
  if (!checkAlarm) {
#ifdef SERIALON
    Serial.print(str);
    Serial.print(" Set :");
    Serial.print(hour);
    Serial.print(" : ");
    Serial.print(min);
    Serial.print(" : ");
    Serial.println(sec);
#endif
    mySerial.print(str);
    mySerial.print(" Set :");
    mySerial.print(hour);
    mySerial.print(" : ");
    mySerial.print(min);
    mySerial.print(" : ");
    mySerial.println(sec);
    if(str == "onTimeSetting") {
      checkPosOn = true;
    } else if(str == "offTimeSetting") {
      checkPosOff = true;
    }
    checkPosCommon = false;
  }
  checkAlarm = true;
}

void settingTime() {
  readTime = millis() / 1000;
  if (millis() >= 86400000) { //24시간 단위 초기화
    timer0_millis = 0;
  }
  timeVal = millis();

  sec = readTime % 60;
  min = (readTime / 60) % 60;
  hour = (readTime / (60 * 60)) % 24;
#ifdef SERIALON
  Serial.print(hour);
  Serial.print(" : ");
  Serial.print(min);
  Serial.print(" : ");
  Serial.print(sec);
  //Serial.println(sec);
  //test
   Serial.print(" / ");
   Serial.print(posOn);
    Serial.print(" / ");
  Serial.print(posCommon);
   Serial.print(" / ");
  Serial.print(posOff);
   Serial.print(" / ");
  Serial.print(onTimeSetting);
   Serial.print(" / ");
  Serial.println(offTimeSetting);
#endif
}
void bluetoothCommand(String str) {
#ifdef SERIALON
  Serial.println(str);
#endif
  if(!checkInit && str.substring(0, 4) == "INIT") { //INIT hour:min:sec:posOn:posCommon:posOff:onTimeSetting:offTimeSetting
    int index1 = str.indexOf(':');
    int index2 = str.indexOf(':', index1 + 1);
    int index3 = str.indexOf(':', index2 + 1);
    int index4 = str.indexOf(':', index3 + 1);
    int index5 = str.indexOf(':', index4 + 1);
    int index6 = str.indexOf(':', index5 + 1);
    int index7 = str.indexOf(':', index6 + 1);
    int index8 = str.length();
    hour = str.substring(4, index1).toInt();
    min = str.substring(index1 + 1, index2).toInt();
    sec = str.substring(index2 + 1, index3).toInt();
    timer0_millis = ((long)hour * 3600 + min * 60 + sec) * 1000;
    timeVal = millis();
    posOn = str.substring(index3 + 1, index4).toInt();
    posCommon = str.substring(index4 + 1, index5).toInt();
    posOff = str.substring(index5 + 1, index6).toInt();
    onTimeSetting = str.substring(index6 + 1, index7).toInt();
    offTimeSetting = str.substring(index7 + 1, index8).toInt();
    checkInit = true;
  }
  else if (str.substring(0, 2) == "CL") { //clock 초기화
#ifdef SERIALON
    Serial.println("clock setting");
#endif
    int index1 = str.indexOf(':');
    int index2 = str.indexOf(':', index1 + 1);
    int index3 = str.length();

    hour = str.substring(2, index1).toInt();
    min = str.substring(index1 + 1, index2).toInt();
    sec = str.substring(index2 + 1, index3).toInt();

    timer0_millis = ((long)hour * 3600 + min * 60 + sec) * 1000;
    timeVal = millis();
  } else if (str.substring(0, 2) == "AN") { //servo motor Test
#ifdef SERIALON
    Serial.println("Angle setting");
#endif
    pos = str.substring(2, str.length()).toInt();
#ifdef SERIALON
    Serial.print("servo : ");
    Serial.println(pos);
#endif
    if (pos > 0 && pos <= 180) {
#ifdef SERIALON
      Serial.print("servo : ");
      Serial.println(pos);
#endif
      myservo.write(pos);
    }
  } else if (str.substring(0, 2) == "CH") { //check time
#ifdef SERIALON
    Serial.println("Check clock");
#endif
    mySerial.print(hour);
    mySerial.print(" : ");
    mySerial.print(min);
    mySerial.print(" : ");
    mySerial.println(sec);
  } else if (str.substring(0, 5) == "POSON") {
    posOn = str.substring(5, str.length()).toInt();
#ifdef SERIALON
    Serial.println("posOn setting");
    Serial.print("posOn : ");
    Serial.println(posOn);
#endif
  } else if (str.substring(0, 6) == "POSOFF") {
    posOff = str.substring(6, str.length()).toInt();
#ifdef SERIALON
    Serial.println("posOff setting");
    Serial.print("posOff : ");
    Serial.println(posOff);
#endif
  } else if (str.substring(0, 6) == "POSCOM") {
    posCommon = str.substring(6, str.length()).toInt();
#ifdef SERIALON
    Serial.println("posCommon setting");
    Serial.print("posCommon : ");
    Serial.println(posCommon);
#endif
  } else if (str.substring(0, 4) == "SEON") {
    onTimeSetting = str.substring(4, str.length()).toInt();
#ifdef SERIALON
    Serial.println("onTimeSetting setting");
    Serial.print("onTimeSetting : ");
    Serial.println(onTimeSetting);
#endif
  } else if (str.substring(0, 5) == "SEOFF") {
    offTimeSetting = str.substring(5, str.length()).toInt();
#ifdef SERIALON
    Serial.println("offTimeSetting setting");
    Serial.print("offTimeSetting : ");
    Serial.println(offTimeSetting);
#endif
  } else if (str == "ON") {
#ifdef SERIALON
    Serial.println("ON start ");
#endif
    checkPosOn = true;
    checkPosCommon = false;
  } else if (str == "OFF") {
#ifdef SERIALON
    Serial.println("OFF start ");
#endif
    checkPosOff = true;
    checkPosCommon = false;
  } else {
#ifdef SERIALON
    Serial.print("Error Code :  ");
    Serial.println(str);
#endif
  }
  mySerial.flush();
}
