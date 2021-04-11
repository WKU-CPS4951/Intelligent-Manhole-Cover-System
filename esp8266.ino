#include <ESP8266WiFi.h>//安装esp8266arduino开发环境
static WiFiClient espClient;

#include <AliyunIoTSDK.h>//引入阿里云 IoT SDK
//需要安装crypto库、PubSubClient库

//设置产品和设备的信息，从阿里云设备信息里查看
#define PRODUCT_KEY     "a1Ld5sDLkhS"//替换自己的PRODUCT_KEY
#define DEVICE_NAME     "esp8266"//替换自己的DEVICE_NAME
#define DEVICE_SECRET   "62255299311af4ef244f4669a1fb5b36"//替换自己的DEVICE_SECRET
#define REGION_ID       "cn-shanghai"//默认cn-shanghai

#define WIFI_SSID       "nova 5"//替换自己的WIFI
#define WIFI_PASSWD     "10061006"//替换自己的WIFI

unsigned long lastMsMain = 0;
float temperature = 0;
int humidity = 0;
int waterLevel = 0;
bool outsign = false;
String strinput = "";


void setup()
{
  outsign = false;
  Serial.begin(9600);
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH);

  //连接到wifi
  wifiInit(WIFI_SSID, WIFI_PASSWD);

  //初始化 iot，需传入 wifi 的 client，和设备产品信息
  AliyunIoTSDK::begin(espClient, PRODUCT_KEY, DEVICE_NAME, DEVICE_SECRET, REGION_ID);

  //绑定一个设备属性回调，当远程修改此属性，会触发LED函数
  AliyunIoTSDK::bindData("LED", LED);
}

void loop()
{
  char ch;
  while(Serial.available()){
    ch = Serial.read();
    if(isspace(ch) or ch=='=')break;
    if(ch==';') {
      outsign = true;
      break;//read end sign
    }
    strinput+=ch;//input buffer
    Serial.print("what we have read: ");
    Serial.println(strinput+" ");
    Serial.print("str[0]: ");
    Serial.println(strinput[0]);
  }
 
  if(outsign==true){
    int len = strinput.length();
    char temp[len-1];
//    Serial.println("temperature"+strinput+" "+temperature);
    switch(strinput[0]){
      case 'T':
        Serial.print("temp: ");
        for(int i=1; i<len;i++){
            temp[i-1] = strinput[i];
            Serial.print(temp[i-1]);
          }
          Serial.println();
          temperature = atof(temp);
          Serial.println("temperature "+strinput+" "+temperature);
        break;
      case 'H':
        Serial.print("temp: ");
        for(int i=1; i<len;i++){
            temp[i-1] = strinput[i];
            Serial.print(temp[i-1]);
          }
          Serial.println();
          humidity = atoi(temp);
          Serial.println("humidity "+strinput+" "+humidity);
        break;
      case 'W':
        switch(strinput[1]){
          case 'E':waterLevel = 0;break;
          case 'L':waterLevel = 1;break;
          case 'M':waterLevel = 2;break;
          case 'H':waterLevel = 3;break;
        }
        Serial.println("waterlevel "+strinput+" "+waterLevel);
      break;
      default: break;
    }
    strinput = "" ;
    outsign = false;
    AliyunIoTSDK::loop();//必要函数
  
    if (millis() - lastMsMain >= 3000)//每2秒发送一次
    {
      lastMsMain = millis();
  
      //发送LED状态到云平台（高电平：1；低电平：0）
      Serial.println("this is reached");
      AliyunIoTSDK::send("IndoorTemperature", temperature);
      AliyunIoTSDK::send("CurrentHumidity", humidity);
      AliyunIoTSDK::send("WaterLevel", waterLevel);
    }
  }
}

//wifi 连接
void wifiInit(const char *ssid, const char *passphrase)
{
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, passphrase);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(1000);
    Serial.println("WiFi not Connect");
  }
  Serial.println("Connected to AP");
}

//灯的属性修改的回调函数
void LED(JsonVariant L)//固定格式，修改参数l
{
  int LED = L["LED"];//参数l
  if (LED == 0)
  {
    digitalWrite(LED_BUILTIN, HIGH);
  }
  else 
  {
    digitalWrite(LED_BUILTIN, LOW);
  }
  Serial.printf("收到的LED是："); Serial.println(LED);
}
