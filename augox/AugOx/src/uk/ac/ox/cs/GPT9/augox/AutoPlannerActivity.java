
package uk.ac.ox.cs.GPT9.augox;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

public class AutoPlannerActivity extends FragmentActivity {

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
	private int startHour; private int startMinute;
	private int endHour; private int endMinute;
	private final int[] intervals = new int[]{8*60,9*60,12*60+30,13*60+30,18*60,19*60,22*60};

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

	}

	// display current time
	public void setCurrentTimeOnView() {


		final Calendar c = Calendar.getInstance();
		startHour = c.get(Calendar.HOUR_OF_DAY); startMinute = c.get(Calendar.MINUTE);
		endHour = c.get(Calendar.HOUR_OF_DAY); endMinute = c.get(Calendar.MINUTE);

		// set current time into textview
		startTime.setText(String.format("Start time: %02d:%02d", startHour, startMinute));
		endTime.setText(String.format("End time: %02d:%02d", startHour, startMinute));
	}

	public void addListenerOnButton() {
        OnClickListener listener = new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				//FragmentManager fm = getSupportFragmentManager();
				//TimePickerDialog tpd = new TimePickerDialog(getBaseContext(), timePickerListener, hour, minute, false);
				//tpd.show(fm, "fragment_edit_time");

				showDialog(v==startTime ? START_TIME_DIALOG_ID : v==endTime ? END_TIME_DIALOG_ID : null);
		}

		};

		startTime.setOnClickListener(listener);
		endTime.setOnClickListener(listener);

	}

	private void updateGalleries() {
		int galleries = findIntervals();
		
	}
	
	private int findIntervals() {
		int sessions = 0;
		int i = 0;
		int n = intervals.length;
		while(i<n) {
			if(startHour*60+startMinute < intervals[i] && intervals[i] < endHour*60+endMinute ) sessions++;
			i++;
		}	
	return sessions;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, timePickerListenerStart, startHour, startMinute, false);
		case END_TIME_DIALOG_ID:
			return new TimePickerDialog(this, timePickerListenerEnd, endHour, endMinute, false);
		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener timePickerListenerEnd = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
			endHour = selectedHour;
			endMinute = selectedMinute;

			// set current time into textview
			endTime.setText(String.format("End time: %02d:%02d", selectedHour, selectedMinute)); 
			updateGalleries();
	}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListenerStart = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
			startHour = selectedHour;
			startMinute = selectedMinute;

			// set current time into textview
			startTime.setText(String.format("Start time: %02d:%02d", selectedHour, selectedMinute));
	
			updateGalleries();
		}
	};
}
