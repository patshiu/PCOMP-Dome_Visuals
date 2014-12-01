import processing.serial.*;
Serial myPort;
boolean firstContact = false;

float pulseNum; 
FlowField colorField; 


void setup() {

	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	pulseNum = 0; 
	String portName = Serial.list()[3];
	myPort = new Serial(this, portName, 9600);


	size(600, 600);
	//size(displayWidth, displayHeight);
	colorField = new FlowField(); 	
	fill(255);
}

void draw() {


	fill(color(80, 80, 80, 10));
	rect(0, 0, width, height);
	//colorField.init();
	colorField.showColorField();		
	
}

//SERIAL STUFF
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
      pulseNum = float(myString);
      pulseNum = map(pulseNum, 125, 135, 10, 30);
      pulseNum = constrain(pulseNum, 10, 30);
      //println(pulseNum);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}
