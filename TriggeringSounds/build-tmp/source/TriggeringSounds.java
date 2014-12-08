import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class TriggeringSounds extends PApplet {

//SOUND 


Minim minim; 
AudioSample heartbeat1;
AudioSample heartbeat2;

AudioSample soundbed;

//data from arduino
float pulseVal; 


//SERIAL com

Serial myPort;
boolean firstContact = false;

public void setup(){
  
  size(512, 200, P2D);
  minim = new Minim(this);
  
  //SERIAL com
  pulseVal = 0; 
  String portName = Serial.list()[3];
  myPort = new Serial(this, portName, 9600);


 // load files from  data folder
  heartbeat1 = minim.loadSample( "data/heartbeat35.mp3", 512 );
  heartbeat2 = minim.loadSample( "data/heartbeat38.wav", 512 );
  soundbed = minim.loadSample("data/soundbed1.wav", 512);
}

public void draw(){
}

/*void serialEvent(Serial myPort) {
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
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}*/

public void keyPressed() {
    if ( key == 's' ) soundbed.trigger();
    if ( key == 'h' ) heartbeat1.trigger();
    if ( key == 'j' ) heartbeat2.trigger();
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "TriggeringSounds" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
