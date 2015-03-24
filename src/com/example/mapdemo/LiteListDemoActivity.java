
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import com.example.mapdemo.MainActivity1.LoadPlaces;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This shows to include a map in lite mode in a ListView.
 * Note the use of the view holder pattern with the
 * {@link com.google.android.gms.maps.OnMapReadyCallback}.
 */
public class LiteListDemoActivity extends FragmentActivity  implements OnMapReadyCallback {

    private ListFragment mList;
    private MapAdapter mAdapter;
    private SupportMapFragment mapFragment;
    private List<MyClass> ObjectsList = new ArrayList<MyClass>();
    /**
     * A Polygon with five points in the Norther Territory, Australia.
     */
    //TODO XMLPullParser Loaded from JSON Object
    
    private static final com.google.android.gms.maps.model.LatLng LatLng[]  = {
    	new LatLng(42.9612, -85.6557), new LatLng(40.750153,-73.985424),
    	new LatLng(41.889765, -87.629335), new LatLng(36.140502,-86.794089),
    	new LatLng(47.614281, -122.196595),new LatLng(34.055700, -118.260874),
    	new LatLng(33.406841, -111.894092)}; 
    
    List<LatLng[]> Coordinates = new ArrayList<LatLng[]>();
    
    /**
     * A list of locations to show in this ListView.
     */
    
    //TODO XMLPullParser Loaded from JSON Object
    
    private static final NamedLocation[] LIST_LOCATIONS = new NamedLocation[] {
            new NamedLocation("Grand Rapids Modustri(HQ) Office", new LatLng(42.9612, -85.6557)),
            new NamedLocation("New York Office", new LatLng(40.750153, -73.985424)),
            new NamedLocation("Chicago Office", new LatLng(41.889765, -87.629335)),
            new NamedLocation("Nashville Office", new LatLng(36.140502,-86.794089)),
            new NamedLocation("Seattle Office", new LatLng(47.614281, -122.196595)),
            new NamedLocation("Los Angeles Office", new LatLng(34.055700, -118.260874)),
            new NamedLocation("Phoenix Office", new LatLng(33.406841, -111.894092))
    };
    
