#include <SoftwareSerial.h>  
 
const int pinTx = 5;  
const int pinRx = 4;  
 
SoftwareSerial   bluetooth( pinRx, pinTx );  
 
void  setup()
{
  bluetooth.begin(9600);  // 블루투스 통신 초기화 (속도= 9600 bps)
  Serial.begin(9600);
}
 
 
void  loop()
{
  
   if (bluetooth.available()) {
      Serial.write(bluetooth.read());  //블루투스측 내용을 시리얼모니터에 출력
    }
    if (Serial.available()) {
      bluetooth.write(Serial.read());  //시리얼 모니터 내용을 블루추스 측에 WRITE
    }
}
