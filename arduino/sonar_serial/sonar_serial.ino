#include <LiquidCrystal.h>

#include <NewPing.h>

#define SONAR_NUM 2
#define MAX_DISTANCE 255

#define PING_DELAY 50

#define BAUD 9600

uint16_t sonar_val[SONAR_NUM];
unsigned long last_millis;


NewPing sonar[SONAR_NUM] = {
  NewPing(3, A3, MAX_DISTANCE),   //S0 - bottom most w coil facing up
  NewPing(5, A2, MAX_DISTANCE)    //S1 
  //NewPing(9, A1, MAX_DISTANCE), //S2
  //NewPing(6, A0, MAX_DISTANCE)  //S3 - top most (PCB centre)
};

String inputString = "";
bool newCommand = false;

uint8_t sonar_index = 0;

void setup() {
  Serial.begin(BAUD);

  inputString.reserve(16);
}

void loop() {
  // put your main code here, to run repeatedly:

  if(millis() - last_millis >= PING_DELAY) {
    sonar_val[sonar_index] = sonar[sonar_index].ping_cm();
    if(sonar_index++ == SONAR_NUM) sonar_index = 0;
    last_millis = millis();
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
    Serial.print('s');
    Serial.print(i);
    Serial.print(':');
    Serial.print(sonar_val[i]);
    Serial.print(',');
  }
  Serial.print('\n');
}

