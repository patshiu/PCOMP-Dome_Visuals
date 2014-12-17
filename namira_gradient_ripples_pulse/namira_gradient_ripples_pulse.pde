//SERIAL
import processing.serial.*;
Serial myPort;
Serial myPort2;
boolean firstContact1 = false;
boolean firstContact2 = false;

//SOUND 
import ddf.minim.*;

Minim minim; 
AudioSample heartbeat1;
AudioSample heartbeat2;

AudioSample soundbed;


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

void setup() {

	size(displayWidth, displayHeight, P2D);
	noCursor();
	//SERIAL STUFF
	//println(Serial.list());// List all the available serial ports
	blackFader = new BlackFader();	

	pulseVal = 0; 
	rippleTimer = 0; 
	pulseVal2 = 0; 
	rippleTimer2 = 0; 
	String portName1 = Serial.list()[2];
	String portName2 = Serial.list()[3];
	
	myPort = new Serial(this, portName1, 9600);
	myPort2 = new Serial(this, portName2, 9600);
	println("Port 1: " + portName1);
	println("Port 2: " + portName2);


	
	ringsArray = new ArrayList<SmokeRing>();
	colorSmoosh = new GradientBackground();

	rippleImg = loadImage("data/ring2.png");
	rippleImg2 = loadImage("data/ring3.png");

	// load files from  data folder
	minim = new Minim(this);
	heartbeat1 = minim.loadSample( "data/heartbeat35.mp3", 512 );
	heartbeat2 = minim.loadSample( "data/heartbeat38.wav", 512 );
	soundbed = minim.loadSample("data/soundbed1.wav", 512);
}

void draw() {
	background(#FF0000);

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

void keyPressed() {
	int btnHit = keyCode;
	blackFader.listen(btnHit);

    if ( key == 's' ) soundbed.trigger();
    if ( key == 'h' ) heartbeat1.trigger();
    if ( key == 'j' ) heartbeat2.trigger();
}

//SERIAL STUFF
void readSerial1() {
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
      pulseVal = float(myString);
      pulseVal = map(pulseVal, 500, 515, height*0.7, height*0.8);
      pulseVal = constrain(pulseVal, height*0.6, height*0.9);

		if (pulseVal > height*0.75 && rippleTimer <= 0 ){
			//println("playPulse"); 
			/*playSounds();*/
			rippleTimer = 10;
			if (blackFader.visualsLive == true){
				heartbeat1.trigger();
			}
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
void readSerial2() {
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
      pulseVal2 = float(myString2);
      pulseVal2 = map(pulseVal2, 500, 515, height*0.7, height*0.8);
      pulseVal2 = constrain(pulseVal2, height*0.6, height*0.9);

		if (pulseVal2 > height*0.8 && rippleTimer2 <= 0 ){
			//println("playPulse"); 
			/*playSounds();*/
			rippleTimer2 = 10;
			if (blackFader.visualsLive == true){
				heartbeat2.trigger();
			}
			ringsArray.add(new SmokeRing(width/2, height/2)); 
		}
      //println(pulseVal);
      println("READING 2: " + myString2);

    }
    // when you've parsed the data you have, ask for more:
    myPort2.write("A");
  }

}
