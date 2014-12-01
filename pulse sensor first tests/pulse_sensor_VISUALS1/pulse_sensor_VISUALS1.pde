import processing.serial.*;
Serial myPort;
boolean firstContact = false;
float pulseVal; 
float r;
float g;
float b;

void setup () {
  size(800, 600);        // window size
  // List all the available serial ports
  println(Serial.list());
  pulseVal = 0; 
  String portName = Serial.list()[3];
  myPort = new Serial(this, portName, 9600);
}

void draw() {
  background(r, g, b,10);
  pulseVal = map(pulseVal, 50, 150,200,100);
  r=pulseVal/2;
  g=pulseVal+10;
  b=pulseVal+100;
}

void serialEvent(Serial myPort) {
  // read the serial buffer:
  String myString = myPort.readStringUntil('\n');
  // if you got any bytes other than the linefeed:
  if (myString != null) {

    myString = trim(myString);

    // if you haven't heard from the microcontroller yet, listen:
    if (firstContact == false) {
      if (myString.equals("hello")) {
        myPort.clear();          // clear the serial port buffer
        firstContact = true;     // you've had first contact from the microcontroller
        myPort.write('A');       // ask for more
      }
    }
    // if you have heard from the microcontroller, proceed:
    else {
      pulseVal = float(myString);
      println(myString);
    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}

