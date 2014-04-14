package uk.ac.ox.cs.GPT9.augox;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

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

	private PlaceData[] choosePlaces(Session session) {
		PlaceCategory[] placecat = SessionHelper
				.getCategoriesForSession(session);
		DatabaseQuery plcat = new CategoryQuery(Arrays.asList(placecat));
		DatabaseQuery open = new OpenAtQuery(
				SessionHelper.getStartTimeForSession(session));
		DatabaseQuery and = new AndQuery(plcat, open);
		List<PlaceData> places = MainScreenActivity.getPlacesDatabase()
				.queryFetchPlaces(and);
		PlaceData[] chosenPlaces = choosePlaces(places);
		return chosenPlaces;
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
	}

}
