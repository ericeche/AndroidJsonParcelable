package com.example.mapdemo;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Global {
    public static Integer playerscore = 0;
    public static String playername = "Zilla3D";
    public static Integer winscore = 50;
    public static int[] intArray = new int[] {0,0,0};
    public static boolean[] settings = new boolean[4];
    public static MediaPlayer mp;
    
    public static Integer playerlevel = 1;
    public static Integer playerlives = 3;
    
    
    private static int menuMusic;
    
    
    public static void startMusic(Context c) {
    	
    	// This player was created in the DisplayAnimation.java
    	
	//	if(Global.settings[1]){		
			/*		
			try {
				mp.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
	
		//	mp.start();
	//	}		
	}
    
    public static void stopMusic() {
    	if(mp.isPlaying()){
    		mp.stop();
    	}
	}
    
    
    
}
