#include <Wire.h>
#include <NewPing.h>

#define SONAR_NUM 2
#define MAX_DISTANCE 255
#define PING_DELAY 50

#define LINE_NUM 6


#define BAUD 115200
#define TWI_CLK 400000

uint16_t sonar_val[SONAR_NUM];
unsigned long last_millis;

uint8_t line_val[LINE_NUM];


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
  Wire.begin();
  Wire.setClock(TWI_CLK);
  
  inputString.reserve(16);
  pinMode(LED_BUILTIN, OUTPUT);

}

void loop() {
  // put your main code here, to run repeatedly:

  digitalWrite(LED_BUILTIN, HIGH);
  Wire.requestFrom(0x32, LINE_NUM+1, true);

  
  while(Wire.available()) {
    byte c = Wire.read();
    for(uint8_t i = 0; i < LINE_NUM; ++i) {
      if(Wire.available()) {
        line_val[i] = Wire.read();
      }
    }
  }
  digitalWrite(LED_BUILTIN, LOW);

  if(millis() - last_millis >= PING_DELAY) {
    sonar_val[sonar_index] = sonar[sonar_index].ping_cm();
    if(sonar_index++ == SONAR_NUM) sonar_index = 0;
    last_millis = millis();
  } else {
    delay(1);
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
      while(Serial.available()) Serial.read();
    }
  }
}

void sendSensorData() {

  for(uint8_t i = 0; i < LINE_NUM; ++i) {
    Serial.print('l');
    Serial.print(i);
    Serial.print(':');
    Serial.print(line_val[i]);
    Serial.print(',');
  }

  for(uint8_t i = 0; i < SONAR_NUM; ++i) {
    Serial.print('s');
    Serial.print(i);
    Serial.print(':');
    Serial.print(sonar_val[i]);
    Serial.print(',');
  }
  Serial.print('\n');
}

