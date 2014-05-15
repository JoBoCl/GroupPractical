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
	private PlacesDatabase db;
	//current route variables
	private ListView currentRouteListView;
	private List<Integer> routePlaces;
	private RouteAdapter routeAdapter;
	//add places variables
	private ListView addPlacesListView;
	private PlacesDatabase placesDat;
	private List<Integer> filterPlaces;
	private AddPlaceAdapter placeAdapter;
	
	protected void onResume(){
		super.onResume();
		
		//get the current route from the main screen and set up an adapter to display items in a listView
		routePlaces = curRoute.getRouteAsIDList();
		routeAdapter = new RouteAdapter(this,routePlaces);
		currentRouteListView.setAdapter(routeAdapter);
		
		reloadLists();
		reloadCheckboxes();
	}
	
	//when user leaves activity, set the current route (on the main screen) to the route the user has created in this activity
	protected void onPause(){
		super.onPause();
		curRoute.setList(routePlaces);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_planner);
		initSetup();
		reloadLists();
		reloadCheckboxes();
	}
	
	//setup the screen including listViews and checkboxes
	private void initSetup(){
		//get values from the intent
		Intent intent = getIntent();
		latitude = intent.getDoubleExtra(EXTRA_LATITUDE,Double.valueOf(0));
		longitude = intent.getDoubleExtra(EXTRA_LONGITUDE,Double.valueOf(0));
		placesDat = MainScreenActivity.getPlacesDatabase();
		//get the current route and set up an adapter to show it in a listView
		curRoute = MainScreenActivity.getCurrentRoute();
		db = MainScreenActivity.getPlacesDatabase();
		routePlaces = curRoute.getRouteAsIDList();
		routeAdapter = new RouteAdapter(this,routePlaces);
		currentRouteListView = ((ListView) findViewById(R.id.listRoutePlannerCurrentRoute));
		currentRouteListView.setAdapter(routeAdapter);
		
		//click listener to go to PlaceFullActivity to get more information about a place
		currentRouteListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemNoClicked,
					long arg3) {
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
		//set up the listview for displaying the places found by the filters that can be added to the route
		filterPlaces = new ArrayList<Integer>();
		placeAdapter = new AddPlaceAdapter(this,filterPlaces);
		
		addPlacesListView = ((ListView) findViewById(R.id.listRoutePlannerAddPlaces));
		addPlacesListView.setAdapter(placeAdapter);
		
		//click listener to go to PlaceFullActivity to get more information about a place
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
		
		
		//setup the continue button, which goes back to the main screen when clicked.
		Button buttonContinue = (Button) findViewById(R.id.buttonRoutePlannerStart);
		buttonContinue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();			
			}
		});
		
		//setup the clear route button, which clears the current route and refreshes the lists accordingly.
		Button buttonClear = (Button) findViewById(R.id.buttonRoutePlannerClear);
		buttonClear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				routePlaces.clear();
				reloadLists(true, false);
			}
		});
		
	}
	
	//refresh the values of the checkboxes
	private void reloadCheckboxes(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		//get references to the checkboxes
		final CheckBox filterVisited = (CheckBox) findViewById(R.id.checkBoxRouteFilterVisited);
		final CheckBox filterUnvisited = (CheckBox) findViewById(R.id.checkBoxRouteFilterUnvisited);
		final CheckBox filterBars = (CheckBox) findViewById(R.id.checkBoxRouteFilterBars);
		final CheckBox filterColleges = (CheckBox) findViewById(R.id.checkBoxRouteFilterColleges);
		final CheckBox filterMuseums = (CheckBox) findViewById(R.id.checkBoxRouteFilterMuseums);
		final CheckBox filterRestaurants = (CheckBox) findViewById(R.id.checkBoxRouteFilterRestaurants);
		
		//set the checkboxes to reflect the current value of the filter
		filterVisited.setChecked(pref.getBoolean("filter_visited", true));
		filterUnvisited.setChecked(pref.getBoolean("filter_unvisited", true));
		filterBars.setChecked(pref.getBoolean("filter_bars", true));
		filterColleges.setChecked(pref.getBoolean("filter_colleges", true));
		filterMuseums.setChecked(pref.getBoolean("filter_museums", true));
		filterRestaurants.setChecked(pref.getBoolean("filter_restaurants",true));
		
		final SharedPreferences.Editor editor = pref.edit();
		
		//add click listeners to each checkbox, to update the value in the SharedPreferences
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

	//reload the listViews on the screen
	private void reloadLists(){reloadLists(true,true);}
	private void reloadLists(boolean route, boolean filtered){
		if(route){ //if updating the route listView, simply notify the adapter than the data has changed.
			routeAdapter.notifyDataSetChanged();

		}
		if(filtered){ //if updating the add places listView, simply notify the adapter than the data has changed.
			//use the current values of the checkboxes to generate a query to query the database
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			List<PlaceCategory> ls = new ArrayList<PlaceCategory>();
			if(pref.getBoolean("filter_bars",true)) ls.add(PlaceCategory.BAR);
			if(pref.getBoolean("filter_colleges",true)) ls.add(PlaceCategory.COLLEGE);
			if(pref.getBoolean("filter_museums",true)) ls.add(PlaceCategory.MUSEUM);
			if(pref.getBoolean("filter_restaurants",true)) ls.add(PlaceCategory.RESTAURANT);
			DatabaseQuery q = new CategoryQuery(ls);
			DatabaseSorter s = new NameSorter(SortOrder.ASC);
			List<Integer> queryList;
			
			//query the database
			if(!pref.getBoolean("filter_visited",true) && pref.getBoolean("filter_unvisited",true)) q = new AndQuery(q, new NotQuery(new VisitedQuery()));
			if(pref.getBoolean("filter_visited",true) && !pref.getBoolean("filter_unvisited",true)) q = new AndQuery(q, new VisitedQuery());
			if(!pref.getBoolean("filter_visited",true) && !pref.getBoolean("filter_unvisited",true)) {
				queryList = new ArrayList<Integer>();
			} else queryList = placesDat.query(q, s);
			
			//add all places that we got from the database to the add places listView after emptying it.
			filterPlaces.clear();
			for(Integer i : queryList){
				filterPlaces.add(i);
			}
			placeAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_planner, menu);
		return true;
	}
	
	//ArrayAdapter for displaying information about a place currently in the route
	public class RouteAdapter extends ArrayAdapter<Integer> {
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
			//get references to the views to display information about an item
			TextView numView = (TextView) rowView.findViewById(R.id.current_route_number);
			TextView nameView = (TextView) rowView.findViewById(R.id.current_route_name);
			ImageView typeView = (ImageView) rowView.findViewById(R.id.current_route_type);
			Button upButton = (Button) rowView.findViewById(R.id.buttonRouteUp);
			Button downButton = (Button) rowView.findViewById(R.id.buttonRouteDown);
			Button removeButton = (Button) rowView.findViewById(R.id.buttonRouteRemove);
			
			//set these views to reflect the place to be displayed
			numView.setText(String.valueOf(position+1));
			nameView.setText(item.getName());
			typeView.setImageResource(item.getCategory().getImageRefNoBorder(item.getVisited()));
			
			//click listener to move the place up in the order of visiting if possible
			upButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(position > 0){ //cannot move the first place earlier
						int i = routePlaces.get(position);
						routePlaces.remove(position);
						routePlaces.add(position-1, i);
		                reloadLists(true,false);
					}
				}
			});
			
			//click listener to move the place down in the order of visiting if possible
			downButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(position < values.size()-1){//cannot move the last place later
						int i = routePlaces.get(position);
						routePlaces.remove(position);
						routePlaces.add(position+1, i);
						reloadLists(true,false);
					}
				}
			});
			//click listener to remove a place from the route
			removeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					//remove it from the route
					routePlaces.remove(position);
				    reloadLists(true,false);
				}
			});
			return rowView; 
		}
	}
	
	//ArrayAdapter for displaying information about a place that can be added to the route
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
			//get references to the views to display information about an item
			TextView nameView = (TextView) rowView.findViewById(R.id.add_places_name);
			ImageView typeView = (ImageView) rowView.findViewById(R.id.add_places_type);
			Button addRouteView = (Button) rowView.findViewById(R.id.buttonAddPlaces);
			
			//set these views to reflect the place to be displayed
			nameView.setText(item.getName());
			typeView.setImageResource(item.getCategory().getImageRefNoBorder(item.getVisited()));
			
			//click listener to add a place to the route
			addRouteView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					routePlaces.add(placeId);
	                reloadLists(true,false);
	                currentRouteListView.setSelection(routeAdapter.getCount() - 1);
				}
			});
			return rowView; 
		}
	}

}
