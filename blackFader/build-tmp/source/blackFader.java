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

public class blackFader extends PApplet {

float blackFader = 255;
boolean visualsLive = false;

public void setup() {
	blackFader = 255; 
}

public void draw() {
	//FADE IN OR OUT OF BLACK
	background(255);
	pushStyle(); 
	fill(0, blackFader);
	rect(0, 0, width, height);
	popStyle();
	if (visualsLive == false){
		if (blackFader < 255){
			blackFader++;
		}
	}

	if (visualsLive == true){
		if (blackFader > 0){
			blackFader--;
		}
	}
}

public void keyPressed(){
	if (keyCode == 73){
		visualsLive = true;
		println("Lets get it started.");

	}
	if (keyCode == 79){
		visualsLive = false; 
		println("Lets get faded.");
	}
}

//Hit key 'i' = keyCode 73
//Hit key 'o' = keyCode 79
class BlackFader {
	float blackFader; 
	boolean visualsLive; 


	BlackFader () {
		blackFader = 255; 
		visualsLive = false; 
	}

	public void overlayFader(){
		//CHECK IF KEYS HAVE BEEN HIT, UPDATE STATUS IF NEEDED
		if (keyPressed == true){
			//FADE IN
			if (keyCode == 73){ //Hit key 'i' = keyCode 73 
				visualsLive = true;
				println("Lets get it started.");
			}

			//FADE OUT
			if (keyCode == 79){
				visualsLive = false; 
				println("Lets get faded.");
			}
		}
		//UPDATE FADER COUNT UP OR DOWN IF NEEDED
		if (visualsLive == false){
			if (blackFader < 255){
				blackFader++;
			}
		}

		if (visualsLive == true){
			if (blackFader > 0){
				blackFader--;
			}
		}

		//DRAW RECT BASED ON STATUS
		pushStyle(); 
		fill(0, blackFader);
		rect(0, 0, width, height);
		popStyle();

	}
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "blackFader" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
