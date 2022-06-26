#include<String.h>
#include"arduino.h"
#define QUEUE_MAX 11
#define BLANK ""

/* 
 *  implement to queue for Bluetooth data
 */
class QUEUE
{
 private:
    String queue_array[QUEUE_MAX];
    int rear = 0;   //to write
    int front = 0;//to read
    bool isFull();
    bool empty();
 public:
    void push(String str) ;
    String pop() ;
    void Reset();
};
