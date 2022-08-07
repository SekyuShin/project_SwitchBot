
#include "queue.h" 

void QUEUE::push(String str) {
   if(!isFull()) {
      queue_array[rear] = str;
      rear = (rear + 1) % QUEUE_MAX;
    }
}
String QUEUE::pop() {
  String tmpStr;
  if(!empty()) {
    tmpStr = queue_array[front];
    front = (front + 1) % QUEUE_MAX;
    return tmpStr;
  }
  return "";
}
void QUEUE::Reset(){
  rear = 0;   
  front = 0;
  for(int i=0;i<QUEUE_MAX;i++){
      queue_array[i]=BLANK;   
  }
}
bool QUEUE::isFull() {
  if((rear + 1) % QUEUE_MAX == front) return true;
  else return false;
}
bool QUEUE::empty() {
  if(rear == front) return true;
  else return false;
}
