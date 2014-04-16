package uk.ac.ox.cs.GPT9.augox.databasetool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

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
	
	public void fetchData(View view) {
		new FetchInternetTask().execute("http://www.google.co.uk/");
	}
	
	private class FetchInternetTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			String input, result = "";
			
			try {
				URL url = new URL(urls[0]);
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				while((input = in.readLine()) != null) {
					result += input;
				}
				in.close();
			} catch (Exception e) {
				String foo = e.toString();
				if(foo == null) foo = "null";
				Log.e("James_DBG", foo);
			}
			
			return result;
		}
		
		protected void onPostExecute(String result) {
			TextView outputpane = (TextView) findViewById(R.id.outputpane);
			outputpane.setText(result);
		}
	}

}
