package uk.ac.ox.cs.GPT9.augox.newsfeed;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONValue;

import uk.ac.ox.cs.GPT9.augox.PlaceData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

// sample request to examine data:  https://api.foursquare.com/v2/venues/4b647488f964a52087b42ae3?client_id=K2SIZJG51WGYFS0MHMG2P2RPNBZQPNDRTQ0QHJ2AYCLHWQ03&client_secret=CI025OMN0VXDXAX0YVJEP4QVCI00AX30H3V3Z2GDFS23JQS3&v=20140419

// News Module getting "tips" from "Foursquare", as well as other data to add to PlaceData
public class NewsModuleFoursquare implements INewsModule
{
	private NewsFeed newsFeed;
	private PlaceData place;
	
	private String getFoursquareID() {
		return place.getFourSquareID();//"4b647488f964a52087b42ae3";
	}
	
	// for the versioning required in all requests
	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}

	// for asynchronous calls
	class FoursquareTask extends AsyncTask<Void, Void, Void> {
		
		@Override
	    protected Void doInBackground(Void... params) {
	    	
			// again, don't show these to anyone.  Shh it's a secret
	    	String apiKey = "K2SIZJG51WGYFS0MHMG2P2RPNBZQPNDRTQ0QHJ2AYCLHWQ03";
	        String apiSecret = "CI025OMN0VXDXAX0YVJEP4QVCI00AX30H3V3Z2GDFS23JQS3";
	        
	        try
	        {
	        	// construct connection for getting venue data
	        	URL url = new URL("https://api.foursquare.com/v2/venues/" + getFoursquareID() + "?client_id=" + apiKey + "&client_secret=" + apiSecret + "&v=" + getDate());
	        	HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
	        	org.json.simple.JSONObject obj = (org.json.simple.JSONObject)JSONValue.parse(NewsFeed.readResponse(connection));
    			
	        	// if we have data can continue
	        	if (obj != null) {
	    			org.json.simple.JSONObject venue = (org.json.simple.JSONObject)(((org.json.simple.JSONObject)((org.json.simple.JSONObject)obj).get("response")).get("venue"));
	    			// get information for rating update and convert from 0-10 to 0-5 integer
	    			Double theirRating = null;
	    			if (venue.get("rating") instanceof Double) theirRating = (Double)venue.get("rating");
	    			int starRating = (int)(theirRating/2);
	    			place.updateRating(starRating);

	    			// get link
	    			try {
		    			String shortUrl = venue.get("shortUrl").toString();
		    			// TODO:  Actually use when database updated
	    			}
	    			catch (Exception e) {/*no photos available*/}
	    			
	    			// get image
	    			try {
		    			org.json.simple.JSONObject photoGroup = (org.json.simple.JSONObject)((org.json.simple.JSONArray)((org.json.simple.JSONObject)venue.get("photos")).get("groups")).get(0);
		    			org.json.simple.JSONObject photo = (org.json.simple.JSONObject)((org.json.simple.JSONArray)photoGroup.get("items")).get(0);
		    			String prefix = (String)photo.get("prefix");
		    			String suffix = (String)photo.get("suffix");
		    			URL photourl = new URL(prefix + "800x200" + suffix + "?client_id=" + apiKey + "&client_secret=" + apiSecret + "&v=" + getDate());
		    			
		    			HttpsURLConnection photoconnection = (HttpsURLConnection)photourl.openConnection();
		    			photoconnection.setDoInput(true);
		    			photoconnection.connect();
		    	        InputStream input = photoconnection.getInputStream();
		    	        Bitmap image = BitmapFactory.decodeStream(input);
		    	        place.updateImage(newsFeed.getDrawable(image));
	    			}
	    			catch (Exception e) {/*no photos available*/}
	    			
	    			// get tips
	    			org.json.simple.JSONObject group = (org.json.simple.JSONObject)((org.json.simple.JSONArray)((org.json.simple.JSONObject)venue.get("tips")).get("groups")).get(0);
	    			org.json.simple.JSONArray items = ((org.json.simple.JSONArray)group.get("items"));
	    			String[] tips = new String[items.size()];
	    			int[] tipLikes = new int[items.size()];
	    			for (int i = 0; i < tips.length; i++) {
	    				org.json.simple.JSONObject tipObject = (org.json.simple.JSONObject)items.get(i);
	    				tips[i] = tipObject.get("text").toString();
	    				
	    				// get likes for better prioritising
	    				Object likeCountObject = ((org.json.simple.JSONObject)tipObject.get("likes")).get("count");
	    				tipLikes[i] = Integer.parseInt(likeCountObject.toString());
	    				
	    				newsFeed.giveResult(tips[i], (int)((10*tips.length)/(i+1)) + tipLikes[i], NewsFeedSource.Foursquare);
	    			}
	    		}
	        } catch(Exception e) {
	        	// ignore failure; remember, we just want any data we can get, ignore what we can't
	        }
	    	
			return null;
	    }
	}
	
	// Place to get news about and NewsFeed to give it to
	public void giveData(NewsFeed targetNewsFeed, PlaceData targetPlace) {
		newsFeed = targetNewsFeed;
		place = targetPlace;
	}
	
	// Tell the module to start trying to get data from the Internet
	public void startCall() {
		new FoursquareTask().execute();
	}
}