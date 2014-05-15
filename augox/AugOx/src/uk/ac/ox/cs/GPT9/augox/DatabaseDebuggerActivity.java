package uk.ac.ox.cs.GPT9.augox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.AllQuery;
import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.DatabaseSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.NameSorter;
import uk.ac.ox.cs.GPT9.augox.dbsort.SortOrder;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

/**
 * This activity exists for debugging the database only, and is not part of the
 * main program. It consists of a handful of buttons that perform tasks used in
 * the construction of the database.
 */
public class DatabaseDebuggerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_database_debugger);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.database_debugger, menu);
		return true;
	}
	
	/*
	 * Populate database from binary file
	 */
	public void loadDatabase(View view) {
		AssetManager ast = getAssets();
		try {
			InputStream inp = ast.open("db.dat");
			MainScreenActivity.getPlacesDatabase().loadFromStream(inp);
			inp.close();
			Toast toast = Toast.makeText(getApplicationContext(), "File Loaded", Toast.LENGTH_SHORT);
			toast.show();
		} catch (IOException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	/*
	 * Write database to binary file
	 */
	public void writeDatabaseToExternal(View view) {
	    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
	    	//File root = Environment.getExternalStorageDirectory();
	    	File dir = getExternalFilesDir(null);
	    	File file = new File(dir, "testdb.dat");
	    	try {
	    		FileOutputStream f = new FileOutputStream(file);
	    		MainScreenActivity.getPlacesDatabase().writeToStream(f);
	    		f.close();
	    		Toast toast = Toast.makeText(getApplicationContext(), "File Written", Toast.LENGTH_SHORT);
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
	 * Launch place info screen for first place in database
	 */
	public void testFullInfo(View view) {
		Intent intent = new Intent(this, PlaceFullInfoActivity.class);
		intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, 0);
		intent.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, "");
		intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE, 13.37);
		startActivity(intent);
	}
	
	/*
	 * Pull foursquare data for all places in database and log to database
	 */
	public void fetchRatings(View view) {
		DatabaseQuery x = new AllQuery();
		DatabaseSorter y = new NameSorter(SortOrder.ASC);
		List<Integer> placeIds = MainScreenActivity.getPlacesDatabase().query(x,  y);
		MassRatingsGetter ratingsGetter = new MassRatingsGetter();
		ratingsGetter.giveData(this, placeIds);
		ratingsGetter.startCall();
	}
	
	/*
	 * Callback for foursquare data scraping
	 */
	public void reportDone() {
		Toast toast = Toast.makeText(getApplicationContext(), "Ratings Fetched", Toast.LENGTH_SHORT);
		toast.show();
	}

}
