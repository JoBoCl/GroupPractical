package uk.ac.ox.cs.GPT9.augox;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import uk.ac.ox.cs.GPT9.augox.dbquery.AllQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.AndQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.InLocusQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.NotQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.RatingRangeQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.VisitedQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.NameSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.SortOrder;
import uk.ac.ox.cs.GPT9.augox.newsfeed.NewsFeed;
import uk.ac.ox.cs.GPT9.augox.newsfeed.NewsFeedImageGatherer;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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

@TargetApi(11)
public class GalleryPickerFragment extends Fragment {

	private RadioGroup categoryChoice;

	private RadioButton[] chosenPlace = new RadioButton[3];
	private int lastChecked;
	private int offset;
	private CheckBox ownPlan;
	private Integer[] placeIds = new Integer[3];
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
		allowUnvisited = AutoPlannerActivity.allowingUnvisited();
		maxDistance = AutoPlannerActivity.getMaxDistance();
		minRating = AutoPlannerActivity.getMinRating();

		placeIds = choosePlaces(currentCat);
		matchPlaceIds();
		updateUiElements();
	}

	private boolean allowRepeats = false;

	private boolean allowVisited = true;

	private double maxDistance = 0.0f;

	private float minRating = 0;

	private boolean allowUnvisited = true;

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

	private Integer[] choosePlaces(List<Integer> idList) {
		Integer[] chosenPlaceIds = new Integer[3];

		for (int i = 0; i < 3; i++) {
			chosenPlaceIds[i] = -1;
		}

		if (!allowRepeats)
			idList.removeAll(AutoPlannerActivity.getSeenPlaces());

		if (idList.size() > 0) {

			Random random = new Random();

			for (int i = 0; i < 3; i++) {
				chosenPlaceIds[i] = idList.get(random.nextInt(idList.size()));
				idList.remove(chosenPlaceIds[i]);
			}

		}

		AutoPlannerActivity.updateSeenPlaces(offset, chosenPlaceIds);

		return chosenPlaceIds;
	}

	private Integer[] choosePlaces(PlaceCategory category) {
		DatabaseQuery query = new CategoryQuery(
				Collections.singletonList(category));

		// If both checked, allow everything within all constraints
		if (!(allowVisited && allowUnvisited))
			if (allowVisited)
				query = new AndQuery(query, new VisitedQuery());
			else if (allowUnvisited)
				query = new AndQuery(query, new NotQuery(new VisitedQuery()));
			else
				query = new NotQuery(new AllQuery());
		// If neither checked, allow nothing

		query = new AndQuery(query, new RatingRangeQuery((int) minRating, 5));

		if(offset == 0) 
		try {
			// Lat at [0], long at [1]
			query = new AndQuery(query, new InLocusQuery(
					MainScreenActivity.getUserLocation()[0],
					MainScreenActivity.getUserLocation()[1], maxDistance));
		} catch (NullPointerException e) {
			query = new AndQuery(query, new InLocusQuery(51.759684, -1.258468,
					maxDistance));
			// If no user data found, use CS Dept
		}
		 else if(offset > 0) try {
			 double latitude = AutoPlannerActivity.getPreviousLatitude(offset);
			 double longitude = AutoPlannerActivity.getPreviousLongitude(offset);
			 query = new AndQuery(query, new InLocusQuery(latitude, longitude, maxDistance));
		 } catch (NullPointerException e) {
			 query = new AndQuery(query, new InLocusQuery(51.759684, -1.258468,
					maxDistance));
			// If no user data found, use CS Dept
		 } else throw new IllegalStateException("Gallery fragment index should not be negative");
		// if offset is negative, we've got bigger problems

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
		for (int i = 0; i < 3; i++) {
			if (places[i] != null && placeIds[i] != -1) {
				try{
				GalleryImageGatherer gatherer = new GalleryImageGatherer(
						places[i]);
				gatherer.giveData(places[i], places[i].getFourSquareURL());
				gatherer.startGathering();
				} catch (Exception e) { }
			}
		}

		for (int j = 0; j < 3; j++) {
			if (placeIds[j] == -1) {
				chosenPlace[j].setText("No suggestion found");
				placeImages[j].setImageDrawable(getResources().getDrawable(
						R.drawable.common_signin_btn_icon_disabled_dark));
			} else {
				chosenPlace[j].setText(places[j].getName());
				placeImages[j].setVisibility(View.VISIBLE);
				placeImages[j].setImageDrawable(places[j].getImage());
			}
			Log.d("Joshua", "Set text for place");
		}
	}

	private class GalleryImageGatherer {
		private String imageUrl;

		class ImageGathererTask extends AsyncTask<Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... params) {
				// get image
				try {
					URL photourl = new URL(imageUrl);

					HttpsURLConnection photoconnection = (HttpsURLConnection) photourl
							.openConnection();
					photoconnection.setDoInput(true);
					photoconnection.connect();
					InputStream input = photoconnection.getInputStream();
					Bitmap image = BitmapFactory.decodeStream(input);
					place.updateImage(new BitmapDrawable(image));
				} catch (Exception e) {/* no photos available */
				}
				return null;
			}
		}

		private PlaceData place;

		public void startGathering() {
			new ImageGathererTask().execute();
		}

		public void giveData(PlaceData placeData, String foursquareImageUrl) {
			place = placeData;
			imageUrl = foursquareImageUrl;
		}

		public GalleryImageGatherer(PlaceData placeData) {
			place = placeData;
		}
	}
}