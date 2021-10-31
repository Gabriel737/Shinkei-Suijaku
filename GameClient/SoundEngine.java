import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * A singleton class to play sound effects
 * Assignment 10 - CPSC 1181 Section 1
 * @author Gabriel Henderson
 * @version March 30
 */
public class SoundEngine {
	
	public static final String SOUND_PATH = "sounds/";
	public static final String SOUND_TYPE = ".wav";
	
	private static SoundEngine soundEngineInstance = null;
	
	private Clip buzz;
	private Clip ding;
	private Clip clap;
	private Clip boo;
	
	/**
	 * Instantiates a new SoundEngine instance
	 */
	private SoundEngine() {
		try {
			buzz = AudioSystem.getClip();
			ding = AudioSystem.getClip();
			clap = AudioSystem.getClip();
			boo = AudioSystem.getClip();
			buzz.open(AudioSystem.getAudioInputStream(new File(SOUND_PATH + "buzz" + SOUND_TYPE)));
			ding.open(AudioSystem.getAudioInputStream(new File(SOUND_PATH + "ding" + SOUND_TYPE)));
			clap.open(AudioSystem.getAudioInputStream(new File(SOUND_PATH + "clap" + SOUND_TYPE)));
			boo.open(AudioSystem.getAudioInputStream(new File(SOUND_PATH + "boo" + SOUND_TYPE)));
		} catch(Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	/**
	 * Returns the current instance and creates one if required.
	 * There should never be more then one instance in use.
	 * @return The instance
	 */
	public static synchronized SoundEngine getInstance() {
		if(soundEngineInstance == null) {
			soundEngineInstance = new SoundEngine();
		}
		return soundEngineInstance;
	}
	
	/**
	 * Plays a "ding" sound effect
	 */
	public void playDing() {
		ding.setFramePosition(0);
		ding.start();
	}
	
	/**
	 * Plays a "buzz" sound effect
	 */
	public void playBuzz() {
		buzz.setFramePosition(0);
		buzz.start();
	}

	/**
	 * Plays a "clap" sound effect
	 */
	public void playClap() {
		clap.setFramePosition(0);
		clap.start();
	}
	
	/**
	 * Plays a "boo" sound effect
	 */
	public void playBoo() {
		boo.setFramePosition(0);
		boo.start();
	}
}
