package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.route.IRoute;
import uk.ac.ox.cs.GPT9.augox.route.Route;
import android.annotation.TargetApi;
import android.os.Bundle;
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

@TargetApi(11)
public class AutoPlannerActivity extends FragmentActivity {
	private static Integer[] _route;

	private static GalleryPickerFragment[] activities;

	private SeekBar activityCount;

	private static boolean allowRepeats = false;
	private CheckBox allowRepeatsCheckbox;

	private static boolean allowVisited = false;
	private RadioGroup visitedUnvisitedPicker;

	private Button finished;

	private int lastVisible = 0;

	// Note: accurate to 50m.
	private static double maxDistance = 0.0f;
	private SeekBar routeDistance;

	private static float minRating = 0;
	private RatingBar minRatingBar;

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

	/**
	 * @return the allowVisited
	 */
	public static boolean allowingVisited() {
		return allowVisited;
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
		
		_route = new Integer[getResources().getInteger(R.integer.activity_limit)];

		activities = new GalleryPickerFragment[getResources().getInteger(
				R.integer.activity_limit)];
		LinearLayout galleryLayout = (LinearLayout) findViewById(R.id.galleryHolders);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Log.d("Joshua", "Before loop start");
		for (int i = 0; i < getResources().getInteger(R.integer.activity_limit); i++) {
			_route[i] = -1;
			activities[i] = GalleryPickerFragment.newInstance(i);
			Log.d("Joshua", "Added fragment to activities");
			ft.add(galleryLayout.getId(), activities[i]);
			Log.d("Joshua", "Added fragment to linear view");
		}
		ft.commit();
		Log.d("Joshua", "Committed changes");
		updateViewableActivities(0);
		activityCount = ((SeekBar) findViewById(R.id.activityCount));
		activityCount
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
							int newValue, boolean userChange) {
						updateViewableActivities(newValue);
					}

					public void onStartTrackingTouch(
							SeekBar paramAnonymousSeekBar) {
					}

					public void onStopTrackingTouch(
							SeekBar paramAnonymousSeekBar) {
					}
				});
		finished = ((Button) findViewById(R.id.routeFinished));
		finished.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				MainScreenActivity.getCurrentRoute().setList(Arrays.asList(_route));
				
				finish();
			}
		});
		Log.d("Joshua", "Finished onCreate");

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

		visitedUnvisitedPicker = (RadioGroup) findViewById(R.id.visitedUnvisitedPicker);
		visitedUnvisitedPicker
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.visited:
							allowVisited = true;
							break;

						case R.id.unvisited:
							allowVisited = false;
							break;
						}
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
		// Currently set to 4km
		routeDistance.setMax(80);
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
		for (int i = 0; i < getResources().getInteger(R.integer.activity_limit); i++) {
				activities[i].preferencesUpdated();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.auto_planner, menu);
		return true;
	}

	private void updateViewableActivities(int visibleActivities) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		for (int i = 0; i < getResources().getInteger(R.integer.activity_limit); i++) {
			if (i < visibleActivities) {
				ft.show(activities[i]);
				Log.d("Joshua", "Showed gallery " + Integer.toString(i));
			} else {
				ft.hide(activities[i]);
				Log.d("Joshua", "Hid gallery " + Integer.toString(i));
			}
		}
		ft.commit();
		lastVisible = visibleActivities;
	}

	public static void updatePlace(int index) {
		Log.d("updatePlace", "Gallery " + Integer.toString(index) + " sends update " + Integer.toString(activities[index].getSelectedPlace()));
		_route[index] = activities[index].getSelectedPlace();
		Log.d("updatePlace", Arrays.toString(activities));
	}
}