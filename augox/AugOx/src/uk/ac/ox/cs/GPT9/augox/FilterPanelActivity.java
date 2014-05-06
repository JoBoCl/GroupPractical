package uk.ac.ox.cs.GPT9.augox;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class FilterPanelActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new FilterFragment()).commit();
	} 

	public static class FilterFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_filters);
		}
	}
	
	
	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filters);
		LinearLayout l = (LinearLayout) findViewById(R.id.filtersLayout);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = pref.edit();
        //final CheckBox checkAllCB = new CheckBox(getApplicationContext());
        final CheckBox visitedCB = new CheckBox(getApplicationContext());
        final CheckBox unvisitedCB = new CheckBox(getApplicationContext());
        //checkAllCB.setText("Enable All"); 
        visitedCB.setText("Visited"); 
        visitedCB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_visited",visitedCB.isChecked());
				editor.commit();
			}
		});
        unvisitedCB.setText("Unvisited");
        unvisitedCB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_unvisited",unvisitedCB.isChecked());
				editor.commit();
			}
		});
        visitedCB.setChecked(pref.getBoolean("filter_visited", true));
        unvisitedCB.setChecked(pref.getBoolean("filter_unvisited", true));
        l.addView(visitedCB); l.addView(unvisitedCB);
		for(final PlaceCategory cat : PlaceCategory.values()){
			if(cat.getID() != 0){
				final CheckBox cb = new CheckBox(getApplicationContext());
	            cb.setText(cat.getName());
	            cb.setChecked(pref.getBoolean(cat.getFilter(), true));
	    		
	    		cb.setOnClickListener(new View.OnClickListener() {
	    			@Override
	    			public void onClick(View v) {
	    				editor.putBoolean(cat.getFilter(),cb.isChecked());
	    				editor.commit();
	    			}
	    		});
	            l.addView(cb);
			}
		}
	} 
	*/
}
