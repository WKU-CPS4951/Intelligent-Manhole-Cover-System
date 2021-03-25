//humidity class
#include <SimpleDHT.h>

// for DHT11, 
//      VCC: 5V or 3V
//      GND: GND
//      DATA: 2
int pinDHT11 = 2;
SimpleDHT11 dht11(pinDHT11);

//water level
int resval = 0;  // holds the value
int respin = A5; // sensor pin used


void setup() {
  // start the serial console
  Serial.begin(9600);
}

void loop() {
  //loop for humidity sensor
  // start working...
  Serial.println("=================");
  delay(1000);
  // read without samples.
  byte temperature = 0;
  byte humidity = 0;
  int err = SimpleDHTErrSuccess;
  if ((err = dht11.read(&temperature, &humidity, NULL)) != SimpleDHTErrSuccess) {
    Serial.print("Read DHT11 failed, err="); Serial.print(SimpleDHTErrCode(err));
    Serial.print(","); Serial.println(SimpleDHTErrDuration(err)); delay(1000);
    return;
  }
  Serial.print("Temperature: ");
  Serial.print((int)temperature); Serial.println("°C");
  //make the delay consistant to humidity sensor
  delay(1000);
  Serial.print("Humidity: "); 
  Serial.print((int)humidity); Serial.println("%");
  
  // DHT11 sampling rate is 1HZ.
  delay(1000);

  //loop for water level sensor
   resval = analogRead(respin); //Read data from analog pin and store it to resval variable
  if (resval<=100){ Serial.println("Water Level: Empty"); }
  else if (resval>100 && resval<=600){ Serial.println("Water Level: Low"); }
  else if (resval>600 && resval<=660){ Serial.println("Water Level: Medium"); } 
  else if (resval>660){ 
    Serial.println("Water Level: High"); 
  }
  delay(1000); 
}
