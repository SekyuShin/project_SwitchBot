#include <SoftwareSerial.h> // 0,1번핀 제외하고 Serial 통신을 하기 위해 선언
#include<String.h>
#include<Servo.h>
#include<avr/wdt.h>

#define SERIALON 1

//블루투스
#define BT_RXD 3
#define BT_TXD 4
SoftwareSerial mySerial(BT_RXD, BT_TXD); // HC-06 TX=11번핀 , RX=10번핀 연결
int bluetoothStatePin = 5;
bool bluetoothState = 0;

//서보모터
int servoPin = 2;
int pos = 0; //test
int posOn = 10;
int posCommon = 50;
int posOff = 80;
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

 pinMode(bluetoothStatePin,INPUT);
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
   if(mySerial.available()>0) { // mySerial 핀에 입력이 들어오면, 바이트단위로 읽어서 PC로 출력
      String readstr = mySerial.readStringUntil('\n');
      bluetoothCommand(readstr);
   }
   if(millis()-timeVal>=1000){ //1초 단위 출력
          settingTime();
    bluetoothState = digitalRead(bluetoothStatePin);
    if(bluetoothState!=1) {
#ifdef SERIALON
      Serial.print("bluetooth fail :");
      Serial.println(bluetoothState);
#endif
    
      //digitalWrite(resetPin,0); //방어코드 요망 (와치독 이용해보자) https://m.blog.naver.com/PostView.nhn?blogId=simjk98&logNo=221663797431&proxyReferer=https:%2F%2Fwww.google.com%2F
     if(++wdtCount>=3600) {
      wdt_enable(WDTO_15MS);
      delay(15);
     }
    } else {
      //settingTime();
      if(onTimeSetting/100 == hour && onTimeSetting%100 == min) {
        if(!checkAlarm) {
#ifdef SERIALON
           Serial.print("onTimeSetting Set :");
           Serial.print(hour);
           Serial.print(" : ");
           Serial.print(min);
           Serial.print(" : ");
           Serial.println(sec); 
#endif
           mySerial.print("onTimeSetting Set :");
           mySerial.print(hour);
           mySerial.print(" : ");
           mySerial.print(min);
           mySerial.print(" : ");
           mySerial.println(sec); 
        }
        checkAlarm = true;
      }else if(offTimeSetting/100 == hour && offTimeSetting%100 == min) {
        if(!checkAlarm) {
#ifdef SERIALON
           Serial.print("offTimeSetting Set :");
           Serial.print(hour);
           Serial.print(" : ");
           Serial.print(min);
           Serial.print(" : ");
           Serial.println(sec); 
#endif
           mySerial.print("onTimeSetting Set :");
           mySerial.print(hour);
           mySerial.print(" : ");
           mySerial.print(min);
           mySerial.print(" : ");
           mySerial.println(sec); 
        }
        checkAlarm = true;
      } else {
        checkAlarm = false;
      }
    }
   }
}
void settingTime(){
      readTime = millis()/1000;
     if(millis()>=86400000){ //24시간 단위 초기화
       timer0_millis=0;
     }
     timeVal = millis();
   
     sec = readTime%60;
     min = (readTime/60)%60;
     hour = (readTime/(60*60))%24;       
#ifdef SERIALON
     Serial.print(hour);
     Serial.print(" : ");
     Serial.print(min);
     Serial.print(" : ");
     Serial.println(sec);  
#endif
}
void bluetoothCommand(String str) {
  #ifdef SERIALON
    Serial.println(str);
    Serial.println(str.substring(0,2));
  #endif
    if(str.substring(0,2) == "CL") { //clock 초기화
      #ifdef SERIALON
      Serial.println("clock setting");
      #endif
      int index1 = str.indexOf(':'); 
      int index2 = str.indexOf(':',index1+1);   
      int index3 = str.length();

      hour = str.substring(2, index1).toInt();
      min = str.substring(index1+1,index2).toInt();
      sec = str.substring(index2+1,index3).toInt();
  
      timer0_millis = ((long)hour*3600+min*60+sec)*1000;
      timeVal=millis();
    } else if(str.substring(0,2) == "AN") { //servo motor Test
      #ifdef SERIALON
       Serial.println("Angle setting");
      #endif
       pos = str.substring(2,str.length()).toInt();
       #ifdef SERIALON
       Serial.print("servo : ");
       Serial.println(pos);
       #endif
       if(pos >0 && pos<= 180) {
        #ifdef SERIALON
        Serial.print("servo : ");
        Serial.println(pos);
        #endif
        myservo.write(pos);
       }
    } else if(str.substring(0,2) == "CH") { //check time
      #ifdef SERIALON
       Serial.println("Check clock");
       #endif
       mySerial.print(hour);
       mySerial.print(" : ");
       mySerial.print(min);
       mySerial.print(" : ");
       mySerial.println(sec);  
    } else if(str.substring(0,5) == "POSON") {
      
    }else if(str.substring(0,6) == "POSOFF") {
      
    }else if(str.substring(0,6) == "POSCOM"){
      
    }else if(str.substring(0,4) == "SEON") {
      
    }else if(str.substring(0,5) == "SEOFF") {
      
    }
    mySerial.flush();
}
