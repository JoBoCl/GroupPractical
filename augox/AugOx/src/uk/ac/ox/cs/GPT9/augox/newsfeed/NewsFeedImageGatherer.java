package uk.ac.ox.cs.GPT9.augox.newsfeed;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import uk.ac.ox.cs.GPT9.augox.PlaceData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

// gets the foursquare image (separate since we don't want to clog downloads)
public class NewsFeedImageGatherer {
	
	private NewsFeed newsFeed;
	private PlaceData place;
	private String imageUrl;
	
	class ImageGathererTask extends AsyncTask<Void, Void, Void> {
		@Override
	    protected Void doInBackground(Void... params) {
			// get image
			try {
    			URL photourl = new URL(imageUrl);
    			
    			HttpsURLConnection photoconnection = (HttpsURLConnection)photourl.openConnection();
    			photoconnection.setDoInput(true);
    			photoconnection.connect();
    	        InputStream input = photoconnection.getInputStream();
    	        Bitmap image = BitmapFactory.decodeStream(input);
    	        place.updateImage(newsFeed.getDrawable(image));
    	        newsFeed.imageUpdated();
			}
			catch (Exception e) {/*no photos available*/}
	    	return null;	
		}
	}
	
	public void startGathering() {
		new ImageGathererTask().execute();
	}
	
	public void giveData(PlaceData placeData, String foursquareImageUrl) {
		place = placeData;
		imageUrl = foursquareImageUrl;
	}
	
	public NewsFeedImageGatherer(NewsFeed newsFeedInitialiser) {
		newsFeed = newsFeedInitialiser;
	}
	
}