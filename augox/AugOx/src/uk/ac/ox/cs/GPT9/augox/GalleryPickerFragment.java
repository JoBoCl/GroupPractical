/**
 * 
 */
package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.AndQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.OpenAtQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.NameSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.SortOrder;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class GalleryPickerFragment extends Fragment {

	private RadioGroup categoryChoice;
	private PlaceData place;
	private PlaceData[] places = new PlaceData[3];
	private RadioButton[] chosenPlace = new RadioButton[3];
	private ImageView placeImage0, placeImage1, placeImage2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gallery_picker, container);

		// Here we are fetching the layoutParams from parent activity and
		// setting it to the fragment's view.

		chosenPlace[0] = (RadioButton) getView()
				.findViewById(R.id.chosenPlace0);
		chosenPlace[1] = (RadioButton) getView()
				.findViewById(R.id.chosenPlace1);
		chosenPlace[2] = (RadioButton) getView()
				.findViewById(R.id.chosenPlace2);

		placeImage0 = (ImageView) getView().findViewById(R.id.placeImage0);
		placeImage1 = (ImageView) getView().findViewById(R.id.placeImage1);
		placeImage2 = (ImageView) getView().findViewById(R.id.placeImage2);

		categoryChoice = (RadioGroup) getView().findViewById(
				R.id.categoryChoice);

		List<PlaceCategory> categories = new ArrayList<PlaceCategory>(
				EnumSet.allOf(PlaceCategory.class));

		List<RadioButton> catButtons = new ArrayList<RadioButton>();

		for (PlaceCategory category : categories) {
			RadioButton button = new RadioButton(getActivity());
			button.setText(category.toString());
			catButtons.add(button);
			categoryChoice.addView(button);
		}

		chosenPlace[0].setText(places[0].getName());
		chosenPlace[1].setText(places[1].getName());
		chosenPlace[2].setText(places[2].getName());

		RadioGroup placesGroup = (RadioGroup) getView().findViewById(
				R.id.placesGroup);

		placesGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				for (int i = 0; i < 3; i++)
					if (checkedId == chosenPlace[i].getId())
						place = places[i];
			}
		});

		
		  /*placeImage0.setImage(places[i]());
		  placeImage1.setImage(place1.getName());
		  placeImage2.setImage(place2.getName());*/
		 
		return view;
	}

	public PlaceData getSelectedPlace() {
		return place;
	}

	private PlaceData[] choosePlaces(Session session) {
		PlaceCategory[] placecat = SessionHelper
				.getCategoriesForSession(session);
		DatabaseQuery plcat = new CategoryQuery(Arrays.asList(placecat));
		DatabaseQuery open = new OpenAtQuery(
				SessionHelper.getStartTimeForSession(session));
		DatabaseQuery and = new AndQuery(plcat, open);
		List<Integer> placeIds = MainScreenActivity.getPlacesDatabase().query(
				and, new NameSorter(SortOrder.ASC));
		List<PlaceData> places = new ArrayList<PlaceData>();
		for (Integer placeId : placeIds)
			places.add(MainScreenActivity.getPlacesDatabase().getPlaceByID(
					placeId));
		PlaceData[] chosenPlaces = choosePlaces(places);
		return chosenPlaces;
	}

	private PlaceData[] choosePlaces(List<PlaceData> places) {
		// TODO: Rewrite to better use list of places and/or not reuse places on
		// the route.
		PlaceData dummy = new PlaceData();
		switch (places.size()) {
		case 0:
			return new PlaceData[] { dummy, dummy, dummy };
		case 1:
			return new PlaceData[] { places.get(0), dummy, dummy };
		case 2:
			return new PlaceData[] { places.get(0), places.get(1), dummy };
		case 3:
			return new PlaceData[] { places.get(0), places.get(1),
					places.get(2) };
		default:
			return new PlaceData[] { places.get(0), places.get(1),
					places.get(2) };
		}
	}
}
