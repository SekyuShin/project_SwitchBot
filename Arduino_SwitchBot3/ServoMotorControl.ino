/*
 * Servo Motor Control Functions
 * Servo Motor needs 20ms time, this is pulse time
 */
Servo myservo;

//모터 위치 및 on/off 수행을 위한 구조체 
typedef struct _pos {
  int posOn;
  int posCom;
  int posOff;
  bool checkOnAct; //Switch On 수행
  bool checkOffAct; //Switch Off 수행
  bool checkComAct;  //기본 위치 수행
}POS;

POS pos;
/*
 * bool ServoSetup()
 * description : Servo Motor Default Setting
 * 
 */
void ServoSetup() {
  pos.posOn = 60;
  pos.posCom = 75;
  pos.posOff = 95;
  pos.checkOnAct = false;
  pos.checkOffAct = false;
  pos.checkComAct = false;
}
// 1초 단위로 동작
// Switch On 동작 : 모터가 PosOn위치 -> PosCom 위치
// Switch Off 동작 : 모터가 PosOff위치 -> PosCom 위치
void ServoProc() { 
  if(pos.checkOnAct) {
    SendServoWrite(pos.posOn);
    pos.checkOnAct = false;
    SetOn_CheckComAct();
  } else if(pos.checkOffAct) {
    SendServoWrite(pos.posOff);
    pos.checkOffAct = false;
    SetOn_CheckComAct();
  } else if(pos.checkComAct) {
    SendServoWrite(pos.posCom);
    pos.checkComAct = false;
  } else return;
}
//서보모터의 동작은 20ms의 주기를 가진 PMW형태로 전송하므로 대기시간을 가져야하기 때문에 별도 함수 정의
void SendServoWrite(int p) {
    myservo.attach(servoPin);
    myservo.write(p);
    delay(200);
    myservo.detach();
}

void SetDefaultServoPos(int posOn, int posCom, int posOff) {
  SetPosOn(posOn);
  SetPosCom(posCom);
  SetPosOff(posOff);
}

void SetPosOn(int posOn) {
  pos.posOn = posOn;
}
int GetPosOn() {
  return pos.posOn;
}
void SetPosOff(int posOff) {
  pos.posOff = posOff;
}
int GetPosOff() {
  return pos.posOff;
}
void SetPosCom(int posCom) {
  pos.posCom = posCom;
}
int GetPosCom() {
  return pos.posCom;
}
void SetOn_CheckOnAct() {
  pos.checkOnAct = true;
}
void SetOn_CheckOffAct() {
  pos.checkOffAct = true;
}
void SetOn_CheckComAct() {
  pos.checkComAct = true;
}
void TerminalTestServo(int p) {
  SendServoWrite(p);
}
