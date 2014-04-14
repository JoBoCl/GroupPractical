package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.R.layout;
import uk.ac.ox.cs.GPT9.augox.dbquery.AndQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.OpenAtQuery;

import android.os.Bundle;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

public class AutoPlannerActivity extends Activity {

	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * setContentView(R.layout.activity_auto_planner); }
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.auto_planner, menu);
		return true;
	}

	private Button startTime, endTime;
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	private List<Fragment> spfs;
	private final int[] intervals = new int[] { 8 * 60, 9 * 60, 12 * 60 + 30,
			13 * 60 + 30, 18 * 60, 19 * 60, 22 * 60, 8 * 60 + 24 * 60,
			9 * 60 + 24 * 60, 12 * 60 + 30 + 24 * 60, 13 * 60 + 30 + 24 * 60,
			18 * 60 + 24 * 60, 19 * 60 + 24 * 60, 22 * 60 };

	static final int START_TIME_DIALOG_ID = 999;
	static final int END_TIME_DIALOG_ID = 998;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_planner);

		startTime = (Button) findViewById(R.id.startTimeButton);
		endTime = (Button) findViewById(R.id.endTimeButton);

		setCurrentTimeOnView();
		addListenerOnButton();

		spfs = new ArrayList<Fragment>();

	}

	// display current time
	public void setCurrentTimeOnView() {

		final Calendar c = Calendar.getInstance();
		startHour = c.get(Calendar.HOUR_OF_DAY);
		startMinute = c.get(Calendar.MINUTE);
		endHour = c.get(Calendar.HOUR_OF_DAY);
		endMinute = c.get(Calendar.MINUTE);

		// set current time into textview
		startTime.setText(String.format("Start time: %02d:%02d", startHour,
				startMinute));
		endTime.setText(String.format("End time: %02d:%02d", startHour,
				startMinute));
	}

	public void addListenerOnButton() {
		OnClickListener listener = new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(v == startTime ? START_TIME_DIALOG_ID
						: v == endTime ? END_TIME_DIALOG_ID : null);
			}

		};
		startTime.setOnClickListener(listener);
		endTime.setOnClickListener(listener);

	}

	private void updateGalleries() {
		int galleries = findIntervals();
		List<Session> sessions = new ArrayList<Session>();
		int startTime = startHour * 60 + startMinute;
		int endTime = endHour * 60 + endMinute;
		for (int time : intervals) {
			if (startTime < time && time < endTime)
				sessions.add(SessionHelper.getSessionAtTime(time));
		}
		Session[] ss = new Session[] { Session.BREAKFAST, Session.MORNING,
				Session.LUNCH, Session.AFTERNOON, Session.TEA, Session.EVENING,
				Session.NIGHT, Session.BREAKFAST, Session.MORNING,
				Session.LUNCH, Session.AFTERNOON, Session.TEA, Session.EVENING,
				Session.NIGHT };
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		int j = 0;
		for (int i = 0; i < ss.length; i++) {
			/*
			 * PlaceCategory[] placecat =
			 * SessionHelper.getCategoriesForSession(session); DatabaseQuery
			 * plcat = new CategoryQuery(Arrays.asList(placecat)); DatabaseQuery
			 * open = new OpenAtQuery(
			 * SessionHelper.getStartTimeForSession(session)); DatabaseQuery and
			 * = new AndQuery(plcat, open); List<PlaceData> places =
			 * MainScreenActivity.getPlacesDatabase() .queryFetchPlaces(and);
			 * PlaceData[] chosenPlaces = choosePlaces(places);
			 */
			Session session = ss[i];
			if (j < sessions.size() && sessions.get(j) == session) {
				Fragment spf = (Fragment) new SessionPlanner(session);
				ft.add(R.id.sessionList, spf,
						"session planner " + session.toString()
								+ (i > ss.length / 2 ? "2" : "1"));
				spfs.add(spf);
				j++;
			} else {
				Fragment f = getFragmentManager().findFragmentByTag(
						"session planner " + session.toString()
								+ (i > ss.length / 2 ? "2" : "1"));
				if (f != null)
					ft.remove(f);
			}
		}
		ft.commit();
	}

	private int findIntervals() {
		int sessions = 0;
		int i = 0;
		int n = intervals.length;
		int startTime = startHour * 60 + startMinute;
		int endTime = endHour * 60 + endMinute;
		// if the end time is "before" the start time, assume it runs until the
		// next day.
		endTime += endTime < startTime ? 36 * 24 : 0;
		while (i < n) {
			if (startTime < intervals[i] && intervals[i] < endTime)
				sessions++;
			i++;
		}
		return sessions;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case START_TIME_DIALOG_ID:
				// set time picker as current time
			return new TimePickerDialog(this, startTimePickerListener,
						startHour, startMinute, false);
			case END_TIME_DIALOG_ID:
			return new TimePickerDialog(this, endTimePickerListener, endHour,
					endMinute, false);
		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			endHour = selectedHour;
			endMinute = selectedMinute;

			// set
			// current
			// time
			// into
			// textview
			endTime.setText(String.format("End time: %02d:%02d", selectedHour,
					selectedMinute));
			updateGalleries();
		}
	};

	private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			startHour = selectedHour;
			startMinute = selectedMinute;

			// set
			// current
			// time
			// into
			// textview
			startTime.setText(String.format("Start time: %02d:%02d",
					selectedHour, selectedMinute));

			updateGalleries();
		}
	};
}
