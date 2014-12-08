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

//BLACK FADER
BlackFader blackFader; 

//SOUND

Minim minim; 

AudioSnippet heartbeatpulse;

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
/*	String portName = Serial.list()[3];
	println(portName);
	myPort = new Serial(this, portName, 9600);*/

	blackFader = new BlackFader();

	minim = new Minim(this);
	heartbeatpulse = minim.loadSnippet("data/heartbeat.aif");

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
	
	blackFader.overlayFader();
}




public void keyPressed(){
	//println(keyCode);
	//UP - 38
	//DOWN - 40 
	//LEFT - 37 
	//RIGHT - 38
	int btnHit = keyCode; 

	blackFader.listen(btnHit);

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
/*void serialEvent(Serial myPort) {
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
      pulseVal = map(pulseVal, 500, 515, height*0.7, height*0.8);
      pulseVal = constrain(pulseVal, height*0.6, height*0.9);

		if (pulseVal > height*0.75){
			//println("playPulse"); 
			playSounds();
		}
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}*/
class BlackFader {
	float blackFader; 
	boolean visualsLive; 


	BlackFader() {
		blackFader = 255; 
		visualsLive = false; 
	}

	public void overlayFader(){
		//UPDATE FADER COUNT UP OR DOWN IF NEEDED
		if (visualsLive == false){
			if (blackFader < 255){
				blackFader += 5;
			}
		}

		if (visualsLive == true){
			if (blackFader > 0){
				blackFader -= 5;
			}
		}

		//DRAW RECT BASED ON STATUS
		pushStyle(); 
		noStroke();
		fill(0, blackFader);
		rect(0, 0, width, height);
		popStyle();
	}

	public void listen(int btnHit) {
	//CHECK IF KEYS HAVE BEEN HIT, UPDATE STATUS IF NEEDED
		//FADE IN
		if (btnHit == 73){ //Hit key 'i' = keyCode 73 
			visualsLive = true;
			println("Lets get it started.");
		}

		//FADE OUT
		if (btnHit == 79){
			visualsLive = false; 
			println("Lets get faded.");
		}
	}
}

// Close the sound files


public void stop() {
  // The doorbell object must close its sound.
  close(heartbeatpulse);

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
		ring(heartbeatpulse);
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
