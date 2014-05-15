package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ox.cs.GPT9.augox.route.IRoute;
import uk.ac.ox.cs.GPT9.augox.route.Route;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

@TargetApi(11)
public class AutoPlannerActivity extends FragmentActivity {
	private static Integer[] _route;

	private static GalleryPickerFragment[] activities;

	private SeekBar activityCount;

	// ADT seenPlaces : Integer -> {Integer}
	//     seenPlaces(i) = activities[i],places
	// for all x in seenPlaces(i), for all y in seenPlaces(j), for i != j, x != y
	private static Map<Integer, Integer[]> seenPlaces;

	// Returns codomain of seenPlaces
	public static List<Integer> getSeenPlaces() {
		List<Integer> values = new ArrayList<Integer>();
		for (Integer[] placeIds : seenPlaces.values())
			for (Integer placeId : placeIds)
				values.add(placeId);

		return values;
	}

	private static boolean allowRepeats = false;
	private CheckBox allowRepeatsCheckbox;

	private static boolean allowVisited = true;
	private static boolean allowUnvisited = true;

	private Button finished;

	// Note: accurate to 50m.
	private static double maxDistance = 0.0f;
	private SeekBar routeDistance;

	private static float minRating = 0;
	private RatingBar minRatingBar;

	private CheckBox allowVisitedCheckbox;

	private CheckBox allowUnvisitedCheckbox;

	private int ACTIVITY_LIMIT;

	// Update MSA with places chosen by the user
	public static void getPlaces() {
		List<Integer> routeList = new ArrayList<Integer>();
		for (int j = 0; j < activities.length; j++) {
			GalleryPickerFragment localGalleryPickerFragment = activities[j];
			if (localGalleryPickerFragment.getSelectedPlace() != null) {
				routeList.add(localGalleryPickerFragment.getSelectedPlace());
			}
		}
		MainScreenActivity.getCurrentRoute().setList(routeList);
	}

	public static Integer[] getPlannedRoute() {
		return _route;
	}

	public static boolean allowingVisited() {
		return allowVisited;
	}

	public static boolean allowingUnvisited() {
		return allowUnvisited;
	}

	public static boolean areRepeatsAllowed() {
		return allowRepeats;
	}

	/**
	 * @return the maxDistance
	 */
	public static double getMaxDistance() {
		return maxDistance;
	}

	/**
	 * @return the minRating
	 */
	public static float getMinRating() {
		return minRating;
	}

	public void onCreate(Bundle savedInstanceState) {
		Log.d("Joshua", "onCreate of AutoPlanner, before other stuff");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_planner);

		// Access single object for shared preferences
		SharedPreferences sharedPref = MainScreenActivity.getSharedPref();
		
		// Update activity limit for use across class
		ACTIVITY_LIMIT = ((int) (Integer.parseInt(sharedPref.getString(
				"setting_autoroute_max_length", "1"))));

		// Initialise new local blank route
		_route = new Integer[ACTIVITY_LIMIT];

		// Initialise new list of places that have been seen and ignored by the user
		seenPlaces = new TreeMap<Integer, Integer[]>();

		//
		activities = new GalleryPickerFragment[ACTIVITY_LIMIT];
		LinearLayout galleryLayout = (LinearLayout) findViewById(R.id.galleryHolders);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		Log.d("Joshua", "Before loop start");

		// Add gallery pickers to the activity
		for (int i = 0; i < ACTIVITY_LIMIT; i++) {
			_route[i] = -1;
			activities[i] = GalleryPickerFragment.newInstance(i);
			Log.d("Joshua", "Added fragment to activities");
			ft.add(galleryLayout.getId(), activities[i]);
			Log.d("Joshua", "Added fragment to linear view");
		}
		ft.commit();
		Log.d("Joshua", "Committed changes");

		// Hide all galleries to ensure correct initialisation
		updateViewableActivities(0);

		Log.d("Joshua", sharedPref.toString());

