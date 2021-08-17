#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include "HX711.h" //HX711로드셀 엠프 관련함수 호출
#include <SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>

// 네오픽셀 라이브러리 이름으로 연결
#define NEO_PIN  13   // 네오픽셀 아날로그 핀과 연결된 핀번호 설정
#define NUM_LEDS   3   // 네오픽셀 LED 갯수 설정
Adafruit_NeoPixel warningNeo = Adafruit_NeoPixel(NUM_LEDS, NEO_PIN, NEO_GRB + NEO_KHZ800);

#define calibration_factor -7050.0 // 로드셀 스케일 값 선언
#define DOUT  3 //엠프 데이터 아웃 핀 넘버 선언
#define CLK  7  //엠프 클락 핀 넘버 
HX711 scale(DOUT, CLK); //엠프 핀 선언 

#define OLED_RESET 4
Adafruit_SSD1306 display(OLED_RESET);

#define NUMFLAKES 10
#define XPOS 0
#define YPOS 1
#define DELTAY 2

#define LOGO16_GLCD_HEIGHT 16 
#define LOGO16_GLCD_WIDTH  16 

#define piezo 8
#define LED 12
#define tilt 9

#define BT_RXD 5
#define BT_TXD 4
SoftwareSerial bluetooth(BT_RXD, BT_TXD);

int init_angleX = 0;
int init_angleY = 0;
int init_weight = 0;

int limit = 0;  // 목표치
int sumAlcohol = 0; // 누적합
int nowcup = 0; // 현재 컵의 무게
int lastcup = 0;  // 지난 컵의 무게

int exceed = 0; // 알림이 울린 후에도 술을 따를 경우 flag
int state = 0;  // 알림모드, 측정모드를 구분해주는 flag

int ledBright = 180;  // 초기 밝기 255(최대) 설정
int istilt = 0;

const int MPU_ADDR = 0x68;
int16_t AcX, AcY, AcZ, Tmp, GyX, GyY, GyZ;

double angleAcX, angleAcY, angleAcZ;

const double RADIAN_TO_DEGREE = 180 / 3.14159;

char readGoalData = '0';  // 0: 안읽음, 1: 읽음
char modeFlag = 'n';  // alarm mode = 'a', measure mode = 'm'
char temp = 0;

void setup() {
  pinMode(piezo, OUTPUT);
  warningNeo.setBrightness(ledBright); // 밝기 설정 (0~255)
  warningNeo.begin();                  // 네오픽셀 시작
  warningNeo.show();                   // 네오픽셀 
  
  initSensor();
  Serial.begin(9600);
  bluetooth.begin(9600); // 블루투스
  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);  // initialize with the I2C addr 0x3C (for the 128x32)
  Serial.println("HX711 scale TEST");  
  scale.set_scale(calibration_factor);  //스케일 지정 
  scale.tare();  //스케일 설정
  Serial.println("Readings:");

  initCup();
}

void loop() {
  while(state == 0){
    display.setTextSize(2);                    // text1은 누적, text2는 현재
    display.setTextColor(WHITE);
    display.setCursor(10,0);
    display.clearDisplay();
    display.println("choose");
    display.setCursor(10,16);
    display.print("mode");
    display.display();
        
    if (bluetooth.available()) {
      temp = bluetooth.read();
      Serial.println(temp);
      //Serial.print(temp);
      Serial.println("do2");
    }
    if (temp == 'a') {
      display.setTextSize(2);                    // text1은 누적, text2는 현재
      display.setTextColor(WHITE);
      display.setCursor(10,0);
      display.clearDisplay();
      display.println("Alram");
      display.setCursor(10,16);
      display.print("mode");
      display.display();

      delay(1000);
        
      state = 1;
    }
    else if (temp == 'm') {
        display.setTextSize(2);                    // text1은 누적, text2는 현재
        display.setTextColor(WHITE);
        display.setCursor(10,0);
        display.clearDisplay();
        display.println("Measure");
        display.setCursor(10,16);
        display.print("mode");
        display.display();
        
        delay(1000);
        
        state = 2;
    }
  }
  if(state == 1){
    alrammode();
    if (bluetooth.available()) {
      temp = bluetooth.read();
      //Serial.print(temp);
      Serial.println(temp);
      Serial.println("do2");
      if (temp == 'n') {
        warningNeo.setPixelColor(0, 0, 0, 0);
        warningNeo.setPixelColor(1, 0, 0, 0);
        warningNeo.setPixelColor(2, 0, 0, 0);
        warningNeo.show();
        sumAlcohol = 0;
        limit = 0;
        state = 0;
      }
    }
  }
  else if(state == 2){
    measuremode();
    if (bluetooth.available()) {
      temp = bluetooth.read();
      //Serial.print(temp);
      Serial.println(temp);
      Serial.println("do2");
      if (temp == 'n') {
        sumAlcohol = 0;
        limit = 0;
        state = 0;
      }
    }    
  }
}

