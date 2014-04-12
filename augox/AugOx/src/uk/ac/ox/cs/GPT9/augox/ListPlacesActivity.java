package uk.ac.ox.cs.GPT9.augox;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.InLocusQuery;
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

public class ListPlacesActivity extends ListActivity {
	/*
	 * Intent Constants
	 */
	public final static String EXTRA_LATITUDE = "uk.ac.ox.cs.GPT9.augox.LATITUDE";
	public final static String EXTRA_LONGITUDE = "uk.ac.ox.cs.GPT9.augox.LONGITUDE";
	private double radius = 0;
	private double latitude = 0;
	private double longitude = 0;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		latitude = intent.getDoubleExtra(EXTRA_LATITUDE,Double.valueOf(0));
		longitude = intent.getDoubleExtra(EXTRA_LONGITUDE,Double.valueOf(0));
		final List<String> places = new ArrayList<String>();
		places.add("Local Places"); places.add("Places by Name"); places.add("Places by Type");
		
		//set click listener for clicking on a list element
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemNoClicked,
					long arg3) {
				//Starts activity PlaceFullInfoActivity for the clicked place
            	switch(itemNoClicked){
            	case 0:
                	Intent intent0 = new Intent(getApplicationContext(), ListPlacesItemsActivity.class);
                	intent0.putExtra(ListPlacesItemsActivity.EXTRA_LATITUDE, latitude);
                	intent0.putExtra(ListPlacesItemsActivity.EXTRA_LONGITUDE, longitude);
                	intent0.putExtra(ListPlacesItemsActivity.EXTRA_QUERYTYPE, 0);
                	startActivity(intent0);
            		break;
            	case 1:
            		Intent intent1 = new Intent(getApplicationContext(), ListPlacesBySubTypeActivity.class);
                	intent1.putExtra(ListPlacesBySubTypeActivity.EXTRA_LATITUDE, latitude);
                	intent1.putExtra(ListPlacesBySubTypeActivity.EXTRA_LONGITUDE, longitude);
                	intent1.putExtra(ListPlacesBySubTypeActivity.EXTRA_QUERYTYPE, 0);
                	startActivity(intent1);
            		break;
            	case 2:
            		Intent intent2 = new Intent(getApplicationContext(), ListPlacesBySubTypeActivity.class);
                	intent2.putExtra(ListPlacesBySubTypeActivity.EXTRA_LATITUDE, latitude);
                	intent2.putExtra(ListPlacesBySubTypeActivity.EXTRA_LONGITUDE, longitude);
                	intent2.putExtra(ListPlacesBySubTypeActivity.EXTRA_QUERYTYPE, 1);
                	startActivity(intent2);
            		break;
            	}
			}
		});	
		//set up the ArrayAdapter
		MyStringAdapter adapter = new MyStringAdapter(this,places);
		setListAdapter(adapter);
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
