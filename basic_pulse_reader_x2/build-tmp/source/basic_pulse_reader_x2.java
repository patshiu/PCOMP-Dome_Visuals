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

public class basic_pulse_reader_x2 extends PApplet {

//SERIAL

Serial myPort;
Serial myPort2;
boolean firstContact1 = false;
boolean firstContact2 = false;

//MASK 
PImage masker; 

//PULSE VALUES
float pulseVal1; 
float prevPulseVal1; 
float pulseVal2; 
float prevPulseVal2;

//DRAWING STYLE
int DRAWINGSTYLE;

int sweeper; 
float lineAlpha; 


public void setup() {
	size(displayWidth, displayHeight);
	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	String portName1 = Serial.list()[3];
	String portName2 = Serial.list()[4];
	
	myPort = new Serial(this, portName1, 9600);
	myPort2 = new Serial(this, portName2, 9600);
	println("Port 1: " + portName1);
	println("Port 2: " + portName2);

	//INIT STUFF
	pulseVal1 = 0; 
	prevPulseVal1 = 0; 
	pulseVal2 = 0; 
	prevPulseVal2 = 0; 
	lineAlpha = 0; 

	DRAWINGSTYLE = 2; 

	masker = loadImage("data/mask.png");
	

	sweeper = 0; 
	fill(0);
	rect(0,0,width,height);
	noStroke();

}

public void draw() {
	//background(0);
	readSerial1();
	readSerial2();

	//Calculate Alpha
	float currentPercentAcrossScreen = sweeper / (0.5f * width) * 100; 
	float currentRadians = map(currentPercentAcrossScreen, 0, 100, 0.5f * PI, 1.5f * PI);
	lineAlpha = abs(cos(currentRadians) * 255);	
	println(lineAlpha);	

	//MODE 1 \u2014 HALF SCREEN
	if ( DRAWINGSTYLE == 1 ){
		//draw pulse 1
		pushMatrix(); 
		translate(width/2 - sweeper, height/2);
		fill(255, 0, 0, lineAlpha);
		stroke(255, 0, 0, lineAlpha);
		point(0, PApplet.parseInt(pulseVal1));
		line(0, PApplet.parseInt(prevPulseVal1), 0, PApplet.parseInt(pulseVal1));
		prevPulseVal1 = pulseVal1; 
		popMatrix();

		//draw pulse2
		pushMatrix();
		translate(width/2 + sweeper, height/2);
		fill(0, 0, 255, lineAlpha);
		stroke(0, 0, 255, lineAlpha);
		point(0, PApplet.parseInt(pulseVal2));
		line(0, PApplet.parseInt(prevPulseVal2), 0, PApplet.parseInt(pulseVal2));
		prevPulseVal2 = pulseVal2; 

		popMatrix();

		//Fading FX
		fill(0,3);
		noStroke();
		rect(0,0,width,height);


		//ADVANCE SWEEPER
		if (sweeper < width/2){
			sweeper++; 
		} else if (sweeper >= width/2) {
			sweeper = 0; //set sweeper back to zero if edge of screens reached.
		}	
		
	}


	//MODE 2 \u2014 MIRRORED, FULL WIDTH
	if ( DRAWINGSTYLE == 2 ){
		//draw pulse 1
		pushMatrix(); 
		translate(sweeper, height/2);
		fill(255, 0, 0, lineAlpha);
		stroke(255, 0, 0, lineAlpha);
		point(0, PApplet.parseInt(pulseVal1));
		line(0, PApplet.parseInt(prevPulseVal1), 0, PApplet.parseInt(pulseVal1));
		prevPulseVal1 = pulseVal1; 
		popMatrix();

		//draw pulse2
		pushMatrix();
		translate(width - sweeper, height/2);
		fill(0, 0, 255, lineAlpha);
		stroke(0, 0, 255, lineAlpha);
		point(0, PApplet.parseInt(pulseVal2));
		line(0, PApplet.parseInt(prevPulseVal2), 0, PApplet.parseInt(pulseVal2));
		prevPulseVal2 = pulseVal2; 

		popMatrix();

		//Fading FX
		fill(0,3);
		noStroke();
		rect(0,0,width,height);
		//Traverse width screen
		if (sweeper < width){
			sweeper++; 
		} else if (sweeper >= width) {
			sweeper = 0; //set sweeper back to zero if edge of screens reached.
	/*		fill(0);
			noStroke();
			rect(0,0,width,height);*/
		}
		
	}

}

//SERIAL STUFF


//SERIAL STUFF
public void readSerial1() {
  // read the serial buffer:
  String myString = myPort.readStringUntil('\n'); 

  //HANDLE PORT 1
  // if you got any bytes other than the linefeed:
	if (myString != null) {

	myString = trim(myString);

		// if you haven't heard from the microcontroller yet, listen:
		if (firstContact1 == false) {
		  if (myString.equals("hello")) {
		    myPort.clear();          // clear the serial port buffer
		    firstContact1 = true;     // you've had first contact from the microcontroller
		    myPort.write('A');       // ask for more
		  }
		}
		// if you have heard from the microcontroller, proceed:
		else {
		  pulseVal1 = PApplet.parseFloat(myString);
		  pulseVal1 = map(pulseVal1, 500, 515, 0, height/10.0f); 
			}
		  //println(pulseVal);
		  println("READING 1: " + myString);

		// when you've parsed the data you have, ask for more:
		myPort.write("A");
	}
}


//SERIAL STUFF
public void readSerial2() {
	String myString2 = myPort2.readStringUntil('\n');

  //HANDLE PORT 2
  if (myString2 != null) {

    myString2 = trim(myString2);

    // if you haven't heard from the microcontroller yet, listen:
    if (firstContact2 == false) {
      if (myString2.equals("hello")) {
        myPort2.clear();          // clear the serial port buffer
        firstContact2 = true;     // you've had first contact from the microcontroller
        myPort2.write('A');       // ask for more
      }
    }
    // if you have heard from the microcontroller, proceed:
    else {
		pulseVal2 = PApplet.parseFloat(myString2);
		pulseVal2 = map(pulseVal2, 500, 515, 0, height/10.0f);
		}
      println("READING 2: " + myString2);

    // when you've parsed the data you have, ask for more:
    myPort2.write("A");
  }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "basic_pulse_reader_x2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
