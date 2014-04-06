package uk.ac.ox.cs.GPT9.augox;

import java.io.*;

import android.app.Activity;
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
			InputStream inp = ast.open("testdb.dat");
			MainScreenActivity.getPlacesDatabase().loadFromStream(inp);
			inp.close();
		} catch (IOException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	public void writeDatabaseToExternal(View view) {
	    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
	    	File root = Environment.getExternalStorageDirectory();
	    	File dir = new File(root.getAbsolutePath() + "/augoxdbg");
	    	dir.mkdirs();
	    	File file = new File(dir, "testdb.dat");
	    	try {
	    		FileOutputStream f = new FileOutputStream(file);
	    		MainScreenActivity.getPlacesDatabase().writeToStream(f);
	    		f.close();
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

}
