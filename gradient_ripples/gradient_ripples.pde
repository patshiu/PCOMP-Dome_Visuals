//Try to get "smoke ring effect"

ArrayList<SmokeRing> ringsArray; 
GradientBackground colorSmoosh;
SmokeRing soloRing; 


void setup() {
	size(displayWidth, displayHeight, P2D);
	ringsArray = new ArrayList<SmokeRing>();
	colorSmoosh = new GradientBackground();
	//soloRing = new SmokeRing(width/2, height/2);
}

void draw() {
	colorSmoosh.display(); 
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