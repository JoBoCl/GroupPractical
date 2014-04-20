package uk.ac.ox.cs.GPT9.augox;

import com.beyondar.android.util.location.BeyondarLocationManager;
//import com.beyondar.android.view.BeyondarGLSurfaceView;
import com.beyondar.android.view.CameraView;
import com.beyondar.android.world.World;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import uk.ac.ox.cs.GPT9.augox.util.SystemUiHider;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainScreenActivity extends Activity /*implements*/ {
	//GooglePlayServicesClient.ConnectionCallbacks,
	//GooglePlayServicesClient.OnConnectionFailedListener {
   
	private CameraView mCameraView;
	//private BeyondarGLSurfaceView mBeyondarGLSurfaceView;
	private World world;
	
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
                
        //mBeyondarGLSurfaceView = (BeyondarGLSurfaceView) findViewById(R.id.customGLSurface);
        mCameraView = (CameraView) findViewById(R.id.camera);
        
        world = new World(this);
        world.setArViewDistance(((android.widget.SeekBar)findViewById(R.id.distanceSlider)).getProgress());
        BeyondarLocationManager.addWorldLocationUpdate(world);
        
        BeyondarLocationManager.setLocationManager((LocationManager) this.getSystemService(Context.LOCATION_SERVICE));
        
        //mBeyondarGLSurfaceView.setWorld(world);
        
        ((android.widget.SeekBar)findViewById(R.id.distanceSlider)).setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	@Override       
            public void onStopTrackingTouch(SeekBar seekBar) { }       

            @Override       
            public void onStartTrackingTouch(SeekBar seekBar) { }       

            @Override       
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {        
            	world.setArViewDistance(((android.widget.SeekBar)findViewById(R.id.distanceSlider)).getProgress());
            	// update radar distance too
            }       
        });
        
        com.beyondar.android.plugin.radar.RadarView radarView = (com.beyondar.android.plugin.radar.RadarView) findViewById(R.id.radarView);
        // Create the Radar module
        com.beyondar.android.plugin.radar.RadarWorldPlugin mRadarPlugin = new com.beyondar.android.plugin.radar.RadarWorldPlugin(this);
        // set the radar view in to our radar module
        mRadarPlugin.setRadarView(radarView);
        // Set how far (in meters) we want to display in the view
        mRadarPlugin.setMaxDistance(100);
        // and finally let's add the module
        world.addPlugin(mRadarPlugin);
    }
   
   @Override
   protected void onResume() {
        super.onResume();
        BeyondarLocationManager.enable();
        //mBeyondarGLSurfaceView.onResume();
   }

   @Override
   protected void onPause() {
        super.onPause();
        BeyondarLocationManager.disable();
        //mBeyondarGLSurfaceView.onPause();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.main_screen, menu);
       return true;
   }
   
   private void updateUserLocation() {
   	mCurrentLocation = mLocationClient.getLastLocation();
       String dbg = String.format("%s | %s", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
       Toast.makeText(this, dbg, Toast.LENGTH_LONG).show();
       world.setLocation(mCurrentLocation);
   }
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Decide what to do based on the original request code
       switch (requestCode) {
           case CONNECTION_FAILURE_RESOLUTION_REQUEST :
           /*
            * If the result code is Activity.RESULT_OK, try
            * to connect again
            */
               switch (resultCode) {
                   case Activity.RESULT_OK :
                   /*
                    * Try the request again
                    */
                   break;
               }            
       }
    }
   
   private boolean servicesConnected() {
       // Check that Google Play services is available
       int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
       // If Google Play services is available
       if (ConnectionResult.SUCCESS == resultCode) {
           // In debug mode, log the status
           Log.d("Location Updates",
                   "Google Play services is available.");
           // Continue
           return true;
       // Google Play services was not available for some reason
       } else {
       	/*
           // Get the error code
           int errorCode = ConnectionResult.getErrorCode();
           // Get the error dialog from Google Play services
           Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                   errorCode,
                   this,
                   CONNECTION_FAILURE_RESOLUTION_REQUEST);

           // If Google Play services can provide an error dialog
           if (errorDialog != null) {
           	// bluh
           }
           */
       	return false;
       }
   }
   
   /*@Override
   public void onConnected(Bundle dataBundle) {
       // Display the connection status
       Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
       
       updateUserLocation();
   }
   
   @Override
   public void onDisconnected() {
       // Display the connection status
       Toast.makeText(this, "Disconnected. Please re-connect.",
               Toast.LENGTH_SHORT).show();
   }*/
   
   //@Override
   public void onConnectionFailed(ConnectionResult connectionResult) {
       /*
        * Google Play services can resolve some errors it detects.
        * If the error has a resolution, try sending an Intent to
        * start a Google Play services activity that can resolve
        * error.
        */
       if (connectionResult.hasResolution()) {
           try {
               // Start an Activity that tries to resolve the error
               connectionResult.startResolutionForResult(
                       this,
                       CONNECTION_FAILURE_RESOLUTION_REQUEST);
               /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
           } catch (IntentSender.SendIntentException e) {
               // Log the error
               e.printStackTrace();
           }
       } else {
           /*
            * If no resolution is available, display a dialog to the
            * user with the error.
            */
           //showErrorDialog(connectionResult.getErrorCode());
       }
   }
   
   @Override
   protected void onStart() {
       super.onStart();
       // Connect the client.
       mLocationClient.connect();
   }
   
   @Override
   protected void onStop() {
       // Disconnecting the client invalidates it.
       mLocationClient.disconnect();
       super.onStop();
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
