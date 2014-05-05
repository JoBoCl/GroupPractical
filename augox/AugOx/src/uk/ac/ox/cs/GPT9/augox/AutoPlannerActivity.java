package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

@TargetApi(11)
public class AutoPlannerActivity extends FragmentActivity {
	private static GalleryPickerFragment[] activities;
	private SeekBar activityCount;
	private Button finished;

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
	}

	public void onCreate(Bundle savedInstanceState) {
		Log.d("Joshua", "onCreate of AutoPlanner, before other stuff");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_planner);

		activities = new GalleryPickerFragment[getResources().getInteger(
				R.integer.activity_limit)];
		LinearLayout galleryLayout = (LinearLayout) findViewById(R.id.galleryHolders);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Log.d("Joshua", "Before loop start");
		for (int i = 0; i < getResources().getInteger(R.integer.activity_limit); i++) {
			activities[i] = GalleryPickerFragment.newInstance(i);
			Log.d("Joshua", "Added fragment to activities");
			ft.add(galleryLayout.getId(), activities[i]);
			Log.d("Joshua", "Added fragment to linear view");
		}
		ft.commit();
		Log.d("Joshua", "Committed changes");
		updateViewableActivities(0);
		this.activityCount = ((SeekBar) findViewById(R.id.activityCount));
		this.activityCount
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
							int newValue, boolean userChange) {
						AutoPlannerActivity.this
								.updateViewableActivities(newValue);
					}

					public void onStartTrackingTouch(
							SeekBar paramAnonymousSeekBar) {
					}

					public void onStopTrackingTouch(
							SeekBar paramAnonymousSeekBar) {
					}
				});
		this.finished = ((Button) findViewById(R.id.routeFinished));
		this.finished.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				getPlaces();
				finish();
			}
		});
		Log.d("Joshua", "Finished onCreate");
		return;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.auto_planner, menu);
		return true;
	}
}