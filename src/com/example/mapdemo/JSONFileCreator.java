package com.example.mapdemo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;
import android.content.Context;
import android.os.Environment;
 
public class JSONFileCreator  {
	

    Context context;
    public JSONFileCreator(Context context){
        this.context = context;
    }
	
	public static  String create(StringBuilder json) throws IOException{
	   		
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
	
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;

		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
			
		}
		
		//Storage NOK ? Then display a message and exit :-)
		if(!mExternalStorageAvailable || !mExternalStorageWriteable){
			return "error";
		}
		else
		{	
			// get the path to sdcard
			File sdcard = Environment.getExternalStorageDirectory();
			// to this path add a new directory path
			File dir = new File(sdcard.getAbsolutePath() + "/maps/");
			// create this directory if not already created
			dir.mkdir();
			// create the file in which we will write the contents
			File file = new File(dir, "locations.txt");
			FileOutputStream  outStream = new FileOutputStream(file);
			String data = json.toString();
			outStream.write(data.getBytes());
			outStream.close();
		}
				
			return read();
		}
	
	
	public static String read() throws IOException{
		String jString = null;
		try{
			File dir = Environment.getExternalStorageDirectory();
	        File yourFile = new File(dir, "/maps/locations.txt");
	        FileInputStream stream = new FileInputStream(yourFile);
  
	        try {
	        	FileChannel fc = stream.getChannel();
	        	MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
	        	/* Instead of using default, pass in a decoder. */
	        	jString = Charset.defaultCharset().decode(bb).toString();
	        }
	        finally {
	        	stream.close();
	        	
	        }
		} 
		catch (Exception e) 
		{e.printStackTrace();
		}
		return jString;
	}
	
	
}