		// Set up slider
		activityCount = ((SeekBar) findViewById(R.id.activityCount));
		activityCount.setMax(ACTIVITY_LIMIT);
		activityCount
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
							int newValue, boolean userChange) {
						// Update number of visible activities
						updateViewableActivities(newValue);
					}

					public void onStartTrackingTouch(
							SeekBar paramAnonymousSeekBar) {
					}

					public void onStopTrackingTouch(
							SeekBar paramAnonymousSeekBar) {
					}
				});
				
		// Set up finished button
		finished = ((Button) findViewById(R.id.routeFinished));
		finished.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				MainScreenActivity.getCurrentRoute().setList(
						Arrays.asList(_route));
				Toast.makeText(getApplicationContext(),
						"Route created, click the radar to view",
						Toast.LENGTH_LONG).show();
				finish();
			}
		});
		Log.d("Joshua", "Finished onCreate");

		// Set up filters
		allowRepeatsCheckbox = (CheckBox) findViewById(R.id.allowRepeats);
		allowRepeatsCheckbox.setChecked(false);
		allowRepeatsCheckbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						allowRepeats = !allowRepeats;
						updateGalleries();
					}
				});

		allowVisitedCheckbox = (CheckBox) findViewById(R.id.visited);
		allowVisitedCheckbox.setChecked(true);
		allowVisitedCheckbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						allowVisited = !allowVisited;
						updateGalleries();
					}
				});

		allowUnvisitedCheckbox = (CheckBox) findViewById(R.id.unvisited);
		allowUnvisitedCheckbox.setChecked(true);
		allowUnvisitedCheckbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						allowUnvisited = !allowUnvisited;
						updateGalleries();
					}
				});

		minRatingBar = (RatingBar) findViewById(R.id.routeMinimumRating);
		minRatingBar
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						minRating = rating;
						updateGalleries();
					}
				});

		routeDistance = (SeekBar) findViewById(R.id.routeDistance);
		// Initialise routeDistance based on preferences
		routeDistance.setMax((int) (Float.parseFloat(sharedPref.getString(
				"setting_arview_max_distance", "1")) * 20));
		// Set starting value to halfway mark
		routeDistance.setProgress((int) (Float.parseFloat(sharedPref.getString(
				"setting_arview_max_distance", "1")) * 10));
		routeDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				maxDistance = progress / 20.0;
				updateGalleries();
			}
		});

		return;
	}

	private void updateGalleries() {
		// Get new filter values
		for (int i = 0; i < ACTIVITY_LIMIT; i++) {
			activities[i].preferencesUpdated();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.auto_planner, menu);
		return true;
	}

	private void updateViewableActivities(int visibleActivities) {
		// Show/hide galleries as user increases/decreases number of choices
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		for (int i = 0; i < ACTIVITY_LIMIT; i++) {
			if (i < visibleActivities) {
				ft.show(activities[i]);
				Log.d("Joshua", "Showed gallery " + Integer.toString(i));
			} else {
				ft.hide(activities[i]);
				Log.d("Joshua", "Hid gallery " + Integer.toString(i));
			}
		}
		ft.commit();
	}

	public static void updatePlace(int index) {
		// Update route
		Log.d("updatePlace",
				"Gallery "
						+ Integer.toString(index)
						+ " sends update "
						+ Integer.toString(activities[index].getSelectedPlace()));
		_route[index] = activities[index].getSelectedPlace();
		Log.d("updatePlace", Arrays.toString(activities));
	}

	public static void updateSeenPlaces(Integer offset, Integer[] chosenPlaceIds) {
		// seenPlaces = seenPlaces_0 (+) offset -> chosenPlaceIds
		seenPlaces.put(offset, chosenPlaceIds);
	}

	public static double getPreviousLongitude(int offset) {
		// For the gallery at offset, return the location of the place chosen by the gallery at offset-1
		return MainScreenActivity.getPlacesDatabase()
				.getPlaceByID(activities[offset - 1].getSelectedPlace())
				.getLongitude();
	}

	public static double getPreviousLatitude(int offset) {
		// For the gallery at offset, return the location of the place chosen by the gallery at offset-1
		return MainScreenActivity.getPlacesDatabase()
				.getPlaceByID(activities[offset - 1].getSelectedPlace())
				.getLatitude();
	}
}
