package uk.ac.ox.cs.GPT9.augox;

import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.maps.GoogleMap;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class MainScreenActivity extends FragmentActivity /*implements OnClickListener*/ {
   
	private RadarView mRadarView;
	private RadarWorldPlugin mRadarPlugin;
	private World mWorld;
	private GoogleMap mMap;
	private GoogleMapWorldPlugin mGoogleMapPlugin;

	private SeekBar mSeekBarMaxDistance;
	
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_screen);
        
        mWorld = new World(this);
        mWorld.setArViewDistance(100);
		
		GeoObject user = new GeoObject(1000l);
		user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
		user.setImageResource(R.drawable.radar_north_small);
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

    }
   
   private void initializeGMaps() {
		mGoogleMapPlugin = new GoogleMapWorldPlugin(this);
		mGoogleMapPlugin.setGoogleMap(mMap);
        mWorld.addPlugin(mGoogleMapPlugin);
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
    
    /*
	 * Single objects and accessors
	 */
	private static PlacesDatabase placesdatabase = new PlacesDatabase();
	public static PlacesDatabase getPlacesDatabase() { return placesdatabase; }
	private static PlaceCategoryService placecategoryservice = new PlaceCategoryService();
	public static PlaceCategoryService getPlaceCategoryService() { return placecategoryservice; }
    
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
    	// DEBUG VERSION
        switch (item.getItemId()) {
            case R.id.action_dbg_placefullinfo:
            	Intent intent1 = new Intent(this, PlaceFullInfoActivity.class);
                intent1.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, "");
                intent1.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, "");
                startActivity(intent1);
                return true;
            case R.id.action_dbg_listplaces:
            	Intent intent2 = new Intent(this, ListPlacesActivity.class);
            	// debug values: CS dept entrance!
            	intent2.putExtra(ListPlacesActivity.EXTRA_LATITUDE, 51.760039);
            	intent2.putExtra(ListPlacesActivity.EXTRA_LONGITUDE, -1.258464);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
    
}
