package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListChooseActivity extends ListActivity {
	/*
	 * Intent Constants
	 */
	public final static String EXTRA_LATITUDE = "uk.ac.ox.cs.GPT9.augox.LATITUDE";
	public final static String EXTRA_LONGITUDE = "uk.ac.ox.cs.GPT9.augox.LONGITUDE";

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		final List<String> items = new ArrayList<String>();
		items.add("Local Places");
		items.add("Places by Name");
		items.add("Places by Type");
		
		//set click listener for clicking on a list element
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
					Log.d("arg2 test",String.valueOf(arg2));
					if(arg2 == 0){
						Intent intent = new Intent(getApplicationContext(), ListPlacesActivity.class);
		            	intent.putExtra(ListPlacesActivity.EXTRA_LATITUDE, intent.getDoubleExtra(EXTRA_LATITUDE,Double.valueOf(0)));
		            	intent.putExtra(ListPlacesActivity.EXTRA_LONGITUDE, intent.getDoubleExtra(EXTRA_LONGITUDE,Double.valueOf(0)));
		            	intent.putExtra(ListPlacesActivity.QUERY_TYPE, arg2);
		            	startActivity(intent);
					}
			}
		});	
		//set up the ArrayAdapter
		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this,items);
		setListAdapter(adapter);
	}
	
	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		private Context context;
		private List<String> values;
		public MySimpleArrayAdapter(Context context,List<String> values){
			super(context,R.layout.listview_item_list_places,values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String item = values.get(position);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listview_item_list_choose, parent,false);
			TextView nameView = (TextView) rowView.findViewById(R.id.list_choose_item_name);
			nameView.setText(item);
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
