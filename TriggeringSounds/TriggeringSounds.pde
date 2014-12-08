//SOUND 
import ddf.minim.*;

Minim minim;
AudioSample heartbeat1;
AudioSample heartbeat2;

AudioSample soundbed;

//data from arduino
float pulseVal; 


//SERIAL com
import processing.serial.*;
Serial myPort;
boolean firstContact = false;

void setup(){
  
  size(512, 200, P3D);
  minim = new Minim(this);
  
  //SERIAL com
  pulseVal = 0; 
  String portName = Serial.list()[2];
  myPort = new Serial(this, portName, 9600);


 // load files from  data folder
heartbeat1 = minim.loadSample( "heartbeat35.mp3", // filename
                          512      // buffer size
                       );
                         
heartbeat2 = minim.loadSample( "heartbeat34.wav", // filename
                          512      // buffer size
                       );
                       
soundbed = minim.loadSample("soundbed1.wav", 512);

}

void draw(){
 

}

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
      println(myString);

    }
    // when you've parsed the data you have, ask for more:
    myPort.write("A");
  }
}

void keyPressed() {
    if ( key == 's' ) soundbed.trigger();
    if ( key == 'h' ) heartbeat1.trigger();
        if ( key == 'j' ) heartbeat2.trigger();


}
