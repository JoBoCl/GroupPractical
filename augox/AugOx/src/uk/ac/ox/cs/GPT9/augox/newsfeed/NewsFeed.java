package uk.ac.ox.cs.GPT9.augox.newsfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpStatus;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import uk.ac.ox.cs.GPT9.augox.PlaceData;
import uk.ac.ox.cs.GPT9.augox.PlaceFullInfoActivity;
import uk.ac.ox.cs.GPT9.augox.R;

// Represents the News Feed:  PlaceFullInfoActivity's list of news items down the right side of the screen
public class NewsFeed {
	private List<INewsModule> _newsModules = new ArrayList<INewsModule>(); // each represents a source
	private List<NewsFeedItem> _newsItems = new ArrayList<NewsFeedItem>(); // each represents an item on the list
	private PlaceData _place; // place we want news about
	private PlaceFullInfoActivity _activity; // parent activity for displaying results
	
	//function common to News Modules for reading a stream of information from the Internet
	public static String ReadResponse(HttpsURLConnection connection) {
    	try {
    		StringBuilder str = new StringBuilder();
    		
    		int responseCode = connection.getResponseCode();
    		if (responseCode != HttpStatus.SC_OK) {
    			if (responseCode != -1) {
	    			InputStream errorStream = connection.getErrorStream();
	    			InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
	    			BufferedReader br = new BufferedReader(inputStreamReader);
	    			String line = "";
	    			while((line = br.readLine()) != null) {
	    				str.append(line + System.getProperty("line.separator"));
	    			}
    			}
    			else {
     				System.out.println("Response code = -1, so can't get error stream");
    			}
    			return str.toString();
    		}
    		InputStream inputStream = connection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(inputStreamReader);
    		String line = "";
    		while((line = br.readLine()) != null) {
    			str.append(line + System.getProperty("line.separator"));
    		}
    		return str.toString();
    	}
    	catch (IOException e) {
    		e.printStackTrace(); 
    		throw new IllegalArgumentException(e); 
    	}
    }
	
	// just a helper method getting the resources associated with the activity so downloading pictures can work
	public BitmapDrawable GetDrawable(Bitmap image) {
		return new BitmapDrawable(_activity.getResources(), image);
	}
	
	// was considering storing as tuple, but extensionability for appearance details, links etc. important
	class NewsFeedItem {
		private String _text;
		private int _priority;
		private NewsFeedSource _source;
		
		public String Text() {return _text;}
		public int Priority() {return _priority;}
		public NewsFeedSource Source() {return _source;}
		
		public NewsFeedItem(String text, int priority, NewsFeedSource source) {
			_text = text;
			_priority = priority;
			_source = source;
		}
	}
	
	// insertion of new news item using priority
	private void addNewItem(NewsFeedItem item) {
		for (int i = 0; i < _newsItems.size(); i++) {
			if (item.Priority() > _newsItems.get(i).Priority()) {
				_newsItems.add(i, item);
				return;
			}
		}
		_newsItems.add(_newsItems.size(), item);
	}
	
	// returns the _newsFeedItems as an array of Strings
	private String[] getItems() {
		String[] items = new String[_newsItems.size()];
		for (int i = 0; i < _newsItems.size(); i++) {
			items[i] =  _newsItems.get(i).Text();
			switch (_newsItems.get(i).Source()) {
			case Twitter:
				items[i] = "T" + items[i];
				break;
			case Foursquare:
				items[i] = "F" + items[i];
				break;
			}
		}
		return items;
	}
	
	// final step in getting new data:  telling the uithread to update the news feed
	// possibly not the most efficient way, but makes encapsulation etc. nice and there will be very few updates
	private void updateNewsFeed(final String[] results) {
		_activity.runOnUiThread(new Runnable(){
	        public void run() {
	        	ListView feedView = (ListView)_activity.findViewById(R.id.listViewFeed);
				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(_activity, R.layout.news_feed_item_1, results);
	        	ArrayAdapter<String> adapter = new NewsFeedArrayAdapter(_activity, results);
				feedView.setAdapter(adapter);
				_activity.DisplayStars(); // redisplay how many stars there are; might have changed in download
				_activity.DisplayImage(); // same for place image
	        }
	    });
	}
	
	// Public constructor
	public NewsFeed(PlaceData place, PlaceFullInfoActivity activity) {
		// basic initialisation (extra news sources can be added here)
		_place = place;
		_newsModules.add(new NewsModuleTwitter());
		_newsModules.add(new NewsModuleFoursquare());
		_activity = activity;
		
		// initially empty newsfeed appearance
		String[] startingValue = new String[] {"No news to show yet"};
		ListView feedView = (ListView)activity.findViewById(R.id.listViewFeed);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.news_feed_item_1, startingValue);
		feedView.setAdapter(adapter);
	}
	
	// Tell modules to start their asynchronous calls
	public void StartCalls() {
		// Iterate through, telling them to start
		for (INewsModule newsModule : _newsModules) {
			newsModule.GiveData(this, _place);
			newsModule.StartCall();
		}
	}
	
	// For modules to return results
	public void GiveResult(final String output, int priority, NewsFeedSource source) {
		addNewItem(new NewsFeedItem(output, priority, source));
		updateNewsFeed(getItems());
	}
}