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

public class color_flow_field extends PApplet {


Serial myPort;
boolean firstContact = false;

float pulseVal; 
FlowField colorField; 


public void setup() {

	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	pulseVal = 0; 
	String portName = Serial.list()[3];
	myPort = new Serial(this, portName, 9600);


	size(600, 600);
	//size(displayWidth, displayHeight);
	colorField = new FlowField(); 	
	fill(255);
}

public void draw() {


	fill(color(80, 80, 80, 10));
	rect(0, 0, width, height);
	//colorField.init();
	colorField.showColorField();		
	
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
      pulseVal = constrain(pulseVal, 10, 30);
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}
class FlowField {
	PVector [] [] field; 
	int cols, rows; 
	int resolution; 

	float xoff; 
	float yoff;

	FlowField(){
		resolution = 10;
		cols = width/resolution; 
		rows = height/resolution; 
		field = new PVector [cols] [rows];

		xoff = millis();
		yoff = millis()*2;

		init();
	}

	public PVector lookup( float x, float y){
		int column = PApplet.parseInt(constrain(x/resolution,0,cols-1));
		int row = PApplet.parseInt(constrain(y/resolution,0,rows-1));

		return field[column][row].get();
	}


	public void init() {
		//Initialize force field directions
		for (int i = 0; i < cols; i++){
			for (int j = 0; j < rows; j++) {
				float theta = map(noise(xoff,yoff),0,1,0,TWO_PI);
				field[i][j] = new PVector( abs(cos(theta)) * 255 , abs(sin(theta)) * 255 , abs(sin(theta) * 255) );
				//yoff += 0.1;
				yoff += random(0,0.2f);
			}
			xoff += 0.1f;
		}
	}

	public void showColorField(){
		//Initialize force field directions
		for (int i = 0; i < cols; i++){
			for (int j = 0; j < rows; j++) {
				int locX = i * resolution; 
				int locY = j * resolution; 
				noStroke();
				PVector currentColorVect = lookup(locX, locY);

				//Get the current color & map brightness to mouse X
				//color currentColor = color(currentColorVect.x , currentColorVect.y , currentColorVect.z );
				
				
				// float adjustBrightness = map(mouseX, 0, width, 0, 1);
				// float r = red(currentColor) * adjustBrightness;
				// float g = green(currentColor) * adjustBrightness;
				// float b = blue(currentColor) * adjustBrightness;

				// Constrain RGB to between 0-255
				float r = constrain(currentColorVect.x ,180 ,255);
				float g = constrain(currentColorVect.y ,0 ,255);
				float b = constrain(currentColorVect.z ,0 ,255);

				int currentColor = color(r, r , r );

				//currentColor = color(r, g, b, 120);

				//Draw the rectangle
				ellipseMode(CENTER);
				if( pulseVal <= 30){
					fill(currentColor, 120);
					ellipse( locX + resolution/2, locY + resolution/2, pulseVal/2, pulseVal/2);
					fill(currentColor, 20);
					ellipse( locX + resolution/2, locY + resolution/2, pulseVal, pulseVal);
					// fill(currentColor, 10);
					// ellipse( locX + resolution/2, locY + resolution/2, pulseVal*2, pulseVal*2);
				}
				//println( "i, j : " + i + " , " + j + "\t" + "locX, locY : " + locX + " , " + locY );
			}
		}
	}


}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "color_flow_field" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
