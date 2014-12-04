import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class breathing_ripples extends PApplet {

//SERIAL

Serial myPort;
boolean firstContact = false;


//SOUND

Minim minim; 

AudioSnippet heartbeat;

float pulseVal; 
PImage rippleImg1; 
PImage rippleImg2; 
PImage rippleImg3; 
PImage rippleImg4; 
PImage rippleImg5; 
PImage rippleImg6; 
int rippleImgBackground; 
int rippleMode; 

public void setup() {

	size(displayWidth, displayHeight);
	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	pulseVal = 0; 
	String portName = Serial.list()[3];
	myPort = new Serial(this, portName, 9600);

	minim = new Minim(this);
	heartbeat = minim.loadSnippet("heartbeat.aif");

	rippleMode = 1; 
	rippleImg1 = loadImage("data/01_RippleImg.png");
	rippleImg2 = loadImage("data/02_RippleImg.png");
	rippleImg3 = loadImage("data/03_RippleImg.png");
	rippleImg4 = loadImage("data/04_RippleImg.png");
	rippleImg5 = loadImage("data/05_RippleImg.png");
	rippleImg6 = loadImage("data/06_RippleImg.png");
	rippleImgBackground = color(0xffdeecfd);

}

public void draw() {
	background(255);

	//display an image and scale it according to the value of pulseVal
	pushMatrix();
	noStroke();
	imageMode(CENTER);
	if ( rippleMode == 1 ){
		fill(rippleImgBackground);
		//fill(255, 10);
		rect(0,0,width,height);
		translate(width/2, height/2); 
		image(rippleImg1, 0, 0, pulseVal, pulseVal);
	}
	if ( rippleMode == 2 ){
		fill(rippleImgBackground);
		rect(0,0,width,height);
		translate(width/2, height/2); 
		image(rippleImg2, 0, 0, pulseVal, pulseVal);
	}
	if ( rippleMode == 3 ){
		fill(rippleImgBackground);
		rect(0,0,width,height);
		translate(width/2, height/2); 
		image(rippleImg3, 0, 0, pulseVal, pulseVal);
	}
	if ( rippleMode == 4 ){
		fill(rippleImgBackground);
		rect(0,0,width,height);
		translate(width/2, height/2); 
		image(rippleImg4, 0, 0, pulseVal, pulseVal);
	}
	if ( rippleMode == 5 ){
		fill(rippleImgBackground);
		rect(0,0,width,height);
		translate(width/2, height/2); 
		image(rippleImg5, 0, 0, pulseVal, pulseVal);
	}
	if ( rippleMode == 6 ){
		fill(rippleImgBackground);
		rect(0,0,width,height);
		translate(width/2, height/2); 
		image(rippleImg6, 0, 0, pulseVal, pulseVal);
	}
	popMatrix();
	
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
      pulseVal = map(pulseVal, 500, 515, height*0.7f, height*0.8f);
      pulseVal = constrain(pulseVal, height*0.6f, height*0.9f);

		if (pulseVal > height*0.75f){
			//println("playPulse"); 
			playSounds();
		}
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}
// Close the sound files


public void stop() {
  // The doorbell object must close its sound.
  close(heartbeat);

  super.stop();
}

public void ring(AudioSnippet ringToPlay) {
	if (!ringToPlay.isPlaying()) {
	  // The ring() function plays the sound, as long as it is not already playing. 
	  // rewind() ensures the sound starts from the beginning.
	  ringToPlay.rewind(); 
	  ringToPlay.play();
	}
}

public void close(AudioSnippet ringToClose){
	ringToClose.close();
}

public void playSounds(){
		ring(heartbeat);
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