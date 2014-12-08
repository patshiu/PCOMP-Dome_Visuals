float blackFader = 255;
boolean visualsLive = false;

void setup() {
	blackFader = 255; 
}

void draw() {
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

void keyPressed(){
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