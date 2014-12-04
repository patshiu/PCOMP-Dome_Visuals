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

public class color_background extends PApplet {

PImage colorSmoosh;

float timer; 

public void setup() {
	size(displayWidth, displayHeight);
	colorSmoosh = createImage(width, height, RGB);
	timer = 140; 
}

public void draw() {

	if (timer <= 0){
		timer = 180; 
	}

	timer--; 
	println(timer);

	colorSmoosh.loadPixels(); 
	for( int i = 0; i < colorSmoosh.pixels.length; i++){
		float currentX, currentY; 
		if ( i <= colorSmoosh.width ){
			currentX = i; 
			currentY = 0; 
		}
		else {
			currentX = i % width; 
			currentY = floor( i / width);
		}
		float currentRed, currentGreen, currentBlue; 
		float currentLongtitude = map(currentX, 0, width, 0, 255);
		float currentLattitude = map(currentY, 0, height, 255, 0);
		currentRed = currentLongtitude;

		currentBlue = abs( sin(radians(timer)) ) * 255; //blue is animated

		currentGreen = currentLattitude;
		int currentColor = color( currentRed, currentBlue, currentGreen ); 
		colorSmoosh.pixels[i] = currentColor; 
	}
	colorSmoosh.updatePixels();
	image(colorSmoosh, 0, 0, width, height);
	fill(255, 100);
	rect(0,0,width,height);
}


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "color_background" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
