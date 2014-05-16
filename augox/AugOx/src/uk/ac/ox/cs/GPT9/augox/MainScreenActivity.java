package uk.ac.ox.cs.GPT9.augox;

import uk.ac.ox.cs.GPT9.augox.route.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.AllQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;	
import uk.ac.ox.cs.GPT9.augox.dbsort.DatabaseSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.DistanceFromSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.SortOrder;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.view.BeyondarViewAdapter;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.RatingBar;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainScreenActivity extends FragmentActivity implements OnClickBeyondarObjectListener, OnSharedPreferenceChangeListener {
   
	// "Global" variables
	private static PlacesDatabase placesDatabase = new PlacesDatabase();

	public static PlacesDatabase getPlacesDatabase() { return placesDatabase; }
	private static IRoute route = new Route();

	public static IRoute getCurrentRoute() { return route; }
	
	// BeyondAR objects
	public static World mWorld;
	public static GoogleMapWorldPlugin mGoogleMapPlugin;
	private RadarWorldPlugin mRadarPlugin;
	public static List<Place> Places = new ArrayList<Place>();
	private List<BeyondarObject> infoViewOn = new ArrayList<BeyondarObject>();
	public static GeoObject user;
	private final int USERID = 20000; // TODO guarantee uniqueness	

 	// UI elements
	private BeyondarFragmentSupport mBeyondarFragment;
	private SeekBar mSeekBarMaxDistance;
	private TextView mMaxDistanceText;
	private RadarView mRadarView;
	
	// User preferences
	private static SharedPreferences sharedPref;
	public static SharedPreferences getSharedPref() { return sharedPref; }

	// Public getter for user location
 	public static double[] getUserLocation() {
 		LatLng latLng = mGoogleMapPlugin.getLatLng();
  		double[] latlong = new double[2];
 		latlong[0] = latLng.latitude;
 		latlong[1] = latLng.longitude;
  
  		return latlong;
  	}

 	// Helper class for linking unique place ids to their beyondar object
	public class Place {
		public Place(Integer placeID, GeoObject geoPlace) {
			this.placeID = placeID;
			this.geoPlace = geoPlace;
		}

		public int placeID;
		public GeoObject geoPlace;
	}
	
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize UI elements etc.
        setContentView(R.layout.activity_main_screen);
		mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        mRadarView = (RadarView) findViewById(R.id.radarView);
        mSeekBarMaxDistance = ((android.widget.SeekBar)findViewById(R.id.distanceSlider));
        mMaxDistanceText = ((TextView)findViewById(R.id.maxDist));
        
        // Load local database and user preferences
        loadDatabase("db.dat");	       
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
 		sharedPref.registerOnSharedPreferenceChangeListener(this);
        
        // Set up beyondar
        mWorld = new World(this);
		mBeyondarFragment.setWorld(mWorld);
        mWorld.setArViewDistance(100); // temporary view distance
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);        
		mBeyondarFragment.setBeyondarViewAdapter(new CustomBeyondarViewAdapter(this));    
        // Add google maps plugin to beyondar
		mGoogleMapPlugin = new GoogleMapWorldPlugin(this);
		mGoogleMapPlugin.setGoogleMap(GoogleMapsActivity.mMap);
		mWorld.addPlugin(mGoogleMapPlugin);
		// Add radar plugin to beyondar
		mRadarPlugin = new RadarWorldPlugin(this);
		mRadarPlugin.setRadarView(mRadarView);
        mRadarPlugin.setMaxDistance(mWorld.getArViewDistance()); // temporary view distance
        mWorld.addPlugin(mRadarPlugin);
		// Add user to beyondar world		
		user = new GeoObject(USERID);
		user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
		user.setImageResource(R.drawable.arrowicon); // TODO give user an oriented custom icon
		user.setVisible(true);
		user.setName("User position");
		mWorld.addBeyondarObject(user);
		// Manage the user's location (gps)
        BeyondarLocationManager.addWorldLocationUpdate(mWorld);
		//BeyondarLocationManager.addGeoObjectLocationUpdate(user);
		BeyondarLocationManager.setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        
		// UI elements managing view distance
        mSeekBarMaxDistance.setMax((int)(Float.parseFloat(sharedPref.getString("setting_arview_max_distance", "1"))*1000));
        mMaxDistanceText.setText(PlaceFullInfoActivity.distanceAsString(((double)mSeekBarMaxDistance.getMax())/1000));
        mSeekBarMaxDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	@Override       
            public void onStopTrackingTouch(SeekBar seekBar) { }       

            @Override       
            public void onStartTrackingTouch(SeekBar seekBar) { }       

            @Override       
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            	mWorld.setArViewDistance(progress);
            	mRadarPlugin.setMaxDistance(progress);
            }       
        });
        mSeekBarMaxDistance.setProgress(mSeekBarMaxDistance.getMax()/2);
        // Managing the radar -> google map
        mRadarView.setOnLongClickListener(new OnLongClickListener() {
        	public boolean onLongClick(View rv) {        		
        		Intent intent2 = new Intent(getApplicationContext(), GoogleMapsActivity.class);
            	intent2.putExtra(ListPlacesActivity.EXTRA_LATITUDE, mWorld.getLatitude());
            	intent2.putExtra(ListPlacesActivity.EXTRA_LONGITUDE, mWorld.getLongitude());
            	startActivity(intent2);
				return true; }
        });

    	// Finally fill our beyondar world with places from the previously loaded database
        fillWorld();
    }
   
   // Loads the local database
   private void loadDatabase(String filename) {
	   AssetManager ast = getAssets();
		try {
			InputStream inp = ast.open(filename);
			getPlacesDatabase().loadFromStream(inp);
			inp.close();
		} catch (IOException e) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Cricital Error! The database could not be loaded.",
					Toast.LENGTH_LONG);
			toast.show();
		}
   }
   
   // Loads the full info activity for a given place
   private void startFullInfoActivity(final BeyondarObject geoPlace) {
	   // Commented section additionally passes a screenshot to PlaceFullInfoActivity to use as a background
	   /*mBeyondarFragment.takeScreenshot(new OnScreenshotListener() {
		   @Override
		   public void onScreenshot (Bitmap screenshot) {
			   Bundle bundle = new Bundle();
			   Bitmap ss2 = Bitmap.createScaledBitmap(screenshot, screenshot.getWidth()/4, screenshot.getHeight()/4, true);
			   bundle.putParcelable("background", (Parcelable)ss2);
			   Intent intent = new Intent(getApplicationContext(), PlaceFullInfoActivity.class);
			   intent.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, bundle);
			   intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE, geoPlace.getDistanceFromUser()/1000);
			   intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, (int)geoPlace.getId());
			   startActivity(intent);
		   }
	   });*/ // TODO: image for background
	   Intent intent = new Intent(getApplicationContext(), PlaceFullInfoActivity.class);
	   intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE, geoPlace.getDistanceFromUser()/1000);
	   intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, (int)geoPlace.getId());
	   startActivity(intent);
   }
   
   @Override
   protected void onResume() {
        super.onResume();
        BeyondarLocationManager.enable(); // run gps service
        routeChanged(); // the route may have been changed by another activity
   }
   
   // Handles potential changes to route (update icons, moveon button)
   public void routeChanged() {
	   for (Place p: Places) {
		   resetImage(p);
	   }
	  updateMoveonButtonVisibility();
   }

   // returns the unique Place associated to a place id
   private Place findPlace(int placeID) {
	   for (Place p: Places) {
		   if (p.placeID == placeID) return p;
	   }
	   return null;
   }
   
   @Override
   protected void onPause() {
        super.onPause();
        BeyondarLocationManager.disable(); // pause gps service
   }
    
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
    	// DEBUG VERSION (which seems to have made it into release...)
        switch (item.getItemId()) {
            case R.id.action_launch_listplaces:
            	Intent intent2 = new Intent(this, ListPlacesActivity.class);
            	intent2.putExtra(ListPlacesActivity.EXTRA_LATITUDE, mWorld.getLatitude());
            	intent2.putExtra(ListPlacesActivity.EXTRA_LONGITUDE, mWorld.getLongitude());
                startActivity(intent2);
                return true;
            case R.id.action_launch_settingspanel:
            	Intent intent3 = new Intent(this, SettingsPanelActivity.class);
                startActivity(intent3);
                return true;
            case R.id.action_launch_filterpanel:
            	Intent intent4 = new Intent(this, FilterPanelActivity.class);
                startActivity(intent4);
                return true;
            case R.id.action_launch_routeplanner:
            	Intent intent5 = new Intent(this, RoutePlannerActivity.class);
            	intent5.putExtra(ListPlacesActivity.EXTRA_LATITUDE, mWorld.getLatitude());
            	intent5.putExtra(ListPlacesActivity.EXTRA_LONGITUDE, mWorld.getLongitude());
                startActivity(intent5);
                return true;
            case R.id.action_launch_autoplanner:
            	Intent intent6 = new Intent(this, AutoPlannerActivity.class);
                startActivity(intent6);
                return true;
            case R.id.action_route_moveon:
            	if (route != null) {
            		Place old = null;
            		if (!route.empty()) { 
            			old = findPlace(route.getNextAsID());
            			route.getNext().updateVisited(true);
            		}
	            	if (route.moveOn()) { // route is ended
	            		updateMoveonButtonVisibility();
	            	}
	            	else { // make the next one be HERE
	            		resetImage(route.getNextAsID());
	            	}
	            	if (old != null) resetImage(old);
            	}
            	return true;
            /*case R.id.action_launch_databasedebugger:
            	Intent intent7 = new Intent(this, DatabaseDebuggerActivity.class);
                startActivity(intent7);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    // shows moveon button iff there's a place on the route
    private void updateMoveonButtonVisibility() {
    	if (findViewById(R.id.action_route_moveon) == null) return;
    	if (route != null && !route.empty()) findViewById(R.id.action_route_moveon).setVisibility(View.VISIBLE);
    	else findViewById(R.id.action_route_moveon).setVisibility(View.GONE);
    }
    
    // functions to set the icon of a place
    private void resetImage(int p) {
    	resetImage(findPlace(p));
    }   
    public static void resetImage(Place p) {
    	p.geoPlace.setImageResource(getImage(p));
    }
    private static int getImage(Place p) {
    	PlaceData pd = placesDatabase.getPlaceByID(p.placeID);
    	if (route != null && !route.empty()) return pd.getCategory().getImageRef(pd.getVisited(), p.placeID == route.getNextAsID());
    	else return pd.getCategory().getImageRef(pd.getVisited());
    }
    
    // callback to display small info view when an icon is pressed
	@Override
	public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
		BeyondarObject clicked = null;
		for (BeyondarObject beyondarObject : beyondarObjects) {
			if (beyondarObject.isVisible()) {
				clicked = beyondarObject;
				break;
			}
		}
		if (clicked != null) {
			if (infoViewOn.contains(clicked)) {
				infoViewOn.remove(clicked);
			} else {
				infoViewOn.add(clicked);
			}
		}
	}

	// callback for preference changes, in case of max max view distance change or filter changes
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		mSeekBarMaxDistance.setMax((int)(Float.parseFloat(sharedPref.getString("setting_arview_max_distance", "1"))*1000));
		mMaxDistanceText.setText(PlaceFullInfoActivity.distanceAsString(((double)mSeekBarMaxDistance.getMax())/1000));
		refreshVisibility();
	}
	
	// return currently active categories
	private static List<PlaceCategory> currentCategories() {
		List<PlaceCategory> pcs = new ArrayList<PlaceCategory>();
		for (PlaceCategory pc: PlaceCategory.values()) {
			if (sharedPref.getBoolean(pc.getFilter(), true)) pcs.add(pc);
				pcs.add(pc);
		}
		return pcs;
	}
	
	// populates the beyondar world from the database
	private void fillWorld() {
		DatabaseQuery dq = new AllQuery();
		DatabaseSorter ds = new DistanceFromSorter(mWorld.getLongitude(), mWorld.getLatitude(), SortOrder.ASC);
		List<Integer> placeIDs = placesDatabase.query(dq, ds);
		for (Integer placeID: placeIDs) {
			PlaceData currPlace = placesDatabase.getPlaceByID(placeID);
			GeoObject currPlaceGeo = new GeoObject(placeID);
			//currPlaceGeo.setGeoPosition(currPlace.getLatitude(), currPlace.getLongitude());
			// randomizing the altitude means the icons don't all appear in a horizontal bar and overlap horribly
			currPlaceGeo.setGeoPosition(currPlace.getLatitude(), currPlace.getLongitude(), mWorld.getAltitude() + (10*Math.random()-5)/50000);
			currPlaceGeo.setName(currPlace.getName());
			currPlaceGeo.setImageResource(currPlace.getCategory().getImageRef(currPlace.getVisited()));
			Places.add(new Place(placeID, currPlaceGeo));
			mWorld.addBeyondarObject(currPlaceGeo);
		}
		refreshVisibility();
	}
	
	public static void refreshVisibility() { 
		for (Place place: Places) {
			boolean vis = (currentCategories().contains(placesDatabase.getPlaceByID(place.placeID).getCategory()));
			place.geoPlace.setVisible(vis);
		}
	}
	
	// custom beyondar view allows for the mini place info bubbles
	private class CustomBeyondarViewAdapter extends BeyondarViewAdapter {

		LayoutInflater inflater;

		public CustomBeyondarViewAdapter(Context context) {
			super(context);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final BeyondarObject beyondarObject, View recycledView, ViewGroup parent) {
			if (!infoViewOn.contains(beyondarObject)) {
				return null;
			}
			if (recycledView == null) {
				recycledView = inflater.inflate(R.layout.mini_info_view, null);
			}

			TextView distance = (TextView)recycledView.findViewById(R.id.placeNameDistance);
			distance.setText(beyondarObject.getName() + " (" + (PlaceFullInfoActivity.distanceAsString(beyondarObject.getDistanceFromUser()/1000) + ")"));
			RatingBar ratingBar = (RatingBar)recycledView.findViewById(R.id.ratingBar);
			ratingBar.setRating((float)(placesDatabase.getPlaceByID((int)beyondarObject.getId()).getRating()));
						
			// Once the view is ready we specify the position
			setPosition(beyondarObject.getScreenPositionTopRight());

			recycledView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startFullInfoActivity(beyondarObject);
				};
			});

			return recycledView;
		}
	}
}
