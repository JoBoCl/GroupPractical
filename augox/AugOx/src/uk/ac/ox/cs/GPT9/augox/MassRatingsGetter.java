package uk.ac.ox.cs.GPT9.augox;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONValue;

import uk.ac.ox.cs.GPT9.augox.newsfeed.NewsFeed;
import android.os.AsyncTask;
import android.util.Log;

public class MassRatingsGetter
{
	private List<Integer> placeIds;
	private DatabaseDebuggerActivity activity;
	
	private PlaceData getPlace(int placeId) {
		return MainScreenActivity.getPlacesDatabase().getPlaceByID(placeId);
	}
	
	private String getFoursquareID(PlaceData place) {
		return place.getFourSquareID();//"4b647488f964a52087b42ae3";
	}
	
	// for the versioning required in all requests
	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}

	// for asynchronous calls
	class GetRatingsTask extends AsyncTask<Void, Void, Void> {
		
		@Override
	    protected Void doInBackground(Void... params) {
	    	
			// again, don't show these to anyone.  Shh it's a secret
	    	String apiKey = "K2SIZJG51WGYFS0MHMG2P2RPNBZQPNDRTQ0QHJ2AYCLHWQ03";
	        String apiSecret = "CI025OMN0VXDXAX0YVJEP4QVCI00AX30H3V3Z2GDFS23JQS3";
	        
	        try
	        {
	        	for (int value : placeIds) {
	        		PlaceData place = getPlace(value);
	        		
		        	// construct connection for getting venue data
		        	URL url = new URL("https://api.foursquare.com/v2/venues/" + getFoursquareID(place) + "?client_id=" + apiKey + "&client_secret=" + apiSecret + "&v=" + getDate());
		        	HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		        	org.json.simple.JSONObject obj = (org.json.simple.JSONObject)JSONValue.parse(NewsFeed.readResponse(connection));
	    			
		        	// if we have data can continue
		        	if (obj != null) {
		    			org.json.simple.JSONObject venue = (org.json.simple.JSONObject)(((org.json.simple.JSONObject)((org.json.simple.JSONObject)obj).get("response")).get("venue"));
		    			
		    			// get information for rating update and convert from 0-10 to 0-5 integer
		    			try {
			    			Double theirRating = null;
			    			if (venue.get("rating") instanceof Double) theirRating = (Double)venue.get("rating");
			    			int starRating = (int)(theirRating/2);
			    			place.updateRating(starRating);
		    			}
		    			catch (Exception e) {/*no rating available*/}
	
		    			// get link
		    			try {
			    			String shortUrl = venue.get("shortUrl").toString();
			    			place.updateFourSquareURL(shortUrl);
		    			}
		    			catch (Exception e) {/*no link available*/}
		    			
		    			// get phonenumber
		    			try {
		    				org.json.simple.JSONObject contact = (org.json.simple.JSONObject)venue.get("contact");
		    				String phonenumber = contact.get("phone").toString();
			    			place.updatePhoneNumber(phonenumber);
		    			}
		    			catch (Exception e) {/*no number available*/}
		    			
		    			Log.d("DBDBG", String.format("Fetched %s", place.getName()));
		    			
		    		}
	        	}
    			activity.reportDone();
	        } catch(Exception e) {
	        	// ignore failure; remember, we just want any data we can get, ignore what we can't
	        }
	    	
			return null;
	    }
	}
	
	// Place to get news about and NewsFeed to give it to
	public void giveData(DatabaseDebuggerActivity databasedebugger, List<Integer> placeIds) {
		this.placeIds = placeIds;
		this.activity = databasedebugger;
	}
	
	// Tell the module to start trying to get data from the Internet
	public void startCall() {
		new GetRatingsTask().execute();
	}
}