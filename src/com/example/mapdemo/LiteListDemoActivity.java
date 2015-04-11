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
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.net.Uri;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 * This shows to include a map in lite mode in a ListView.
 * Note the use of the view holder pattern with the
 * {@link com.google.android.gms.maps.OnMapReadyCallback}.
 */
public class LiteListDemoActivity extends FragmentActivity  implements OnMapReadyCallback {
    private ListFragment mList;
    private MapAdapter mAdapter;
    private LazyAdapter lAdapter;
    private SupportMapFragment mapFragment;
    
    // Places List
    PlacesList nearPlaces =  new PlacesList();
    List<Place> places;
    
    
    List<NamedLocation> locations = new ArrayList<NamedLocation>();
    ArrayList<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, NamedLocation>> locationmapList = new ArrayList<HashMap<String, NamedLocation>>();
    // Image List Nodes
    // XML node keys
    static final String KEY_THUMB_URL = "thumb_url";
    static final String KEY_SONG = "song"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_ADDRESS = "address";
    static final String KEY_MAP = "map";
    // GPS Location
     GPSTracker gps;
    ListView list;

    /**
     * A Polygon with five points in the Norther Territory, Australia.
     */
    
    private  com.google.android.gms.maps.model.LatLng LatLng[]  = {}; 
    
    /**
     * A list of locations to show in this ListView.
     */
    
    //TODO XMLPullParser Loaded from JSON Object
    
