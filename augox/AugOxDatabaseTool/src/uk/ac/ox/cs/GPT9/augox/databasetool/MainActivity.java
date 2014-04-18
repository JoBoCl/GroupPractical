package uk.ac.ox.cs.GPT9.augox.databasetool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
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
		String apihost = "http://api.openstreetmap.org/api/0.6/";
		String apicommand = "map?bbox=-1.2605,51.76,-1.26,51.7605";
		new FetchInternetTask().execute(apihost + apicommand);
	}
	
	private class FetchInternetTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			String input, result = "";
			
			try {
				//Authenticator.setDefault(new OSMAuthenticator());
				URL url = new URL(urls[0]);
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				while((input = in.readLine()) != null) {
					Log.d("James_DBG", input);
					result += input + "\n";
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
		
		private class OSMAuthenticator extends Authenticator {
			protected PasswordAuthentication getPasswordAuthentication() {
				String username = "oxfordgrouppracticalteam9@gmail.com";
				String password = "IDon'tCare";
				return new PasswordAuthentication(username, password.toCharArray());				
			}
		}
	}

}
