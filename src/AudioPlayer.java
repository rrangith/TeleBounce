/*
 *Audio Player
 *@author Rahul Rangith and Justin Liao
 *@version Jan 17th, 2017 
 *Plays music throughout program
 */

/**Imports**/
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class AudioPlayer implements Runnable {

	private String soundFileName; //file name of song
	private Clip clip = null;
	
	/***Constructor***/
	AudioPlayer(String fileName) {
		this.soundFileName = fileName; //sets file name
	}

	/** run
	 * void method which continuously plays music in thread
	 */
	public void run() {
		//loop to check if the clip is removed
		while(true) {
			//recreate the clip if it is removed
			if (clip == null) {
				try{
					File audioFile = new File("res//" + soundFileName); //makes file
					AudioInputStream audioStream = AudioSystem.getAudioInputStream(GUILauncher.class.getClassLoader().getResourceAsStream(soundFileName)); //makes input stream
					DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
					clip = (Clip) AudioSystem.getLine(info); //makes clip

					clip.open(audioStream); //opens clip
					clip.start(); //starts clip
					
					//add LineListener to check if the clip ended 
					clip.addLineListener(new LineListener() {
						  public void update(LineEvent evt) {
						    if (evt.getType() == LineEvent.Type.STOP) {
						      clip = null;	//if so, remove the clip
						    }
						  }
					});
				} 
				catch (Exception ex){ //catches exception
					ex.printStackTrace(); //Error should never be thrown as file will always be there
				}
			}	
		}
	}
}