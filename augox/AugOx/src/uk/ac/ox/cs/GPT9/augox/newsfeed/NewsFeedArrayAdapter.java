package uk.ac.ox.cs.GPT9.augox.newsfeed;
 
import uk.ac.ox.cs.GPT9.augox.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class NewsFeedArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
 
	public NewsFeedArrayAdapter(Context context, String[] values) {
		super(context, R.layout.listview_item_news_feed, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.listview_item_news_feed, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.newsFeedText);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.newsFeedSource);
		textView.setText(values[position].substring(1));
 
		// Change icon based on name
		String s = values[position].substring(0,1);
 
		if (s.equals("T")) {
			imageView.setImageResource(R.drawable.twittericon);
		} else if (s.equals("F")) {
			imageView.setImageResource(R.drawable.foursquareicon);
		}
 
		return rowView;
	}
}