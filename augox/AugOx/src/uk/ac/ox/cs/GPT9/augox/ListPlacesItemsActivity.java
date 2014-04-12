package uk.ac.ox.cs.GPT9.augox;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.CategoryQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.ClickedQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.InLocusQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.DatabaseSorter;
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

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
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
			break;
		case 1: //places by name, parameter in QUERYDATA
			q = new ClickedQuery(); //temp until needed query implemented
			break;
		case 2: //places by type, parameter in QUERYDATA
			int queryData = intent.getIntExtra(EXTRA_QUERYDATA, 0);
			List<PlaceCategory> cats = new ArrayList<PlaceCategory>();
			cats.add(PlaceCategory.getCategoryByID(queryData));
			q = new CategoryQuery(cats);
			break;
		default:
			q = null;
			break;
		}		

		final List<Integer> places = db.query(q,s);
		
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
		MyArrayAdapter adapter = new MyArrayAdapter(this,places);
		setListAdapter(adapter);
	}
	
	public class MyArrayAdapter extends ArrayAdapter<Integer> {
		private Context context;
		private List<Integer> values;
		public MyArrayAdapter(Context context,List<Integer> values){
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
			typeView.setImageResource(R.drawable.ic_launcher);
			distView.setText(String.format("%.1f", PlaceData.getDistanceBetween(
					latitude,longitude,item.getLatitude(),item.getLongitude()))+"km"); 
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
