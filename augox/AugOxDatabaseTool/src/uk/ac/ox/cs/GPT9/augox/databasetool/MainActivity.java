package uk.ac.ox.cs.GPT9.augox.databasetool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import uk.ac.ox.cs.GPT9.augox.OpeningHours;
import uk.ac.ox.cs.GPT9.augox.PlaceCategory;
import uk.ac.ox.cs.GPT9.augox.PlaceData;
import uk.ac.ox.cs.GPT9.augox.PlacesDatabase;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	/*
	 * Variables
	 */
	PlacesDatabase db = new PlacesDatabase();	// The current database

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("DBGJames", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView outputpane = (TextView) findViewById(R.id.outputpane);
		outputpane.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/*
	 * Handle press of 'Parse Data' button
	 */
	public void parseData(View view) {
		// Prepare asset manager and XML parser
		AssetManager ast = getAssets();
		XmlPullParserFactory parserFactory;
		XmlPullParser parser;
		
		// Parse data
		try {
			// Load data file
			// http://api.openstreetmap.org/api/0.6/map?bbox=minlong,minlat,maxlong,maxlat
			InputStream in = ast.open("map.osm");
			
			// Create database from data file
			parserFactory = XmlPullParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			parser = parserFactory.newPullParser();
	        parser.setInput(in, null);
	        List<PlaceData> places = createPlaceListFromOSM(parser);
	        db = new PlacesDatabase(places);
	        
	        // Clean up
			in.close();
			Toast toast = Toast.makeText(getApplicationContext(), "Data Parsed", Toast.LENGTH_SHORT);
			toast.show();
		} catch (XmlPullParserException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			toast.show();
		} catch (IOException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	/*
	 * Handle press of 'Write Database' button
	 */
	public void writeDatabase(View view) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
	    	File dir = getExternalFilesDir(null);
	    	File file = new File(dir, "db.dat");
	    	try {
	    		FileOutputStream f = new FileOutputStream(file);
	    		db.writeToStream(f);
	    		f.close();
	    		Toast toast = Toast.makeText(getApplicationContext(), "Database Written", Toast.LENGTH_SHORT);
				toast.show();
	    	} catch (FileNotFoundException e) {
	    		Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
				toast.show();
	    	} catch (IOException e) {
	    		Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
				toast.show();
	    	}
	    } else {
	    	Toast toast = Toast.makeText(getApplicationContext(), "External Storage Unwritable", Toast.LENGTH_SHORT);
			toast.show();
	    }
	}
	
	/*
	 * Create a list of PlaceData from the given XML parser
	 */
	private List<PlaceData> createPlaceListFromOSM(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		// Variables
		int eventType = parser.getEventType();
		Map<Long, OSMNode> nodes = new HashMap<Long, OSMNode>();
		Map<Long, OSMWay> ways = new HashMap<Long, OSMWay>();
		List<OSMItem> allitems = new ArrayList<OSMItem>();
		OSMNode currentNode = null;
		OSMWay currentWay = null;
		
		String result = "";
		
		// Loop through each tag in turn
		while(eventType != XmlPullParser.END_DOCUMENT) {
			// Get name of current tag
			String tagname = parser.getName();
			
			switch(eventType) {
			case XmlPullParser.START_TAG:
				// Is this a degenerate tag?
				boolean isDegenerate = parser.isEmptyElementTag();
				
				// If the tag is a node, add it to the node map
				if(tagname.equalsIgnoreCase("node")) {
					// Parse and add node
					long id = Long.parseLong(parser.getAttributeValue(
							XmlPullParser.NO_NAMESPACE, "id"));
					double lat = Double.parseDouble(parser.getAttributeValue(
							XmlPullParser.NO_NAMESPACE, "lat"));
					double lon = Double.parseDouble(parser.getAttributeValue(
							XmlPullParser.NO_NAMESPACE, "lon"));
					OSMNode newnode = new OSMNode(lat, lon); 
					nodes.put(id, newnode);
					allitems.add(newnode);
					
					// Will we be continuing to parse this node in later steps?
					if(!isDegenerate) currentNode = newnode;
				}
				
				// If the tag is a way, add it to the way map
				if(tagname.equalsIgnoreCase("way")) {
					// Parse and add way
					long id = Long.parseLong(parser.getAttributeValue(
							XmlPullParser.NO_NAMESPACE, "id"));
					OSMWay newway = new OSMWay();
					ways.put(id, newway);
					allitems.add(newway);
					
					// Will we be continuing to parse this way in later steps?
					if(!isDegenerate) currentWay = newway;
				}
				
				// If the tag is a tag, attach it to the current node / way
				if(tagname.equalsIgnoreCase("tag")) {
					// Parse key and value
					String key = parser.getAttributeValue(
							XmlPullParser.NO_NAMESPACE, "k");
					String value = parser.getAttributeValue(
							XmlPullParser.NO_NAMESPACE, "v");
					
					// Attach to current node / way
					if(currentNode != null) currentNode.addTag(key, value);
					if(currentWay != null) currentWay.addTag(key, value);
				}
				
				// If the tag is a node of a way, attach it to the current way
				if(tagname.equalsIgnoreCase("nd")) {
					// Parse reference id
					long id = Long.parseLong(parser.getAttributeValue(
							XmlPullParser.NO_NAMESPACE, "ref"));
					
					// Attach to current way
					if(currentWay != null) currentWay.addNode(nodes.get(id));
				}
				break;
				
			case XmlPullParser.TEXT:
				break;
				
			case XmlPullParser.END_TAG:
				// Stop parsing the current node / way
				if(tagname.equalsIgnoreCase("node")) currentNode = null;
				if(tagname.equalsIgnoreCase("way")) currentWay = null;
				break;
				
			default:
				break;
			}
			
			// Next tag
			eventType = parser.next();
		}
		
		// Loop through all parsed nodes and ways, building a list of places
		List<PlaceData> places = new ArrayList<PlaceData>();
		for(OSMItem item : allitems) {
			// PlaceData being made
			PlaceData n = null;
			// Bar
			if(item.hasTag("amenity", "bar")) {
				n = new PlaceData(item.getTagValue("name"),
						item.getLatitude(), item.getLongitude(), 0, false,
						PlaceCategory.BAR, "", new OpeningHours(), "");
			}
			// Restaurant
			if(item.hasTag("amenity", "restaurant")) {
				n = new PlaceData(item.getTagValue("name"),
						item.getLatitude(), item.getLongitude(), 0, false,
						PlaceCategory.RESTAURANT, "", new OpeningHours(), "");
			}
			// College
			if(item.hasTag("amenity", "college")
					|| item.hasTag("amenity", "university")) {
				n = new PlaceData(item.getTagValue("name"),
						item.getLatitude(), item.getLongitude(), 0, false,
						PlaceCategory.COLLEGE, "", new OpeningHours(), "");
			}
			// Museum
			if(item.hasTag("tourism", "museum")) {
				n = new PlaceData(item.getTagValue("name"),
						item.getLatitude(), item.getLongitude(), 0, false,
						PlaceCategory.MUSEUM, "", new OpeningHours(), "");
			}
			// Add PlaceData to list if one was made
			if(n != null) places.add(n);
		}
		
		// Update output pane
		TextView outputpane = (TextView) findViewById(R.id.outputpane);
		result += String.format("%s entries generated\n", places.size());
		for(PlaceData place : places) {
			result += String.format("%s (%s)\n", place.getName(),
					place.getCategory().getName());
		}
		outputpane.setText(result);
		
		// Return built list of places
		return places;
	}

}
