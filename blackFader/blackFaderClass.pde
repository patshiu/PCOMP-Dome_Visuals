class BlackFader {
	float blackFader; 
	boolean visualsLive; 


	BlackFader () {
		blackFader = 255; 
		visualsLive = false; 
	}

	void overlayFader(){
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