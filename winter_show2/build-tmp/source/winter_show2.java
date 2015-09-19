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

public class winter_show2 extends PApplet {

// ###Winter Show To-Do
// - Implement controls for start / end experience fade in / out  
// - Implement visuals for 2 pulses  
// - Implement pulse reading calibration  
// -- read for 5 seconds  
// -- find average  
// -- find find threshold for pulse  

// SKETCH STATES
// 0. Standby State - Just pulse logo
// 1. Starting State - Run pulseInit() in background, show colored targets in foreground, fade in to 3 after 5000ms 
// 2. Starting State \u2014 Run pulseInit() in background, if fail, show notice, calibrate again until ESC, on which return to 1. 
// 3. Running State - Running visuals
// 4. Ending State - Fade out, return to 1

//GLOBAL UI CONTRLS 

int currentState; 

PVector leftOrigin = new PVector(0,0); 
PVector rightOrigin = new PVector(0,0); 

//GLOBAL UI SETUP
PFont ProximaNova;
PFont ProximaNovaBold;
PFont ProximaNovaLight;

boolean calibrationDone = false;
boolean originReplotMode = false;
boolean originLeftReplotted = false; 
boolean originRightReplotted = false; 

boolean liveMode = true; //liveMode true = using pulse sensors; false = using mouse

//IMAGE SETUP 
PImage maskHard;
PImage maskSoft; 
PImage blueCrosshairs;
PImage redCrosshairs; 
PImage logo; 
float gridAnimate;

//MATH 
float expTimer;


//VISUAL VARIABLES
int theBlue = 0xff00a8ff;
int theRed = 0xfff05315;
float animateSpeed; 
float blackness; 
float strokeWeightRed; 
float strokeWeightBlue; 


//SERIAL

Serial myPort;
Serial myPort2;
boolean firstContact1 = false;
boolean firstContact2 = false;

float calibrationTimer; 


float pulseVal1; 
float pulseVal1Low; 
float pulseVal1High; 
float pulseVal1Diff; 
float pulseVal1Threshold; 

// float pulseVal1; 
// float pulseVal1Low; 
// float pulseVal1High; 
// float pulseVal1Diff;  
// float pulseVal1Threshold; 


public void setup() {
	size(1024,768);
	noCursor();

	//Serial Setup
    //println(Serial.list());// List all the available serial ports
	String portName1 = Serial.list()[3];
	String portName2 = Serial.list()[2];
	
	myPort = new Serial(this, portName1, 9600);
	myPort2 = new Serial(this, portName2, 9600);
	println("Port 1: " + portName1);
	println("Port 2: " + portName2);


	//background(0, 255, 0);\
	background(0);
	gridAnimate = 0;
	maskHard = loadImage("data/mask_hard.png");
	maskSoft = loadImage("data/mask_soft.png");
	blueCrosshairs = loadImage("data/crosshairs_blue.png");
	redCrosshairs = loadImage("data/crosshairs_red.png");
	logo = loadImage("data/pulse_logo.png");
	animateSpeed = 0.05f; 
	blackness = 255;
	expTimer = 500; 


	//Setup Origins
	setLeftOrigin( width*0.25f, 300+(height-300)*0.5f ); 
	setRightOrigin( width*0.75f, 300+(height-300)*0.5f ); 

	//Setup visuals variables
	valuePlotter(); 

	//Setup fonts
	ProximaNovaLight = loadFont("ProximaNova-Light-12.vlw");
	ProximaNova = loadFont("ProximaNova-Regular-12.vlw");
	ProximaNovaBold = loadFont("ProximaNova-Semibold-20.vlw");

}

public void draw() {

	valuePlotter();

	//THE MAIN SWITCHBOARD
	switch(currentState) {

		case 0:
			//println("Bitches be like 2.");
			textAlign(CENTER, TOP);
			background(0);
			fill(255,100);
			textFont(ProximaNovaBold, 12);
			text("LOADING", leftOrigin.x, leftOrigin.y);
			text("LOADING", rightOrigin.x, rightOrigin.y);
			replotOrigin();
			break;


		case 1:
			//println("Bitches be like 0.");
			//Animate fade in 
			if (blackness > 0 ){
				blackness -= 1;
				//println("OPACITY : " + blackness);
			}
			background(0);
			animateSpeed = 0.01f; 
			animateGrid();
			drawGridWhite();
			drawLogos();
			imageMode(CORNER);
			image(maskSoft, 0, 0, width, height);
			fill(0,blackness);
			noStroke();
			rect(0,0,width,height);
			break;

		case 2:
			//println("Bitches be like 1.");	
			background(0);
			animateSpeed = 0.01f; 
			animateGrid();
			drawGridWhite();
			fill(0,50);
			noStroke();
			rect(0,0,width,height);
			fill(255);
			textFont(ProximaNovaBold, 12);
			textAlign(CENTER, BOTTOM);
			text("CALIBRATING", leftOrigin.x, leftOrigin.y);
			text("CALIBRATING", rightOrigin.x, rightOrigin.y);
			imageMode(CORNER);
			image(maskSoft, 0, 0, width, height);

			//CALIBRATION CODE GOES HERE
			if ( calibrationDone == false ) {
				calibrateSensors(); 
			}

			if ( calibrationDone == true ) {
				if ( blackness < 255 ){
					blackness += 1;
					//println("OPACITY : " + blackness);
				}
				fill(0,blackness);
				noStroke();
				rect(0,0,width,height);
				if ( blackness >= 255 ){
					setCurrentState(51); //jump to state 3
				}
			}
			break;

		case 3:
			//println("Bitches be like 3.");
			//Animate fade in 
			if (blackness > 10 ){
				blackness -= 1;
				//println("OPACITY : " + blackness);
			}

			//COUNTDOWN
			if (expTimer > 0 ){
				expTimer -= 1;
				println("COUNTDOWN : " + expTimer);
			}

			if (expTimer <= 0){
				setCurrentState(52); //jump to state 4 when 3 mins up
			}
			animateGrid();
			drawGrid1();
			drawGrid2();
			imageMode(CORNER);
			image(maskSoft, 0, 0, width, height);
			fill(0, blackness);
			noStroke();
			rect(0,0,width,height);
			break;

		case 4:
			//println("Bitches be like 4.");
			if (blackness < 255 ){
				blackness += 0.005f;
				//println("OPACITY : " + blackness);
			}

			//COUNTDOWN
			if (expTimer > 0 ){
				expTimer -= 1;
				println("COUNTDOWN : " + expTimer);
			}

			if (expTimer <= 0){
				setCurrentState(49); //jump to state 1
				blackness = 255;
			}

			textAlign(CENTER, TOP);
			fill(0, blackness);
			noStroke();
			rect(0,0,width,height);
			fill(255,255-blackness-100);
			textFont(ProximaNovaBold, 18);
			text("3 MINUTES", leftOrigin.x, leftOrigin.y);
			textFont(ProximaNova, 12);
			text("pulse", leftOrigin.x, leftOrigin.y+20);

			textFont(ProximaNovaBold, 18);
			text("3 MINUTES", rightOrigin.x, rightOrigin.y);
			textFont(ProximaNova, 12);
			text("pulse", rightOrigin.x, rightOrigin.y+20);
			break;

		default:
			println("Bitches be like that shit cray. Reset plz.");
			break;

	}
}

//UTILS



public void keyTyped() {
	// println("Key pressed is " + key);
	// println("Key pressed is " + int(key));	
	if ( key == 'o' || key == 'O' ) {
		setCurrentState(48);
	} else {
		setCurrentState(key);
	}
}

public void animateGrid() {
	if ( gridAnimate < 1.0f ){
		//gridAnimate += map(mouseY, 0, height, 0, 0.5); 
		gridAnimate += animateSpeed; 
	} else {
		gridAnimate = 0;
	}
}

//Draw pairs of lines that keep going away into the horizon 
//Distort lines according to pulse val 

public void valuePlotter() {
	if ( liveMode == false ){ //MOUSE MODE
		strokeWeightBlue = map(mouseX, 0, width, 0, 12);
		strokeWeightRed = map(mouseX, 0, width, 0, 12);
		if ( currentState == 3){
			animateSpeed = map(mouseY, 0, height, 0, 0.2f); 
		}
	}
	else {
		readSerial1();
		strokeWeightBlue =  map(pulseVal1, pulseVal1High, pulseVal1Low, 0, 12);
		strokeWeightBlue = constrain(strokeWeightBlue, 0, 15);

		readSerial2();
		strokeWeightRed = map(pulseVal1, pulseVal1High, pulseVal1Low, 0, 12);
		strokeWeightRed = constrain(strokeWeightBlue, 0, 15);

		float pulseDiff = abs(pulseVal1 - pulseVal1);

		animateSpeed = 0.003f;
		// animateSpeed = map(pulseDiff, 0, 5, 0, 0.01);
		// animateSpeed = constrain(pulseDiff, 0, 0.1);
	}
}

public void replotOrigin(){
	if (originRightReplotted == false || originLeftReplotted == false){
		println("NOW REPLOTTING ORIGINS");
		cursor();
		if(originRightReplotted == false || originLeftReplotted == false) {
			if (mouseY > 300){
				//if mouse in left zone

				if (mouseX < width*0.5f){
					  
					imageMode(CENTER);
					image(redCrosshairs, mouseX, mouseY);

					if ( mousePressed == true){
						leftOrigin.x = mouseX; 
						leftOrigin.y = mouseY;
						originLeftReplotted = true;
						println("Origin left replotted."); 
					}
				}

				//if mouse in right zone
				if (mouseX > width*0.5f){  
					imageMode(CENTER);
					image(blueCrosshairs, mouseX, mouseY);

					if ( mousePressed == true){
						rightOrigin.x = mouseX; 
						rightOrigin.y = mouseY; 
						originRightReplotted = true; 
						println("Origin right replotted."); 

					}
				}
			} else {
				//if mouse in left zone
				if (mouseX < width*0.5f){
					if ( mousePressed == true){
						setLeftOrigin( width*0.25f, 300+(height-300)*0.5f );
						originLeftReplotted = true;
						println("Origin left replotted."); 
					}
				}
				//if mouse in right zone
				if (mouseX > width*0.5f){
					if ( mousePressed == true){
						setRightOrigin( width*0.75f, 300+(height-300)*0.5f );	
						originRightReplotted = true;
						println("Origin right replotted."); 

					}
				}
			}
		}

		//display crosshair if origin fixed
		if (originRightReplotted == true) {
			image(blueCrosshairs, rightOrigin.x,  rightOrigin.y );
		}	
		if (originLeftReplotted == true) {
			image(redCrosshairs, leftOrigin.x,  leftOrigin.y );
		}	

	} else {
		originReplotMode = false; 
		println("Done replotting.");
		noCursor();
		setCurrentState(49);
	}
}

public void drawGrid1() {
	pushMatrix();
	translate(0, 300);
	stroke(255,50,20);

	strokeWeight(1);
	float opacity = 255; 
	for ( int i = 0 ; i < 60; i++ ){
		float gridHeight = (height-300)/(i + 1);
		float gridNext = (height-300)/(i + 2);
		pushMatrix();

		translate(0, gridHeight);
		strokeWeight(1);
		//stroke(#f05315, opacity * 0.6);
		stroke(255,120);
		line(0,0, width, 0);		
		//translate(0, gridHeight + gridAnimate * (gridNext-gridHeight)); //plus percentage to next line

		//Draw red line
		//translate(0, gridHeight * gridAnimate); //Correct if starting
		translate(0, gridAnimate * (gridNext-gridHeight));
		strokeWeight(strokeWeightRed);
		stroke(theRed, opacity * 0.6f);
		line(0,0, width, 0);

		//opacity -= 255/10;
		popMatrix();
	}
	popMatrix();
}


public void drawGrid2() {
	pushMatrix();
	translate(0, 300);
	stroke(255,50,20);

	strokeWeight(1);
	float opacity = 255; 
	for ( int i = 0 ; i < 60; i++ ){
		float gridHeight = (height-300)/(i + 1);
		float gridNext = (height-300)/(i + 2);
		pushMatrix();

		translate(0, (height-300) - gridHeight);
		strokeWeight(1);
		//stroke(#00a8ff, opacity * 0.6);
		stroke(255,120);
		line(0,0, width, 0);		
		//translate(0, gridHeight + gridAnimate * (gridNext-gridHeight)); //plus percentage to next line

		//Draw red line
		//translate(0, gridHeight * gridAnimate); //Correct if starting
		translate(0, -gridAnimate * (gridNext-gridHeight));
		strokeWeight(strokeWeightBlue);
		stroke(theBlue, opacity * 0.6f);
		line(0,0, width, 0);

		//opacity -= 255/10;
		popMatrix();
	}
	popMatrix();
}

public void drawGridWhite() {
	pushMatrix();
	translate(0, 300);
	stroke(255,50,20);

	strokeWeight(1);
	float opacity = 255; 
	for ( int i = 0 ; i < 60; i++ ){
		float gridHeight = (height-300)/(i + 1);
		float gridNext = (height-300)/(i + 2);
		pushMatrix();

		translate(0, gridHeight);
		strokeWeight(2);
		//stroke(#f05315, opacity * 0.6);
		stroke(255,120);
		line(0,0, width, 0);		
		//translate(0, gridHeight + gridAnimate * (gridNext-gridHeight)); //plus percentage to next line

		//Draw red line
		//translate(0, gridHeight * gridAnimate); //Correct if starting
		translate(0, gridAnimate * (gridNext-gridHeight));
		float var = map(mouseX, 0, width, 0, 12);
		strokeWeight(1);
		stroke(255,120);
		line(0,0, width, 0);

		//opacity -= 255/10;
		popMatrix();
	}
	popMatrix();
}


public void drawLogos() {
	//Draw Pulse Logos
	pushStyle();
	imageMode(CENTER);
	tint(255);

	//TO SET ORIGIN
	//image(logo, leftOrigin.x, leftOrigin.y, 200, 50);
	//image(logo, rightOrigin.x, rightOrigin.y, 200, 50);

	//TO TRUE ORIGIN
	image(logo, width*0.25f, 300+(height-300)*0.5f, 200, 50);
	image(logo, width*0.75f, 300+(height-300)*0.5f,  200, 50);
	
	popStyle();
}


public void drawCrosshairs() {
	//Draw Pulse Logos
	pushStyle();
	imageMode(CENTER);
	tint(255);
	
	PGraphics redCrosshairsCanvas = createGraphics(400, 400);
	PGraphics blueCrosshairsCanvas = createGraphics(400, 400);
	
	redCrosshairsCanvas.imageMode(CENTER);
	redCrosshairsCanvas.image(redCrosshairs, 200, 200);
	redCrosshairsCanvas.filter(BLUR, 3);

	blueCrosshairsCanvas.imageMode(CENTER);
	blueCrosshairsCanvas.image(blueCrosshairs, 200, 200);
	blueCrosshairsCanvas.filter(BLUR, 3);
	

	image(redCrosshairsCanvas, leftOrigin.x, leftOrigin.y, 250, 250);
	image(blueCrosshairsCanvas, rightOrigin.x, rightOrigin.y, 250, 250);

	popStyle();
}










//UTIL 

//Pulse read init
//0. Reset all values
//1. Read for 6 seconds where variance is less than X amount
//2. If not less than X amount variance, poll again. If yes, 
//3. Find the largest drop, set that as the threshold

//Start Util 
//pulse read init
//fade in
//play for 5 mins
//fade out


//GLOBAL STATE CONTROL 
public void setCurrentState( int state) {
	if( state == 48 ||
		state == 49 ||
		state == 50 ||
		state == 51 ||
		state == 52 ){

		switch (state) {
			case 48:
				currentState = 0; 
				originReplotMode = true;
				originLeftReplotted = false; 
				originRightReplotted = false; 
				println("Current state is now " + currentState + ".");
				break;

			case 49:
				blackness = 255; 
				currentState = 1; 
				println("Current state is now " + currentState + ".");
				break;

			case 50:
				currentState = 2; 
				if ( liveMode == true ){
					resetCalibration();
				}
				calibrationDone = false;
				println("Current state is now " + currentState + ".");
				break;

			case 51:
				currentState = 3; 
				blackness = 255; 
				expTimer = 2800*1.2f; // 3000 = 1 min
				println("Current state is now " + currentState + ".");
				break;

			case 52:
				currentState = 4; 
				blackness = 10; 
				expTimer = 500;
				println("Current state is now " + currentState + ".");
				break;
		}
	} else {
		println("Invalid state. States are 0 through 4. ");
	}	
}

//UI UTILS 

public void setLeftOrigin(float x, float y) {
	leftOrigin.x = x; 
	leftOrigin.y = y; 
	println("Left Origin is now x:" + leftOrigin.x + "   y:" + leftOrigin.y + ".");
}


public void setRightOrigin(float x, float y) {
	rightOrigin.x = x; 
	rightOrigin.y = y; 
	println("Left Origin is now x:" + leftOrigin.x + "   y:" + leftOrigin.y + ".");
}
//SERIAL STUFF


//SERIAL #1
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
		  //pulseVal1 = map(pulseVal1, 500, 515, 0, height/10.0); 
			}
		  //println(pulseVal);
		  //println("READING 1: " + myString);

		// when you've parsed the data you have, ask for more:
		myPort.write("A");
	}
}


