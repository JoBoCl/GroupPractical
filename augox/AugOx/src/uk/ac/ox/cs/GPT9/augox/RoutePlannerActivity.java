package uk.ac.ox.cs.GPT9.augox;

import java.util.List;

import uk.ac.ox.cs.GPT9.augox.route.IRoute;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class RoutePlannerActivity extends Activity {
	/*
	 * Intent Constants
	 */
	public final static String EXTRA_PLACELIST = "uk.ac.ox.cs.GPT9.augox.PLACELIST";
	private IRoute curRoute;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_planner);
		curRoute = MainScreenActivity.getCurrentRoute();
		
	    Button buttonFilters = (Button) findViewById(R.id.buttonRoutePlannerFilters);
		buttonFilters.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), FilterPanelActivity.class);
                startActivity(intent);				
			}
		});
		
		Button buttonContinue = (Button) findViewById(R.id.buttonRoutePlannerStart);
		buttonContinue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();			
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_planner, menu);
		return true;
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

}
