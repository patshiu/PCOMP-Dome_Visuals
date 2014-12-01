class FlowField {
	PVector [] [] field; 
	int cols, rows; 
	int resolution; 

	float xoff; 
	float yoff;

	FlowField(){
		resolution = 10;
		cols = width/resolution; 
		rows = height/resolution; 
		field = new PVector [cols] [rows];

		xoff = millis();
		yoff = millis()*2;

		init();
	}

	PVector lookup( float x, float y){
		int column = int(constrain(x/resolution,0,cols-1));
		int row = int(constrain(y/resolution,0,rows-1));

		return field[column][row].get();
	}


	void init() {
		//Initialize force field directions
		for (int i = 0; i < cols; i++){
			for (int j = 0; j < rows; j++) {
				float theta = map(noise(xoff,yoff),0,1,0,TWO_PI);
				field[i][j] = new PVector( abs(cos(theta)) * 255 , abs(sin(theta)) * 255 , abs(sin(theta) * 255) );
				//yoff += 0.1;
				yoff += random(0,0.2);
			}
			xoff += 0.1;
		}
	}

	void showColorField(){
		//Initialize force field directions
		for (int i = 0; i < cols; i++){
			for (int j = 0; j < rows; j++) {
				int locX = i * resolution; 
				int locY = j * resolution; 
				noStroke();
				PVector currentColorVect = lookup(locX, locY);

				//Get the current color & map brightness to mouse X
				//color currentColor = color(currentColorVect.x , currentColorVect.y , currentColorVect.z );
				
				
				// float adjustBrightness = map(mouseX, 0, width, 0, 1);
				// float r = red(currentColor) * adjustBrightness;
				// float g = green(currentColor) * adjustBrightness;
				// float b = blue(currentColor) * adjustBrightness;

				// Constrain RGB to between 0-255
				float r = constrain(currentColorVect.x ,180 ,255);
				float g = constrain(currentColorVect.y ,0 ,255);
				float b = constrain(currentColorVect.z ,0 ,255);

				color currentColor = color(r, r , r );

				//currentColor = color(r, g, b, 120);

				//Draw the rectangle
				ellipseMode(CENTER);
				if( pulseNum <= 30){
					fill(currentColor, 120);
					ellipse( locX + resolution/2, locY + resolution/2, pulseNum/2, pulseNum/2);
					fill(currentColor, 20);
					ellipse( locX + resolution/2, locY + resolution/2, pulseNum, pulseNum);
					// fill(currentColor, 10);
					// ellipse( locX + resolution/2, locY + resolution/2, pulseNum*2, pulseNum*2);
				}
				//println( "i, j : " + i + " , " + j + "\t" + "locX, locY : " + locX + " , " + locY );
			}
		}
	}


}