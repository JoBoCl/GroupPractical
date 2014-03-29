package uk.ac.ox.cs.GPT9.augox;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PlaceFullInfoActivity extends Activity {
	/*
	 * Intent Constants
	 */
	public final static String EXTRA_PLACE = "uk.ac.ox.cs.GPT9.augox.PLACE";
	public final static String EXTRA_BACKGROUND = "uk.ac.ox.cs.GPT9.augox.BACKGROUND";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_full_info);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_full_info, menu);
		return true;
	}

}
