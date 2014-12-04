class GradientBackground {
	PImage canvas; 
	float timer; 

	GradientBackground(){
		timer = 140; 
		canvas = createImage(width, height, RGB);
	}

	void draw() {

		if (timer <= 0){
			timer = 180; 
		}

		timer--; 
		println(timer);

		canvas.loadPixels(); 
		for( int i = 0; i < canvas.pixels.length; i++){
			float currentX, currentY; 
			if ( i <= canvas.width ){
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
			color currentColor = color( currentRed, currentBlue, currentGreen ); 
			canvas.pixels[i] = currentColor; 
		}
		canvas.updatePixels();
		image(canvas, 0, 0, width, height);
		fill(255, 100);
		rect(0,0,width,height);
	}



}