void measuremode(){
  lastcup = nowcup;
  // 현재 잔의 무게가 증가하였다면 lastcup을 증가한 값으로 갱신해준다.
  
  Serial.print("\n Reading: ");
  Serial.print(nowcup);  //무게 출력 
  Serial.println(" lbs"); //단위

  getData();
  angleAcY = atan(-AcX / sqrt(pow(AcY, 2) + pow(AcZ, 2)));
  angleAcY *= RADIAN_TO_DEGREE;
  angleAcY = abs(angleAcY);
  angleAcX = atan(AcY / sqrt(pow(AcX, 2) + pow(AcZ, 2)));
  angleAcX *= RADIAN_TO_DEGREE;
  angleAcX = abs(angleAcX);
  Serial.println(angleAcY);
  Serial.println(angleAcX);
  
  while(angleAcX > 30 || angleAcY > 30){ // 잔이 기울어져 있는 경우
    int text = scale.get_units()*20;
    Serial.print("\n read value: ");
    Serial.print(text);
    istilt = 1;
    getData();  // 로드셀의 값이 음수가 아닌 상태에서 기울었을 때에도 정확한 무게 측정이 어려우므로 대기한다.
  }

  while(scale.get_units() < -2){ // 로드셀의 무게가 마이너스인경우
    ;  // 들려있는 상태를 말하며 이 때 동안은 다른 동작을 하지 않고 대기한다.
  }
  
  nowcup = scale.get_units()*20 - init_weight;
   // nowcup =  scale.get_units()*20 - lastcup;
  testscrolltext(sumAlcohol, nowcup);
  if(nowcup < lastcup && istilt == 1){
    sumAlcohol += (lastcup - nowcup);
    // 누적량과 현재 잔의 무게 출력
    String str = String(lastcup - nowcup);
    char* str2 = (char *)malloc(sizeof(str));
    for (int i = 0; i < sizeof(str); i++) {
      str2[i] = str[i];
    }
    bluetooth.write(str2);
    free(str2); 
    // 블루투스로 값 전송
    istilt = 0;
  }
}

void alrammode(){
  while(limit == 0){
    int t = 0;
    while (bluetooth.available()) {
      t = bluetooth.read() - 48;
      if(t == -2){
        break;  
      }
      limit = limit*10 + t;
    }
    Serial.print("\n limit = ");
    Serial.print(limit);
  }
  
  lastcup = nowcup;
  // 현재 잔의 무게가 증가하였다면 lastcup을 증가한 값으로 갱신해준다.
  
  Serial.print("\n Reading: ");
  Serial.print(nowcup);  //무게 출력 
  Serial.println(" lbs"); //단위

  getData();
  Serial.println(angleAcY);
  Serial.println(angleAcX);
  
  while(angleAcX > 30 || angleAcY > 30){ // 잔이 기울어져 있는 경우
    int text = scale.get_units()*20;
    Serial.print("\n read value: ");
    Serial.print(text);
    istilt = 1;
    getData();  // 로드셀의 값이 음수가 아닌 상태에서 기울었을 때에도 정확한 무게 측정이 어려우므로 대기한다.
  }

  while(scale.get_units() < -2){ // 로드셀의 무게가 마이너스인경우
    ;  // 들려있는 상태를 말하며 이 때 동안은 다른 동작을 하지 않고 대기한다.
  }
  
  nowcup = scale.get_units()*20 - init_weight;
 // nowcup =  scale.get_units()*20 - lastcup;
  testscrolltext(sumAlcohol, nowcup);
  if(nowcup < lastcup && istilt == 1){
    sumAlcohol += (lastcup - nowcup);
    // 누적량과 현재 잔의 무게 출력
    String str = String(lastcup - nowcup);
    char* str2 = (char *)malloc(sizeof(str));
    for (int i = 0; i < sizeof(str); i++) {
      str2[i] = str[i];
    }
    bluetooth.write(str2);
    free(str2);
    // 블루투스로 값 전송
    istilt = 0;
    exceed = 1;
  }
  // 현재 잔의 무게가 줄었다면
  // 잔의 무게차이를 sumAlcohol에 더해주어 누적값을 구한다.

  if(sumAlcohol >= limit && exceed == 1){
  // 술잔의 무게가 올라간 상태에서 목표치 이상이 되었을 경우 부저가 울린다.
  // 목표치를 초과한 이후에도 술을 따르게 되면 무게가 증가하므로 부저를 계속 울릴 수 있다.
      for(int i = 0; i < 5; i++){
        //tone(piezo, 349); // 파
        warningNeo.setPixelColor((i%3), 30, 0, 0);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(빨강)
        warningNeo.setPixelColor(((i+1)%3), 30, 0, 0);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(빨강)
        warningNeo.setPixelColor(((i+2)%3), 0, 0, 0);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(빨강)
        warningNeo.show();
        delay(500);
        //tone(piezo, 261); // 4옥타브 도
        digitalWrite(LED, LOW);
        delay(500); 
      }
      noTone(piezo);
      exceed = 0;
  }
  else if(sumAlcohol >= (limit*0.6)){   // 목표치의 60% 이상 주황색 LED
    warningNeo.setPixelColor(0, 150, 30, 0);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(주황)
    warningNeo.setPixelColor(1, 150, 30, 0);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(주황)
    warningNeo.setPixelColor(2, 150, 30, 0);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(주황)
    warningNeo.show();
  }
  else if(sumAlcohol >= (limit*0.3)){   // 목표치의 30~60% 초록색 LED
    warningNeo.setPixelColor(0, 0, 30, 0);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(초록)
    warningNeo.setPixelColor(1, 0, 30, 0);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(초록)
    warningNeo.setPixelColor(2, 0, 30, 0);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(초록)
    warningNeo.show();
  }
  else{ // 목표치의 0~30% 파랑색 LED
    warningNeo.setPixelColor(0, 0, 0, 30);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(파랑)
    warningNeo.setPixelColor(1, 0, 0, 30);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(파랑)
    warningNeo.setPixelColor(2, 0, 0, 30);  // (A,R,G,B) A번째 LED를 RGB (0~255) 만큼의 밝기로 켭니다(파랑)
    warningNeo.show();
  }
}

