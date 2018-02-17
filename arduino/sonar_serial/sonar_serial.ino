#include <NewPing.h>

#define SONAR_NUM 2
#define MAX_DISTANCE 255

#define PING_DELAY 250

#define BAUD 9600

uint16_t sonar_val[SONAR_NUM];


NewPing sonar[SONAR_NUM] = {
  NewPing(3, A3, MAX_DISTANCE),   //S0 - bottom most w coil facing up
  NewPing(5, A2, MAX_DISTANCE)    //S1 
  //NewPing(9, A1, MAX_DISTANCE), //S2
  //NewPing(6, A0, MAX_DISTANCE)  //S3 - top most (PCB centre)
};

String inputString = "";
bool newCommand = false;

void setup() {
  Serial.begin(BAUD);

  inputString.reserve(16);
}

void loop() {
  // put your main code here, to run repeatedly:
  
  for(uint8_t i = 0; i < SONAR_NUM; ++i) {
    //delay(PING_DELAY);  
    sonar_val[i] = i;// sonar[i].ping_cm();
  }

  if(newCommand == true) {
    //Serial.print(inputString);
    sendSensorData();
    newCommand = false;
    inputString = "";
  }
  
  
 
}

void serialEvent() {
  while(Serial.available()) {
    char inChar = (char)Serial.read();
    inputString += inChar;
    if(inChar == '\n') {
      newCommand = true;
    }
  }
}

void sendSensorData() {

  for(uint8_t i = 0; i < SONAR_NUM; ++i) {
    Serial.print(i);
    Serial.print(':');
    Serial.print(sonar_val[i]);
    Serial.print(',');
  }
  Serial.print('\n');
}

