
/*
 * Serial Communication functions 
 */
SoftwareSerial mySerial(BT_RXD, BT_TXD); 
/*
 * check Communication between Com and Arduino
 * => check Serial0 Option
 */


//송신 및 수신 데이터에 대한 큐
QUEUE receiveQueue;
QUEUE sendQueue;

void SerialSetup() {
#ifdef SERIALON
  Serial.begin(9600);
#endif
  pinMode(BTSTATEPIN, INPUT);
  mySerial.begin(9600); // 통신 속도 9600bps로 블루투스 시리얼 통신 시작
  receiveQueue.Reset();
  sendQueue.Reset();
}

bool ReadBT() { //블루투스로 부터 받은 데이터를 receiveQueue에 저장 및 블루투스 상태 리턴
  if(mySerial.available()) receiveQueue.push(mySerial.readStringUntil('\n'));
  return digitalRead(BTSTATEPIN);
}

String GetReceiveQueue() {
  return receiveQueue.pop();
}
void SetSendQueue(String str) {
    sendQueue.push(str);
}
void RunSendQueue() { //sendQueue에 저장된 데이터를 전부 송신
  String str;
  while(true){
    str = sendQueue.pop();
    if(str == "") break;
    mySerial.println(str);
  }
}
