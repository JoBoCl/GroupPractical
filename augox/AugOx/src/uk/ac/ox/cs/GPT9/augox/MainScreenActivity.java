package uk.ac.ox.cs.GPT9.augox;

import uk.ac.ox.cs.GPT9.augox.route.*;

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
import com.beyondar.android.screenshot.OnScreenshotListener;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainScreenActivity extends FragmentActivity implements OnClickBeyondarObjectListener, OnSharedPreferenceChangeListener {
   
	private static PlacesDatabase placesDatabase = new PlacesDatabase();
	public static PlacesDatabase getPlacesDatabase() { return placesDatabase; }
	private static IRoute route = new Route();
	public static IRoute getCurrentRoute() { return route; }
	private final int USERID = 20000; // TODO guarantee uniqueness
	private final int MAXICONDIST = 50;
	
	private BeyondarFragmentSupport mBeyondarFragment;
	private RadarView mRadarView;
	private RadarWorldPlugin mRadarPlugin;
	private World mWorld;
	private GoogleMap mMap;
	private GoogleMapWorldPlugin mGoogleMapPlugin;
	//private BeyondarViewAdapter mViewAdapter;
	private List<Place> Places = new ArrayList<Place>();

	private SeekBar mSeekBarMaxDistance;
	private View mMapFrame;
	
	private SharedPreferences sharedPref;
	
	private class Place {
		public Place(Integer placeID, GeoObject geoPlace, Marker marker) {
			this.placeID = placeID;
			this.geoPlace = geoPlace;
			//this.marker = marker;
		}
		public int placeID;
		public GeoObject geoPlace;
		//public Marker marker;
	}
	
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_screen);
        
		mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        
        mWorld = new World(this);
		mBeyondarFragment.setWorld(mWorld);
        mWorld.setArViewDistance(100);
		
		GeoObject user = new GeoObject(USERID);
		user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
		user.setImageResource(R.drawable.ic_launcher); // TODO give user an oriented custom icon
		user.setName("User position");
		mWorld.addBeyondarObject(user);
        
        BeyondarLocationManager.addWorldLocationUpdate(mWorld);
		BeyondarLocationManager.addGeoObjectLocationUpdate(user);

		// We need to set the LocationManager to the BeyondarLocationManager.
		BeyondarLocationManager
				.setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		
        mRadarView = (RadarView) findViewById(R.id.radarView);
        // Create the Radar module
        mRadarPlugin = new RadarWorldPlugin(this);
        // set the radar view in to our radar module
        mRadarPlugin.setRadarView(mRadarView);
        // Set how far (in meters) we want to display in the view
        mRadarPlugin.setMaxDistance(mWorld.getArViewDistance());
        // and finally let's add the module
        mWorld.addPlugin(mRadarPlugin);
        
        mSeekBarMaxDistance = ((android.widget.SeekBar)findViewById(R.id.distanceSlider));
        mSeekBarMaxDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	@Override       
            public void onStopTrackingTouch(SeekBar seekBar) { }       

            @Override       
            public void onStartTrackingTouch(SeekBar seekBar) { }       

            @Override       
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            	mWorld.setArViewDistance(seekBar.getProgress());
            	mRadarPlugin.setMaxDistance(mWorld.getArViewDistance());
            }       
        });
        mWorld.setArViewDistance(mSeekBarMaxDistance.getProgress());
        mRadarPlugin.setMaxDistance(mSeekBarMaxDistance.getProgress());
        
        mRadarView.setOnLongClickListener(new OnLongClickListener() {
        	public boolean onLongClick(View rv) {
        		
        		if (mGoogleMapPlugin == null) initializeGMaps();
        		centreCamera();
        		mMapFrame.setVisibility(View.VISIBLE);
				return true; }
        });

        mBeyondarFragment.setOnClickBeyondarObjectListener(this);
        
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        
		/*mViewAdapter = new CustomBeyondarViewAdapter(this);
		mBeyondarFragment.setBeyondarViewAdapter(mViewAdapter);*/
    	mBeyondarFragment.setMaxFarDistance(MAXICONDIST);
        
        fillWorld();
    }
   
   private void startFullInfoActivity(final BeyondarObject geoPlace) {
	   mBeyondarFragment.takeScreenshot(new OnScreenshotListener() {
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
	   });
   }
   
   private void initializeGMaps() {
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		if (mMap == null){
			return;
		}
		
		mMapFrame = (View)findViewById(R.id.map_frame);
		mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng l) {
				mMapFrame.setVisibility(View.GONE);
			}
		});
	   
		mGoogleMapPlugin = new GoogleMapWorldPlugin(this);
		mGoogleMapPlugin.setGoogleMap(mMap);
        mWorld.addPlugin(mGoogleMapPlugin);
        
        /*for (Place place: Places) {
        	mMap.addMarker(new MarkerOptions()
        		.position(new LatLng(place.geoPlace.getLatitude(), place.geoPlace.getLongitude()))
        		.title(place.geoPlace.getName())
        		.snippet(placesDatabase.getPlaceByID(place.placeID).getDescription()));
        }*/
        refreshVisibility();
   }
   
   private void centreCamera() {
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mWorld.getLatitude(), mWorld.getLongitude()), 15));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
   }
   
   @Override
   protected void onResume() {
        super.onResume();
        BeyondarLocationManager.enable();
   }


   @Override
   protected void onPause() {
        super.onPause();
        BeyondarLocationManager.disable();
   }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
    	// DEBUG VERSION
        switch (item.getItemId()) {
            case R.id.action_dbg_placefullinfo:
            	Intent intent1 = new Intent(this, PlaceFullInfoActivity.class);
            	intent1.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, 0);
        		intent1.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, "");
        		intent1.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE, 13.37);
                startActivity(intent1);
                return true;
            case R.id.action_dbg_listplaces:
            	Intent intent2 = new Intent(this, ListPlacesActivity.class);
            	// debug values: CS dept entrance!
            	intent2.putExtra(ListPlacesActivity.EXTRA_LATITUDE, mWorld.getLatitude());
            	intent2.putExtra(ListPlacesActivity.EXTRA_LONGITUDE, mWorld.getLongitude());
                startActivity(intent2);
                return true;
            case R.id.action_dbg_settingspanel:
            	Intent intent3 = new Intent(this, SettingsPanelActivity.class);
                startActivity(intent3);
                return true;
            case R.id.action_dbg_filterpanel:
            	Intent intent4 = new Intent(this, FilterPanelActivity.class);
                startActivity(intent4);
                return true;
            case R.id.action_dbg_routeplanner:
            	Intent intent5 = new Intent(this, RoutePlannerActivity.class);
            	intent5.putExtra(RoutePlannerActivity.EXTRA_PLACELIST, "");
                startActivity(intent5);
                return true;
            case R.id.action_dbg_autoplanner:
            	Intent intent6 = new Intent(this, AutoPlannerActivity.class);
                startActivity(intent6);
                return true;
            case R.id.action_dbg_databasedebugger:
            	Intent intent7 = new Intent(this, DatabaseDebuggerActivity.class);
                startActivity(intent7);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
	@Override
	public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
		/*if (beyondarObjects.size() > 0) {
			Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(),
					Toast.LENGTH_LONG).show();
		}*/
		// TODO
		BeyondarObject clicked = null;
		for (BeyondarObject beyondarObject: beyondarObjects) {
			if (beyondarObject.isVisible()) {
				clicked = beyondarObject;
				break;
			}
		}
		if (clicked != null) startFullInfoActivity(clicked);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		refreshVisibility();
	}
	
	private List<PlaceCategory> currentCategories() {
		List<PlaceCategory> pcs = new ArrayList<PlaceCategory>();
		/*for (PlaceCategory pc: PlaceCategory.values()) {
			if (sharedPref.getBoolean(pc.getFilter(), true)) pcs.add(pc);
		}*/
		if (sharedPref.getBoolean("filter_bars", true)) pcs.add(PlaceCategory.BAR);
		if (sharedPref.getBoolean("filter_colleges", true)) pcs.add(PlaceCategory.COLLEGE);
		if (sharedPref.getBoolean("filter_museums", true)) pcs.add(PlaceCategory.MUSEUM);
		if (sharedPref.getBoolean("filter_restaurants", true)) pcs.add(PlaceCategory.RESTAURANT);
		return pcs;
	}
	
	private void fillWorld() {
		DatabaseQuery dq = new AllQuery();
		DatabaseSorter ds = new DistanceFromSorter(mWorld.getLongitude(), mWorld.getLatitude(), SortOrder.ASC);
		List<Integer> placeIDs = placesDatabase.query(dq, ds);
		for (Integer placeID: placeIDs) {
			PlaceData currPlace = placesDatabase.getPlaceByID(placeID);
			GeoObject currPlaceGeo = new GeoObject(placeID);
			currPlaceGeo.setGeoPosition(currPlace.getLatitude(), currPlace.getLongitude());
			currPlaceGeo.setName(currPlace.getName());
			currPlaceGeo.setImageResource(R.drawable.ic_launcher); // TODO custom icons per category
			Places.add(new Place(placeID, currPlaceGeo, null));
			mWorld.addBeyondarObject(currPlaceGeo);
		}
		refreshVisibility();
	}
	
	private void refreshVisibility() { 
		for (Place place: Places) {
			boolean vis = (currentCategories().contains(placesDatabase.getPlaceByID(place.placeID).getCategory()));
			place.geoPlace.setVisible(vis);
		    //if (place.marker != null) place.marker.setVisible(vis);
		}
	}
	
	/*private int sizeIcon (double dist) {
		return (int)((1-(dist/mSeekBarMaxDistance.getMax())) * (MAXICONSIZE-MINICONSIZE) + MINICONSIZE);
	}
	
	private class CustomBeyondarViewAdapter extends BeyondarViewAdapter {

		LayoutInflater inflater;

		public CustomBeyondarViewAdapter(Context context) {
			super(context);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(BeyondarObject beyondarObject, View recycledView, ViewGroup parent) {
			if (beyondarObject.getId() == USERID) return null;
			if (!currentCategories().contains(placesDatabase.getPlaceByID((int)beyondarObject.getId()).getCategory())) {
				return null;
			}
			if (recycledView == null) {
				recycledView = inflater.inflate(R.layout.beyondar_object_view, null);
			}

			ImageView imageView = (ImageView) recycledView.findViewById(R.id.iconView);
			imageView.setBackgroundResource(R.drawable.ic_launcher);
			
			int iconSize = sizeIcon((int)beyondarObject.getDistanceFromUser());
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
			imageView.setLayoutParams(layoutParams);

			// Once the view is ready we specify the position
			Point2 poss = beyondarObject.getScreenPositionCenter();
			pos.x = pos.x - iconSize/2;
			pos.y = pos.y - iconSize/2;
			setPosition(pos);
			
			return recycledView;
		}

	}*/
}
