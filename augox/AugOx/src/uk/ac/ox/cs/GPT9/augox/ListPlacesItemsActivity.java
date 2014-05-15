package uk.ac.ox.cs.GPT9.augox;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.InLocusQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.NameStartsWithQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.NotQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.OrQuery;
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

//Activity for displaying a list of items to the user via a query call to the database
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

	//reload the list on coming back to the activity.
	protected void onResume(){
		super.onResume();
		setup();
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setup();
	}
	
	private void setup(){ //populate the listview
		//get values from the intent
		Intent intent = getIntent();
		latitude = intent.getDoubleExtra(EXTRA_LATITUDE,Double.valueOf(0));
		longitude = intent.getDoubleExtra(EXTRA_LONGITUDE,Double.valueOf(0));
		radius = 2;
		queryType = intent.getIntExtra(EXTRA_QUERYTYPE, 0);
		
		//set up queries and a sort for the database
		DatabaseQuery q = null;
		DatabaseSorter s = new NameSorter(SortOrder.ASC);
		final PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
	
		switch(queryType){
		case 0: //local places
			q = new InLocusQuery(latitude,longitude,radius);
			s = new DistanceFromSorter(latitude, longitude, SortOrder.ASC);
			setTitle("Places: Local");
			break;
		case 1: //visited places
			q = new VisitedQuery();
			setTitle("Places: Visited");
			break;
		case 2: //unvisited places
			q = new NotQuery(new VisitedQuery());
			setTitle("Places: Unvisited");
			break;
		case 3: //places by name
			int queryDataChar = intent.getIntExtra(EXTRA_QUERYDATA,0);
			if( (char) queryDataChar == '0'){
				q = createNumQuery();
				setTitle("Places: 0..9");
			} else {
				//create a query that finds all places beginning with three adjacent letters where queryDataChar is the first letter
				q = new NameStartsWithQuery(String.valueOf( (char) queryDataChar));
				q = new OrQuery(q, new NameStartsWithQuery(String.valueOf( (char) (queryDataChar+1))));
				
				if((char) queryDataChar == 'Y'){ //edge case as 26 is not divisible by 3, so requires one pair.
					setTitle("Places: " + (char) (queryDataChar) + (char) (queryDataChar+1));
				} else {
					q = new OrQuery(q, new NameStartsWithQuery(String.valueOf( (char) (queryDataChar+2))));
					setTitle("Places: " + (char) (queryDataChar) + (char) (queryDataChar+1) + (char) (queryDataChar+2));
				}
			}
			break;
		case 4: //places by type
			int queryData = intent.getIntExtra(EXTRA_QUERYDATA, 0);
			List<PlaceCategory> cats = new ArrayList<PlaceCategory>();
			cats.add(PlaceCategory.getCategoryByID(queryData));
			q = new CategoryQuery(cats);
			setTitle("Places: " + PlaceCategory.getCategoryByID(queryData).getName() + "s");
			break;
		default:
			q = null;
			break;
		}		
		//query the database
		final List<Integer> places = db.query(q,s);
		if(places.size() != 0){
			//set click listener for clicking on a list element
			getListView().setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int itemNoClicked,
						long arg3) {
					//Starts activity PlaceFullInfoActivity for the clicked place
					PlaceData place = db.getPlaceByID(places.get(itemNoClicked));
	            	Intent intent = new Intent(getApplicationContext(), PlaceFullInfoActivity.class);
	            	//put the place in the intent
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, places.get(itemNoClicked));
	                //calculate the distance between the user and the clicked place
	                double dist = PlaceData.getDistanceBetween(place.getLatitude(), place.getLongitude(), latitude, longitude);
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE, dist);
	                //put the background to include in the intent, "" as not passing an image.
	                intent.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, "");
	                startActivity(intent);
				}
			});	
			//set up the ArrayAdapter
			MyPlaceAdapter adapter = new MyPlaceAdapter(this,places);
			setListAdapter(adapter);
		} else {
			//if there were no results found from the query...
			List<String> noneFoundList = new ArrayList<String>(); noneFoundList.add("No Places Found");
			MyStringAdapter adapter = new MyStringAdapter(this,noneFoundList);
			setListAdapter(adapter);
		}
	}
	
	//method for creating a query that returns all places beginning with a number
	private DatabaseQuery createNumQuery(){
		DatabaseQuery q = new NameStartsWithQuery("0");
		for(int i = 1;i<=9;i++) q = new OrQuery(q,new NameStartsWithQuery(String.valueOf(i)));
		return q;
	}
	
	//ArrayAdapter for displaying a place in a listView
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
			//populate the view
			PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
			PlaceData item = db.getPlaceByID(values.get(position));
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listview_item_list_places, parent,false);
			//get the individual views within a single item of the listView
			TextView nameView = (TextView) rowView.findViewById(R.id.list_places_item_name);
			ImageView pictureView = (ImageView) rowView.findViewById(R.id.list_places_item_type);
			TextView distView = (TextView) rowView.findViewById(R.id.list_places_item_distance);
			//set the values of these views using information about the place
			nameView.setText(item.getName());
			pictureView.setImageResource(item.getCategory().getImageRef(item.getVisited()));
			distView.setText(PlaceFullInfoActivity.distanceAsString(PlaceData.getDistanceBetween(
					latitude,longitude,item.getLatitude(),item.getLongitude())));
			return rowView;
		}
	}
	
	//ArrayAdapter for displaying a string in a listView
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
			//populate the view
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
