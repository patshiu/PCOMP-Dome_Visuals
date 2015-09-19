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
void setCurrentState( int state) {
	if( state == 48 ||
		state == 49 ||
		state == 50 ||
		state == 51 ||
		state == 52 ||
		state == 53 ){

		switch (state) {
			case 48:
				currentState = 0; 
				originReplotMode = true;
				originLeftReplotted = false; 
				originRightReplotted = false; 
				println("Current state is now " + currentState + ".");
				break;

			case 49:
				//1 - TITLE SCREEN
				blackness = 255; 
				expTimer = 1000;
				currentState = 1; 
				println("Current state is now " + currentState + ".");
				break;

			case 50:
				//2 —  CALIBRATION MODE
				currentState = 2; 
				if ( liveMode == true ){
					resetCalibration();
				}
				calibrationDone = false;
				println("Current state is now " + currentState + ".");
				break;

			case 51:
				//3 — PLAY VISUALS MODE
				currentState = 3; 
				blackness = 255; 
				expTimer = 2800*1.2; // 3000 = 1 min
				println("Current state is now " + currentState + ".");
				break;

			case 52:
				//4 — 1 MINUTE UP NOTICE
				currentState = 4; 
				blackness = 10; 
				expTimer = 500;
				println("Current state is now " + currentState + ".");
				break;
			
			case 53:
				//1 - TITLE SCREEN
				blackness = 255; 
				expTimer = 1000;
				currentState = 5; 
				println("Current state is now " + currentState + ".");
				break;

		}
	} else {
		println("Invalid state. States are 0 through 4. ");
	}	
}

//UI UTILS 

void setLeftOrigin(float x, float y) {
	leftOrigin.x = x; 
	leftOrigin.y = y; 
	println("Left Origin is now x:" + leftOrigin.x + "   y:" + leftOrigin.y + ".");
}


void setRightOrigin(float x, float y) {
	rightOrigin.x = x; 
	rightOrigin.y = y; 
	println("Left Origin is now x:" + leftOrigin.x + "   y:" + leftOrigin.y + ".");
}