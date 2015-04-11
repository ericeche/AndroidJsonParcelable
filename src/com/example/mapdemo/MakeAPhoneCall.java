package com.example.mapdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
 
public class MakeAPhoneCall extends Activity {
 
	private Button button;
    private String phone_search_option = "";
	public void onCreate(Bundle savedInstanceState) {
 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
 
		button = (Button) findViewById(R.id.buttonCall);
		// Getting intent data
	    Intent i = getIntent();
		// Dial phone
	    phone_search_option = i.getStringExtra("user_selection");
		// add button listener
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
 
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse(phone_search_option));
				startActivity(callIntent);
 
			}
 
		});
 
	}
 
}