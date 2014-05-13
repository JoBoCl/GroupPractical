package uk.ac.ox.cs.GPT9.augox;

import uk.ac.ox.cs.GPT9.augox.GoogleRouteHelper.DownloadTask;
import uk.ac.ox.cs.GPT9.augox.newsfeed.NewsFeed;
import uk.ac.ox.cs.GPT9.augox.route.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONValue;

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
import com.beyondar.android.util.math.geom.Point2;
import com.beyondar.android.view.BeyondarViewAdapter;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainScreenActivity extends FragmentActivity implements
		OnClickBeyondarObjectListener, OnSharedPreferenceChangeListener {

	private static PlacesDatabase placesDatabase = new PlacesDatabase();

	public static PlacesDatabase getPlacesDatabase() {
		return placesDatabase;
	}

	private static IRoute route = new Route();

	public static IRoute getCurrentRoute() {
		return route;
	}

	private final int USERID = 20000; // TODO guarantee uniqueness
	private final int MAXICONDIST = 50;

	private BeyondarFragmentSupport mBeyondarFragment;
	private RadarView mRadarView;
	private RadarWorldPlugin mRadarPlugin;
	public static World mWorld;
	public static GoogleMapWorldPlugin mGoogleMapPlugin;
	public static List<Place> Places = new ArrayList<Place>();
	private List<BeyondarObject> infoViewOn = new ArrayList<BeyondarObject>();

	public static GeoObject user;

	public static double[] getUserLocation() {
		LatLng latLng = mGoogleMapPlugin.getLatLng();
		double[] latlong = new double[2];
		latlong[0] = latLng.latitude;
		latlong[1] = latLng.longitude;

		return latlong;
	}

	private SeekBar mSeekBarMaxDistance;
	private View mMapFrame;

	private static SharedPreferences sharedPref;

	/**
	 * @return the sharedPref
	 */
	public static SharedPreferences getSharedPref() {
		return sharedPref;
	}

	public class Place {
		public Place(Integer placeID, GeoObject geoPlace, Marker marker) {
			this.placeID = placeID;
			this.geoPlace = geoPlace;
			// this.marker = marker;
		}

		public int placeID;
		public GeoObject geoPlace;
		// public Marker marker;
	}

	private void getDirections() {
		String s = "http://maps.googleapis.com/maps/api/directions/json?";
		s.concat("origin=");
		s.concat(mGoogleMapPlugin.getLatLng().toString());
		s.concat("&destination=");
		s.concat(new LatLng(route.getNext().getLatitude(), route.getNext()
				.getLongitude()).toString());
		s.concat("&mode=walking");
		s.concat("&key=");
		s.concat((String) getResources().getText(R.string.google_apikey));
		URL url;
		try {
			url = new URL(s);
			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();
			org.json.simple.JSONObject obj = (org.json.simple.JSONObject) JSONValue
					.parse(NewsFeed.readResponse(connection));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_screen);

		loadDatabase("db.dat");

		mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager()
				.findFragmentById(R.id.beyondarFragment);

		mWorld = new World(this);
		mBeyondarFragment.setWorld(mWorld);
		mWorld.setArViewDistance(100);

		mGoogleMapPlugin = new GoogleMapWorldPlugin(this);
		mGoogleMapPlugin.setGoogleMap(GoogleMapsActivity.mMap);
		mWorld.addPlugin(mGoogleMapPlugin);

		user = new GeoObject(USERID);
		user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
		// user.setGeoPosition(51.757674, -1.257535); // 31 Museum Road
		user.setImageResource(R.drawable.arrowicon); // TODO give user an
														// oriented custom icon
		user.setVisible(true);
		user.setName("User position");
		mWorld.addBeyondarObject(user);

		BeyondarLocationManager.addWorldLocationUpdate(mWorld);
		// BeyondarLocationManager.addGeoObjectLocationUpdate(user);

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

		mSeekBarMaxDistance = ((android.widget.SeekBar) findViewById(R.id.distanceSlider));
		mSeekBarMaxDistance
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						mWorld.setArViewDistance(seekBar.getProgress());
						mRadarPlugin.setMaxDistance(mWorld.getArViewDistance());
					}
				});
		mSeekBarMaxDistance.setProgress(mSeekBarMaxDistance.getMax() / 2);
		mWorld.setArViewDistance(mSeekBarMaxDistance.getProgress());
		mRadarPlugin.setMaxDistance(mSeekBarMaxDistance.getProgress());

		mRadarView.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View rv) {
				// if (mGoogleMapPlugin == null) initializeGMaps();
				// centreCamera();
				// mMapFrame.setVisibility(View.VISIBLE);
				Intent intent2 = new Intent(getApplicationContext(),
						GoogleMapsActivity.class);
				intent2.putExtra(ListPlacesActivity.EXTRA_LATITUDE,
						mWorld.getLatitude());
				intent2.putExtra(ListPlacesActivity.EXTRA_LONGITUDE,
						mWorld.getLongitude());
				// user.setVisible(true);
				startActivity(intent2);
				return true;
			}
		});

		mBeyondarFragment.setOnClickBeyondarObjectListener(this);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPref.registerOnSharedPreferenceChangeListener(this);

		// mViewAdapter = new CustomBeyondarViewAdapter(this);
		mBeyondarFragment.setBeyondarViewAdapter(new CustomBeyondarViewAdapter(
				this));
		mBeyondarFragment.setMaxFarDistance(MAXICONDIST);

		updateMoveonButtonVisibility();

		// GoogleMapsActivity.mMap = ((SupportMapFragment)
		// getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

		fillWorld();
	}

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

	private void startFullInfoActivity(final BeyondarObject geoPlace) {
		/*
		 * mBeyondarFragment.takeScreenshot(new OnScreenshotListener() {
		 * 
		 * @Override public void onScreenshot (Bitmap screenshot) { Bundle
		 * bundle = new Bundle(); Bitmap ss2 =
		 * Bitmap.createScaledBitmap(screenshot, screenshot.getWidth()/4,
		 * screenshot.getHeight()/4, true); bundle.putParcelable("background",
		 * (Parcelable)ss2); Intent intent = new Intent(getApplicationContext(),
		 * PlaceFullInfoActivity.class);
		 * intent.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, bundle);
		 * intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE,
		 * geoPlace.getDistanceFromUser()/1000);
		 * intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE,
		 * (int)geoPlace.getId()); startActivity(intent); } });
		 */// TODO: image for background
		Intent intent = new Intent(getApplicationContext(),
				PlaceFullInfoActivity.class);
		intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE,
				geoPlace.getDistanceFromUser() / 1000);
		intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE,
				(int) geoPlace.getId());
		startActivity(intent);
	}

	/*
	 * private void initializeGMaps() { mMap = ((SupportMapFragment)
	 * getSupportFragmentManager().findFragmentById(R.id.map)).getMap(); if
	 * (mMap == null){ return; } mMapFrame = (View)findViewById(R.id.map_frame);
	 * mMap.setOnMapLongClickListener(new OnMapLongClickListener() { public void
	 * onMapLongClick(LatLng l) { mMapFrame.setVisibility(View.GONE); } });
	 * 
	 * mGoogleMapPlugin = new GoogleMapWorldPlugin(this);
	 * mGoogleMapPlugin.setGoogleMap(mMap); mWorld.addPlugin(mGoogleMapPlugin);
	 * 
	 * refreshVisibility();
	 * 
	 * if(!route.empty()){ LatLng origin = mGoogleMapPlugin.getLatLng(); LatLng
	 * dest = new LatLng(route.getNext().getLatitude(),
	 * route.getNext().getLongitude());
	 * 
	 * // Getting URL to the Google Directions API String url =
	 * GoogleRouteHelper.getDirectionsUrl(origin, dest);
	 * 
	 * DownloadTask downloadTask = new DownloadTask();
	 * 
	 * // Start downloading json data from Google Directions API
	 * downloadTask.execute(url); } }
	 * 
	 * private void centreCamera() {
	 * mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new
	 * LatLng(mWorld.getLatitude(), mWorld.getLongitude()), 15));
	 * mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null); }
	 */

	@Override
	protected void onResume() {
		super.onResume();
		BeyondarLocationManager.enable();
		// user.setVisible(false);
		routeChanged();
	}

	public void routeChanged() {
		for (Place p : Places) {
			resetImage(p);
		}
		updateMoveonButtonVisibility();
	}

	private Place findPlace(int placeID) {
		for (Place p : Places) {
			if (p.placeID == placeID)
				return p;
		}
		return null;
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
		case R.id.action_launch_listplaces:
			Intent intent2 = new Intent(this, ListPlacesActivity.class);
			intent2.putExtra(ListPlacesActivity.EXTRA_LATITUDE,
					mWorld.getLatitude());
			intent2.putExtra(ListPlacesActivity.EXTRA_LONGITUDE,
					mWorld.getLongitude());
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
			intent5.putExtra(ListPlacesActivity.EXTRA_LATITUDE,
					mWorld.getLatitude());
			intent5.putExtra(ListPlacesActivity.EXTRA_LONGITUDE,
					mWorld.getLongitude());
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
				} else { // make the next one be HERE
					resetImage(route.getNextAsID());
				}
				if (old != null)
					resetImage(old);
			}
			return true;
			/*
			 * case R.id.action_launch_databasedebugger: Intent intent7 = new
			 * Intent(this, DatabaseDebuggerActivity.class);
			 * startActivity(intent7); return true;
			 */
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void updateMoveonButtonVisibility() {
		if (findViewById(R.id.action_route_moveon) == null)
			return;
		if (route != null && !route.empty())
			findViewById(R.id.action_route_moveon).setVisibility(View.VISIBLE);
		else
			findViewById(R.id.action_route_moveon).setVisibility(View.GONE);
	}

	private void resetImage(int p) {
		resetImage(findPlace(p));
	}

	public static void resetImage(Place p) {
		p.geoPlace.setImageResource(getImage(p));
	}

	private static int getImage(Place p) {
		PlaceData pd = placesDatabase.getPlaceByID(p.placeID);
		if (route != null && !route.empty())
			return pd.getCategory().getImageRef(pd.getVisited(),
					p.placeID == route.getNextAsID());
		else
			return pd.getCategory().getImageRef(pd.getVisited());
	}

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

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		mSeekBarMaxDistance.setMax((int) (Float.parseFloat(sharedPref
				.getString("setting_arview_max_distance", "1")) * 1000));
		refreshVisibility();
	}

	private List<PlaceCategory> currentCategories() {
		List<PlaceCategory> pcs = new ArrayList<PlaceCategory>();
		for (PlaceCategory pc : PlaceCategory.values()) {
			if (sharedPref.getBoolean(pc.getFilter(), true))
				pcs.add(pc);
		}
		/*
		 * if (sharedPref.getBoolean("filter_bars", true))
		 * pcs.add(PlaceCategory.BAR); if
		 * (sharedPref.getBoolean("filter_colleges", true))
		 * pcs.add(PlaceCategory.COLLEGE); if
		 * (sharedPref.getBoolean("filter_museums", true))
		 * pcs.add(PlaceCategory.MUSEUM); if
		 * (sharedPref.getBoolean("filter_restaurants", true))
		 * pcs.add(PlaceCategory.RESTAURANT);
		 */
		return pcs;
	}

	private void fillWorld() {
		DatabaseQuery dq = new AllQuery();
		DatabaseSorter ds = new DistanceFromSorter(mWorld.getLongitude(),
				mWorld.getLatitude(), SortOrder.ASC);
		List<Integer> placeIDs = placesDatabase.query(dq, ds);
		for (Integer placeID : placeIDs) {
			PlaceData currPlace = placesDatabase.getPlaceByID(placeID);
			GeoObject currPlaceGeo = new GeoObject(placeID);
			// currPlaceGeo.setGeoPosition(currPlace.getLatitude(),
			// currPlace.getLongitude());
			currPlaceGeo.setGeoPosition(currPlace.getLatitude(),
					currPlace.getLongitude(),
					mWorld.getAltitude() + (10 * Math.random() - 5) / 50000);
			currPlaceGeo.setName(currPlace.getName());
			currPlaceGeo.setImageResource(currPlace.getCategory().getImageRef(
					currPlace.getVisited()));
			Places.add(new Place(placeID, currPlaceGeo, null));
			mWorld.addBeyondarObject(currPlaceGeo);
		}
		refreshVisibility();
	}

	private void refreshVisibility() {
		for (Place place : Places) {
			boolean vis = (currentCategories().contains(placesDatabase
					.getPlaceByID(place.placeID).getCategory()));
			place.geoPlace.setVisible(vis);
			// if (place.marker != null) place.marker.setVisible(vis);
		}
	}

	/*
	 * private int sizeIcon (double dist) { return
	 * (int)((1-(dist/mSeekBarMaxDistance.getMax())) * (MAXICONSIZE-MINICONSIZE)
	 * + MINICONSIZE); }
	 * 
	 * private class CustomBeyondarViewAdapter extends BeyondarViewAdapter {
	 * 
	 * LayoutInflater inflater;
	 * 
	 * public CustomBeyondarViewAdapter(Context context) { super(context);
	 * inflater = (LayoutInflater)
	 * context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); }
	 * 
	 * @Override public View getView(BeyondarObject beyondarObject, View
	 * recycledView, ViewGroup parent) { if (beyondarObject.getId() == USERID)
	 * return null; if
	 * (!currentCategories().contains(placesDatabase.getPlaceByID
	 * ((int)beyondarObject.getId()).getCategory())) { return null; } if
	 * (recycledView == null) { recycledView =
	 * inflater.inflate(R.layout.beyondar_object_view, null); }
	 * 
	 * ImageView imageView = (ImageView)
	 * recycledView.findViewById(R.id.iconView);
	 * imageView.setBackgroundResource(R.drawable.ic_launcher);
	 * 
	 * int iconSize = sizeIcon((int)beyondarObject.getDistanceFromUser());
	 * RelativeLayout.LayoutParams layoutParams = new
	 * RelativeLayout.LayoutParams(iconSize, iconSize);
	 * imageView.setLayoutParams(layoutParams);
	 * 
	 * // Once the view is ready we specify the position Point2 pos =
	 * beyondarObject.getScreenPositionCenter(); pos.x = pos.x - iconSize/2;
	 * pos.y = pos.y - iconSize/2; setPosition(pos);
	 * 
	 * return recycledView; }
	 * 
	 * }
	 */

	private class CustomBeyondarViewAdapter extends BeyondarViewAdapter {

		LayoutInflater inflater;

		public CustomBeyondarViewAdapter(Context context) {
			super(context);
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final BeyondarObject beyondarObject,
				View recycledView, ViewGroup parent) {
			if (!infoViewOn.contains(beyondarObject)) {
				return null;
			}
			if (recycledView == null) {
				recycledView = inflater.inflate(R.layout.mini_info_view, null);
			}

			TextView textView = (TextView) recycledView
					.findViewById(R.id.placeName);
			textView.setText(beyondarObject
					.getName()
					.concat("\n")
					.concat(PlaceFullInfoActivity
							.distanceAsString(beyondarObject
									.getDistanceFromUser() / 1000)));

			List<ImageView> stars = new ArrayList<ImageView>();
			stars.add((ImageView) recycledView
					.findViewById(R.id.imageViewStar1));
			stars.add((ImageView) recycledView
					.findViewById(R.id.imageViewStar2));
			stars.add((ImageView) recycledView
					.findViewById(R.id.imageViewStar3));
			stars.add((ImageView) recycledView
					.findViewById(R.id.imageViewStar4));
			stars.add((ImageView) recycledView
					.findViewById(R.id.imageViewStar5));
			int rating = (placesDatabase.getPlaceByID((int) beyondarObject
					.getId()).getRating());
			for (int i = 0; i < rating; i++)
				stars.get(i).setVisibility(View.VISIBLE);
			for (int i = rating; i <= 4; i++)
				stars.get(i).setVisibility(View.INVISIBLE);

			// boolean open =
			// (placesDatabase.getPlaceByID((int)beyondarObject.getId()).getOpeningHours().isOpenAt(null));
			recycledView.setBackgroundColor(getResources()
					.getColor(R.color.red));
			textView.setTextColor(getResources().getColor(R.color.white));

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
