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

public class export_data extends PApplet {


Serial mySerial;
PrintWriter output;  
String[] list;


public void setup() {
size(720,700);
background(255);

String portName = Serial.list()[2];
println(portName);
mySerial = new Serial(this, portName, 9600);
output = createWriter("test.txt");
}
public void draw() {
if (mySerial.available() > 0 ) {
     String value = mySerial.readString();
     if ( value != null ) {
        fill(50);
        text(value,10,10,700,700);
        output.println(value);
      saveStrings("test.txt", PApplet.parseInt(value));
     }
  } 
}

//SERIAL STUFF
public void serialEvent(Serial myPort) {
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
      pulseVal = PApplet.parseFloat(myString);
      pulseVal = map(pulseVal, 500, 515, 0, height/80.0f);

		if (pulseVal > height*0.75f){
			//DO SOMETHING 
		}
      //println(pulseVal);
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "export_data" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
