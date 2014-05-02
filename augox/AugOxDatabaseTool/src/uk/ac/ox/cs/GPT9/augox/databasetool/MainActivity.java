package uk.ac.ox.cs.GPT9.augox.databasetool;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
	
	public void parseData(View view) {
		AssetManager ast = getAssets();
		XmlPullParserFactory parserFactory;
		XmlPullParser parser;
		try {
			// http://api.openstreetmap.org/api/0.6/map?bbox=minlong,minlat,maxlong,maxlat
			InputStream in = ast.open("map.osm");
			
			parserFactory = XmlPullParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			parser = parserFactory.newPullParser();
	        parser.setInput(in, null);
	        createDatabaseFromOSM(parser);
	        
			in.close();
			Toast toast = Toast.makeText(getApplicationContext(), "File Loaded", Toast.LENGTH_SHORT);
			toast.show();
		} catch (XmlPullParserException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			toast.show();
		} catch (IOException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void createDatabaseFromOSM(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		// Variables
		int eventType = parser.getEventType();
		Map<Long, OSMNode> nodes = new HashMap<Long, OSMNode>();
		Map<Long, OSMWay> ways = new HashMap<Long, OSMWay>();
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
		
		// Loop through all parsed nodes and ways, adding entries to database
		TextView outputpane = (TextView) findViewById(R.id.outputpane);
		result += "\n**Nodes**\n";
		//for(OSMNode node : nodes.values()) {
		for(Map.Entry<Long, OSMNode> entry : nodes.entrySet()) {
			long key = entry.getKey();
			OSMNode node = entry.getValue();
			if(node.getTagValue("shop") != null) {
				result += String.format("%s %s is a %s shop.\n", key,
						node.getTagValue("name"), node.getTagValue("shop"));
			}
		}
		result += "\n**Ways**\n";
		//for(OSMWay way : ways.values()) {
		for(Map.Entry<Long, OSMWay> entry : ways.entrySet()) {
			long key = entry.getKey();
			OSMWay way = entry.getValue();
			if(way.getTagValue("shop") != null) {
				result += String.format("%s %s is a %s shop.\n", key,
						way.getTagValue("name"), way.getTagValue("shop"));
			}
		}
		outputpane.setText(result);
	}

}
