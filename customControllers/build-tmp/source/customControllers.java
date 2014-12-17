import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class customControllers extends PApplet {

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



public void setup() {
	
	background(0xff222222);
	testImg = loadImage("data/wheel.png");

	vignette = loadImage("slice_vignette.png");
	size(displayWidth, displayHeight, OPENGL);

	notification = "";

	//Setup fonts
	ProximaNovaLight = loadFont("ProximaNova-Light-12.vlw");
	ProximaNova = loadFont("ProximaNova-Regular-12.vlw");
	ProximaNovaBold = loadFont("ProximaNova-Semibold-20.vlw");
}

public void draw() {

	background(0xff222222);
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

public void resetSketch(){
/* Reset Sketch */
}

public void mouseDragged() {

	squareGlitchSize.listen();
	
	squareGlitchAspect.listen();

	huemixxGlitchness.listen();

}

public void mousePressed(){

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


public void fileSelected(File selection) {
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
 


public void keyPressed() {
	//println(keyCode); 

	if (keyCode == 9){ //If "tab" is hit, toggle the UI
		toggleHeader();
		toggleSidebar();
	}

}




//UTILITY MODULE - GUI 
//-------------------
//This module creates a GUI for this image glitching app. 

//This adds the top bar

int headerHeight = 10 + 44 + 15; // Top Padding + button height + Bottom Padding
float headerPos = 0; 
boolean headerInFull = true;
PGraphics headerBar; //NOTE: This MUST be declared in setup 

int sidebarWidth = 330; 
float sidebarPos; 
boolean sidebarInFull = true; 
PGraphics sidebar; 

//Elements for header
PImage logo; 
PImage headerBg; 
Btn importBtn;
Btn resetBtn; 
Btn pauseBtn; 
Btn exportBtn; 

//Elements for sidebar
PImage sidebarBg; 
PImage sidebarDiv; 
PImage toggleInstructions; 


//Elements for square glitch
ToggleBtn squareGlitchOnOff; 
Slider squareGlitchSize; 
Slider squareGlitchAspect; 

//Elements for huemixx glitch
ToggleBtn huemixxGlitchOnOff; 
Slider huemixxGlitchness;

//Elements for drift glitch
ToggleBtn driftToggle; 

//Hides the GUI for snapshot
boolean snapshotTime = false; 


public void drawUI(){
	addHeader();
	addSidebar();
}




//This adds the top bar
public void addHeader(){
	if (headerBar == null){
		headerInit();
	}
	headerBar.beginDraw();
	headerBar.fill(50, 50, 50);
	headerBar.rect(0, 0, width, headerHeight); //Draw header background color, in case background image does not loga
	headerBar.image(headerBg, 0, 0, width, headerHeight);
	headerBar.image(logo, 100, 15);
	//headerBar.rect(10,5, 300, 38); //Holder for logo

	//Notification
	headerBar.fill(255, notificationBrightness);
	headerBar.textSize(12);//Setting text for label. Proxima Nova Semibold 16px
	headerBar.textAlign(RIGHT, CENTER);
	headerBar.textFont(ProximaNovaLight);
	headerBar.text(notification, width - 40 - importBtn.state1.width - 20 - resetBtn.state1.width - 20 - pauseBtn.state1.width - 20 - exportBtn.state1.width - 45, headerPos + headerHeight/2);//Label; 4px added to 2 to adjust label baseline

	
	//Import btn
	headerBar.image(importBtn.show(), width - importBtn.state1.width - 20 - resetBtn.state1.width - 20 - pauseBtn.state1.width - 20 - exportBtn.state1.width - 45, 10);
	importBtn.setCanvasLoc(width - importBtn.state1.width - 20 - resetBtn.state1.width - 20 - pauseBtn.state1.width - 20 - exportBtn.state1.width - 45, headerPos + 10);

	//Reset btn
	headerBar.image(resetBtn.show(), width - resetBtn.state1.width - 20 - pauseBtn.state1.width - 20 - exportBtn.state1.width - 45, 10);
	resetBtn.setCanvasLoc(width - resetBtn.state1.width - 20 - pauseBtn.state1.width - 20 - exportBtn.state1.width - 45, headerPos + 10);

	//Pause btn
	headerBar.image(pauseBtn.show(), width - pauseBtn.state1.width - 20 - exportBtn.state1.width - 45, 10);
	pauseBtn.setCanvasLoc(width - pauseBtn.state1.width - 20 - exportBtn.state1.width - 45, headerPos + 10);
	
	//Export btn
	headerBar.image(exportBtn.show(), width - exportBtn.state1.width - 45, 10);
	exportBtn.setCanvasLoc(width - exportBtn.state1.width - 45, headerPos + 10);
	
	headerBar.endDraw();
	displayHeader();
}

public void headerInit(){
	headerBar = createGraphics(width, headerHeight); //Set up header canvas
	logo = loadImage("slice_logo.png");
	headerBg = loadImage("slice_header_background.png");
	importBtn = new Btn("sliced_btn_import.png");
	resetBtn = new Btn("slice_btn_reset.png");
	pauseBtn = new Btn("slice_btn_pause.png");
	exportBtn = new Btn("sliced_btn_export2.png");
}

//This hides / unhides the header
public void toggleHeader(){
	if (headerInFull == true) {
		headerInFull = false;
		println("Header is now gonna be hiding.");
		return;
	}
	if (headerInFull == false) {
		headerInFull = true; 
		println("Header is now gonna be showing.");
		return;
	}
}

public void displayHeader(){
	//Make the header hide
	if (snapshotTime == false){
		if (headerInFull == false){
			if (headerPos > -48){
				headerPos-= 5 + ( abs(headerPos) - 48 / 10); 
				constrain(headerPos, 0, -48);
			}
			image(headerBar, 0, headerPos, width, headerHeight);
			return;
		}
		//Made the header show
		if (headerInFull == true){
			if (headerPos < 0){
				headerPos += 5 + ( abs(headerPos) / 10); 
				constrain(headerPos, 0, -48);
			}
			image(headerBar, 0, headerPos, width, headerHeight);
			return;
		}
	}
}


//This adds the sidebar control panel 
public void addSidebar(){
	if (sidebar == null){
		initSidebar();
	}
	sidebar.beginDraw();
	sidebar.fill(0xff2b2b2b);
	sidebar.stroke(0xffff00e6); //Magenta stroke
	sidebar.rect( 1, 1, sidebarBg.width -1, sidebarBg.height -10); //Draw a background in case sidebarBg image does not load
	sidebar.image(sidebarBg, 0, 0, sidebarBg.width, sidebarBg.height);
	
	///Add Filter Well
	sidebar.fill(255, 25);
	sidebar.noStroke();
	sidebar.rect(10,15, sidebarBg.width - 10*2, 20 + 80);

	//Adding squareGlitchToggle
	sidebar.fill(255);
	sidebar.textSize(16);//Setting text for label. Proxima Nova Semibold 16px
	sidebar.textAlign(LEFT, TOP);
	sidebar.textFont(ProximaNovaBold);
	sidebar.text("PIXELITE", 20 + squareGlitchOnOff.btnOn.width + 10, 40 + 2);//Label; 4px added to 2 to adjust label baseline
	sidebar.image(squareGlitchOnOff.show(), 20, 40);
	squareGlitchOnOff.setCanvasLoc( 20 + sidebarPos , 40 + headerHeight + 10);

	//Adding squareGlitchAspect slider
	// sidebar.fill(255);
	// sidebar.textAlign(LEFT, TOP);
	// sidebar.textFont(ProximaNovaLight);
	// sidebar.textSize(10);//Setting text for label. Proxima Nova Semibold 16px
	// sidebar.text("GLITCH ASPECT RATIO", 20 + squareGlitchOnOff.btnOn.width + 10 , 100 - 20);//Label; 4px added to 2 to adjust label baseline
	// sidebar.image(squareGlitchAspect.show(),20 + squareGlitchOnOff.btnOn.width + 10 , 100);
	// squareGlitchAspect.setCanvasLoc( 20 + squareGlitchOnOff.btnOn.width + 10 + sidebarPos , 100 + headerHeight + 10);
	

	//Adding squareGlitchSize slider
	// sidebar.fill(255);
	// sidebar.textAlign(LEFT, TOP);
	// sidebar.textFont(ProximaNovaLight);
	// sidebar.textSize(10);//Setting text for label. Proxima Nova Semibold 16px
	// sidebar.text("GLITCH MIN SIZE", 20 + squareGlitchOnOff.btnOn.width + 10 , 80 - 20);//Label; 4px added to 2 to adjust label baseline
	sidebar.image(squareGlitchSize.show(), 20 + squareGlitchOnOff.btnOn.width + 10 , 80);
	squareGlitchSize.setCanvasLoc( 20 + squareGlitchOnOff.btnOn.width + 10  + sidebarPos , 80 + headerHeight + 10); 

	//Adding a divider
	//sidebar.image(sidebarDiv, 20, 200);

	///Add Filter Well
	sidebar.fill(255, 25);
	sidebar.noStroke();
	sidebar.rect(10,135, sidebarBg.width - 10*2, 20 + 80);
	

	//Adding hueGlitchToggle
	sidebar.fill(255);
	sidebar.textSize(16);//Setting text for label. Proxima Nova Semibold 16px
	sidebar.textAlign(LEFT, TOP);
	sidebar.textFont(ProximaNovaBold);
	sidebar.text("HUEMIXX", 20 + huemixxGlitchOnOff.btnOn.width + 10, 160 + 2);//Label; 4px added to 2 to adjust label baseline
	sidebar.image(huemixxGlitchOnOff.show(), 20, 160);
	huemixxGlitchOnOff.setCanvasLoc( 20 + sidebarPos , 160 + headerHeight + 10);
	//Adding hueGlitchSize slider
	sidebar.image(huemixxGlitchness.show(), 20 + huemixxGlitchOnOff.btnOn.width + 10 , 200);
	huemixxGlitchness.setCanvasLoc( 20 + huemixxGlitchOnOff.btnOn.width + 10  + sidebarPos , 200 + headerHeight + 10); 

	///Add Filter Well
	sidebar.fill(255, 25);
	sidebar.noStroke();
	sidebar.rect(10,255, sidebarBg.width - 10*2, 20 + 40);

	//Adding driftGlitch toggle
	sidebar.fill(255);
	sidebar.textSize(16);//Setting text for label. Proxima Nova Semibold 16px
	sidebar.textAlign(LEFT, TOP);
	sidebar.textFont(ProximaNovaBold);
	sidebar.text("DRIFT", 20 + squareGlitchOnOff.btnOn.width + 10, 275 + 2);//Label; 4px added to 2 to adjust label baseline
	sidebar.image(driftToggle.show(), 20, 275);
	driftToggle.setCanvasLoc( 20 + sidebarPos , 275 + headerHeight + 10);

	//Adding a divider
	//sidebar.image(sidebarDiv, 20, 260);

	//Adding the instructions to toggle control panel hiding
	sidebar.image(toggleInstructions, 20, 335);

	//More filters coming soon notice
	sidebar.fill(0xff5a5a5a);
	sidebar.textSize(12);//Setting text for label. Proxima Nova Light 12px
	sidebar.textAlign(LEFT, BOTTOM);
	sidebar.textFont(ProximaNovaLight);
	sidebar.text("MORE FILTERS COMING SOON", 20, sidebarBg.height - 30);//Label aligned to bottom, to be 30px away from bottom of sidebar
	sidebar.endDraw();
	displaySidebar();
	
}

public void initSidebar() { //sets up sidebar

	//set up sidebar PImages
	sidebarBg = loadImage("slice_sidebar_background.png");
	sidebarDiv = loadImage("slice_sidebar_divider.png");
	toggleInstructions = loadImage("slice_toggle_instructions.png");

	//set up sidebar canvas
	//sidebar = createGraphics(sidebarWidth, height - headerHeight - 20); //Set height dynamically to canvas height
	sidebar = createGraphics(sidebarBg.width, sidebarBg.height);
	sidebarPos = width - sidebarWidth;

	//set up all the control elements
	//Elements for Drift Glitch
	driftToggle = new ToggleBtn(true, 0, 0);

	//Elements for Square Glitch
	squareGlitchOnOff = new ToggleBtn(true, 0, 0);
	squareGlitchSize = new Slider(0, 0, 0); //Make sure slider inits with right button position
	squareGlitchAspect = new Slider(0, 0, 0);

	//Elements for Square Glitch
	huemixxGlitchOnOff = new ToggleBtn(true, 0, 0);
	huemixxGlitchness = new Slider(0, 0, 0); //Make sure slider inits with right button position
}

public void toggleSidebar() {
	if (sidebarInFull == true) {
		sidebarInFull = false;
		println("Sidebar is now gonna be hiding.");
		return;
	}
	if (sidebarInFull == false) {
		sidebarInFull = true; 
		println("Sidebar is now gonna be in full.");
		return;
	}
}

public void displaySidebar() {
	if (snapshotTime == false ){
		//Make the sidebar hide
		if (sidebarInFull == false){
			if (sidebarPos < width){
				sidebarPos+= 5 + (abs(width - sidebarPos) / 10);
				constrain(sidebarPos, width - sidebarWidth, width);
			}
			image(sidebar, sidebarPos, headerHeight + 10);
			return;
		}
		//Made the sidebar show | decrease width to width-sidebarWidth
		if (sidebarInFull == true){
			if (sidebarPos > (width - sidebarWidth) ){
				sidebarPos -= 5 + (abs((width - sidebarWidth) - sidebarPos) / 10);
				constrain(sidebarPos, width - sidebarWidth, width);
			}
			image(sidebar, sidebarPos, headerHeight + 10);
			return;
		}
	}
}


//GUI element classes
//-------------------
//Button & Slider class defined here

class Btn {
	PImage state1; 
	PImage state2; 
	PGraphics btnCanvas; 
	boolean isUnderCursor; 
	float locX; 
	float locY; 

	Btn(String stateALoc , String stateBLoc){
		state1 = loadImage(stateALoc); 
		state2 = loadImage(stateBLoc); 
		locX = 0; 
		locY = 0; 
		btnCanvas = createGraphics(state1.width, state1.height);
	}

	Btn(String stateALoc){
		state1 = loadImage(stateALoc); 
		state2 = loadImage(stateALoc); 
		locX = 0; 
		locY = 0; 
		btnCanvas = createGraphics(state1.width, state1.height);
	}

	public PGraphics show(){
		btnCanvas.beginDraw();
		btnCanvas.image(state1, 0, 0);
		btnCanvas.endDraw();
		return btnCanvas; 
	}

	public void setCanvasLoc(float x, float y) {
		locX = x; 
		locY = y;
	}

	public boolean isUnderCursor() {
		if( mouseX >= locX && mouseX <= (locX + state1.width) && mouseY >= locY && mouseY <= (locY + state1.height)){
			return true; 
		} else {
			return false;
		}
	}
}

class ToggleBtn {
	boolean isOn; 
	PImage btnOn; 
	PImage btnOff;
	PGraphics toggleBtnCanvas;
	boolean isUnderCursor; 
	float locX; 
	float locY; 


	ToggleBtn(boolean initState, float x, float y) {
		isOn = initState; 
		btnOn = loadImage("slice_toggle_ON.png");
		btnOff = loadImage("slice_toggle_OFF.png");
		locX = x; 
		locY = y; 
		toggleBtnCanvas = createGraphics(btnOn.width, btnOn.height); 
		isUnderCursor = false; 
	}

	public void toggle() {

		//Check if the mouse click is on the object. 
		if (isOn == true){
			isOn = false;
			return; 
		}
		if (isOn == false){
			isOn = true;
			return; 
		}
	}

	public PGraphics show(){ //THIS RETURNS A PGRAPHICS
		toggleBtnCanvas.beginDraw();
		toggleBtnCanvas.clear();
		if( isOn == true){
			toggleBtnCanvas.image(btnOn, 0, 0);
		}
		else {
			toggleBtnCanvas.image(btnOff, 0, 0);
		}
		toggleBtnCanvas.endDraw();
		return toggleBtnCanvas;
	}


	public void setCanvasLoc(float x, float y) {
		locX = x; 
		locY = y;
	}


	public Boolean isUnderCursor() {
		if( mouseX >= locX && mouseX <= (locX + btnOn.width) && mouseY >= locY && mouseY <= (locY + btnOn.height)){
			return true; 
		} else {
			return false;
		}
	}

	//This function checks if the button is under the mouse, 
	//if yes, button state is toggled
	public void listen(){
 		if ( mousePressed == true && isUnderCursor() == true ) {
			toggle();
		}
	}
}

//SLIDER
//Have a value that is constrained to a range
//When user clicks and drags on the object, move the slider btn image, update the value stored

class Slider {
	float sliderValue; 
	PImage sliderBtn = loadImage("slice_slider_btn.png");; 
	PImage sliderLine = loadImage("slice_slider_spine.png");;
	PGraphics sliderCanvas;
	boolean isUnderCursor; 
	float locX; 
	float locY; 
	float locBtn;

	Slider(float initVal, float x, float y ){
		sliderValue = constrain(initVal, 0, 255); //This constrains the slider's value 0 - 255
		locX = x; 
		locY = y; 
		locBtn = constrain(sliderValue, 0, sliderLine.width - sliderBtn.width - 0); 
		sliderCanvas = createGraphics(sliderLine.width, sliderLine.height);
		isUnderCursor = false; 
	}

	public void setValue(float input){

		//If mouse press is valid, then move sliderBtn to the correct location on slider
		//Update "sliderValue" to match, using map on 
		float valueIn = input - locX; //convert MouseX to pos of mouse on slider
		locBtn = constrain(valueIn, 0, sliderLine.width - sliderBtn.width - 0); //Calculate the position of the sliderbutton (from edge of slider)
		sliderValue = map(locX + locBtn, locX + 0, locX + sliderLine.width - sliderBtn.width - 0, 0, 255);

	}

	public void setCanvasLoc(float x, float y) {
		locX = x; 
		locY = y;
	}

	public PGraphics show(){ //THIS RETURNS A PGRAPHICS
		sliderCanvas.beginDraw();
		sliderCanvas.clear();
		sliderCanvas.image(sliderLine, 0, 0);
		sliderCanvas.image(sliderBtn, locBtn, 0);
		sliderCanvas.endDraw();
		return sliderCanvas;
	}

	public boolean isUnderCursor() {
		if( mouseX >= locX && mouseX <= (locX + sliderLine.width) && mouseY >= locY && mouseY <= (locY + sliderLine.height) ) {
			return true; 
		} else {
			return false;
		}
	}

	public void listen() {
		if( mousePressed == true && isUnderCursor() == true ) {
			setValue(mouseX);
			println("Slider value is now " + sliderValue );
		}
	}


}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "customControllers" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
