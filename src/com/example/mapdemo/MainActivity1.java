package com.example.mapdemo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity1 extends Activity {

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	ConnectionDetector cd;
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Google Places
	GooglePlaces googlePlaces;

	// Places List
	PlacesList nearPlaces;

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
	PlacesList placelist = new PlacesList();
	
	GoogleType mType = new GoogleType();
	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name

	// User search from activity GoogleSearchType 
	String user_search_option = "";
	String radius="";
	
    private static final String SERVICE_URL = "http://www.helloworld.com/helloworld_locations.json";
    private static final String LOG_TAG = "ExampleApp";
    private Place JsonObjectClass;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_json);

		
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity1.this, "Internet Connection Error",
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
			alert.showAlertDialog(MainActivity1.this, "GPS Status",
					"Couldn't get location information. Please enable GPS",
					false);
			// stop executing code by return
			return;
		}

		// Getting listview
		lv = (ListView) findViewById(R.id.list);
		
		// button show on map
		btnShowOnMap = (Button) findViewById(R.id.btn_show_map);

		// Getting intent data
		Intent i = getIntent();

		// Users current geo location
		user_search_option = i.getStringExtra("user_selection");
		radius = i.getStringExtra("radius");		
		mType = i.getParcelableExtra(GoogleSearchType.PAR_KEY);
		Bundle b = getIntent().getExtras(); 
		GoogleType obj = b.getParcelable("GoogleType");
	    Log.i("Your Selection",  obj.getStrValue());
		// calling background Async task to load Google Places
		// After getting places from Google all the data is shown in listview
		
	    try {
			retrieveAndAddCities();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		new LoadPlaces().execute(user_search_option,radius,obj.getStrValue());

		/** Button click event for shown on map */
		btnShowOnMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),
						PlacesMapActivity.class);
				// Sending user current geo location
				i.putExtra("user_latitude", Double.toString(gps.getLatitude()));
				i.putExtra("user_longitude", Double.toString(gps.getLongitude()));
				
				// passing near places to map activity
				i.putExtra("near_places", nearPlaces);
				// staring activity
				startActivity(i);
			}
		});
		
		
		/**
		 * ListItem click event
		 * On selecting a listitem SinglePlaceActivity is launched
		 * */
		lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	// getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();
                
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SinglePlaceActivity.class);
                
                // Sending place refrence id to single place activity
                // place refrence id used to get "Place full details"
                in.putExtra(KEY_REFERENCE, reference);
                startActivity(in);
            }
        });
	}

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity1.this);
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
			
			try {
				// Separeate your place types by PIPE symbol "|"
				// If you want all types places make it as null
				// Check list of types supported by google
				// 
				// Replace by the user Option from GoogleSearchType activity
				
		//	String types = "library"; // Listing places only cafes, restaurants
			   String types = "cafe|";
				// Radius in meters - increase this value if you don't find any places
				double radius_in_miles = 5000; // 1000 meters
				
			   if (!args[0].equals(""))
			   	types = args[0];
			   Log.i("Selection Main Activity", "" + args[0]);
			   Log.i("Distance Main Activity", "" + args[1]);
			   Log.i("String from Object", "" + args[2]);
			   if ( !args[1].equals("") ) 
				   try{
					   	radius_in_miles = Double.parseDouble(args[1])*1.6*1000;
				   }   	
				   catch(NumberFormatException ex){
					   radius_in_miles = 5000;
				   }
				   
				
				// get nearest places
				nearPlaces = googlePlaces.search(gps.getLatitude(),
						gps.getLongitude(), radius_in_miles, types);
			
			} catch (Exception e) {
				e.printStackTrace();
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
					// Get json response status
					String status = nearPlaces.status;
					
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
							ListAdapter adapter = new SimpleAdapter(MainActivity1.this, placesListItems,
					                R.layout.list_item,
					                new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
					                        R.id.reference, R.id.name });
							
							// Adding data into listview
							lv.setAdapter(adapter);
						}
					}
					else if(status.equals("ZERO_RESULTS")){
						// Zero results found
						alert.showAlertDialog(MainActivity1.this, "Near Places" ,
								"Sorry no places found. Try to change the types of places",
								false);
					}
					else if(status.equals("UNKNOWN_ERROR"))
					{
						alert.showAlertDialog(MainActivity1.this, "Places Error",
								"Sorry unknown error occured.",
								false);
					}
					else if(status.equals("OVER_QUERY_LIMIT"))
					{
						alert.showAlertDialog(MainActivity1.this, "Places Error",
								"Sorry query limit to google places is reached",
								false);
					}
					else if(status.equals("REQUEST_DENIED"))
					{
						alert.showAlertDialog(MainActivity1.this, "Places Error",
								"Sorry error occured. Request is denied",
								false);
					}
					else if(status.equals("INVALID_REQUEST"))
					{
						alert.showAlertDialog(MainActivity1.this, "Places Error",
								"Sorry error occured. Invalid Request",
								false);
					}
					else
					{
						alert.showAlertDialog(MainActivity1.this, "Places Error",
								"Sorry error occured.",
								false);
					}
				}
			});

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	   protected void retrieveAndAddCities() throws IOException {
	        HttpURLConnection conn = null;
	        final StringBuilder json = new StringBuilder();
	        try {
	            // Connect to the web service
	            URL url = new URL(SERVICE_URL);
	            conn = (HttpURLConnection) url.openConnection();
	            InputStreamReader in = new InputStreamReader(conn.getInputStream());
	 
	            // Read the JSON data into the StringBuilder
	            int read;
	            char[] buff = new char[1024];
	            while ((read = in.read(buff)) != -1) {
	                json.append(buff, 0, read);
	            }
	        } catch (IOException e) {
	            Log.e(LOG_TAG, "Error connecting to service", e);
	            throw new IOException("Error connecting to service", e);
	        } finally {
	            if (conn != null) {
	                conn.disconnect();
	            }
	        }
	 
	        // Create markers for the city data.
	        // Must run this on the UI thread since it's a UI operation.
	        runOnUiThread(new Runnable() {
	            public void run() {
	                try {
	                    createMarkersFromJson(json.toString());
	                } catch (JSONException e) {
	                    Log.e(LOG_TAG, "Error processing JSON", e);
	                } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        });
	    }
	   
	   
	    void createMarkersFromJson(String json) throws JSONException, IOException {
	        // De-serialize the JSON string into an array of city objects
	        JSONArray jsonArray = new JSONArray(json);
	        for (int i = 0; i < jsonArray.length(); i++) {
	        	
	            // Create a marker for each city in the JSON data.
	            JSONObject jsonObj = jsonArray.getJSONObject(i);
	            
	            // RETRIEVE EACH JSON OBJECT'S FIELDS
	  
	            String name = jsonObj.getString("name");
	            String address = jsonObj.getString("address");
	            String address2 = jsonObj.getString("address2");
	            String city = jsonObj.getString("city");
	            String state = jsonObj.getString("state");
	            String country = jsonObj.getString("country");
	            String zip = jsonObj.getString("zip_postal_code");
	            String phone = jsonObj.getString("phone");
	            String fax = jsonObj.getString("fax");
	            String clat = jsonObj.getString("latitude");
	            String clon = jsonObj.getString("longitude");
	            String office_image_url = jsonObj.getString("office_image_url");
	     
	             
	            // new instance of Place
	            JsonObjectClass = new Place(name, address, address2, city,state,zip,phone,fax,clat,clon,office_image_url);
	            // add to a Place map object
	            
	            
	        
	        }
	    }
	   
	   
}	   

