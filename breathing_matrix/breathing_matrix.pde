import processing.serial.*;
Serial myPort;
boolean firstContact = false;

float pulseVal; 
FlowField colorField;  
PImage rippleImg1; 
PImage rippleImg2; 
PImage rippleImg3; 
PImage rippleImg4; 
PImage rippleImg5; 
PImage rippleImg6; 
color rippleImgBackground; 
int rippleMode; 


void setup() {

	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	pulseVal = 0; 
	String portName = Serial.list()[3];
	myPort = new Serial(this, portName, 9600);

	//size(600, 600);
	size(displayWidth, displayHeight);
	colorField = new FlowField(); 	

	rippleMode = 1; 
}

void draw() {
	colorField.showColorField();	
}

void keyPressed(){
	//println(keyCode);
	//UP - 38
	//DOWN - 40 
	//LEFT - 37 
	//RIGHT - 38
	if (rippleMode == 6 && keyCode == 38){
		//change to 1 on increase
		rippleMode = 1; 
		return;
	}
	if (rippleMode == 1 && keyCode == 40){
		//change to 6 on decrease
		rippleMode = 6;
		return;
	}
	else{
		if( keyCode == 38){
			rippleMode++; 
			return;
		}
		if ( keyCode == 40){
			rippleMode--;
			return;
		}
	}
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
      pulseVal = float(myString);
      pulseVal = map(pulseVal, 125, 135, 10, 30);
      pulseVal = constrain(pulseVal, 10, 40);
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}