    private static final String SERVICE_URL = "http://www.helloworld.com/helloworld_locations.json";
    private static final String LOG_TAG = "ExampleApp";
    private Place JsonObjectClass;
    Map<String, Place> placelistmap = new HashMap<String, Place>();
    

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lite_list_demo);
        
	
        
        for (int i = 0; i < 7; i++){
        		Coordinates.add(new LatLng[i]);
        	}
       
                    
        // Set a custom list adapter for a list of locations
        mAdapter = new MapAdapter(this, LIST_LOCATIONS);
        mList = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list);
        mList.setListAdapter(mAdapter);
        mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);        
        // Set a RecyclerListener to clean up MapView from ListView
        AbsListView lv = mList.getListView();
        lv.setRecyclerListener(mRecycleListener);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // Notify all MapViews of low memory
        for (MapView m : mAdapter.getMaps()) {
            m.onLowMemory();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause all instantiated MapViews
        for (MapView m : mAdapter.getMaps()) {
            m.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume all instantiated MapViews
        for (MapView m : mAdapter.getMaps()) {
            m.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        // Destroy all instantiated MapViews and remove from list
        for (MapView m : mAdapter.getMaps()) {
            m.onResume();
        }
        mAdapter.getMaps().clear();

        super.onDestroy();
    }

    /**
     * Adapter that displays a title and {@link com.google.android.gms.maps.MapView} for each item.
     * The layout is defined in <code>lite_list_demo_row.xml</code>. It contains a MapView
     * that is programatically initialised in
     * {@link #getView(int, android.view.View, android.view.ViewGroup)}
     */
    private class MapAdapter extends ArrayAdapter<NamedLocation> {

        private final HashSet<MapView> mMaps = new HashSet<MapView>();

        public MapAdapter(Context context, NamedLocation[] locations) {
            super(context, R.layout.lite_list_demo_row, R.id.lite_listrow_text, locations);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;

            // Check if a view can be reused, otherwise inflate a layout and set up the view holder
            if (row == null) {
                // Inflate view from layout file
                row = getLayoutInflater().inflate(R.layout.lite_list_demo_row, null);

                // Set up holder and assign it to the View
                holder = new ViewHolder();
                holder.mapView = (MapView) row.findViewById(R.id.lite_listrow_map);
                holder.title = (TextView) row.findViewById(R.id.lite_listrow_text);
                // Set holder as tag for row for more efficient access.
                row.setTag(holder);

                // Initialise the MapView
                holder.initializeMapView();

                // Keep track of MapView
                mMaps.add(holder.mapView);
            } else {
                // View has already been initialised, get its holder
                holder = (ViewHolder) row.getTag();
            }

            // Get the NamedLocation for this item and attach it to the MapView
            NamedLocation item = getItem(position);
            holder.mapView.setTag(item);

            // Ensure the map has been initialised by the on map ready callback in ViewHolder.
            // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
            // when the callback is received.
            if (holder.map != null) {
                // The map is already ready to be used
                setMapLocation(holder.map, item);
            }

            // Set the text label for this item
            holder.title.setText(item.name);

            return row;
        }

        /**
         * Retuns the set of all initialised {@link MapView} objects.
         *
         * @return All MapViews that have been initialised programmatically by this adapter
         */
        public HashSet<MapView> getMaps() {
            return mMaps;
        }
    }

    /**
     * Displays a {@link LiteListDemoActivity.NamedLocation} on a
     * {@link com.google.android.gms.maps.GoogleMap}.
     * Adds a marker and centers the camera on the NamedLocation with the normal map type.
     *
     * @param map
     * @param data
     */
    private static void setMapLocation(GoogleMap map, NamedLocation data) {
    	
        // Add a marker for this item and set the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.location, 13f));
        map.addMarker(new MarkerOptions().position(data.location));

        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    /**
     * Holder for Views used in the {@link LiteListDemoActivity.MapAdapter}.
     * Once the  the <code>map</code> field is set, otherwise it is null.
     * When the {@link #onMapReady(com.google.android.gms.maps.GoogleMap)} callback is received and
     * the {@link com.google.android.gms.maps.GoogleMap} is ready, it stored in the {@link #map}
     * field. The map is then initialised with the NamedLocation that is stored as the tag of the
     * MapView. This ensures that the map is initialised with the latest data that it should
     * display.
     */
    class ViewHolder implements OnMapReadyCallback {
        MapView mapView;
        TextView title;
        GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getApplicationContext());
            map = googleMap;
            NamedLocation data = (NamedLocation) mapView.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                mapView.onResume();
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

    }

    /**
     * RecycleListener that completely clears the {@link com.google.android.gms.maps.GoogleMap}
     * attached to a row in the ListView.
     * Sets the map type to {@link com.google.android.gms.maps.GoogleMap#MAP_TYPE_NONE} and clears
     * the map.
     */
    private AbsListView.RecyclerListener mRecycleListener = new AbsListView.RecyclerListener() {

        @Override
        public void onMovedToScrapHeap(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder != null && holder.map != null) {
                // Clear the map and free up resources by changing the map type to none
                holder.map.clear();
                holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
            }

        }
    };

    /**
     * Location represented by a position ({@link com.google.android.gms.maps.model.LatLng} and a
     * name ({@link java.lang.String}).
     */
    private static class NamedLocation {
        public final String name;
        public final LatLng location;

        NamedLocation(String name, LatLng location) {
            this.name = name;
            this.location = location;
        }
    }



    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
    	// TODO his should come from the XMLPullParser
    	for (int i = 0; i < 7; i++){
    			map.addMarker(new MarkerOptions().position(LatLng[i]).title(LIST_LOCATIONS[i].name));
    }
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
	            placelistmap.put(Integer.toString(i), JsonObjectClass);
	            
	        
	        }
	    }    
    
 
		
	    

}
