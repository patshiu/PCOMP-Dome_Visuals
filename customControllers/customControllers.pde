// CUSTOM CONTROLLERS
// obsesstheprocess.com


PFont ProximaNova;
PFont ProximaNovaBold;
PFont ProximaNovaLight;

PImage testImg; 

//For global UI
boolean pauseSketch = false;


//For editing area UI
PImage vignette; 

boolean toggleDisperse = true;

//Set up for import
String choosenFilePath;
String notification;
float notificationBrightness = 0; 



void setup() {
	
	background(#222222);
	testImg = loadImage("data/wheel.png");

	vignette = loadImage("slice_vignette.png");
	size(displayWidth, displayHeight, OPENGL);

	notification = "";

	//Setup fonts
	ProximaNovaLight = loadFont("ProximaNova-Light-12.vlw");
	ProximaNova = loadFont("ProximaNova-Regular-12.vlw");
	ProximaNovaBold = loadFont("ProximaNova-Semibold-20.vlw");
}

void draw() {

	background(#222222);
	image(vignette, 0, 0, width, height);
 	float x = (width - testImg.width)/2;
 	float y = (height - testImg.height)/2;
 	pushMatrix();
	translate( x, y );
	/* Glitch FX goes here */	
	popMatrix();

	//Helper for the "push notification" in uppder right
  	if ( notificationBrightness > 0){
  		notificationBrightness -= 5;
  		//println(notificationBrightness);
  	}

  	//DRAW UI
  	drawUI();

  	//UI listeners 

}

//WRAP CONTROL UPDATES INTO CONTROL

void resetSketch(){
/* Reset Sketch */
}

void mouseDragged() {

	squareGlitchSize.listen();
	
	squareGlitchAspect.listen();

	huemixxGlitchness.listen();

}

void mousePressed(){

	squareGlitchOnOff.listen();
	//Update recursiveSplitObject on / off based on toggle button status
		if( squareGlitchOnOff.isOn == false){
			//recursiveSplitObject.isOn = false; 
		}
		else {
			//recursiveSplitObject.isOn = true;
		}


	huemixxGlitchOnOff.listen();
	//Update huemixxObject on / off based on toggle button state
		if( huemixxGlitchOnOff.isOn == false){
			//huemixxObject.isOn = false; 
		}
		else {
			//huemixxObject.isOn = true;
		}

	driftToggle.listen();
	//Update drift FX based on toggle button state
		if( driftToggle.isOn == false){
			//imgCloud.goHome(true);
		}
		else {
			//imgCloud.flockToField(true);
			//imgCloud.goHome(false);
		}

	//THESE BUTTONS IN THE UPPER RIGHT DON'T HAVE A LISTENER FUNCTION YET
	//CAUSE I'M TIRED. 
	if (importBtn.isUnderCursor() == true){
		//Setup Import function
		selectInput("Select a file to process:", "fileSelected");
		println("Import button was pressed.");
		notificationBrightness = 255 * 2; 
		notification = "Open a file";

	}

	if (resetBtn.isUnderCursor() == true){
		println("Reset button was pressed");
		resetSketch();
		notificationBrightness = 255 * 2; 
		notification = "Filter settings reset to default";
	}

	if (pauseBtn.isUnderCursor() == true){
		println("Pause button was pressed");
		if ( pauseSketch == false ){
			pauseSketch = true; 
			notificationBrightness = 255 * 2;
			notification = "Sketch is now paused";	
			return;		
		} else {
			pauseSketch = false; 
			notificationBrightness = 255 * 2;
			notification = "Sketch is now playing";	
		}
	}

	if (exportBtn.isUnderCursor() == true){
		println("Export button was pressed");
		//snapshotTime = true;
		notificationBrightness = 255 * 2; 
		notification = "Image saved to _SNAPSHOTS folder";
	}

}


void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    choosenFilePath = selection.getAbsolutePath();
    println("User selected " + choosenFilePath);
    testImg = loadImage(choosenFilePath);
	//imgCloud.updateImg(testImg);
	//recursiveSplitObject.updateImg(testImg);
	//huemixxObject.updateImg(testImg);
  }
}
 


void keyPressed() {
	//println(keyCode); 

	if (keyCode == 9){ //If "tab" is hit, toggle the UI
		toggleHeader();
		toggleSidebar();
	}

}




