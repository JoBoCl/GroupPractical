package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.List;
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
		radius = 100;
		
		//final version will use this after database implemented
		//PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
		//final List<PlaceData> places = db.getPlacesInLocus(latitude,longitude,radius);
		final List<PlaceData> places = new ArrayList<PlaceData>();
		//test place data
		OpeningHours h = null;
		PlaceCategory p = null;
		double d = (double) 0;
		for(int i = 0; i < 30; i ++){
			places.add(new PlaceData("Test Place " + String.valueOf(i),d,d,0,false,p,"desc1",h,"test","4b647488f964a52087b42ae3"));
		}		
		
		//set click listener for clicking on a list element
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Starts activity PlaceFullInfoActivity for the clicked place
            	Intent intent = new Intent(getApplicationContext(), PlaceFullInfoActivity.class);
            	//put the place in the intent
                intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, ""); //places.get(arg2)
                //put the background to include in the intent
                intent.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, "");
                startActivity(intent);
			}
		});	
		//set up the ArrayAdapter
		MyArrayAdapter adapter = new MyArrayAdapter(this,places);
		setListAdapter(adapter);
	}
	
	public class MyArrayAdapter extends ArrayAdapter<PlaceData> {
		private Context context;
		private List<PlaceData> values;
		public MyArrayAdapter(Context context,List<PlaceData> values){
			super(context,R.layout.listview_item_list_places,values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PlaceData item = values.get(position);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listview_item_list_places, parent,false);
			TextView nameView = (TextView) rowView.findViewById(R.id.list_places_item_name);
			ImageView typeView = (ImageView) rowView.findViewById(R.id.list_places_item_type);
			TextView distView = (TextView) rowView.findViewById(R.id.list_places_item_distance);
			nameView.setText(item.getName());
			//after we have icons for each type of place, set it here
			typeView.setImageResource(R.drawable.ic_launcher);
			distView.setText(String.format("%.1f", getDistanceBetween(
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
	
	public double getDistanceBetween(	double lat1, double long1,
			double lat2, double long2	) {
		// Formula based on spherical law of cosines
		// http://www.movable-type.co.uk/scripts/latlong.html
		double lat1r = Math.toRadians(lat1);
		double lat2r = Math.toRadians(lat2);
		double dlongr = Math.toRadians(long2 - long1);
		double earthrad = 6371;		// Radius of earth (km)
		double dist = Math.acos(Math.sin(lat1r) * Math.sin(lat2r)
				+ Math.cos(lat1r) * Math.cos(lat2r) * Math.cos(dlongr)) * earthrad;
		return dist;
	}
	
}
