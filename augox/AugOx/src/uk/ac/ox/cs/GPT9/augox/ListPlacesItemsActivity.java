package uk.ac.ox.cs.GPT9.augox;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.ClickedQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.InLocusQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.NameStartsWithQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.NotQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.VisitedQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.DatabaseSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.DistanceFromSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.NameSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.SortOrder;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListPlacesItemsActivity extends ListActivity {
	/*
	 * Intent Constants
	 */
	public final static String EXTRA_LATITUDE = "uk.ac.ox.cs.GPT9.augox.LATITUDE";
	public final static String EXTRA_LONGITUDE = "uk.ac.ox.cs.GPT9.augox.LONGITUDE";
	public final static String EXTRA_QUERYTYPE = "uk.ac.ox.cs.GPT9.augox.QUERYTYPE";
	public final static String EXTRA_QUERYDATA = "uk.ac.ox.cs.GPT9.augox.QUERYDATA";
	private double radius = 0;
	private double latitude = 0;
	private double longitude = 0;
	private int queryType = 0;

	protected void onResume(){
		super.onResume();
		setup();
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setup();
	}
	
	private void setup(){
		Intent intent = getIntent();
		latitude = intent.getDoubleExtra(EXTRA_LATITUDE,Double.valueOf(0));
		longitude = intent.getDoubleExtra(EXTRA_LONGITUDE,Double.valueOf(0));
		radius = 100;
		queryType = intent.getIntExtra(EXTRA_QUERYTYPE, 0);
		DatabaseQuery q;
		DatabaseSorter s = new NameSorter(SortOrder.ASC);
		final PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
	
		switch(queryType){
		case 0: //local places
			q = new InLocusQuery(latitude,longitude,radius);
			s = new DistanceFromSorter(latitude, longitude, SortOrder.ASC);
			setTitle("Local Places");
			break;
		case 1: //visited places
			q = new VisitedQuery();
			setTitle("Visited Places");
			break;
		case 2: //unvisited places
			q = new NotQuery(new VisitedQuery());
			setTitle("Unvisited Places");
			break;
		case 3: //places by name
			char queryDataChar = (char)intent.getIntExtra(EXTRA_QUERYDATA, 0);
			q = new NameStartsWithQuery(String.valueOf(queryDataChar));
			setTitle(queryDataChar + ".." + " Places");
			break;
		case 4: //places by type
			int queryData = intent.getIntExtra(EXTRA_QUERYDATA, 0);
			List<PlaceCategory> cats = new ArrayList<PlaceCategory>();
			cats.add(PlaceCategory.getCategoryByID(queryData));
			q = new CategoryQuery(cats);
			setTitle(PlaceCategory.getCategoryByID(queryData) + " Places");
			break;
		default:
			q = null;
			break;
		}		

		final List<Integer> places = db.query(q,s);
		if(places.size() != 0){
			//set click listener for clicking on a list element
			getListView().setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int itemNoClicked,
						long arg3) {
					//Starts activity PlaceFullInfoActivity for the clicked place
	            	Intent intent = new Intent(getApplicationContext(), PlaceFullInfoActivity.class);
	            	//put the place in the intent
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, places.get(itemNoClicked));
	                //put the background to include in the intent
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, "");
	                startActivity(intent);
				}
			});	
			//set up the ArrayAdapter
			MyPlaceAdapter adapter = new MyPlaceAdapter(this,places);
			setListAdapter(adapter);
		} else {
			List<String> noneFoundList = new ArrayList<String>(); noneFoundList.add("No Places Found");
			MyStringAdapter adapter = new MyStringAdapter(this,noneFoundList);
			setListAdapter(adapter);
		}
	}
	public class MyPlaceAdapter extends ArrayAdapter<Integer> {
		private Context context;
		private List<Integer> values;
		public MyPlaceAdapter(Context context,List<Integer> values){
			super(context,R.layout.listview_item_list_places,values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
			PlaceData item = db.getPlaceByID(values.get(position));
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listview_item_list_places, parent,false);
			TextView nameView = (TextView) rowView.findViewById(R.id.list_places_item_name);
			ImageView typeView = (ImageView) rowView.findViewById(R.id.list_places_item_type);
			TextView distView = (TextView) rowView.findViewById(R.id.list_places_item_distance);
			nameView.setText(item.getName());
			//after we have icons for each type of place, set it here
			switch(item.getCategory()){
				case MUSEUM:
					typeView.setImageResource(R.drawable.museumicon);
					break;
				case BAR:
					typeView.setImageResource(R.drawable.baricon);
					break;
				case COLLEGE:
					typeView.setImageResource(R.drawable.collegeicon);
					break;
				case RESTAURANT:
					typeView.setImageResource(R.drawable.restauranticon);
					break;
				default:
					typeView.setImageResource(R.drawable.ic_launcher);
					break;
			}
			distView.setText(String.format("%.1f", PlaceData.getDistanceBetween(
					latitude,longitude,item.getLatitude(),item.getLongitude()))+"km"); 
			return rowView;
		}
	}
	
	public class MyStringAdapter extends ArrayAdapter<String> {
		private Context context;
		private List<String> values;
		public MyStringAdapter(Context context,List<String> values){
			super(context,R.layout.listview_item_list_places,values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listview_standard_layout, parent,false);
			TextView nameView = (TextView) rowView.findViewById(R.id.list_places_single_view);
			nameView.setText(values.get(position));
			return rowView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_places, menu);
		return true;
	}
	
}
