package uk.ac.ox.cs.GPT9.augox;

import uk.ac.ox.cs.GPT9.augox.util.SystemUiHider;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainScreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    
    /*
	 * Single objects and accessors
	 */
	private static PlacesDatabase placesdatabase = new PlacesDatabase();
	public static PlacesDatabase getPlacesDatabase() { return placesdatabase; }
    
    /*
	 * Screen components the activity owns
	 */
	private RadarComponent radar;
	private RangeSliderComponent distanceslider;
	// Also BeyondAR stuff

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
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
            case R.id.action_dbg_databasedebugger:
            	Intent intent7 = new Intent(this, DatabaseDebuggerActivity.class);
                startActivity(intent7);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    
    /*
	 * Update viewing range (called back from the RangeSliderComponent)
	 */
	public void updateViewRange(double viewRange) {
	}
}