//SERIAL #2 
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
		pulseVal1 = PApplet.parseFloat(myString2);
		//pulseVal1 = map(pulseVal1, 500, 515, 0, height/10.0);
		}
      //println("READING 2: " + myString2);

    // when you've parsed the data you have, ask for more:
    myPort2.write("A");
  }

}


//Remember to set timer and set findMinInit to false
public void calibrateSensors() {

	//Collect Data for 
	if (calibrationTimer > 0){
		calibrationTimer -= 1;
		println("Calibration Countdown: " + calibrationTimer);
		//Record Max Values
		pulseVal1High = max(pulseVal1High, pulseVal1);
		pulseVal1High = max(pulseVal1High, pulseVal1);
		
		pulseVal1Low = min(pulseVal1Low, pulseVal1);
		pulseVal1Low = min(pulseVal1Low, pulseVal1);

	}
	if (calibrationTimer <= 0){
		pulseVal1Diff = pulseVal1High - pulseVal1Low; 
		pulseVal1Diff = pulseVal1High - pulseVal1Low; 
		if ( pulseVal1Diff < 40 && pulseVal1Diff < 40 ){
			pulseVal1Threshold = pulseVal1Low + pulseVal1Diff * 0.75f; 
			pulseVal1Threshold = pulseVal1Low + pulseVal1Diff * 0.75f; 
			calibrationDone = true;
			println("CALIBRATION DONE");
			println("------------------------");
			println("------------------------");
			println("Pulse 1 High : " + pulseVal1High);
			println("Pulse 1 Low : " + pulseVal1Low);
			println("Pulse 1 Diff : " + pulseVal1Diff);
			println("Pulse 1 Threshold : " + pulseVal1Threshold);
			println("------------------------");
			println("Pulse 2 High : " + pulseVal1High);
			println("Pulse 2 Low : " + pulseVal1Low);
			println("Pulse 2 Diff : " + pulseVal1Diff);
			println("Pulse 2 Threshold : " + pulseVal1Threshold);
			println("------------------------");
			println("------------------------");
		} else {
			println("Calibration needs to be re-run. Hit the 2 key. ");
			println("------------------------");
			println("------------------------");
			println("Pulse 1 High : " + pulseVal1High);
			println("Pulse 1 Low : " + pulseVal1Low);
			println("Pulse 1 Diff : " + pulseVal1Diff);
			println("Pulse 1 Threshold : " + pulseVal1Threshold);
			println("------------------------");
			println("Pulse 2 High : " + pulseVal1High);
			println("Pulse 2 Low : " + pulseVal1Low);
			println("Pulse 2 Diff : " + pulseVal1Diff);
			println("Pulse 2 Threshold : " + pulseVal1Threshold);
			println("------------------------");
			println("------------------------");
			calibrationDone = false;
		}
	}
		
}

public void resetCalibration() {
	//Reset for calibration
	calibrationTimer = 500; 

	pulseVal1Low = 600; 
	pulseVal1High = 0; 

	pulseVal1Low = 600; 
	pulseVal1High = 0; 

	calibrationDone = false; 
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "winter_show2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
