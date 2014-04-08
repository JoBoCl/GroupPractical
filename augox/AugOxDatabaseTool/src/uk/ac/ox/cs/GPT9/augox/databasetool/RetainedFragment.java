package uk.ac.ox.cs.GPT9.augox.databasetool;

import uk.ac.ox.cs.GPT9.augox.PlacesDatabase;
import android.app.Fragment;
import android.os.Bundle;

/**
 * Handles activity reload preserved data.
 */

public class RetainedFragment extends Fragment {
	private PlacesDatabase db;
	private String outputpanecontent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	public void setData(PlacesDatabase db, String outputpanecontent) {
		this.db = db;
		this.outputpanecontent = outputpanecontent;
	}
	
	public PlacesDatabase getDB() {
		return db;
	}
	
	public String getOutputPaneContent() {
		return outputpanecontent;
	}
}