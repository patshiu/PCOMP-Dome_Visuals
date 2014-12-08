class BlackFader {
	float blackFader; 
	boolean visualsLive; 


	BlackFader() {
		blackFader = 255; 
		visualsLive = false; 
	}

	void overlayFader(){
		//println("FADER IS RUNNING"); //DEBUGs
		//UPDATE FADER COUNT UP OR DOWN IF NEEDED
		if (visualsLive == false){
			if (blackFader < 255){
				blackFader += 5;
			}
		}

		if (visualsLive == true){
			if (blackFader > 0){
				blackFader -= 5;
			}
		}

		//DRAW RECT BASED ON STATUS
		pushStyle(); 
		fill(0, blackFader);
		rect(0, 0, width, height);
		popStyle();
	}

	void listen(int btnHit) {
	//CHECK IF KEYS HAVE BEEN HIT, UPDATE STATUS IF NEEDED
		//FADE IN
		if (btnHit == 73){ //Hit key 'i' = keyCode 73 
			visualsLive = true;
			println("Lets get it started.");
		}

		//FADE OUT
		if (btnHit == 79){
			visualsLive = false; 
			println("Lets get faded.");
		}
	}
}

