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

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class FilterPanelActivity extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<FilterItem> items = new ArrayList<FilterItem>();
		items.add(new FilterItem("Visited", R.drawable.visitedicon, "filter_visited"));
		items.add(new FilterItem("Unvisited",R.drawable.unvisitedicon,"filter_unvisited"));
		for(PlaceCategory cat : PlaceCategory.values()){
			if(cat.getID() != 0)
				items.add(new FilterItem(cat.getName(),cat.getImageRef(false),cat.getFilter()));
		}
		
		FilterAdapter adapter = new FilterAdapter(this,items);
		setListAdapter(adapter);
	} 
	
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
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listview_item_filters, parent,false);
			final FilterItem item = values.get(position);
			ImageView iconView = (ImageView) rowView.findViewById(R.id.filters_image);
			TextView nameView = (TextView) rowView.findViewById(R.id.filters_name);
			final CheckBox checkView = (CheckBox) rowView.findViewById(R.id.filters_checkbox);
			iconView.setImageResource(item.imageRef);
			nameView.setText(item.name);
			checkView.setChecked(pref.getBoolean(item.filterString,true));
			
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
	
	
	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filters);
		LinearLayout l = (LinearLayout) findViewById(R.id.filtersLayout);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = pref.edit();
        //final CheckBox checkAllCB = new CheckBox(getApplicationContext());
        final CheckBox visitedCB = new CheckBox(getApplicationContext());
        final CheckBox unvisitedCB = new CheckBox(getApplicationContext());
        //checkAllCB.setText("Enable All"); 
        visitedCB.setText("Visited"); 
        visitedCB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_visited",visitedCB.isChecked());
				editor.commit();
			}
		});
        unvisitedCB.setText("Unvisited");
        unvisitedCB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean("filter_unvisited",unvisitedCB.isChecked());
				editor.commit();
			}
		});
        visitedCB.setChecked(pref.getBoolean("filter_visited", true));
        unvisitedCB.setChecked(pref.getBoolean("filter_unvisited", true));
        l.addView(visitedCB); l.addView(unvisitedCB);
		for(final PlaceCategory cat : PlaceCategory.values()){
			if(cat.getID() != 0){
				final CheckBox cb = new CheckBox(getApplicationContext());
	            cb.setText(cat.getName());
	            cb.setChecked(pref.getBoolean(cat.getFilter(), true));
	    		
	    		cb.setOnClickListener(new View.OnClickListener() {
	    			@Override
	    			public void onClick(View v) {
	    				editor.putBoolean(cat.getFilter(),cb.isChecked());
	    				editor.commit();
	    			}
	    		});
	            l.addView(cb);
			}
		}
	} 
	*/
}
