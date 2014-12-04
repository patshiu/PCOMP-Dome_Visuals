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

public class breathing_matrix extends PApplet {


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
int rippleImgBackground; 
int rippleMode; 


public void setup() {

	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	pulseVal = 0; 
	String portName = Serial.list()[4];
	myPort = new Serial(this, portName, 9600);

	//size(600, 600);
	size(displayWidth, displayHeight);
	colorField = new FlowField(); 	

	rippleMode = 1; 
}

public void draw() {
	colorField.showColorField();	
}

public void keyPressed(){
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
      pulseVal = map(pulseVal, 125, 135, 10, 30);
      pulseVal = constrain(pulseVal, 10, 40);
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}
class FlowField {
	float [] [] field; 
	int cols, rows; 
	int resolution; 

	float xoff; 
	float yoff;

	PImage currentImage; 

	FlowField(){

		//Set up images
		rippleImg1 = loadImage("data/01_RippleImg.png");
		rippleImg2 = loadImage("data/02_RippleImg.png");
		rippleImg3 = loadImage("data/03_RippleImg.png");
		rippleImg4 = loadImage("data/04_RippleImg.png");
		rippleImg5 = loadImage("data/05_RippleImg.png");
		rippleImg6 = loadImage("data/06_RippleImg.png");
		rippleImgBackground = color(0xffdeecfd);
		currentImage = rippleImg1; 

		resolution = 80;
		cols = width/resolution; 
		rows = height/resolution; 
		field = new float [cols] [rows];

		xoff = millis();
		yoff = millis()*2;

		init();
	}

	public float lookup( float x, float y){
		int column = PApplet.parseInt(constrain(x/resolution,0,cols-1));
		int row = PApplet.parseInt(constrain(y/resolution,0,rows-1));

		return field[column][row];
	}


	public void init() {
		//Initialize force field directions
		for (int i = 0; i < cols; i++){
			for (int j = 0; j < rows; j++) {
				float theta = map(noise(xoff,yoff),0,1,0,TWO_PI);
				field[i][j] = abs(cos(theta)) * 255; //store noise alpha value
				//yoff += 0.1;
				yoff += random(0,0.2f);
			}
			xoff += 0.1f;
		}
	}

	public void showColorField(){
		//Initialize force field directions
		fill(rippleImgBackground, 100);
		rect(0,0,width,height);
		for (int i = 0; i < cols; i++){
			for (int j = 0; j < rows; j++) {
				int locX = i * resolution; 
				int locY = j * resolution; 
				noStroke();

				//Draw the rectangle
				ellipseMode(CENTER);
				if ( rippleMode == 1 ){
					currentImage = rippleImg1; 
				}
				if ( rippleMode == 2 ){
					currentImage = rippleImg2; 
				}
				if ( rippleMode == 3 ){
					currentImage = rippleImg3; 
				}
				if ( rippleMode == 4 ){
					currentImage = rippleImg4; 
				}
				if ( rippleMode == 5 ){
					currentImage = rippleImg5; 
				}
				if ( rippleMode == 6 ){
					currentImage = rippleImg6; 
				}
				if( pulseVal <= 40){

					imageMode(CENTER);
					tint(255, 40);
					image( currentImage, locX + resolution/2 , locY + resolution/2, pulseVal*2, pulseVal*2);
					tint(255, 55);
					image( currentImage, locX + resolution/2 , locY + resolution/2, pulseVal, pulseVal);
					// fill(currentColor, 10);
					// ellipse( locX + resolution/2, locY + resolution/2, pulseVal*2, pulseVal*2);
				}
				//println( "i, j : " + i + " , " + j + "\t" + "locX, locY : " + locX + " , " + locY );
			}
		}
	}


}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "breathing_matrix" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
