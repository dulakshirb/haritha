//libraries
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include "DHTesp.h"

DHTesp dht;
#ifdef ESP32
#pragma message(THIS IS FOR ESP8266 ONLY!)
#error Select ESP8266 board.
#endif

#define FIREBASE_HOST "harithamobileapp-1d05e-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "FVotxRZCe3ISqQks6inKF9003HIbUQamshuKPLQC"
#define WIFI_SSID "DN_HOME_FIBER"
#define WIFI_PASSWORD "KenethDihen@2020"

//define ports
#define GAS_A0 A0 //A0
const unsigned char BUZZER = 14; //D5
#define RED 4 //D2
#define GREEN 5 //D1
#define BLUE 16 //D0
#define ON_OFF_LED 0 //D3

//timing
int two_sec = 2000;
int sec = 1000;
int half_sec = 500;

//global variables
float humidity, temperature;
String mAuth = "eRnfl0eZrOdYV7DOMoAIrtSPCOG2";
int on_off;

void setup() {
  Serial.begin(9600);
  delay(half_sec);

  //INPUT or OUTPUT
  pinMode(GAS_A0, INPUT);
  pinMode(RED, OUTPUT);
  pinMode(GREEN, OUTPUT);
  pinMode(BLUE, OUTPUT);
  pinMode(ON_OFF_LED, OUTPUT);
  pinMode(BUZZER, OUTPUT);

  //connect to wifi
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    delay(sec);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected..!");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());

  //connect to firebase
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  if (Firebase.failed()) {
      Serial.print("setting /message failed:");
      Serial.println(Firebase.error());
      return;
    }

  //default values
  digitalWrite(RED, LOW);
  digitalWrite(GREEN, LOW);
  digitalWrite(BLUE, LOW);
  noTone(BUZZER);

  dht.setup(13, DHTesp::DHT11); // D7

  on_off = Firebase.getInt("Farmer/BioGas/" + mAuth + "/unit/status");
}

void loop() {
  on_off = Firebase.getInt("Farmer/BioGas/" + mAuth + "/unit/status");

  if (on_off == 1) {
    digitalWrite(ON_OFF_LED, HIGH);

    //humidity & temperature
    humidity = dht.getHumidity();
    temperature = dht.getTemperature();

    unsigned int gasSensorValue = analogRead(GAS_A0);
    int gasLowLevel = 65;

    //display gas level
    Serial.print("GAS LEVEL : ");
    Serial.println(gasSensorValue);

    //display temerature and humidity
    Serial.print("Status: ");
    Serial.print(dht.getStatusString());
    Serial.print("\t");
    Serial.print("Humidity (%): ");
    Serial.print(humidity, 1);
    Serial.print("\t");
    Serial.print("Temperature (C): ");
    Serial.print(temperature, 1);
    Serial.print("\t");
    Serial.println();

    if (gasSensorValue > 300) {
      digitalWrite(GREEN, HIGH);
      tone(BUZZER, 1000);
      delay(half_sec);
      digitalWrite(GREEN, LOW);
      noTone(BUZZER);
      delay(half_sec);
    }
    else {
      digitalWrite(RED, HIGH);
      delay(half_sec);
      digitalWrite(RED, LOW);
      delay(half_sec);
    }

    //set value in firebase
    Firebase.setFloat("Farmer/BioGas/" + mAuth + "/unit/lpg", gasSensorValue);
    Firebase.setFloat("Farmer/BioGas/" + mAuth + "/unit/temperature", (temperature));
    Firebase.setFloat("Farmer/BioGas/" + mAuth + "/unit/humidity", (humidity));
    if (Firebase.failed()) {
      Serial.print("setting /message failed:");
      Serial.println(Firebase.error());
      return;
    }

    delay(two_sec);
  }

  if (on_off == 0) {
    digitalWrite(ON_OFF_LED, LOW);
  }
}
