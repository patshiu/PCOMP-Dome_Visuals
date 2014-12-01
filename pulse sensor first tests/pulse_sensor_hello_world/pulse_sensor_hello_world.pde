import processing.serial.*;
Serial myPort;
boolean firstContact = false;
float pulseVal; 

void setup () {
  size(800, 600);        // window size
  // List all the available serial ports
  println(Serial.list());
  pulseVal = 0; 
  String portName = Serial.list()[2];
  myPort = new Serial(this, portName, 9600);
}

void draw() {
  fill(0, 50);
  rect(0, 0, width, height);
  //  translate( width/2, height/2 );
  //  text(pulseVal, 0, 0);

  pulseVal = constrain(pulseVal, 80, 150);
  translate( width/2, height/2 );
  noStroke();
  fill(#FF0000);
  ellipseMode(CENTER);
  ellipse(0,0, pulseVal, pulseVal);
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
      pulseVal = float(myString);
      println(myString);
    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}



