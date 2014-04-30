package uk.ac.ox.cs.GPT9.augox.databasetool;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
			InputStream in = ast.open("map.osm");
			
			parserFactory = XmlPullParserFactory.newInstance();
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
		int eventType = parser.getEventType();
		while(eventType != XmlPullParser.END_DOCUMENT) {
			String tagname = parser.getName();
			
			switch(eventType) {
			case XmlPullParser.START_TAG:
				break;
				
			case XmlPullParser.TEXT:
				break;
				
			case XmlPullParser.END_TAG:
				break;
				
			default:
				break;
			}
			
			eventType = parser.next();
		}
	}

}
