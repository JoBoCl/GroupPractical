package uk.ac.ox.cs.GPT9.augox;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
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

}
