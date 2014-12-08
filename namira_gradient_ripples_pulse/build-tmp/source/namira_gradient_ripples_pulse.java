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

public class namira_gradient_ripples_pulse extends PApplet {

//SERIAL

Serial myPort;
Serial myPort2;
boolean firstContact1 = false;
boolean firstContact2 = false;

//SOUND
/*import ddf.minim.*;
Minim minim; 

AudioSnippet heartbeat;
*/

//VISUALS

BlackFader blackFader;

ArrayList<SmokeRing> ringsArray; 
GradientBackground colorSmoosh;
SmokeRing soloRing;

float pulseVal; 
float pulseVal2; 
PImage rippleImg; 
PImage rippleImg2; 
float rippleTimer; 
float rippleTimer2; 

public void setup() {

	size(displayWidth, displayHeight, P2D);
	noCursor();
	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	blackFader = new BlackFader();	

	pulseVal = 0; 
	rippleTimer = 0; 
	pulseVal2 = 0; 
	rippleTimer2 = 0; 
	String portName1 = Serial.list()[3];
	// String portName2 = Serial.list()[4];
	
	myPort = new Serial(this, portName1, 9600);
	// myPort2 = new Serial(this, portName2, 9600);
	println("Port 1: " + portName1);
	// println("Port 2: " + portName2);


	
	ringsArray = new ArrayList<SmokeRing>();
	colorSmoosh = new GradientBackground();

/*	minim = new Minim(this);
	heartbeat = minim.loadSnippet("heartbeat.aif");*/
	rippleImg = loadImage("data/ring2.png");
	rippleImg2 = loadImage("data/ring3.png");
}

public void draw() {
	background(0xffFF0000);

	colorSmoosh.display(); 
	for (int i = ringsArray.size()-1; i >= 0 ; i--){
		SmokeRing currentRing = ringsArray.get(i);
		currentRing.update(); 
		//remove if dead
		if (currentRing.isDead == true){
			ringsArray.remove(i);
		}
	}

	//Reduce rippleTimer if necessary
	if ( rippleTimer > 0 ){
		rippleTimer--;
		//println("rippleTimer: " + rippleTimer); //DEBUG
	}

	//Reduce rippleTimer if necessary
	if ( rippleTimer2 > 0 ){
		rippleTimer2--;
		//println("rippleTimer2: " + rippleTimer2); //DEBUG
	}

	//RIPPLE
	pushMatrix();
	noStroke();
	pushStyle();
	imageMode(CENTER);

	readSerial1();
	//readSerial2();

	//fill(rippleImgBackground);
	fill(255, 10);
	rect(0,0,width,height);
	translate(width/2, height/2); 
	image(rippleImg, 0, 0, pulseVal, pulseVal);
	//image(rippleImg2, 0, 0, pulseVal2, pulseVal2);	
	//image(rippleImg, 0, 0, 300, 300);//FOR DEBUG
	image(rippleImg2, 0, 0, pulseVal-60, pulseVal-60);//FOR DEBUG
	popStyle();
	popMatrix();

	//FADE IN OR OUT OF BLACK
	blackFader.overlayFader();
}

//Add ring on mouse press
/*void mousePressed() {
	ringsArray.add(new SmokeRing(width/2, height/2)); 
	println(ringsArray.size());
}*/

public void keyPressed() {
	int btnHit = keyCode;
	blackFader.listen(btnHit);
}

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
      pulseVal = PApplet.parseFloat(myString);
      pulseVal = map(pulseVal, 500, 515, height*0.7f, height*0.8f);
      pulseVal = constrain(pulseVal, height*0.6f, height*0.9f);

		if (pulseVal > height*0.75f && rippleTimer <= 0 ){
			//println("playPulse"); 
			/*playSounds();*/
			rippleTimer = 10;
			ringsArray.add(new SmokeRing(width/2, height/2)); 
		}
      //println(pulseVal);
      println("READING 1: " + myString);

    }
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
      pulseVal2 = map(pulseVal2, 500, 515, height*0.7f, height*0.8f);
      pulseVal2 = constrain(pulseVal2, height*0.6f, height*0.9f);

		if (pulseVal2 > height*0.8f && rippleTimer2 <= 0 ){
			//println("playPulse"); 
			/*playSounds();*/
			rippleTimer2 = 10;
			ringsArray.add(new SmokeRing(width/2, height/2)); 
		}
      //println(pulseVal);
      println("READING 2: " + myString2);

    }
    // when you've parsed the data you have, ask for more:
    myPort2.write("A");
  }

}
class GradientBackground {
	PImage canvas; 
	float timer; 

	GradientBackground(){
		timer = 140; 
		canvas = createImage(width, height, RGB);
	}

