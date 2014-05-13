package uk.ac.ox.cs.GPT9.augox;

import uk.ac.ox.cs.GPT9.augox.GoogleRouteHelper.DownloadTask;
import uk.ac.ox.cs.GPT9.augox.route.IRoute;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

public class GoogleMapsActivity extends FragmentActivity {
   
	private IRoute route;
	private World mWorld;
	public static GoogleMap mMap;
	
	public static Polyline routeLine = null;
	
   @Override
    protected void onCreate(Bundle savedInstanceState) { // TODO: filter icons on map too
        super.onCreate(savedInstanceState);
        
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_google_maps);
        
        mWorld = MainScreenActivity.mWorld;
		route = MainScreenActivity.getCurrentRoute();
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		if (mMap == null){
			return;
		}
		
		MainScreenActivity.mGoogleMapPlugin.setGoogleMap(GoogleMapsActivity.mMap);

		// As we want to use GoogleMaps, we are going to create the plugin and
		// attach it to the World
		//mGoogleMapPlugin = new GoogleMapPlugin(this);
		// Then we need to set the map in to the GoogleMapPlugin
		//mGoogleMapPlugin.setGoogleMap(mMap);
		// Now that we have the plugin created let's add it to our world.
		// NOTE: It is better to load the plugins before start adding object in to the world.
		//mWorld.addPlugin(mGoogleMapPlugin);

		//mMap.setOnMarkerClickListener(this);

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainScreenActivity.mGoogleMapPlugin.getLatLng(), 15));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
               
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {
				GeoObject geoPlace = MainScreenActivity.mGoogleMapPlugin.getGeoObjectOwner(marker);
				if (geoPlace != null) {
					Intent intent = new Intent(getApplicationContext(), PlaceFullInfoActivity.class);
					intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE, geoPlace.getDistanceFromUser()/1000); // TODO: make it work
					intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, (int)geoPlace.getId());
					startActivity(intent);
				}
			}
		});
		
		MainScreenActivity.refreshVisibility();
		routeChanged();
    }
   
   @Override
   protected void onResume() {
        super.onResume();
        //MainScreenActivity.user.setVisible(true);
        //BeyondarLocationManager.enable();
        routeChanged();
   }
   @Override
   protected void onPause() {
	   super.onPause();
	   //BeyondarLocationManager.disable();
   }
   
   private void routeChanged() {
	   for (MainScreenActivity.Place p: MainScreenActivity.Places) {
		   MainScreenActivity.resetImage(p);
	   }
	   
	   if (routeLine != null) routeLine.remove();
	   
		if(!route.empty()){
           LatLng origin = MainScreenActivity.mGoogleMapPlugin.getLatLng();
           LatLng dest = new LatLng(route.getNext().getLatitude(), route.getNext().getLongitude());
        	 
        	NetworkInfo activeNetwork = ((ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        	if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {           
	           // Getting URL to the Google Directions API
	           String url = GoogleRouteHelper.getDirectionsUrl(origin, dest);
	
	           DownloadTask downloadTask = new DownloadTask();
	
	           // Start downloading json data from Google Directions API
	           downloadTask.execute(url);
        	}
       }
   }
}
