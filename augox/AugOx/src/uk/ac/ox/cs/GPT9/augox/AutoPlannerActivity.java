package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import uk.ac.ox.cs.GPT9.augox.R;
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
	private int oldCount;

	public static void getPlaces() {
		ArrayList localArrayList = new ArrayList();
		GalleryPickerFragment[] arrayOfGalleryPickerFragment = activities;
		int i = arrayOfGalleryPickerFragment.length;
		for (int j = 0;; j++) {
			if (j >= i) {
				MainScreenActivity.getCurrentRoute().setList(localArrayList);
				return;
			}
			GalleryPickerFragment localGalleryPickerFragment = arrayOfGalleryPickerFragment[j];
			if (localGalleryPickerFragment.getSelectedPlace() != null) {
				localArrayList.add(localGalleryPickerFragment
						.getSelectedPlace());
			}
		}
	}

	private void updateViewableActivities(int visibleActivities) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		for(int i = 0; i < 14; i++) {
		if (i < visibleActivities) {
			ft.show(activities[i]);
			Log.d("Joshua", "Showed gallery " + Integer.toString(i));
		}
		else {
			ft.hide(activities[i]);
			Log.d("Joshua", "Hid gallery " + Integer.toString(i));
		}
		}
			this.oldCount = visibleActivities;
			ft.commit();
	}

	public void onCreate(Bundle savedInstanceState) {
		Log.d("Joshua", "onCreate of AutoPlanner, before other stuff");
		super.onCreate(savedInstanceState);
		setContentView(2130903040);

		activities = new GalleryPickerFragment[14];
		LinearLayout galleryLayout = (LinearLayout) findViewById(2131492867);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Log.d("Joshua", "Before loop start");
		for (int i = 0; i < 14; i++) {
			activities[i] = GalleryPickerFragment.newInstance(i);
			Log.d("Joshua", "Added fragment to activities");
			ft.add(galleryLayout.getId(), activities[i]);
			Log.d("Joshua", "Added fragment to linear view");
		}
		ft.commit();
		Log.d("Joshua", "Committed changes");
		this.oldCount = 0;
		updateViewableActivities(0);
		this.activityCount = ((SeekBar) findViewById(2131492866));
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
			public void onClick(View paramAnonymousView) {
			}
		});
		Log.d("Joshua", "Finished onCreate");
		return;
	}

	public boolean onCreateOptionsMenu(Menu paramMenu) {
		getMenuInflater().inflate(2131427328, paramMenu);
		return true;
	}
}