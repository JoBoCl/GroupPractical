package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.AndQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.NotQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.VisitedQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.DatabaseSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.NameSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.SortOrder;
import uk.ac.ox.cs.GPT9.augox.route.IRoute;
import uk.ac.ox.cs.GPT9.augox.route.Route;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RoutePlannerActivity extends Activity {
	/*
	 * Intent Constants
	 */
	public final static String EXTRA_LATITUDE = "uk.ac.ox.cs.GPT9.augox.LATITUDE";
	public final static String EXTRA_LONGITUDE = "uk.ac.ox.cs.GPT9.augox.LONGITUDE";
	private IRoute curRoute = new Route();
	private double latitude = 0;
	private double longitude = 0;
	
	protected void onResume(){
		super.onResume();
		Intent intent = getIntent();
		latitude = intent.getDoubleExtra(EXTRA_LATITUDE,Double.valueOf(0));
		longitude = intent.getDoubleExtra(EXTRA_LONGITUDE,Double.valueOf(0));
		reloadLists();
		reloadCheckboxes();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_planner);
		curRoute = MainScreenActivity.getCurrentRoute();
		Button buttonContinue = (Button) findViewById(R.id.buttonRoutePlannerStart);
		buttonContinue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();			
			}
		});
		Button buttonClear = (Button) findViewById(R.id.buttonRoutePlannerClear);
		buttonClear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				curRoute.clear();
				reloadLists(true, false);
			}
		});
		
		reloadLists();
		reloadCheckboxes();
	}
	
	
	private void reloadCheckboxes(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final CheckBox filterVisited = (CheckBox) findViewById(R.id.checkBoxRouteFilterVisited);
		final CheckBox filterUnvisited = (CheckBox) findViewById(R.id.checkBoxRouteFilterUnvisited);
		final CheckBox filterBars = (CheckBox) findViewById(R.id.checkBoxRouteFilterBars);
		final CheckBox filterColleges = (CheckBox) findViewById(R.id.checkBoxRouteFilterColleges);
		final CheckBox filterMuseums = (CheckBox) findViewById(R.id.checkBoxRouteFilterMuseums);
		final CheckBox filterRestaurants = (CheckBox) findViewById(R.id.checkBoxRouteFilterRestaurants);
		filterVisited.setChecked(pref.getBoolean("filter_visited", true));
		filterUnvisited.setChecked(pref.getBoolean("filter_unvisited", true));
		filterBars.setChecked(pref.getBoolean("filter_bars", true));
		filterColleges.setChecked(pref.getBoolean("filter_colleges", true));
		filterMuseums.setChecked(pref.getBoolean("filter_museums", true));
		filterRestaurants.setChecked(pref.getBoolean("filter_restaurants",true));
		
		final SharedPreferences.Editor editor = pref.edit();
		
		filterVisited.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_visited",filterVisited.isChecked());
				editor.commit();
				reloadLists();
			}
		});
		filterUnvisited.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_unvisited",filterUnvisited.isChecked());
				editor.commit();
				reloadLists();
			}
		});
		filterBars.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_bars",filterBars.isChecked());
				editor.commit();
				reloadLists();
			}
		});
		filterColleges.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_colleges",filterColleges.isChecked());
				editor.commit();
				reloadLists();
			}
		});
		filterMuseums.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_museums",filterMuseums.isChecked());
				editor.commit();
				reloadLists();
			}
		});
		filterRestaurants.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_restaurants",filterRestaurants.isChecked());
				editor.commit();
				reloadLists();
			}
		});
		
	}

	private void reloadLists(){reloadLists(true,true);}
	private void reloadLists(boolean route, boolean filtered){
		if(route){
			//Current Route List
			final List<Integer> routePlaces = curRoute.getRouteAsIDList();
			ListView currentRouteListView = ((ListView) findViewById(R.id.listRoutePlannerCurrentRoute));
			int lastScroll = currentRouteListView.getFirstVisiblePosition();
			RouteAdapter adapter = new RouteAdapter(this,routePlaces);
			currentRouteListView.setAdapter(adapter);
			currentRouteListView.setSelection(lastScroll);

			currentRouteListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int itemNoClicked,
						long arg3) {
					PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
					//Starts activity PlaceFullInfoActivity for the clicked place
					PlaceData place = db.getPlaceByID(routePlaces.get(itemNoClicked));
	            	Intent intent = new Intent(getApplicationContext(), PlaceFullInfoActivity.class);
	            	double dist = PlaceData.getDistanceBetween(place.getLatitude(), place.getLongitude(), latitude, longitude);
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE, dist);
	            	//put the place in the intent
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, routePlaces.get(itemNoClicked));
	                //put the background to include in the intent
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, "");
	                startActivity(intent);
				}
			});
		}
		
		if(filtered){
			//Add Places List
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			List<PlaceCategory> ls = new ArrayList<PlaceCategory>();
			if(pref.getBoolean("filter_bars",true)) ls.add(PlaceCategory.BAR);
			if(pref.getBoolean("filter_colleges",true)) ls.add(PlaceCategory.COLLEGE);
			if(pref.getBoolean("filter_museums",true)) ls.add(PlaceCategory.MUSEUM);
			if(pref.getBoolean("filter_restaurants",true)) ls.add(PlaceCategory.RESTAURANT);
			DatabaseQuery q = new CategoryQuery(ls);
			DatabaseSorter s = new NameSorter(SortOrder.ASC);
			final PlacesDatabase d = MainScreenActivity.getPlacesDatabase();
			final List<Integer> filterPlaces;
			
			if(!pref.getBoolean("filter_visited",true) && pref.getBoolean("filter_unvisited",true)) q = new AndQuery(q, new NotQuery(new VisitedQuery()));
			if(pref.getBoolean("filter_visited",true) && !pref.getBoolean("filter_unvisited",true)) q = new AndQuery(q, new VisitedQuery());
			if(!pref.getBoolean("filter_visited",true) && !pref.getBoolean("filter_unvisited",true)) {
				filterPlaces = new ArrayList<Integer>();
			} else filterPlaces = d.query(q, s);
			
			/*
			for(Integer i : filterPlaces){
				PlaceData place = d.getPlaceByID(i);
				if(curRoute.contains(place)){
					filterPlaces.remove(i);
				}
			} */
			AddPlaceAdapter adapter2 = new AddPlaceAdapter(this,filterPlaces);
			ListView addPlacesListView = ((ListView) findViewById(R.id.listRoutePlannerAddPlaces));
			addPlacesListView.setAdapter(adapter2);
			
			addPlacesListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int itemNoClicked,
						long arg3) {
	                PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
					//Starts activity PlaceFullInfoActivity for the clicked place
					PlaceData place = db.getPlaceByID(filterPlaces.get(itemNoClicked));
	            	Intent intent = new Intent(getApplicationContext(), PlaceFullInfoActivity.class);
	            	double dist = PlaceData.getDistanceBetween(place.getLatitude(), place.getLongitude(), latitude, longitude);
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE, dist);
	            	//put the place in the intent
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, filterPlaces.get(itemNoClicked));
	                //put the background to include in the intent
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, "");
	                startActivity(intent);
				}
			});	
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_planner, menu);
		return true;
	}
	
	public class RouteAdapter extends ArrayAdapter<Integer> { //adapter for the current route records
		private Context context;
		private List<Integer> values;
		public RouteAdapter(Context context,List<Integer> values){
			super(context,R.layout.listview_item_current_route,values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listview_item_current_route, parent,false);
			final PlaceData item = db.getPlaceByID(values.get(position));
			TextView numView = (TextView) rowView.findViewById(R.id.current_route_number);
			TextView nameView = (TextView) rowView.findViewById(R.id.current_route_name);
			ImageView typeView = (ImageView) rowView.findViewById(R.id.current_route_type);
			Button upButton = (Button) rowView.findViewById(R.id.buttonRouteUp);
			Button downButton = (Button) rowView.findViewById(R.id.buttonRouteDown);
			Button removeButton = (Button) rowView.findViewById(R.id.buttonRouteRemove);
			numView.setText(String.valueOf(position+1));
			nameView.setText(item.getName());
			typeView.setImageResource(item.getCategory().getImageRef(item.getVisited()));
			upButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(position > 0){
						curRoute.changePosition(position, position-1);
		                reloadLists(true,false);
					}
				}
			});
			downButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(position < values.size()-1){
						curRoute.changePosition(position,position+1);
						reloadLists(true,false);
					}
				}
			});
			removeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					curRoute.removePlace(item);	
				    reloadLists(true,false);
				}
			});
			return rowView; 
		}
	}
	
	public class AddPlaceAdapter extends ArrayAdapter<Integer> {
		private Context context;
		private List<Integer> values;
		public AddPlaceAdapter(Context context,List<Integer> values){
			super(context,R.layout.listview_item_add_places,values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listview_item_add_places, parent,false);
			final int placeId = values.get(position);
			final PlaceData item = db.getPlaceByID(placeId);
			TextView nameView = (TextView) rowView.findViewById(R.id.add_places_name);
			ImageView typeView = (ImageView) rowView.findViewById(R.id.add_places_type);
			Button addRouteView = (Button) rowView.findViewById(R.id.buttonAddPlaces);
			nameView.setText(item.getName());
			typeView.setImageResource(item.getCategory().getImageRef(item.getVisited()));
			addRouteView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Log.d("TESTING","ADDTOROUTE");
					curRoute.addEnd(placeId);
					curRoute.setList(curRoute.getRouteAsIDArray());
					Log.d("TESTING","ADDEDTOROUTE");
	                reloadLists(true,false);
				}
			});
			return rowView; 
		}
	}

}
