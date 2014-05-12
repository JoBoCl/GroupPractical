package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.AndQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.InLocusQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.NotQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.RatingRangeQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.VisitedQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.NameSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.SortOrder;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

@TargetApi(11)
public class GalleryPickerFragment extends Fragment {

	private RadioGroup categoryChoice;

	private RadioButton[] chosenPlace = new RadioButton[3];
	private int lastChecked;
	private int offset;
	private CheckBox ownPlan;
	private int[] placeIds = new int[3];
	private PlaceCategory currentCat;
	private ImageView[] placeImages = new ImageView[3];

	private PlaceData[] places = new PlaceData[3];

	public static GalleryPickerFragment newInstance(int offset) {
		GalleryPickerFragment localGalleryPickerFragment = new GalleryPickerFragment();
		Bundle localBundle = new Bundle();
		localBundle.putInt("offset", offset);
		localGalleryPickerFragment.setArguments(localBundle);
		return localGalleryPickerFragment;
	}

	public void preferencesUpdated() {
		allowRepeats = AutoPlannerActivity.areRepeatsAllowed();
		allowVisited = AutoPlannerActivity.allowingVisited();
		maxDistance = AutoPlannerActivity.getMaxDistance();
		minRating = AutoPlannerActivity.getMinRating();

		placeIds = choosePlaces(currentCat);
		matchPlaceIds();
		updateUiElements();
	}

	private boolean allowRepeats = false;

	private boolean allowVisited = false;

	private double maxDistance = 0.0f;

	private float minRating = 0;

	public Integer getSelectedPlace() {
		if (ownPlan.isChecked()) {
			return null;
		}
		return placeIds[lastChecked];
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_gallery_picker,
				container, false);
		offset = getArguments().getInt("offset");
		Log.d("Joshua", "Get offset from args");
		placeImages[0] = ((ImageView) view.findViewById(R.id.placeImage0));
		placeImages[1] = ((ImageView) view.findViewById(R.id.placeImage1));
		placeImages[2] = ((ImageView) view.findViewById(R.id.placeImage2));
		Log.d("Joshua", "Get text descriptions");
		chosenPlace[0] = ((RadioButton) view.findViewById(R.id.chosenPlace0));
		chosenPlace[1] = ((RadioButton) view.findViewById(R.id.chosenPlace1));
		chosenPlace[2] = ((RadioButton) view.findViewById(R.id.chosenPlace2));
		Log.d("Joshua", "Get radio buttons");

		Calendar localTime = Calendar.getInstance();
		LocalTime start = new LocalTime(localTime.YEAR, localTime.MONTH,
				localTime.DATE, localTime.HOUR + offset, 0);
		TextView startTime = (TextView) view.findViewById(R.id.startTime);
		startTime.setText(String.format("Start Time - %d:00", start.getHour()));

		OnClickListener placeClickedListener = new OnClickListener() {
			public void onClick(View view) {
				for (int i = 0; i < 3; i++) {
					if ((view == chosenPlace[i]) || (view == placeImages[i])) {
						chosenPlace[i].setChecked(true);
						lastChecked = i;
					} else
						chosenPlace[i].setChecked(false);
				}
				updateAutoPlanner();
			}
		};

		for (int i = 0; i < 3; i++) {
			chosenPlace[i].setOnClickListener(placeClickedListener);
			placeImages[i].setOnClickListener(placeClickedListener);
		}

		final ArrayList<RadioButton> categoryRadioButtons;
		categoryChoice = ((RadioGroup) view.findViewById(R.id.categoryChoice));
		categoryChoice.setOrientation(RadioGroup.HORIZONTAL);
		Log.d("Joshua", "Get category holder");
		final List<PlaceCategory> categoryList = new ArrayList<PlaceCategory>(
				EnumSet.allOf(PlaceCategory.class));
		Log.d("Joshua", "Create list of categories");
		categoryRadioButtons = new ArrayList<RadioButton>();
		Log.d("Joshua", "Create list of category buttons");

