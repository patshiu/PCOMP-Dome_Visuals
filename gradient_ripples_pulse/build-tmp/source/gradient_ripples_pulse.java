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

public class gradient_ripples_pulse extends PApplet {

//SERIAL

Serial myPort;
boolean firstContact = false;


//SOUND

Minim minim; 

AudioSnippet heartbeat;


//VISUALS
ArrayList<SmokeRing> ringsArray; 
GradientBackground colorSmoosh;
SmokeRing soloRing; 


float pulseVal; 
PImage rippleImg; 

public void setup() {

	//SERIAL STUFF
	println(Serial.list());// List all the available serial ports
	pulseVal = 0; 
	String portName = Serial.list()[3];
	myPort = new Serial(this, portName, 9600);


	size(displayWidth, displayHeight, P2D);
	ringsArray = new ArrayList<SmokeRing>();
	colorSmoosh = new GradientBackground();
	//soloRing = new SmokeRing(width/2, height/2);

	minim = new Minim(this);
	heartbeat = minim.loadSnippet("heartbeat.aif");
	rippleImg = loadImage("data/ring.png");
}

public void draw() {

	colorSmoosh.display(); 
	//soloRing.update();
	for (int i = ringsArray.size()-1; i >= 0 ; i--){
		SmokeRing currentRing = ringsArray.get(i);
		currentRing.update(); 
		//remove if dead
		if (currentRing.isDead == true){
			ringsArray.remove(i);
		}
	}


	//RIPPLE
	pushMatrix();
	noStroke();
	pushStyle();
	imageMode(CENTER);
	//fill(rippleImgBackground);
	//fill(255, 10);
	rect(0,0,width,height);
	translate(width/2, height/2); 
	image(rippleImg, 0, 0, pulseVal, pulseVal);
	popStyle();
	popMatrix();
}

public void mousePressed() {
	ringsArray.add(new SmokeRing(width/2, height/2)); 
	println(ringsArray.size());
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

			currentBlue = abs( cos(radians(timer)) ) * 180; //blue is animated


			currentGreen = currentLattitude;
			int currentColor = color( currentRed, currentBlue, currentGreen ); 
			canvas.pixels[i] = currentColor; 
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

		float calculateTint = abs( cos( radians( constrain ( map( lifespan, 200, lifespanFull, 90, 270 ), 90, 270 ) ) ) ) * 255;
		pushStyle();
		tint(255, calculateTint);
		imageMode(CENTER);
		image(ring, 0, 0, radius*2.8f, radius*2.8f);
		popStyle();

		for (float i = 0; i < 360; i ++){
			if (i % 5 == 0){
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
    String[] appletArgs = new String[] { "gradient_ripples_pulse" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
