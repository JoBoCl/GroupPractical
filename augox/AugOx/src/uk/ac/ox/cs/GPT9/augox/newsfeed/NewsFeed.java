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
	private List<INewsModule> newsModules = new ArrayList<INewsModule>(); // each represents a source
	private List<NewsFeedItem> newsItems = new ArrayList<NewsFeedItem>(); // each represents an item on the list
	private PlaceData place; // place we want news about
	private PlaceFullInfoActivity activity; // parent activity for displaying results
	private boolean dirty = true; // represents whether next call to start should be respected
	
	//function common to News Modules for reading a stream of information from the Internet
	public static String readResponse(HttpsURLConnection connection) {
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
    	} catch (IOException e) {
    		e.printStackTrace(); 
    		throw new IllegalArgumentException(e); 
    	}
    }
	
	// just a helper method getting the resources associated with the activity so downloading pictures can work
	public BitmapDrawable getDrawable(Bitmap image) {
		return new BitmapDrawable(activity.getResources(), image);
	}
	
	// was considering storing as tuple, but extensionability for appearance details, links etc. important
	class NewsFeedItem {
		private String text;
		private int priority;
		private NewsFeedSource source;
		
		public String Text() {return text;}
		public int Priority() {return priority;}
		public NewsFeedSource Source() {return source;}
		
		public NewsFeedItem(String textInitialiser, int priorityInitialiser, NewsFeedSource sourceInitialiser) {
			text = textInitialiser;
			priority = priorityInitialiser;
			source = sourceInitialiser;
		}
	}
	
	// insertion of new news item using priority
	private void addNewItem(NewsFeedItem item) {
		for (int i = 0; i < newsItems.size(); i++) {
			if (item.Priority() > newsItems.get(i).Priority()) {
				newsItems.add(i, item);
				return;
			}
		}
		newsItems.add(newsItems.size(), item);
	}
	
	// returns the _newsFeedItems as an array of Strings
	private String[] getItems() {
		String[] items = new String[newsItems.size()];
		for (int i = 0; i < newsItems.size(); i++) {
			items[i] =  newsItems.get(i).Text();
			switch (newsItems.get(i).Source()) {
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
		activity.runOnUiThread(new Runnable(){
	        public void run() {
	        	ListView feedView = (ListView)activity.findViewById(R.id.listViewFeed);
	        	ArrayAdapter<String> adapter = new NewsFeedArrayAdapter(activity, results);
				feedView.setAdapter(adapter);
				activity.DisplayStars(); // redisplay how many stars there are; might have changed in download
				activity.DisplayImage(); // same for place image
				activity.DisplayFoursquareLink(); // and foursquare link
				activity.DisplayPhoneNumber(); // and phone number off foursquare
	        }
	    });
	}
	
	public void setTarget(PlaceData targetPlace, PlaceFullInfoActivity newActivity) {
		activity = newActivity;
		if (place != targetPlace) {
			dirty = true; // we will need to recalculate when StartCalls() used
			
			place = targetPlace;
			newsItems.clear(); // we will recalculate and don't want old data
			
			// initially empty newsfeed appearance
			String[] startingValue = new String[] {"No news to show yet"};
			ListView feedView = (ListView)newActivity.findViewById(R.id.listViewFeed);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.news_feed_item_1, startingValue);
			feedView.setAdapter(adapter);
		}
		else {
			updateNewsFeed(getItems());
		}
	}
	
	// Tell modules to start their asynchronous calls
	public void startCalls() {
		if (dirty) {
			dirty = false;
			// Iterate through, telling them to start
			for (INewsModule newsModule : newsModules) {
				newsModule.giveData(this, place);
				newsModule.startCall();
			}
		}
	}
	
	// For modules to return results
	public void giveResult(final String output, int priority, NewsFeedSource source) {
		addNewItem(new NewsFeedItem(output, priority, source));
		updateNewsFeed(getItems());
	}
	
	// Public constructor
	public NewsFeed() {
		// basic initialisation (extra news sources can be added here)
		newsModules.add(new NewsModuleTwitter());
		newsModules.add(new NewsModuleFoursquare());
	}
}