		for (PlaceCategory cat : categoryList) {
			RadioButton catRadioButton = new RadioButton(getActivity());
			catRadioButton.setText(cat.getName());
			Log.d("Joshua", "Create category button for " + cat.toString());
			categoryRadioButtons.add(catRadioButton);
			if (cat != PlaceCategory.UNKNOWN) {
				categoryChoice.addView(catRadioButton);
				Log.d("Joshua", "Add category button for " + cat.toString());
			}
		}

		currentCat = PlaceCategory.UNKNOWN;

		categoryChoice
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						for (RadioButton button : categoryRadioButtons) {
							if (button == (RadioButton) view
									.findViewById(checkedId)) {
								int i = categoryRadioButtons.indexOf(button);
								PlaceCategory cat = categoryList.get(i);
								currentCat = cat;
								placeIds = choosePlaces(cat);
								matchPlaceIds();
								updateUiElements();
							}
						}
					}
				});

		((RadioButton) categoryRadioButtons.get(0)).setChecked(true);
		Log.d("Joshua", "Set checked boolean");
		placeIds = choosePlaces((PlaceCategory) categoryList.get(0));
		matchPlaceIds();
		Log.d("Joshua", "Create list of places");
		updateUiElements();
		ownPlan = ((CheckBox) view.findViewById(R.id.missSession));
		ownPlan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton checkBox,
					boolean isChecked) {
				for (int i = 0; i < 3; i++) {
					chosenPlace[i].setEnabled(!isChecked);
					placeImages[i].setEnabled(!isChecked);
				}
			}
		});

		return view;
	}

	private void updateAutoPlanner() {
		AutoPlannerActivity.updatePlace(offset);
	}

	private int[] choosePlaces(List<Integer> idList) {
		int[] chosenPlaceIds = new int[3];
		for (int i = 0; i < 3; i++) {
			chosenPlaceIds[i] = -1;
		}
		int chosen = 0;
		finished: if (allowRepeats) {
			for (int id : idList) {
				chosenPlaceIds[chosen] = id;
				chosen++;
				if (chosen == 3)
					break finished;
			}
		} else {
			for (int id : idList)
				if (!plannedRouteContainsAfter(id)) {
					chosenPlaceIds[chosen] = id;
					chosen++;
					if (chosen == 3)
						break finished;
				}
		}

		return chosenPlaceIds;
	}

	private boolean plannedRouteContainsAfter(int id) {
		Integer[] places = AutoPlannerActivity.getPlannedRoute();
		for(int i = offset+1; i < getResources().getInteger(R.integer.activity_limit); i++) {
			if(places[i] == id) return true;
		}

		return false;
	}

	private int[] choosePlaces(PlaceCategory category) {
		DatabaseQuery query = new CategoryQuery(
				Collections.singletonList(category));

		if (allowVisited)
			query = new AndQuery(query, new VisitedQuery());
		else
			query = new AndQuery(query, new NotQuery(new VisitedQuery()));

		query = new AndQuery(query, new RatingRangeQuery((int) minRating, 5));

		// Lat at [0], long at [1]
		query = new AndQuery(query, new InLocusQuery(
				MainScreenActivity.getUserLocation()[0],
				MainScreenActivity.getUserLocation()[1], maxDistance));

		List<Integer> idList = MainScreenActivity.getPlacesDatabase().query(
				query, new NameSorter(SortOrder.ASC));

		return choosePlaces(idList);
	}

	private void matchPlaceIds() {
		for (int i = 0; i < 3; i++)
			places[i] = MainScreenActivity.getPlacesDatabase().getPlaceByID(
					placeIds[i]);
	}

	private void updateUiElements() {
		for (int j = 0; j < 3; j++) {
			if (placeIds[j] == -1) {
				chosenPlace[j].setText("No suggestion found");
				placeImages[j].setImageDrawable(getResources().getDrawable(
						R.drawable.common_signin_btn_icon_disabled_dark));
			} else {
				chosenPlace[j].setText(places[j].getName());
				placeImages[j].setImageDrawable(places[j].getImage());
				placeImages[j].setVisibility(View.VISIBLE);
			}
			Log.d("Joshua", "Set text for place");
		}
	}
}