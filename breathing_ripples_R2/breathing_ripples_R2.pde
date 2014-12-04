//Try to get "smoke ring effect"

ArrayList<SmokeRing> ringsArray; 
SmokeRing soloRing; 


void setup() {
	size(600, 600);
	ringsArray = new ArrayList<SmokeRing>();
	//soloRing = new SmokeRing(width/2, height/2);
}

void draw() {
	background(0);
	//soloRing.update();
	for (int i = ringsArray.size()-1; i >= 0 ; i--){
		SmokeRing currentRing = ringsArray.get(i);
		currentRing.update(); 
		//remove if dead
		if (currentRing.isDead == true){
			ringsArray.remove(i);
		}
	}
}

void mousePressed() {
	ringsArray.add(new SmokeRing(width/2, height/2)); 
	println(ringsArray.size());
}

class SmokeRing {
	PImage smokeImg = loadImage("data/smoke2.png");
	//ArrayList<PImage> ring; 
	PVector origin; 
	float lifespan; 
	float lifespanFR; 
	float lifespanFull; 
	boolean isDead; 

	SmokeRing(float posX, float posY) {
		origin = new PVector( posX, posY); 
		lifespan = 60;  
		lifespanFR = lifespan * frameRate; //lifespan in frames
		lifespanFull = lifespan * frameRate; 
		isDead = false; 
	}

	void update(){
		lifespanFR -= 10; //Reduce lifespan by one everytime it runs. 
		float radius = map(lifespanFR, 0, lifespanFull, width*0.75, 100);
		drawRing(radius);
		checkAlive();
	}

	void drawRing(float radius) {
		pushMatrix(); 
		//Move to origin
		translate(origin.x, origin.y);
		for (float i = 0; i < 360; i ++){
			pushMatrix(); 
			rotate(radians(i));
			float offset = random(5);
			translate( radius + (sin(i) + offset ) * width/400, 0);
			imageMode(CENTER);
			tint(255, map(radius, 10, width, 0, 255));
			image(smokeImg, 0, 0, radius/width*100, radius/width*100);
			popMatrix();
		}
		popMatrix();
	}

	void checkAlive(){
		if (lifespanFR <= 0){
			isDead = true;
		}
	}

}