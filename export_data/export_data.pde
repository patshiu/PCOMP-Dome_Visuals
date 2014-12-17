import processing.serial.*;
Serial mySerial;
PrintWriter output;  
String[] list;


void setup() {
size(720,700);
background(255);

String portName = Serial.list()[2];
println(portName);
mySerial = new Serial(this, portName, 9600);
output = createWriter("test.txt");
}
void draw() {
if (mySerial.available() > 0 ) {
     String value = mySerial.readString();
     if ( value != null ) {
        fill(50);
        text(value,10,10,700,700);
        output.println(value);
      saveStrings("test.txt", value);
     }
  } 
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
      pulseVal = map(pulseVal, 500, 515, 0, height/80.0);

		if (pulseVal > height*0.75){
			//DO SOMETHING 
		}
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}