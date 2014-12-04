class SmokeRing {
	PImage smokeImg = loadImage("data/smoke2.png");
	PImage particleImg1 = loadImage("data/particleImage1.png");
	PImage particleImg2 = loadImage("data/particleImage2.png");
	PImage particleImg3 = loadImage("data/particleImage3.png");
	PImage ring = loadImage("data/ring.png");
	//ArrayList<PImage> ring; 

	PVector origin; 
	float lifespan; 
	float lifespanFull; 
	boolean isDead; 

	SmokeRing(float posX, float posY) {
		origin = new PVector( posX, posY); 
		lifespan = 600;  
		lifespanFull = 600; 
		isDead = false; 
	}

	void update(){
		lifespan -= 10; //Reduce lifespan by one everytime it runs. 
		float radius = map(lifespan, 0, lifespanFull, width*0.5, 100);
		drawRing(radius);
		checkAlive();
	}

	void drawRing(float radius) {
		pushMatrix(); 
		//Move to origin
		translate(origin.x, origin.y);

		float calculateTint = abs( cos( radians( constrain ( map( lifespan, 200, lifespanFull, 90, 270 ), 90, 270 ) ) ) ) * 255;
		pushStyle();
		tint(255, calculateTint);
		imageMode(CENTER);
		image(ring, 0, 0, radius*2.8, radius*2.8);
		popStyle();

		for (float i = 0; i < 360; i ++){
			if (i % 5 == 0){
				pushMatrix(); 
				rotate(radians(i));
				float offset = noise(millis())* 5.0;
				translate( radius + (sin(i) + offset ) * width/400, 0);
				imageMode(CENTER);
				pushStyle();

				tint(255, calculateTint);
				float imageSize = map(lifespan, 0, lifespanFull, 100, 40);
				image(smokeImg, 0, 0, imageSize, imageSize);

				//DRAW RANDOM SHAPE
/*				float imageSize = map(lifespan, 0, lifespanFull, 20, 8);
				float rando = floor(random(4));
				println("Rando = " + rando);
				if ( rando <= 1){
					rotate(random(1) * 2 * PI);
					image(particleImg1, 0, 0, imageSize, imageSize);
				}
				else if ( rando == 2){
					rotate(random(1) * 2 * PI);
					image(particleImg2, 0, 0, imageSize, imageSize);
				}
				else {
					rotate(random(1) * 2 * PI);
					image(particleImg3, 0, 0, imageSize, imageSize);
				}
*/

				//DRAW CIRCLES, CHANGE HUE
/*				pushStyle();
				noStroke();
				float imageSize = map(lifespan, 0, lifespanFull, 20, 8);
				colorMode(HSB, 360, 255, 255, 255);
				float calculateHue = lifespan/lifespanFull * 270; 
				fill( calculateHue, 200, 200, calculateTint);
				ellipseMode(CENTER);
				ellipse(0,0, imageSize, imageSize);
				popStyle();
*/

				//FOR DEBUGGING, DRAW CIRCLES
/*				ellipseMode(CENTER);
				ellipse(0,0, imageSize, imageSize);*/
				popStyle();
				popMatrix();
			}
		}
		popMatrix();
	}

	void checkAlive(){
		if (lifespan <= 0){
			isDead = true;
		}
	}

}