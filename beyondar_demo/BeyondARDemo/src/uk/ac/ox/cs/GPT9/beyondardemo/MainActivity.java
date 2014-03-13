package uk.ac.ox.cs.GPT9.beyondardemo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.beyondar.android.view.BeyondarGLSurfaceView;
import com.beyondar.android.view.CameraView;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class MainActivity extends Activity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	
	private CameraView mCameraView;
	private BeyondarGLSurfaceView mBeyondarGLSurfaceView;
	private World world;
	private GeoObject museum = new GeoObject(1);
	private GeoObject rdb = new GeoObject(2);
	private GeoObject keble = new GeoObject(3);
	
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mBeyondarGLSurfaceView = (BeyondarGLSurfaceView) findViewById(R.id.customGLSurface);
        mCameraView = (CameraView) findViewById(R.id.camera);
        
        mLocationClient = new LocationClient(this, this, this);
        
        world = new World(this);
        world.setArViewDistance(10000);
        
        museum.setGeoPosition(51.758692, -1.255523);
        museum.setImageResource(R.drawable.red);
        museum.setName("Natural History Museum");
        world.addBeyondarObject(museum);
        
        museum.setGeoPosition(51.762360, -1.261361);
        rdb.setImageResource(R.drawable.green);
        rdb.setName("Ruth Deech Building");
        world.addBeyondarObject(rdb);
        
        keble.setGeoPosition(51.759065, -1.257919);
        keble.setImageResource(R.drawable.blue);
        keble.setName("Keble College");
        world.addBeyondarObject(keble);
        
        mBeyondarGLSurfaceView.setWorld(world);
    }
    
    @Override
    protected void onResume() {
         super.onResume();
         mBeyondarGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
         super.onPause();
         mBeyondarGLSurfaceView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_location:
            	updateUserLocation();
                return true;
            /*case R.id.action_dist:
            	showDistanceToTarget();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void updateUserLocation() {
    	mCurrentLocation = mLocationClient.getLastLocation();
        String dbg = String.format("%s | %s", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        Toast.makeText(this, dbg, Toast.LENGTH_LONG).show();
        world.setLocation(mCurrentLocation);
    }
    
    private void showDistanceToTarget() {
    	String dbg = String.format("%s", museum.getDistanceFromUser());
        Toast.makeText(this, dbg, Toast.LENGTH_LONG).show();
        Toast.makeText(this, museum.getScreenPositionCenter().toString(), Toast.LENGTH_LONG).show();
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
    
    @Override
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
    }
    
    @Override
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
    
}
