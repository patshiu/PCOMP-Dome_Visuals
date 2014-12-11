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

public class basic_pulse_reader extends PApplet {

//SERIAL

Serial myPort;
boolean firstContact = false;
float pulseVal; 

int sweeper; 


public void setup() {
	size(displayWidth, displayHeight);
	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	pulseVal = 0; 
	String portName = Serial.list()[3];
	println(portName);
	myPort = new Serial(this, portName, 9600);
	sweeper = 0; 
	fill(0);
	rect(0,0,width,height);
	noStroke();

}

public void draw() {
	//background(0);

	//draw pulse 1
	pushMatrix(); 
	translate(sweeper, height/2);
	fill(255);
	stroke(255);
	point(0, PApplet.parseInt(pulseVal));
	popMatrix();
	//Traverse width screen
	if (sweeper < width){
		sweeper++; 
	} else if (sweeper >= width) {
		sweeper = 0; //set sweeper back to zero if edge of screens reached.
		fill(0);
		noStroke();
		rect(0,0,width,height);
	}	
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
      pulseVal = map(pulseVal, 500, 515, 0, height/80.0f);

		if (pulseVal > height*0.75f){
			//DO SOMETHING 
		}
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "basic_pulse_reader" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
