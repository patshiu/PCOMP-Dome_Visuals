import processing.serial.*;
Serial myPort;
boolean firstContact = false;
float pulseNum; 
float r;
float g;
float b;

void setup () {
  size(800, 600);        // window size
  // List all the available serial ports
  println(Serial.list());
  pulseNum = 0; 
  String portName = Serial.list()[2];
  myPort = new Serial(this, portName, 9600);
}

void draw() {
  background(r, g, b,10);
  pulseNum = map(pulseNum, 50, 150,200,100);
  r=pulseNum/2;
  g=pulseNum+10;
  b=pulseNum+100;
}

void serialEvent(Serial myPort) {
  // read the serial buffer:
  String myString = myPort.readStringUntil('\n');
  // if you got any bytes other than the linefeed:
  if (myString != null) {

    myString = trim(myString);

    // if you haven't heard from the microncontroller yet, listen:
    if (firstContact == false) {
      if (myString.equals("hello")) {
        myPort.clear();          // clear the serial port buffer
        firstContact = true;     // you've had first contact from the microcontroller
        myPort.write('A');       // ask for more
      }
    }
    // if you have heard from the microcontroller, proceed:
    else {
      pulseNum = float(myString);
      println(myString);
    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}

