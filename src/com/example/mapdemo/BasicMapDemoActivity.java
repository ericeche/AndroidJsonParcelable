package com.example.mapdemo;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
/**
 * @author Eric Echeverri
 * upload JSON Places using Asynch tasks in Main UI
 */

public class BasicMapDemoActivity extends FragmentActivity {
    private static final String LOG_TAG = "ExampleApp";
    Place JsonObjectClass;
    List<Place> ObjectsList = new ArrayList<Place>();

    
    private static final String SERVICE_URL = "http://www.helloworld.com/helloworld_locations.json";
 
    protected GoogleMap map;
 
	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	ConnectionDetector cd;
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Google Places
	GooglePlaces googlePlaces;

	// Places List
	PlacesList nearPlaces =  new PlacesList();
	

	// GPS Location
	GPSTracker gps;

	// Button
	Button btnShowOnMap;

	// Progress dialog
	ProgressDialog pDialog;
	
	// Places Listview
	ListView lv;
	
	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
	// create map to store
	
	
	
	GoogleType mType = new GoogleType();
	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_json);
      //  setUpMapIfNeeded();
        
    	cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		
		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(BasicMapDemoActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

    // creating GPS Class object
    gps = new GPSTracker(this);

    // check if GPS location can get
    if (gps.canGetLocation()) {
      Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
    } else {
      // Can't get user's current location
      alert.showAlertDialog(BasicMapDemoActivity.this, "GPS Status",
          "Couldn't get location information. Please enable GPS",
          false);
      // stop executing code by return
      return;
    }

    // Getting listview
    lv = (ListView) findViewById(R.id.list);

    // button show on map
    btnShowOnMap = (Button) findViewById(R.id.btn_show_map);

        // Set Data that will come from the JSON or the XMLPullParser
        // Define this better boundaries should be a length property
        //TODO Collections
        new LoadPlaces().execute(SERVICE_URL);
        
		/** Button click event to show ListView maps */
		btnShowOnMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),
						LiteListDemoActivity.class);

				// Sending user current geo location
				i.putExtra("user_latitude", Double.toString(gps.getLatitude()));
				i.putExtra("user_longitude", Double.toString(gps.getLongitude()));
				
				// passing near places to map activity
				i.putExtra("near_places", nearPlaces);
				// staring activity
				startActivity(i);
			}
		});
        

    }
 
    @Override
    protected void onResume() {
        super.onResume();
      //  setUpMapIfNeeded();
    }
 
    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }
 
    private void setUpMap() {
        // Retrieve the city data from the web service
        // In a worker thread since it's a network operation.
    }

    
    
    /**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {
		final StringBuilder json = new StringBuilder();
		
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(BasicMapDemoActivity.this);
			pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			// creating Places class object
			googlePlaces = new GooglePlaces();
			
	        HttpURLConnection conn = null;
	        final StringBuilder json = new StringBuilder();
	       
	            // Connect to the web service
	            URL url;
			
	            try {
	            	url = new URL(SERVICE_URL);
					conn = (HttpURLConnection) url.openConnection();
					InputStreamReader in = new InputStreamReader(conn.getInputStream());
		            // Read the JSON data into the StringBuilder
		            int read;
		            char[] buff = new char[1024];
		            while ((read = in.read(buff)) != -1) {
		                json.append(buff, 0, read);
		            }
		
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            String sb = json.toString();
	            if (json != null) {
	                try {
	                	   
	            		   JSONObject jsonObj = new JSONObject(sb);
	            		   // De-serialize the JSON string into an array of city objects
	            	        JSONArray locations = jsonObj.getJSONArray("locations");
	            	     // Getting JSON Array node
	                        
	                      
	            	        for (int i = 0; i < locations.length(); i++) {
	            	        	
	            	            // Create a marker for each city in the JSON data.
	            	            JSONObject jsonObjLocation = locations.getJSONObject(i);
	            	            
	            	            // RETRIEVE EACH JSON OBJECT'S FIELDS
	            	  
	            	            String name = jsonObjLocation.getString("name");
	            	            String address = jsonObjLocation.getString("address");
	            	            String address2 = jsonObjLocation.getString("address2");
	            	            String city = jsonObjLocation.getString("city");
	            	            String state = jsonObjLocation.getString("state");
	            	            String zip = jsonObjLocation.getString("zip_postal_code");
	            	            String phone = jsonObjLocation.getString("phone");
	            	            String fax = jsonObjLocation.getString("fax");
	            	            String clat = jsonObjLocation.getString("latitude");
	            	            String clon = jsonObjLocation.getString("longitude");
	            	            String office_image_url = jsonObjLocation.getString("office_image");
	            	     
	            	             
	            	            // JSON  TO HellowWorld OBJECT
	            	            JsonObjectClass = new Place(name, address, address2, city,state,zip,phone,fax,clat,clon,office_image_url);
	            	            ObjectsList.add(JsonObjectClass );
	                    }
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
	            } else {
	                Log.e("ServiceHandler", "Couldn't get any data from the url");
	            }

	           
	            
	            
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * and show the data in UI
		 * Always use runOnUiThread(new Runnable()) to update UI from background
		 * thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					
					
					String status;
					
					nearPlaces.results = ObjectsList ;
					nearPlaces.status ="OK";			
				
					status = nearPlaces.status;
					
					
					// Check for all possible status
					if(status.equals("OK")){
						// Successfully got places details
						if (nearPlaces.results != null) {
							// loop through each place
							for (Place p : nearPlaces.results) {
								HashMap<String, String> map = new HashMap<String, String>();
								
								// Place reference won't display in listview - it will be hidden
								// Place reference is used to get "place full details"
								map.put(KEY_REFERENCE, p.reference);
								
								// Place name
								map.put(KEY_NAME, p.name);
								
								
								// adding HashMap to ArrayList
								placesListItems.add(map);
							}
							// list adapter
							ListAdapter adapter = new SimpleAdapter(BasicMapDemoActivity.this, placesListItems,
					                R.layout.list_item,
					                new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
					                        R.id.reference, R.id.name });
							
							// Adding data into listview
							lv.setAdapter(adapter);
						}
					}
					else if(status.equals("ZERO_RESULTS")){
						// Zero results found
						alert.showAlertDialog(BasicMapDemoActivity.this, "Near Places" ,
								"Sorry no places found. Try to change the types of places",
								false);
					}
					else if(status.equals("UNKNOWN_ERROR"))
					{
						alert.showAlertDialog(BasicMapDemoActivity.this, "Places Error",
								"Sorry unknown error occured.",
								false);
					}
					else if(status.equals("OVER_QUERY_LIMIT"))
					{
						alert.showAlertDialog(BasicMapDemoActivity.this, "Places Error",
								"Sorry query limit to google places is reached",
								false);
					}
					else if(status.equals("REQUEST_DENIED"))
					{
						alert.showAlertDialog(BasicMapDemoActivity.this, "Places Error",
								"Sorry error occured. Request is denied",
								false);
					}
					else if(status.equals("INVALID_REQUEST"))
					{
						alert.showAlertDialog(BasicMapDemoActivity.this, "Places Error",
								"Sorry error occured. Invalid Request",
								false);
					}
					else
					{
						alert.showAlertDialog(BasicMapDemoActivity.this, "Places Error",
								"Sorry error occured.",
								false);
					}
				}
			});

		}

	
		
		
	}
    
}