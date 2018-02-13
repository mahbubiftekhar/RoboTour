#include <NewPing.h>

#define SONAR_NUM 2
#define MAX_DISTANCE 255

#define PING_DELAY 50

#define BAUD 9600

byte sonar_val[SONAR_NUM];

NewPing sonar[SONAR_NUM] = {
  NewPing(3, A3, MAX_DISTANCE),   //S0 - bottom most w coil facing up
  NewPing(5, A2, MAX_DISTANCE)    //S1 
  //NewPing(9, A1, MAX_DISTANCE), //S2
  //NewPing(6, A0, MAX_DISTANCE)  //S3 - top most (PCB centre)
}

void setup() {
  

  Serial.begin(BAUD);
}

void loop() {
  // put your main code here, to run repeatedly:

  for(uint8_t i = 0; i < SONAR_NUM; ++i) {
    delay(50);
    sonar_val[i] = sonar[i].ping_cm();
    Serial.print(i);
    Serial.print(sonar_val[i]);
    Serial.print(',');
    
  }
  Serial.println();

}
