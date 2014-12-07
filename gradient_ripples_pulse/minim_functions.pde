/*// Close the sound files


public void stop() {
  // The doorbell object must close its sound.
  close(heartbeat);

  super.stop();
}

void ring(AudioSnippet ringToPlay) {
	if (!ringToPlay.isPlaying()) {
	  // The ring() function plays the sound, as long as it is not already playing. 
	  // rewind() ensures the sound starts from the beginning.
	  ringToPlay.rewind(); 
	  ringToPlay.play();
	}
}

void close(AudioSnippet ringToClose){
	ringToClose.close();
}

void playSounds(){
		ring(heartbeat);
}*/