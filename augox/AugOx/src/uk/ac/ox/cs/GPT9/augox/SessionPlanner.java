package uk.ac.ox.cs.GPT9.augox;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SessionPlanner extends FragmentActivity {

	SeekBar activityCount;
	TextView activityText;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    activityCount = (SeekBar) findViewById(R.id.activityCount);
	    activityText = (TextView) findViewById(R.id.activityText);
	    
	    activityCount.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	    	@Override
	    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	    		activityText.setText(String.format("How many things do you want to do?\n%d chosen", progress));
	    	}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
	    });
	}

}
