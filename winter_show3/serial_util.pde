//SERIAL STUFF


//SERIAL #1
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
		  pulseVal1 = float(myString);
		  //pulseVal1 = map(pulseVal1, 500, 515, 0, height/10.0); 
			}
		  //println(pulseVal);
		  //println("READING 1: " + myString);

		// when you've parsed the data you have, ask for more:
		myPort.write("A");
	}
}


//SERIAL #2 
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
		//pulseVal2 = map(pulseVal2, 500, 515, 0, height/10.0);
		}
      //println("READING 2: " + myString2);

    // when you've parsed the data you have, ask for more:
    myPort2.write("A");
  }

}


//Remember to set timer and set findMinInit to false
void calibrateSensors() {

	//Collect Data for 
	if (calibrationTimer > 0){
		calibrationTimer -= 1;
		println("Calibration Countdown: " + calibrationTimer);
		//Record Max Values
		pulseVal1High = max(pulseVal1High, pulseVal1);
		pulseVal2High = max(pulseVal2High, pulseVal2);
		
		pulseVal1Low = min(pulseVal1Low, pulseVal1);
		pulseVal2Low = min(pulseVal2Low, pulseVal2);

	}
	if (calibrationTimer <= 0){
		pulseVal1Diff = pulseVal1High - pulseVal1Low; 
		pulseVal2Diff = pulseVal2High - pulseVal2Low; 
		if ( pulseVal1Diff < 40 && pulseVal2Diff < 40 ){
			pulseVal1Threshold = pulseVal1Low + pulseVal1Diff * 0.75; 
			pulseVal2Threshold = pulseVal2Low + pulseVal2Diff * 0.75; 
			calibrationDone = true;
			println("CALIBRATION DONE");
			println("------------------------");
			println("------------------------");
			println("Pulse 1 High : " + pulseVal1High);
			println("Pulse 1 Low : " + pulseVal1Low);
			println("Pulse 1 Diff : " + pulseVal1Diff);
			println("Pulse 1 Threshold : " + pulseVal1Threshold);
			println("------------------------");
			println("Pulse 2 High : " + pulseVal2High);
			println("Pulse 2 Low : " + pulseVal2Low);
			println("Pulse 2 Diff : " + pulseVal2Diff);
			println("Pulse 2 Threshold : " + pulseVal2Threshold);
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
			println("Pulse 2 High : " + pulseVal2High);
			println("Pulse 2 Low : " + pulseVal2Low);
			println("Pulse 2 Diff : " + pulseVal2Diff);
			println("Pulse 2 Threshold : " + pulseVal2Threshold);
			println("------------------------");
			println("------------------------");
			calibrationDone = false;
		}
	}
		
}

void resetCalibration() {
	//Reset for calibration
	calibrationTimer = 500; 

	pulseVal1Low = 600; 
	pulseVal1High = 0; 

	pulseVal2Low = 600; 
	pulseVal2High = 0; 

	calibrationDone = false; 
}