int putInt(){       // 시리얼창에 입력한 숫자를 반환
  int inNum = -1;
  Serial.println(limit);
  while(1){
    if(Serial.available()){
      inNum = Serial.parseInt();
      Serial.println(inNum);
      if(inNum != -1){
        break;
      }
    }
  }
  return inNum;  
}

void testscrolltext(int text1, int text2) {  // OLED에 누적과 현재의 무게를 출력
  display.setTextSize(2);                    // text1은 누적, text2는 현재
  display.setTextColor(WHITE);
  display.setCursor(10,0);
  display.clearDisplay();
  display.print("Sum:");
  display.println(String(text1));
  display.setCursor(10,16);
  display.print("now:");
  display.println(String(text2));
  display.display();
}

void initSensor() {   // 자이로 센서 초기화
  Wire.begin();
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
}

void initCup(){
  display.setTextSize(2);                    // text1은 누적, text2는 현재
  display.setTextColor(WHITE);
  display.setCursor(10,0);
  display.clearDisplay();
  display.println("Wait for");
  display.setCursor(10,16);
  display.print("Initializing");
  display.display();

  for(int i = 0; i < 5; i++){
    Wire.beginTransmission(MPU_ADDR);
    Wire.write(0x3B);
    Wire.endTransmission(false);
    Wire.requestFrom(MPU_ADDR, 14, true);

    AcX = Wire.read() << 8 | Wire.read();
    AcY = Wire.read() << 8 | Wire.read();
    AcZ = Wire.read() << 8 | Wire.read();
    Tmp = Wire.read() << 8 | Wire.read();
    GyX = Wire.read() << 8 | Wire.read();
    GyY = Wire.read() << 8 | Wire.read();
    GyZ = Wire.read() << 8 | Wire.read();
  
    angleAcY = atan(-AcX / sqrt(pow(AcY, 2) + pow(AcZ, 2)));
    angleAcY *= RADIAN_TO_DEGREE;
    angleAcX = atan(AcY / sqrt(pow(AcX, 2) + pow(AcZ, 2)));
    angleAcX *= RADIAN_TO_DEGREE;

    init_angleX += angleAcX;
    init_angleY += angleAcY;

    init_weight += scale.get_units()*20;                                        
    delay(300);
  }
  init_angleX /= 5;
  init_angleY /= 5;
  init_weight /= 5;
}

void getData() {      // 자이로 센서로 가속도 및 각속도 측정
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_ADDR, 14, true);

  AcX = Wire.read() << 8 | Wire.read();
  AcY = Wire.read() << 8 | Wire.read();
  AcZ = Wire.read() << 8 | Wire.read();
  Tmp = Wire.read() << 8 | Wire.read();
  GyX = Wire.read() << 8 | Wire.read();
  GyY = Wire.read() << 8 | Wire.read();
  GyZ = Wire.read() << 8 | Wire.read();

  
  angleAcY = atan(-AcX / sqrt(pow(AcY, 2) + pow(AcZ, 2)));
  angleAcY *= RADIAN_TO_DEGREE;
  angleAcY -= init_angleY;
  angleAcY = abs(angleAcY);
  angleAcX = atan(AcY / sqrt(pow(AcX, 2) + pow(AcZ, 2)));
  angleAcX *= RADIAN_TO_DEGREE;
  angleAcX -= init_angleX;
  angleAcX = abs(angleAcX);
}
