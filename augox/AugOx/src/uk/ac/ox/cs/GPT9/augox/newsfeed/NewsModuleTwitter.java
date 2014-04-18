package uk.ac.ox.cs.GPT9.augox.newsfeed;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONValue;

import android.os.AsyncTask;
import uk.ac.ox.cs.GPT9.augox.PlaceData;

// News Module getting "tweets" from popular social media site "Twitter"
public class NewsModuleTwitter implements INewsModule
{
	private NewsFeed _newsFeed;
	private PlaceData _place;
	
	private String getTwitterHandle() {
		return "WhiteRabbitOx";
	}
	
	// for asynchronous calls
	class TwitterTask extends AsyncTask<Void, Void, Void> {
		
		// Tries to fetch "tweets" from the url given
		private String[] fetchTweets(String endPointUrl, String bearerToken) {
		    	
			HttpsURLConnection connection = null;
		    				
		    try {
	    		URL url = new URL(endPointUrl); 
	    		connection = (HttpsURLConnection) url.openConnection();           
	    		connection.setDoInput(true); 
	    		connection.setRequestMethod("GET"); 
	    		connection.setRequestProperty("Host", "api.twitter.com");
	    		connection.setRequestProperty("User-Agent", "Augmented Reaility Oxford");
	    		connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
	    		connection.setUseCaches(false);

	    		// Parse the JSON response into a JSON object
	    		org.json.simple.JSONArray obj = (org.json.simple.JSONArray)JSONValue.parse(NewsFeed.readResponse(connection));
	    		
	    		// make sure we get something
	    		if (obj != null) {
	    			String[] tweets = new String[obj.size()];
	    			for (int i = 0; i < obj.size(); i++) {
	    				// return date and contents
	    				tweets[i] = ((org.json.simple.JSONObject)obj.get(i)).get("created_at").toString().substring(0, 10) + " - " + ((org.json.simple.JSONObject)obj.get(i)).get("text").toString();
	    			}
	    			return tweets;
	    		}
	    		return new String[0];
	    	}
	    	catch (Exception e) {
	    		// ignore failure; remember, we just want any data we can get, ignore what we can't
	    		return new String[0];
	    	}
		    finally { // clean up
	    		if (connection != null) {
	    			connection.disconnect();
	    		}
	    	}
	    }
		
		// tries to get a token for future authorisation
		private String requestBearerToken(String endPointUrl) throws IOException {
	    	HttpsURLConnection connection = null;
	    	// don't let anyone see these!  I'm serious guys!
	    	String encodedCredentials = encodeKeys("HcLGkv2qXouqWeRyG0PeZRvJP","VjHE1hXZ7zL28YXBISlbVUwA9iYJlQFk4aMNATLNcqBaUSHVxc");
	    		
	    	try {
	    		// set up connection
	    		URL url = new URL(endPointUrl); 
	    		connection = (HttpsURLConnection) url.openConnection();           
	    		connection.setDoOutput(true);
	    		connection.setDoInput(true); 
	    		connection.setRequestMethod("POST"); 
	    		connection.setRequestProperty("Host", "api.twitter.com");
	    		connection.setRequestProperty("User-Agent", "Augmented Reality Oxford");
	    		connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
	    		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); 
	    		connection.setRequestProperty("Content-Length", "29");
	    		connection.setUseCaches(false);
	    		
	    		// write to connection
	    		try {
		    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
		    		writer.write("grant_type=client_credentials");
		    		writer.flush();
		    		writer.close();
		    	}
		    	catch (IOException e) {
		    		// ignore failure; remember, we just want any data we can get, ignore what we can't
		    	}
	    			
	    		// Parse the JSON response into a JSON object
	    		Object parse = JSONValue.parse(NewsFeed.readResponse(connection));
				org.json.simple.JSONObject obj = (org.json.simple.JSONObject)parse;
	    			
				// get token from result
	    		if (obj != null) {
	    			String tokenType = (String)obj.get("token_type");
	    			String token = (String)obj.get("access_token");
	    		
	    			return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
	    		}
	    		return "";
	    		
	    	}
	    	catch (Exception e) {
	    		// ignore failure; remember, we just want any data we can get, ignore what we can't
	    		return "";
	    	}
	    	finally { // tidy up
	    		if (connection != null) {
	    			connection.disconnect();
	    		}
	    	}
	    }
		
		// helper function to encode the keys
		private  String encodeKeys(String consumerKey, String consumerSecret) {
	    	try {
	    		String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
	    		String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");
	    		
	    		String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
	    		byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
	    		
	    		return new String(encodedBytes);  
	    	}
	    	catch (UnsupportedEncodingException e) {
	    		return new String();
	    	}
	    }
		
	    @Override
	    protected Void doInBackground(Void... params) {

	    	// step 1:  get authorisation
			String bearerToken = "";
	    	try {
    			bearerToken = requestBearerToken("https://api.twitter.com/oauth2/token");
    		}
	    	catch (Exception ex) {
    			// ignore failure; remember, we just want any data we can get, ignore what we can't
    		}
	    	
	    	// step 2:  get tweets
			try {
				String[] tweets = fetchTweets("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + getTwitterHandle() + "&count=10", bearerToken);
				for (int i = 0; i < tweets.length; i++)
					_newsFeed.GiveResult(tweets[i], (int)((10*tweets.length)/(i+1)));
            }
			catch (Exception ex) {
            	// ignore failure; remember, we just want any data we can get, ignore what we can't
            }
	    	
			return null;
	    }
	}
	
	// Place to get news about and NewsFeed to give it to
	public void GiveData(NewsFeed newsFeed, PlaceData place)
	{
		_newsFeed = newsFeed;
		_place = place;
	}
	
	// Tell the module to start trying to get data from the Internet
	public void StartCall()
	{
		new TwitterTask().execute();
	}
}