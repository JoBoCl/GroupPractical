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

//Activity for getting more information from the user about the list they want.
public class ListPlacesBySubTypeActivity extends ListActivity {
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
		//populate the listview based on the value passed in the intent of EXTRA_QUERYTYPE. This refers to the type of list the user wants (local places, places by category etc) 
		final List<String> items = new ArrayList<String>();
		switch(queryType){
			case 3: //places by name
				items.add("0-9");
				int count = 0;
				String createdString = "";
				//create a list f strings, 0-9 followed by the alphabet in triplets
				for(char ch = 'A' ; ch <= 'Z' ; ch++ ){
					if(count == 3) {items.add(createdString); createdString = ""; count = 0;}
					createdString = createdString + String.valueOf(ch); 
					count ++;
				}
				if(createdString != "") {items.add(createdString); createdString = ""; count = 0;}
				setTitle("Places by Name");
				break;
			case 4: //places by type
				//create a list of categories.
				for(PlaceCategory cat : PlaceCategory.values()){
					if(cat.getID() != 0) //i.e. not UNKNOWN (which is not intended to be used)
						items.add(cat.getName());
				}
				setTitle("Places by Type");
				break;
		}
		
		//set click listener for clicking on a list element
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemNoClicked,
					long arg3) {
				//Starts activity ListPlacesItemActivity to display places to the user
            	switch(queryType){
            	case 3: //places by name
            		Intent intent0 = new Intent(getApplicationContext(), ListPlacesItemsActivity.class);
                	intent0.putExtra(ListPlacesItemsActivity.EXTRA_LATITUDE, latitude);
                	intent0.putExtra(ListPlacesItemsActivity.EXTRA_LONGITUDE, longitude);
                	intent0.putExtra(ListPlacesItemsActivity.EXTRA_QUERYTYPE, 3);
                	//calculate the first character in the text of the item pressed.
                	if(itemNoClicked == 0){
                    	intent0.putExtra(ListPlacesItemsActivity.EXTRA_QUERYDATA,(int) '0' +itemNoClicked);
                    	} else {
                    	intent0.putExtra(ListPlacesItemsActivity.EXTRA_QUERYDATA, (int) 'A' + 3*(itemNoClicked-1));
                    }
                	startActivity(intent0);
            		break;
            	case 4: //places by type
            		Intent intent1 = new Intent(getApplicationContext(), ListPlacesItemsActivity.class);
                	intent1.putExtra(ListPlacesItemsActivity.EXTRA_LATITUDE, latitude);
                	intent1.putExtra(ListPlacesItemsActivity.EXTRA_LONGITUDE, longitude);
                	intent1.putExtra(ListPlacesItemsActivity.EXTRA_QUERYTYPE, 4);
                    intent1.putExtra(ListPlacesItemsActivity.EXTRA_QUERYDATA, itemNoClicked+1); //id of PlaceCategory
                    startActivity(intent1);
            		break;
            	}
			}
		});	
		//set up the ArrayAdapter
		MyStringAdapter adapter = new MyStringAdapter(this,items);
		setListAdapter(adapter);
	}
	
	//ArrayAdapter for displaying strings in a list view.
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