    private  NamedLocation[] LIST_LOCATIONS = new NamedLocation[100];

    
    Map<String, Place> placelistmap = new HashMap<String, Place>();
    Map<String, NamedLocation> namelocationlistmap = new HashMap<String, NamedLocation>();
    // URL Strings manipulation
    String[] imageUrls;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lite_list_demo);
       
        // creating GPS Class object
        // User location
        gps = new GPSTracker(this);
        Bundle b = getIntent().getExtras();
        
        nearPlaces = (PlacesList) b.getSerializable("near_places");
        places = nearPlaces.results;
       
        LIST_LOCATIONS = new NamedLocation[nearPlaces.results.size()];
        imageUrls = new String[nearPlaces.results.size()];
        
        // TODO This i++ resembles C. Can be buggy memory leaks
        int i = 0;
        for (Place place : places) {
            // creating new HashMap
            HashMap<String, String> placesmap = new HashMap<String, String>();
            HashMap<String, NamedLocation> locationsmap = new HashMap<String, NamedLocation>();
            placesmap.put(KEY_ID, place.id);
            placesmap.put(KEY_ID, place.name);
            placesmap.put(KEY_ADDRESS, place.address);
            placesmap.put(KEY_THUMB_URL, place.office_image_url);
            placesList.add(placesmap);
            // Set Locations
            LIST_LOCATIONS[i] =   new NamedLocation(place.name, new LatLng(place.location.lat, place.location.lng),place.formatted_phone_number, place.address, place.office_image_url, place.address2);
            locationmapList.add(locationsmap);
            locations.add(new NamedLocation(place.name, new LatLng(place.location.lat, place.location.lng),place.formatted_phone_number, place.address, place.office_image_url, place.zip_postal_code));      
          
            // Get image URL's
            imageUrls[i] = place.office_image_url;
            i++;
        }
          
        //  list=(ListView)findViewById(R.id.listview);
        // Getting adapter by passing xml data ArrayList
        //    lAdapter=new LazyAdapter(this.getApplicationContext(), places);
        //   list.setAdapter(lAdapter);
        GoogleType obj = b.getParcelable("GoogleType");
  
        // Set a custom list adapter for a list of locations
        mAdapter = new MapAdapter(this, R.layout.lite_list_demo_row , locations);
        
        // Set a custom list adapter for a list of locations
   //     mAdapter = new MapAdapter(this, locations_list);
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

		private LayoutInflater inflater=null;
        private List<NamedLocation> items;
        private Context context;
        private  Button callButton;
        private final HashSet<MapView> mMaps = new HashSet<MapView>();
        
        public MapAdapter(Context context, int resource,
        		List<NamedLocation> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			this.context = context;
			this.items = objects;
			
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
                holder.place_name = (TextView)row.findViewById(R.id.place_name); 
                holder.place_image=(ImageView)row.findViewById(R.id.place_image); 
                holder.place_distance = (TextView)row.findViewById(R.id.place_distance); 
                holder.place_address=(TextView)row.findViewById(R.id.place_address); 
                holder.callButton = (Button)row.findViewById(R.id.childButton);
                // Set holder as tag for row for more efficient access.
                row.setTag(holder);
                // Initialise the MapView
                holder.initializeMapView();
                // Keep track of MapView
                mMaps.add(holder.mapView);
            } 
            else 
            {
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
            String url = item.office_image_url;
            // Setting all values in list row
            Glide.with(context.getApplicationContext()).load(url).into(holder.place_image);
            // Set the text label for this item
            holder.title.setText(item.name);
            holder.place_distance.setText(item.distance);          /***************************/
    //        holder.place_phone.setText(item.formatted_phone_number);
            holder.place_distance.setText(item.distance);
            holder.place_address.setText(item.address);
            holder.place_name.setText(item.name);
            // Set button listener
            final String formatted_phone = item.formatted_phone_number;
            holder.callButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                public void onClick(View v) {
                        //TODO
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    String uri = "tel:" + formatted_phone.trim() ;
                    callIntent.setData(Uri.parse(uri));
                    startActivity(callIntent);
                }
            });
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
       
        public int getCount() {
          // TODO Auto-generated method stub
          return items.size();
        }
  
       
        public long getItemId(int position) {
          // TODO Auto-generated method stub
          return position;
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
        ImageView place_image;
        TextView place_distance;
        TextView place_address;
        TextView place_name;
        Button callButton;  

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
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
      // TODO his should come from the XMLPullParser, faster render.
        for (Place place : places) {
          map.addMarker(new MarkerOptions().position(new LatLng(place.location.lat, place.location.lng)).title(place.name));
        }
            
    }
    

    public class LazyAdapter extends ArrayAdapter<Place> {
        private LayoutInflater inflater=null;
        private List<Place> items;
        private Context context;
        private  Button callButton;
        public LazyAdapter(Context context, List<Place> d) {
          super(context, R.layout.lite_list_demo_row, R.id.list_image, d);
            items=d;
            this.context = context;    
        }
        public int getCount() {
            return items.size();
        }
        public long getItemId(int position) {
            return position;
        }
       
        // For a list:
        @Override
        public View getView(int position, View recycled, ViewGroup container) {
            View row = recycled;
            
            if (row == null) {
                // Inflate view from layout file
                row = getLayoutInflater().inflate(R.layout.lite_list_demo_row, null);
	            // get an Iterator object .
	            Iterator<Place> itr = items.iterator();
	            while (itr.hasNext()) {
	              itr.next();
	                final Place place = items.get(position);
	                if ( place != null ){
	                    TextView title = (TextView)row.findViewById(R.id.title); // title
	                    TextView artist = (TextView)row.findViewById(R.id.artist); // artist name
	                    TextView duration = (TextView)row.findViewById(R.id.duration); // duration
	                    ImageView thumb_image=(ImageView)row.findViewById(R.id.list_image); // thumb image
	                    Button callButton = (Button)row.findViewById(R.id.childButton);
	
	                    String url = place.office_image_url;
	                    // Setting all values in list row
	                    Glide.with(context.getApplicationContext()).load(url).into(thumb_image);
	
	                    title.setText(place.name);
	                    artist.setText(place.address);
	                    duration.setText(place.formatted_phone_number);
	
	                    // Set button listener
	                    callButton.setOnClickListener(new View.OnClickListener() {
	                        @Override
	                        public void onClick(View v) {
	                            //TODO
	                            Intent callIntent = new Intent(Intent.ACTION_CALL);
	                            String uri = "tel:" + place.formatted_phone_number.trim() ;
	                            callIntent.setData(Uri.parse(uri));
	                            startActivity(callIntent);
	                        }
	
	                    });
	                }
	            }
            } 
            else 
            {
            	row =  recycled;
            }
            return row;
        }
    
    }   
        
}