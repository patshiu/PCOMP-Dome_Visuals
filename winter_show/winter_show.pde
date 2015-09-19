natiproject// ###Winter Show To-Do
// - Implement controls for start / end experience fade in / out  
// - Implement visuals for 2 pulses  
// - Implement pulse reading calibration  
// -- read for 5 seconds  
// -- find average  
// -- find find threshold for pulse  

// SKETCH STATES
// 0. Standby State - Just pulse logo
// 1. Starting State - Run pulseInit() in background, show colored targets in foreground, fade in to 3 after 5000ms 
// 2. Starting State — Run pulseInit() in background, if fail, show notice, calibrate again until ESC, on which return to 1. 
// 3. Running State - Running visuals
// 4. Ending State - Fade out, return to 1


//********************
//*** DEMO CONTROLLS
//********************
boolean liveMode = false; //liveMode true = using pulse sensors; false = using mouse
boolean autoPilotMode = false; 
boolean calibrationOverride = false; 
boolean pulseRedWorking = false; 
boolean pulseBlueWorking = false; 




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


//IMAGE SETUP 
PImage maskHard;
PImage maskSoft; 
PImage blueCrosshairs;
PImage redCrosshairs; 
PImage logo; 
float gridAnimate;

//MATH 
float expTimer;
float degClock; 


//VISUAL VARIABLES
color theBlue = #00a8ff;
color theRed = #f05315;
float animateSpeed; 
float blackness; 
float strokeWeightRed; 
float strokeWeightBlue; 


//SERIAL
import processing.serial.*;
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

float pulseVal2; 
float pulseVal2Low; 
float pulseVal2High; 
float pulseVal2Diff;  
float pulseVal2Threshold; 


void setup() {
	size(1024,768);
	noCursor();

	//Serial Setup
    //println(Serial.list());// List all the available serial ports
//	String portName1 = Serial.list()[3];
//	String portName2 = Serial.list()[2];
//	
//	myPort = new Serial(this, portName1, 9600);
//	myPort2 = new Serial(this, portName2, 9600);
//	println("Port 1: " + portName1);
//	println("Port 2: " + portName2);


	//background(0, 255, 0);\
	background(0);
	gridAnimate = 0;
	maskHard = loadImage("data/mask_hard.png");
	maskSoft = loadImage("data/mask_soft.png");
	blueCrosshairs = loadImage("data/crosshairs_blue.png");
	redCrosshairs = loadImage("data/crosshairs_red.png");
	logo = loadImage("data/pulse_logo.png");
	animateSpeed = 0.05; 
	blackness = 255;
	expTimer = 500; 
	degClock = 360; 


	//Setup Origins
	setLeftOrigin( width*0.25, 300+(height-300)*0.5 ); 
	setRightOrigin( width*0.75, 300+(height-300)*0.5 ); 

	//Setup visuals variables
	valuePlotter(); 

	//Setup fonts
	ProximaNovaLight = loadFont("ProximaNova-Light-12.vlw");
	ProximaNova = loadFont("ProximaNova-Regular-12.vlw");
	ProximaNovaBold = loadFont("ProximaNova-Semibold-20.vlw");

}

