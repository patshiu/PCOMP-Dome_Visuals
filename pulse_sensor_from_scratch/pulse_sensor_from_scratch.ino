

void setup() {
  //serial communication at 9600 bits per second, the other sketch had a different number
  Serial.begin(9600);
  establishContact();
}


void loop() {
  if (Serial.available() > 0) {
      // read the incoming byte:
      int inByte = Serial.read();
      // read the input on analog pin 0:
      int sensorValue = analogRead(A0);
      Serial.println(sensorValue);
      delay(2);
  }  
}

void establishContact() {
 while (Serial.available() <= 0) {
      Serial.println("hello");   // send a starting message
      delay(300);
   }
 }

