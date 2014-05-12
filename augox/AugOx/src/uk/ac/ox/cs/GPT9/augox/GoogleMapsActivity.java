package uk.ac.ox.cs.GPT9.augox;

import uk.ac.ox.cs.GPT9.augox.GoogleRouteHelper.DownloadTask;
import uk.ac.ox.cs.GPT9.augox.route.IRoute;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class GoogleMapsActivity extends FragmentActivity implements OnMarkerClickListener{
   
	private IRoute route;
	private World mWorld;
	public static GoogleMap mMap;
	private GoogleMapWorldPlugin mGoogleMapPlugin;
	
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_google_maps);
        
        mWorld = MainScreenActivity.mWorld;
		route = MainScreenActivity.getCurrentRoute();
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		if (mMap == null){
			return;
		}

		// As we want to use GoogleMaps, we are going to create the plugin and
		// attach it to the World
		mGoogleMapPlugin = new GoogleMapWorldPlugin(this);
		// Then we need to set the map in to the GoogleMapPlugin
		mGoogleMapPlugin.setGoogleMap(mMap);
		// Now that we have the plugin created let's add it to our world.
		// NOTE: It is better to load the plugins before start adding object in to the world.
		mWorld.addPlugin(mGoogleMapPlugin);

		//mMap.setOnMarkerClickListener(this);

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mGoogleMapPlugin.getLatLng(), 15));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
		
		// Lets add the user position
		GeoObject user = new GeoObject(12);
		user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
		user.setImageResource(R.drawable.arrowicon); // TODO give user an oriented custom icon
		user.setName("User position");
		mWorld.addBeyondarObject(user);
		
		if(!route.empty()){
            LatLng origin = mGoogleMapPlugin.getLatLng();
            LatLng dest = new LatLng(route.getNext().getLatitude(), route.getNext().getLongitude());

            // Getting URL to the Google Directions API
            String url = GoogleRouteHelper.getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
               
    }

	@Override
	public boolean onMarkerClick(Marker marker) {
		// To get the GeoObject that owns the marker we use the following
		// method:
		GeoObject geoObject = mGoogleMapPlugin.getGeoObjectOwner(marker);
		if (geoObject != null) {
			Toast.makeText(this,
					"Click on a marker owned by a GeoOject with the name: " + geoObject.getName(),
					Toast.LENGTH_SHORT).show();
			finish();
		}
		return false;
	}
}