void draw() {

	valuePlotter();
	radClock();

	//THE MAIN SWITCHBOARD
	switch(currentState) {

		case 0:
			//println("Bitches be like 2.");
			textAlign(CENTER, TOP);
			background(0);
			fill(255,100);
			textFont(ProximaNovaBold, 12);
			text("SET CENTER", leftOrigin.x, leftOrigin.y);
			text("SET CENTER", rightOrigin.x, rightOrigin.y);
			replotOrigin();
			break;


		case 1:
			//println("Bitches be like 0.");
			//Animate fade in 
			if (blackness > 0 ){
				blackness -= 1;
				//println("OPACITY : " + blackness);
			}

			if (autoPilotMode == true){
				//COUNTDOWN
				if (expTimer > 0 ){
					expTimer -= 1;
					println("COUNTDOWN : " + expTimer);
				}

				if (expTimer <= 0){
					setCurrentState(51); //jump to visuals on auto pilot
				}
			}

			background(0);
			animateSpeed = 0.01; 
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
			//CALIBRATION MODE	
			background(0);
			animateSpeed = 0.01; 
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

			//2 — CALIBRATION MODE
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
			//3 — PLAY VISUALS MODE
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
			//4 — 1 MINUTE UP NOTICE
			if (blackness < 255 ){
				blackness += 0.005;
				//println("OPACITY : " + blackness);
			}

			//COUNTDOWN
			if (expTimer > 0 ){
				expTimer -= 1;
				println("COUNTDOWN : " + expTimer);
			}

			if (expTimer <= 0){
				setCurrentState(49); //jump back to state 1 titles
				blackness = 255;
			}			

			textAlign(CENTER, TOP);
			fill(0, blackness);
			noStroke();
			rect(0,0,width,height);
			fill(255,255-blackness-100);
			textFont(ProximaNovaBold, 18);
			text("1 MINUTE", leftOrigin.x, leftOrigin.y);
			textFont(ProximaNova, 12);
			text("pulse", leftOrigin.x, leftOrigin.y+20);

			textFont(ProximaNovaBold, 18);
			text("1 MINUTE", rightOrigin.x, rightOrigin.y);
			textFont(ProximaNova, 12);
			text("pulse", rightOrigin.x, rightOrigin.y+20);
			break;

		case 5: //BE RIGHT BACK
			
			//Animate fade in 
			if (blackness > 0 ){
				blackness -= 1;
				//println("OPACITY : " + blackness);
			}

			background(0);
			animateSpeed = 0.01; 
			animateGrid();
			drawGridWhite();
			beRightBack();
			imageMode(CORNER);
			image(maskSoft, 0, 0, width, height);
			fill(0,blackness);
			noStroke();
			rect(0,0,width,height);
			break;

		default:
			println("Bitches be like that shit cray. Reset plz.");
			break;

	}
}

//UTILS



void keyTyped() {
	// println("Key pressed is " + key);
	// println("Key pressed is " + int(key));	
	if ( key == 'o' || key == 'O' ) {
		setCurrentState(48);
	} else {
		setCurrentState(key);
	}
}

void animateGrid() {
	if ( gridAnimate < 1.0 ){
		//gridAnimate += map(mouseY, 0, height, 0, 0.5); 
		gridAnimate += animateSpeed; 
	} else {
		gridAnimate = 0;
	}
}

//Draw pairs of lines that keep going away into the horizon 
//Distort lines according to pulse val 

float radClock() {
	if ( degClock > 0 ){
		degClock -= 1; 
	} else {
		degClock = 360; 
	}
	println(degClock);
	return radians(degClock);
} 


