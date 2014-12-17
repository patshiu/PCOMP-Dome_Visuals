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
// 2. Starting State — Run pulseInit() in background, if fail, show notice, calibrate again until ESC, on which return to 1. 
// 3. Running State - Running visuals
// 4. Ending State - Fade out, return to 1

//GLOBAL UI CONTRLS 

int currentState; 

PVector leftOrigin = new PVector(0,0); 
PVector rightOrigin = new PVector(0,0); 

//IMAGE SETUP 
PImage maskHard;
PImage maskSoft; 
PImage blueCrosshairs;
PImage redCrosshairs; 
PImage logo; 
float gridAnimate;


//VISUAL VARIABLES
color theBlue = #00a8ff;
color theRed = #f05315;
float animateSpeed; 
float blackness; 

void setup() {
	size(1024,768);

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


	//Setup Origns
	setLeftOrigin( width*0.25, 300+(height-300)*0.5 ); 
	setRightOrigin( width*0.75, 300+(height-300)*0.5 ); 
}

void draw() {

	//THE MAIN SWITCHBOARD
	switch(currentState) {
		case 0:
			println("Bitches be like 0.");
			blackness = 255; 
			background(0);
			animateGrid();
			animateSpeed = 0.01; 
			drawGridWhite();
			drawLogos();
			image(maskSoft, 0, 0, width, height);
			break;

		case 1:
			println("Bitches be like 1.");	
			blackness = 255; 
			background(0);
			animateGrid();
			drawGridWhite();
			drawCrosshairs();
			image(maskSoft, 0, 0, width, height);
			break;

		case 2:
			println("Bitches be like 2.");

			//Animate fade in 
			if (blackness > 10 ){
				blackness -= 1;
				println("OPACITY : " + blackness);
			}
			animateGrid();
			drawGrid1();
			drawGrid2();
			image(maskSoft, 0, 0, width, height);
			fill(0, blackness);
			rect(0,0,width,height);
			break;

		case 3:
			println("Bitches be like 3.");
			blackness = 255; 
			break;

		case 4:
			println("Bitches be like 4.");
			blackness = 255; 
			break;

		default:
			println("Bitches be like that's cray.");
			break;

	}
}

//UTILS



void keyTyped() {
	// println("Key pressed is " + key);
	// println("Key pressed is " + int(key));
	setCurrentState(key);
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
		float var = map(mouseX, 0, width, 0, 12);
		strokeWeight(var);
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
		float var = map(mouseX, 0, width, 0, 12);
		strokeWeight(var);
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
	image(logo, leftOrigin.x, leftOrigin.y, 200, 50);
	image(logo, rightOrigin.x, rightOrigin.y, 200, 50);
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










