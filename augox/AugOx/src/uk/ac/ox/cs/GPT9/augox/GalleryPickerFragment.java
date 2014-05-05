package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.AndQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.OpenAtQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.NameSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.SortOrder;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import uk.ac.ox.cs.GPT9.augox.R;
import android.widget.RadioGroup;

@TargetApi(11)
public class GalleryPickerFragment extends Fragment {
	private RadioGroup categoryChoice;
	private RadioButton[] chosenPlace = new RadioButton[3];
	private int lastChecked;
	private int offset;
	private CheckBox ownPlan;
	private ImageView[] placeImages = new ImageView[3];
	private PlaceData[] places = new PlaceData[3];

	public static GalleryPickerFragment newInstance(int offset) {
		GalleryPickerFragment localGalleryPickerFragment = new GalleryPickerFragment();
		Bundle localBundle = new Bundle();
		localBundle.putInt("offset", offset);
		localGalleryPickerFragment.setArguments(localBundle);
		return localGalleryPickerFragment;
	}

	public PlaceData getSelectedPlace() {
		if (ownPlan.isChecked()) {
			return null;
		}
		return places[lastChecked];
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(2130903046, container, false);
		offset = getArguments().getInt("offset");
		Log.d("Joshua", "Get offset from args");
		placeImages[0] = ((ImageView) view.findViewById(2131492892));
		placeImages[1] = ((ImageView) view.findViewById(2131492894));
		placeImages[2] = ((ImageView) view.findViewById(2131492896));
		Log.d("Joshua", "Get text descriptions");
		chosenPlace[0] = ((RadioButton) view.findViewById(2131492893));
		chosenPlace[1] = ((RadioButton) view.findViewById(2131492895));
		chosenPlace[2] = ((RadioButton) view.findViewById(2131492897));
		Log.d("Joshua", "Get radio buttons");

		View.OnClickListener placeClickedListener = new View.OnClickListener() {
			public void onClick(View view) {
				for (int i = 0; i < 3; i++) {
					if ((view == chosenPlace[i]) || (view == placeImages[i])) {
						chosenPlace[i].setChecked(true);
						lastChecked = i;
						chosenPlace[i].setChecked(false);
					}
				}
			}
		};

		for (int i = 0; i < 3; i++) {
			chosenPlace[i].setOnClickListener(placeClickedListener);
			placeImages[i].setOnClickListener(placeClickedListener);

		}
		ArrayList<RadioButton> categoryRadioButtons;
		categoryChoice = ((RadioGroup) view.findViewById(2131492890));
		categoryChoice.setOrientation(RadioGroup.VERTICAL);
		Log.d("Joshua", "Get category holder");
		List<PlaceCategory> categoryList = new ArrayList<PlaceCategory>(
				EnumSet.allOf(PlaceCategory.class));
		Log.d("Joshua", "Create list of categories");
		categoryRadioButtons = new ArrayList<RadioButton>();
		Log.d("Joshua", "Create list of category buttons");
		for (PlaceCategory cat : categoryList) {
			if (cat != PlaceCategory.UNKNOWN) {
				RadioButton catRadioButton = new RadioButton(getActivity());
				catRadioButton.setText(cat.getName());
				Log.d("Joshua", "Create category button for " + cat.toString());
				categoryRadioButtons.add(catRadioButton);
				categoryChoice.addView(catRadioButton);
				Log.d("Joshua", "Add category button for " + cat.toString());
			}
		}

		((RadioButton) categoryRadioButtons.get(0)).setChecked(true);
		Log.d("Joshua", "Set checked boolean");
		places = choosePlaces((PlaceCategory) categoryList.get(0));
		Log.d("Joshua", "Create list of places");
		for (int j = 0; j < 3; j++) {
			chosenPlace[j].setText(places[j].getName());
			Log.d("Joshua", "Set text for place");
		}
		ownPlan = ((CheckBox) view.findViewById(2131492898));
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

	private PlaceData[] choosePlaces(List<PlaceData> paramList) {
		// TODO: Improve this and clarify
		PlaceData localPlaceData = new PlaceData();
		switch (paramList.size()) {
		default:
			PlaceData[] arrayOfPlaceData4 = new PlaceData[3];
			arrayOfPlaceData4[0] = ((PlaceData) paramList.get(0));
			arrayOfPlaceData4[1] = ((PlaceData) paramList.get(1));
			arrayOfPlaceData4[2] = ((PlaceData) paramList.get(2));
			return arrayOfPlaceData4;
		case 0:
			return new PlaceData[] { localPlaceData, localPlaceData,
					localPlaceData };
		case 1:
			PlaceData[] arrayOfPlaceData3 = new PlaceData[3];
			arrayOfPlaceData3[0] = ((PlaceData) paramList.get(0));
			arrayOfPlaceData3[1] = localPlaceData;
			arrayOfPlaceData3[2] = localPlaceData;
			return arrayOfPlaceData3;
		case 2:
			PlaceData[] arrayOfPlaceData2 = new PlaceData[3];
			arrayOfPlaceData2[0] = ((PlaceData) paramList.get(0));
			arrayOfPlaceData2[1] = ((PlaceData) paramList.get(1));
			arrayOfPlaceData2[2] = localPlaceData;
			return arrayOfPlaceData2;
		}
	}

	private PlaceData[] choosePlaces(PlaceCategory paramPlaceCategory) {
		// TODO: clarify the contents of this method
		Time localTime = new Time();
		LocalTime localLocalTime = new LocalTime(localTime.year,
				localTime.month, localTime.monthDay, localTime.hour + offset, 0);
		AndQuery localAndQuery = new AndQuery(new CategoryQuery(
				Collections.singletonList(paramPlaceCategory)),
				new OpenAtQuery(localLocalTime));
		List<?> localList = MainScreenActivity.getPlacesDatabase().query(
				localAndQuery, new NameSorter(SortOrder.ASC));
		ArrayList<PlaceData> localArrayList = new ArrayList<PlaceData>();
		Iterator<?> localIterator = localList.iterator();
		for (;;) {
			if (!localIterator.hasNext()) {
				return choosePlaces(localArrayList);
			}
			Integer localInteger = (Integer) localIterator.next();
			localArrayList.add(MainScreenActivity.getPlacesDatabase()
					.getPlaceByID(localInteger.intValue()));
		}
	}
}