void valuePlotter() {
	if ( liveMode == false || autoPilotMode == true ){ //MOUSE MODE
		// strokeWeightBlue = map(mouseX, 0, width, 0, 12) * cos(radClock());
		// strokeWeightRed = map(mouseX, 0, width, 0, 12) * cos(radClock());
			strokeWeightBlue = 6 * abs(cos(radClock()));
			strokeWeightRed = 8 * abs(sin(radClock()));
		if ( currentState == 3){
			animateSpeed = 0.01 * constrain(abs(sin(radClock())), 0.2, 1.0); 
			//println("Animate speed = " + animateSpeed);
		}
	}
	else {

		//BOOLEANS ARE FOR FALL BACK 
		//IF EITHER SENSORS ARE WORKING 
		if ( pulseRedWorking == true || pulseBlueWorking == true){
			if ( pulseBlueWorking == true ){
				readSerial1();
				strokeWeightBlue =  map(pulseVal1, pulseVal1High, pulseVal1Low, 0, 12);
				strokeWeightBlue = constrain(strokeWeightBlue, 0, 15);
			} else {
				readSerial2();
				strokeWeightBlue = map(pulseVal2, pulseVal2High, pulseVal2Low, 0, 12);
				strokeWeightBlue = constrain(strokeWeightRed, 0, 15);
			}

			if ( pulseRedWorking == true){
				readSerial2();
				strokeWeightRed = map(pulseVal2, pulseVal2High, pulseVal2Low, 0, 12);
				strokeWeightRed = constrain(strokeWeightRed, 0, 15);
			} else {
				readSerial1();
				strokeWeightRed =  map(pulseVal1, pulseVal1High, pulseVal1Low, 0, 12);
				strokeWeightRed = constrain(strokeWeightBlue, 0, 15);
			}
		} else { //IF BOTH SENSORS NOT WORKING, USE AUTOIMAGES
			strokeWeightBlue = 6 * abs(cos(radClock()));
			strokeWeightRed = 8 * abs(sin(radClock()));
			if ( currentState == 3){
				animateSpeed = 0.01 * constrain(abs(sin(radClock())), 0.2, 1.0); 
				//println("Animate speed = " + animateSpeed);
			}
		}

		//float pulseDiff = abs(pulseVal1 - pulseVal2);

		animateSpeed = 0.003;
		// animateSpeed = map(pulseDiff, 0, 5, 0, 0.01);
		// animateSpeed = constrain(pulseDiff, 0, 0.1);
	}
}

void replotOrigin(){
	if (originRightReplotted == false || originLeftReplotted == false){
		println("NOW REPLOTTING ORIGINS");
		cursor();
		if(originRightReplotted == false || originLeftReplotted == false) {
			if (mouseY > 300){
				//if mouse in left zone

				if (mouseX < width*0.5){
					  
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
				if (mouseX > width*0.5){  
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
				if (mouseX < width*0.5){
					if ( mousePressed == true){
						setLeftOrigin( width*0.25, 300+(height-300)*0.5 );
						originLeftReplotted = true;
						println("Origin left replotted."); 
					}
				}
				//if mouse in right zone
				if (mouseX > width*0.5){
					if ( mousePressed == true){
						setRightOrigin( width*0.75, 300+(height-300)*0.5 );	
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

void drawGrid1() {
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
		stroke(theRed, opacity * 0.6);
		line(0,0, width, 0);

		//opacity -= 255/10;
		popMatrix();
	}
	popMatrix();
}


void drawGrid2() {
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
		stroke(theBlue, opacity * 0.6);
		line(0,0, width, 0);

		//opacity -= 255/10;
		popMatrix();
	}
	popMatrix();
}

void drawGridWhite() {
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


void drawLogos() {
	//Draw Pulse Logos
	pushStyle();
	imageMode(CENTER);
	tint(255);

	if ( autoPilotMode == false || autoPilotMode == true  ){
		//TO CALIBRATED ORIGIN
		image(logo, leftOrigin.x, leftOrigin.y, 200, 50);
		image(logo, rightOrigin.x, rightOrigin.y, 200, 50);

	} else {
		//TO TRUE ORIGIN / CHAIR
		image(logo, width*0.25, 300+(height-300)*0.5, 200, 50);
		image(logo, width*0.75, 300+(height-300)*0.5,  200, 50);		
	}	
	popStyle();
}

void beRightBack() {
	//Draw Pulse Logos
	pushStyle();
	imageMode(CENTER);
	tint(255);
	image(logo, leftOrigin.x, leftOrigin.y, 200, 50);
	image(logo, rightOrigin.x, rightOrigin.y, 200, 50);
	textAlign(CENTER, TOP);
	fill(255,100);
	textFont(ProximaNovaBold, 20);
	text("INTERMISSION : BACK 7:10PM", leftOrigin.x, leftOrigin.y + 40);
	text("INTERMISSION : BACK 7:10PM", rightOrigin.x, rightOrigin.y + 40);
	popStyle();
}



void drawCrosshairs() {
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










