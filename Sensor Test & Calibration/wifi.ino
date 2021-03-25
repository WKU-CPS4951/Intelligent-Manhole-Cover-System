#include <ESP8266WiFi.h>

const char* ssid = "nova 5";
const char* password = "10061006";
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  WiFi.begin(ssid, password);
  Serial.print("Connecting to ");
  Serial.print(ssid);
  Serial.println(" ...");

  int i = 0;
  while (WiFi.status()!=WL_CONNECTED){
    Serial.println(WL_CONNECTED);
    Serial.println(WiFi.status());
    delay(2500);
    Serial.print(i++);
    Serial.print(' ');
    }

   Serial.println("");
   Serial.println("Connection established!");
   Serial.print("IP address: ");
   Serial.println(WiFi.localIP());
}

void loop() {
  // put your main code here, to run repeatedly:

}
