//SERIAL
import processing.serial.*;
Serial myPort;
boolean firstContact = false;

//SOUND
/*import ddf.minim.*;
Minim minim; 

AudioSnippet heartbeat;
*/

//VISUALS
ArrayList<SmokeRing> ringsArray; 
GradientBackground colorSmoosh;
SmokeRing soloRing; 


float pulseVal; 
PImage rippleImg; 
float rippleTimer; 

void setup() {

	size(displayWidth, displayHeight, P2D);
	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	pulseVal = 0; 
	rippleTimer = 0; 
	String portName = Serial.list()[3];
	println(portName);
	myPort = new Serial(this, portName, 9600);


	
	ringsArray = new ArrayList<SmokeRing>();
	colorSmoosh = new GradientBackground();
	//soloRing = new SmokeRing(width/2, height/2);

/*	minim = new Minim(this);
	heartbeat = minim.loadSnippet("heartbeat.aif");*/
	rippleImg = loadImage("data/ring.png");
}

void draw() {

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

	//Reduce rippleTimer if necessary
	if ( rippleTimer > 0 ){
		rippleTimer--;
		println("rippleTimer: " + rippleTimer);
	}
	//fill(rippleImgBackground);
	fill(255, 10);
	rect(0,0,width,height);
	translate(width/2, height/2); 
	image(rippleImg, 0, 0, pulseVal, pulseVal);
	popStyle();
	popMatrix();
}

void mousePressed() {
	ringsArray.add(new SmokeRing(width/2, height/2)); 
	println(ringsArray.size());
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
      pulseVal = map(pulseVal, 500, 515, height*0.7, height*0.8);
      pulseVal = constrain(pulseVal, height*0.6, height*0.9);

		if (pulseVal > height*0.8 && rippleTimer <= 0 ){
			//println("playPulse"); 
			/*playSounds();*/
			rippleTimer = 10;
			ringsArray.add(new SmokeRing(width/2, height/2)); 
		}
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}