	public void display() {

		if (timer <= 0){
			timer = 180; 
		}

		timer--; 
		//println(timer);

		canvas.loadPixels(); 
		for( int i = 0; i < canvas.pixels.length; i++){
			float currentX, currentY; 
			if ( i <= canvas.width ){
				currentX = i; 
				currentY = 0; 
			}
			else {
				currentX = i % width; 
				currentY = floor( i / width);
			}
			float currentRed, currentGreen, currentBlue; 
			float currentLongtitude = map(currentX, 0, width, 0, 200);
			float currentLattitude = map(currentY, 0, height, 200, 0);
			currentRed = currentLongtitude;

			currentBlue = abs( cos(radians(timer)) ) * 100; //blue is animated


			currentGreen = currentLattitude;
			//color currentColor = color( currentRed/2, currentBlue/2, currentGreen/2 ); 
			//canvas.pixels[i] = currentColor; 
		}
		canvas.updatePixels();
		imageMode(CORNER);
		image(canvas, 0, 0, width, height);
		//fill(55, 50, 50, 100);
		//rect(0,0,width,height);
	}



}


class SmokeRing {
	PImage smokeImg = loadImage("data/smoke2.png");
	PImage particleImg1 = loadImage("data/particleImage1.png");
	PImage particleImg2 = loadImage("data/particleImage2.png");
	PImage particleImg3 = loadImage("data/particleImage3.png");
	PImage ring = loadImage("data/ring.png");
	//ArrayList<PImage> ring; 

	PVector origin; 
	float lifespan; 
	float lifespanFull; 
	boolean isDead; 

	SmokeRing(float posX, float posY) {
		origin = new PVector( posX, posY); 
		lifespan = 600;  
		lifespanFull = 600; 
		isDead = false; 
	}

	public void update(){
		lifespan -= 10; //Reduce lifespan by one everytime it runs. 
		float radius = map(lifespan, 0, lifespanFull, width*0.5f, 100);
		drawRing(radius);
		checkAlive();
	}

	public void drawRing(float radius) {
		pushMatrix(); 
		//Move to origin
		translate(origin.x, origin.y);

		float calculateTint = abs( cos( radians( constrain ( map( lifespan, 200, lifespanFull, 90, 270 ), 90, 270 ) ) ) ) * 155;
		pushStyle();
		tint(255, calculateTint);
		imageMode(CENTER);
		image(ring, 0, 0, radius*2.8f, radius*2.8f);
		popStyle();

		for (float i = 0; i < 360; i ++){
			if (i % 10 == 0){
				pushMatrix(); 
				rotate(radians(i));
				float offset = noise(millis())* 5.0f;
				translate( radius + (sin(i) + offset ) * width/400, 0);
				imageMode(CENTER);
				pushStyle();

				tint(255, calculateTint);
				float imageSize = map(lifespan, 0, lifespanFull, 100, 40);
				image(smokeImg, 0, 0, imageSize, imageSize);

				//DRAW RANDOM SHAPE
/*				float imageSize = map(lifespan, 0, lifespanFull, 20, 8);
				float rando = floor(random(4));
				println("Rando = " + rando);
				if ( rando <= 1){
					rotate(random(1) * 2 * PI);
					image(particleImg1, 0, 0, imageSize, imageSize);
				}
				else if ( rando == 2){
					rotate(random(1) * 2 * PI);
					image(particleImg2, 0, 0, imageSize, imageSize);
				}
				else {
					rotate(random(1) * 2 * PI);
					image(particleImg3, 0, 0, imageSize, imageSize);
				}
*/

				//DRAW CIRCLES, CHANGE HUE
/*				pushStyle();
				noStroke();
				float imageSize = map(lifespan, 0, lifespanFull, 20, 8);
				colorMode(HSB, 360, 255, 255, 255);
				float calculateHue = lifespan/lifespanFull * 270; 
				fill( calculateHue, 200, 200, calculateTint);
				ellipseMode(CENTER);
				ellipse(0,0, imageSize, imageSize);
				popStyle();
*/

				//FOR DEBUGGING, DRAW CIRCLES
/*				ellipseMode(CENTER);
				ellipse(0,0, imageSize, imageSize);*/
				popStyle();
				popMatrix();
			}
		}
		popMatrix();
	}

	public void checkAlive(){
		if (lifespan <= 0){
			isDead = true;
		}
	}

}
class BlackFader {
	float blackFader; 
	boolean visualsLive; 


	BlackFader() {
		blackFader = 255; 
		visualsLive = false; 
	}

	public void overlayFader(){
		//println("FADER IS RUNNING"); //DEBUGs
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

/*// Close the sound files


public void stop() {
  // The doorbell object must close its sound.
  close(heartbeat);

  super.stop();
}

void ring(AudioSnippet ringToPlay) {
	if (!ringToPlay.isPlaying()) {
	  // The ring() function plays the sound, as long as it is not already playing. 
	  // rewind() ensures the sound starts from the beginning.
	  ringToPlay.rewind(); 
	  ringToPlay.play();
	}
}

void close(AudioSnippet ringToClose){
	ringToClose.close();
}

void playSounds(){
		ring(heartbeat);
}*/
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "namira_gradient_ripples_pulse" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
