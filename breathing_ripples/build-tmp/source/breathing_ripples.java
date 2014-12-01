import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class breathing_ripples extends PApplet {


Serial myPort;
boolean firstContact = false;

float pulseVal; 
PImage rippleImg; 

public void setup() {

	size(displayWidth, displayHeight);
	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	pulseVal = 0; 
	String portName = Serial.list()[3];
	myPort = new Serial(this, portName, 9600);

	rippleImg = loadImage("data/01_RippleImg_cropped.png");
	
}

public void draw() {
	fill(255, 10);
	rect(0,0,width,height);
	//display an image and scale it according to the value of pulseVal
	pushMatrix();
	translate(width/2, height/2); 
	imageMode(CENTER);
	image(rippleImg, 0, 0, pulseVal, pulseVal);
	popMatrix();
	
}

//SERIAL STUFF
public void serialEvent(Serial myPort) {
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
      pulseVal = PApplet.parseFloat(myString);
      pulseVal = map(pulseVal, 125, 135, 320, 400);
      pulseVal = constrain(pulseVal, 320, 400);
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "breathing_ripples" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
