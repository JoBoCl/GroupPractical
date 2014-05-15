package uk.ac.ox.cs.GPT9.augox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class FilterPanelActivity extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//create a list of all of the filters
		List<FilterItem> items = new ArrayList<FilterItem>();
		items.add(new FilterItem("Visited", R.drawable.visitedicon, "filter_visited"));
		items.add(new FilterItem("Unvisited",R.drawable.unvisitedicon,"filter_unvisited"));
		for(PlaceCategory cat : PlaceCategory.values()){
			if(cat.getID() != 0)
				items.add(new FilterItem(cat.getName(),cat.getImageRef(false),cat.getFilter()));
		}
		//set up an adapter to display the filters in a listView
		FilterAdapter adapter = new FilterAdapter(this,items);
		setListAdapter(adapter);
	} 
	
	//A specialised ArrayAdapter for displaying items in a listView
	public class FilterAdapter extends ArrayAdapter<FilterItem> {
		private Context context;
		private List<FilterItem> values;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final SharedPreferences.Editor editor = pref.edit();
		
		public FilterAdapter(Context context, List<FilterItem> values){
			super(context,R.layout.listview_item_list_places,values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//set up an item of the listView
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listview_item_filters, parent,false);
			final FilterItem item = values.get(position);
			//find and set the image, text and checkbox of the item
			ImageView iconView = (ImageView) rowView.findViewById(R.id.filters_image);
			TextView nameView = (TextView) rowView.findViewById(R.id.filters_name);
			final CheckBox checkView = (CheckBox) rowView.findViewById(R.id.filters_checkbox);
			iconView.setImageResource(item.imageRef);
			nameView.setText(item.name);
			checkView.setChecked(pref.getBoolean(item.filterString,true));
			//click listener for changing the state of a filter
			checkView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					editor.putBoolean(item.filterString,checkView.isChecked());
					editor.commit();
				}
			});
			
			return rowView;
		}
	}
	
	//class representing an item in the listView representing one filter.
	private class FilterItem{
		private String name;
		private int imageRef;
		private String filterString;
		
		public FilterItem(String name, int imageRef, String filterString){
			this.name = name;
			this.imageRef = imageRef;
			this.filterString = filterString;
		}
	}
}
