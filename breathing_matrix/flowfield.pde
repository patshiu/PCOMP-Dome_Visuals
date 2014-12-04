class FlowField {
	float [] [] field; 
	int cols, rows; 
	int resolution; 

	float xoff; 
	float yoff;

	PImage currentImage; 

	FlowField(){

		//Set up images
		rippleImg1 = loadImage("data/01_RippleImg.png");
		rippleImg2 = loadImage("data/02_RippleImg.png");
		rippleImg3 = loadImage("data/03_RippleImg.png");
		rippleImg4 = loadImage("data/04_RippleImg.png");
		rippleImg5 = loadImage("data/05_RippleImg.png");
		rippleImg6 = loadImage("data/06_RippleImg.png");
		rippleImgBackground = color(#deecfd);
		currentImage = rippleImg1; 

		resolution = 80;
		cols = width/resolution; 
		rows = height/resolution; 
		field = new float [cols] [rows];

		xoff = millis();
		yoff = millis()*2;

		init();
	}

	float lookup( float x, float y){
		int column = int(constrain(x/resolution,0,cols-1));
		int row = int(constrain(y/resolution,0,rows-1));

		return field[column][row];
	}


	void init() {
		//Initialize force field directions
		for (int i = 0; i < cols; i++){
			for (int j = 0; j < rows; j++) {
				float theta = map(noise(xoff,yoff),0,1,0,TWO_PI);
				field[i][j] = abs(cos(theta)) * 255; //store noise alpha value
				//yoff += 0.1;
				yoff += random(0,0.2);
			}
			xoff += 0.1;
		}
	}

	void showColorField(){
		//Initialize force field directions
		fill(rippleImgBackground, 100);
		rect(0,0,width,height);
		for (int i = 0; i < cols; i++){
			for (int j = 0; j < rows; j++) {
				int locX = i * resolution; 
				int locY = j * resolution; 
				noStroke();

				//Draw the rectangle
				ellipseMode(CENTER);
				if ( rippleMode == 1 ){
					currentImage = rippleImg1; 
				}
				if ( rippleMode == 2 ){
					currentImage = rippleImg2; 
				}
				if ( rippleMode == 3 ){
					currentImage = rippleImg3; 
				}
				if ( rippleMode == 4 ){
					currentImage = rippleImg4; 
				}
				if ( rippleMode == 5 ){
					currentImage = rippleImg5; 
				}
				if ( rippleMode == 6 ){
					currentImage = rippleImg6; 
				}
				if( pulseVal <= 40){

					imageMode(CENTER);
					tint(255, 40);
					image( currentImage, locX + resolution/2 , locY + resolution/2, pulseVal*2, pulseVal*2);
					tint(255, 55);
					image( currentImage, locX + resolution/2 , locY + resolution/2, pulseVal, pulseVal);
					// fill(currentColor, 10);
					// ellipse( locX + resolution/2, locY + resolution/2, pulseVal*2, pulseVal*2);
				}
				//println( "i, j : " + i + " , " + j + "\t" + "locX, locY : " + locX + " , " + locY );
			}
		}
	}


}