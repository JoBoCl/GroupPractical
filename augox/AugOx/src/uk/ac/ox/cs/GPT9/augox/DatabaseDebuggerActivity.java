package uk.ac.ox.cs.GPT9.augox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

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
	
	public void testFullInfo(View view) {
		Intent intent = new Intent(this, PlaceFullInfoActivity.class);
		intent.putExtra(PlaceFullInfoActivity.EXTRA_PLACE, 0);
		intent.putExtra(PlaceFullInfoActivity.EXTRA_BACKGROUND, "");
		intent.putExtra(PlaceFullInfoActivity.EXTRA_DISTANCE, 13.37);
		startActivity(intent);
	}

}
