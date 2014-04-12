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
import android.widget.TextView;
import android.widget.Toast;

public class ListPlacesSubTypeActivity extends ListActivity {
	/*
	 * Intent Constants
	 */
	public final static String EXTRA_LATITUDE = "uk.ac.ox.cs.GPT9.augox.LATITUDE";
	public final static String EXTRA_LONGITUDE = "uk.ac.ox.cs.GPT9.augox.LONGITUDE";
	public final static String EXTRA_QUERYTYPE = "uk.ac.ox.cs.GPT9.augox.QUERYTYPE";
	private double latitude = 0;
	private double longitude = 0;
	private int queryType = 0;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		latitude = intent.getDoubleExtra(EXTRA_LATITUDE,Double.valueOf(0));
		longitude = intent.getDoubleExtra(EXTRA_LONGITUDE,Double.valueOf(0));
		queryType = intent.getIntExtra(EXTRA_QUERYTYPE, 0);
		final List<String> items = new ArrayList<String>();
		switch(queryType){
			case 0:
				for(char ch = '0' ; ch <= '9' ; ch++ )
			        items.add(String.valueOf(ch));
				for(char ch = 'A' ; ch <= 'Z' ; ch++ )
			        items.add(String.valueOf(ch));
				break;
			case 1:
				for(PlaceCategory cat : PlaceCategory.values()){
					items.add(cat.getName());
				}
				break;
		}
		
		//set click listener for clicking on a list element
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemNoClicked,
					long arg3) {
				//Starts activity PlaceFullInfoActivity for the clicked place
            	switch(queryType){
            	case 0:
            		Intent intent0 = new Intent(getApplicationContext(), ListPlacesItemActivity.class);
                	intent0.putExtra(ListPlacesItemActivity.EXTRA_LATITUDE, latitude);
                	intent0.putExtra(ListPlacesItemActivity.EXTRA_LONGITUDE, longitude);
                	intent0.putExtra(ListPlacesItemActivity.EXTRA_QUERYTYPE, 1);
                	if(itemNoClicked <= 9){
                    	intent0.putExtra(ListPlacesItemActivity.EXTRA_QUERYDATA, 48+itemNoClicked);
                    	} else {
                    	intent0.putExtra(ListPlacesItemActivity.EXTRA_QUERYDATA, 47+itemNoClicked); //57-10
                    }
                	startActivity(intent0);
            		break;
            	case 1:
            		Intent intent1 = new Intent(getApplicationContext(), ListPlacesItemActivity.class);
                	intent1.putExtra(ListPlacesItemActivity.EXTRA_LATITUDE, latitude);
                	intent1.putExtra(ListPlacesItemActivity.EXTRA_LONGITUDE, longitude);
                	intent1.putExtra(ListPlacesItemActivity.EXTRA_QUERYTYPE, 2);
                    intent1.putExtra(ListPlacesItemActivity.EXTRA_QUERYDATA, itemNoClicked); //id of PlaceCategory
                    startActivity(intent1);
            		break;
            	}
			}
		});	
		//set up the ArrayAdapter
		MyStringAdapter adapter = new MyStringAdapter(this,items);
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
