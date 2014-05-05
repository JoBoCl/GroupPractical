package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.Arrays;
import uk.ac.ox.cs.*;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.AndQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.OpenAtQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.NameSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.SortOrder;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class SessionPlanner extends Fragment {

	private Session activity;
	private PlaceCategory category;

	public SessionPlanner(Session activity) {
		this.activity = activity;
	}

	SeekBar activityCount;
	TextView activityText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.session_planner, container, false);
	}


}
