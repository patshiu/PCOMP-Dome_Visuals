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

	PGraphics show(){
		btnCanvas.beginDraw();
		btnCanvas.image(state1, 0, 0);
		btnCanvas.endDraw();
		return btnCanvas; 
	}

	void setCanvasLoc(float x, float y) {
		locX = x; 
		locY = y;
	}

	boolean isUnderCursor() {
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

	void toggle() {

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

	PGraphics show(){ //THIS RETURNS A PGRAPHICS
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


	void setCanvasLoc(float x, float y) {
		locX = x; 
		locY = y;
	}


	Boolean isUnderCursor() {
		if( mouseX >= locX && mouseX <= (locX + btnOn.width) && mouseY >= locY && mouseY <= (locY + btnOn.height)){
			return true; 
		} else {
			return false;
		}
	}

	//This function checks if the button is under the mouse, 
	//if yes, button state is toggled
	void listen(){
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

	void setValue(float input){

		//If mouse press is valid, then move sliderBtn to the correct location on slider
		//Update "sliderValue" to match, using map on 
		float valueIn = input - locX; //convert MouseX to pos of mouse on slider
		locBtn = constrain(valueIn, 0, sliderLine.width - sliderBtn.width - 0); //Calculate the position of the sliderbutton (from edge of slider)
		sliderValue = map(locX + locBtn, locX + 0, locX + sliderLine.width - sliderBtn.width - 0, 0, 255);

	}

	void setCanvasLoc(float x, float y) {
		locX = x; 
		locY = y;
	}

	PGraphics show(){ //THIS RETURNS A PGRAPHICS
		sliderCanvas.beginDraw();
		sliderCanvas.clear();
		sliderCanvas.image(sliderLine, 0, 0);
		sliderCanvas.image(sliderBtn, locBtn, 0);
		sliderCanvas.endDraw();
		return sliderCanvas;
	}

	boolean isUnderCursor() {
		if( mouseX >= locX && mouseX <= (locX + sliderLine.width) && mouseY >= locY && mouseY <= (locY + sliderLine.height) ) {
			return true; 
		} else {
			return false;
		}
	}

	void listen() {
		if( mousePressed == true && isUnderCursor() == true ) {
			setValue(mouseX);
			println("Slider value is now " + sliderValue );
		}
	}


}