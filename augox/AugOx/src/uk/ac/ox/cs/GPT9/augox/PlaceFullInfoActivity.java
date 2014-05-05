package uk.ac.ox.cs.GPT9.augox;

import uk.ac.ox.cs.GPT9.augox.route.*;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import uk.ac.ox.cs.GPT9.augox.newsfeed.NewsFeed;


public class PlaceFullInfoActivity extends Activity {
	/*
	 * Intent Constants
	 */
	public final static String EXTRA_PLACE = "uk.ac.ox.cs.GPT9.augox.PLACE";
	public final static String EXTRA_BACKGROUND = "uk.ac.ox.cs.GPT9.augox.BACKGROUND";
	public final static String EXTRA_DISTANCE = "uk.ac.ox.cs.GPT9.augox.DISTANCE";
	
	private PlaceData _place; // = new PlaceData("Matthew's Awesome Pub", 0.0, 0.0, 5, false, PlaceCategory.BAR, "This isn't the greatest pub in the world.  This is a tribute.  I'm also going to try to make this description long so I can make sure it doesn't go too far to the right and wraps around properly, kind of ruining the preceding one-liner.  Which is a great shame, really.  At some point I'm going to have to make a way of sourcing the description from the Internet, which is going to be annoying and hard and stuff but at least for now I can get this prototype working.  And hey, getting the pretty layout is what really matters.  Having the most up-to-date data is not as important, as xkcd 937 tells us (there's an xkcd for everything)", new OpeningHours(new ArrayList<OpeningHours.Period>()));
	private int _placeId;
	private double _distance;
	private NewsFeed _newsFeed;
	
	// returns a string representing the distance in metres or kilometres
	public static String distanceAsString(double distanceAsKm) {
        if (distanceAsKm < 0.95) {
        	String result = new DecimalFormat(".#").format(distanceAsKm).substring(1) + "00 m";
        	if (result.contains("000 m")) return "0 m";
        	else return result;
        }
        else {
        	return new DecimalFormat("#.#").format(distanceAsKm) + " km";
        }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// standard boilerplate
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_full_info);
		
		// load place from intent
		Intent intent = getIntent();
		_placeId = intent.getIntExtra(EXTRA_PLACE, 0); 
		_place = MainScreenActivity.getPlacesDatabase().getPlaceByID(_placeId);        
		
		// ensure we have valid place data before continuing.  All internal so this error SHOULD NEVER EXIST
		// if it does it's NOT MY FAULT
		if (_place == null) fullInfoPopup("Error", "Invalid place data have been passed to this screen.");
		else {
			// set up news feed (first so asynchronous calls can begin)
			_newsFeed = new NewsFeed(_place, this);
			_newsFeed.StartCalls();
			
			// display name
			TextView nameView = (TextView)findViewById(R.id.textViewName);
			nameView.setText(name());
			
			// get and display distance
			_distance = intent.getDoubleExtra(EXTRA_DISTANCE, 0.0d);
			String distanceString = distanceAsString(_distance);
			TextView distanceView = (TextView)findViewById(R.id.textViewDistance);
			distanceView.setText(distanceString + " " + getResources().getText(R.string.fullinfo_distanceaway));
			
			// display rating
			DisplayStars();
			
			// display description
			TextView descriptionView = (TextView)findViewById(R.id.textViewDescription);
			descriptionView.setText(description());
			descriptionView.setMovementMethod(new ScrollingMovementMethod()); // so it scrolls properly
			
			// display correct background image
			Bundle bundle = intent.getExtras();
			Bitmap background = (Bitmap) bundle.getParcelable("background");
			ImageView imgV = (ImageView)findViewById(R.id.imageViewBackground);
			//imgV.setImageBitmap(background);
			// TODO
			
			// display place image
			DisplayImage();
			
			// set up add next button
			final IRoute route = MainScreenActivity.getCurrentRoute();
			Button buttonAddNext = (Button) findViewById(R.id.buttonAddNext);
			buttonAddNext.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	route.addNext(_placeId);
	            	fullInfoPopup("Route", "Location is now next on route.");
	            }
	         });
			
			// set up add at end button
			Button buttonAddEnd = (Button) findViewById(R.id.buttonAddEnd);
			buttonAddEnd.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	route.addEnd(_placeId);
	            	fullInfoPopup("Route", "Location has been added to route.");
	            }
	         });
			
			// display correct visited
			Button buttonVisited = (Button) findViewById(R.id.buttonVisited);
			if (visited()) buttonVisited.setText("Have visited");
			else buttonVisited.setText("Have not visited");
			buttonVisited.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	_place.updateVisited(!visited());
	            	if (visited()) ((Button)v).setText("Have visited");
	     			else ((Button)v).setText("Have not visited");
	            }
	         });
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_full_info, menu);
		return true;
	}
	
	// getters to abstract away from any changes in place representation/data gathering
	private String name() {return _place.getName();}
	private double distance() {return _distance;}
	private String description() {return _place.getDescription();}
	private PlaceCategory category() {return _place.getCategory();}
	private int rating() {return _place.getRating();}
	private boolean visited() {return _place.getVisited();}
	
	// for all those nasty popups that may appear
	private void fullInfoPopup(String title, String message) {
		new AlertDialog.Builder(this)
	    	.setTitle(title)
	    	.setMessage(message)
	    	.show();
	}
	
	// displays stars for the ratings of places
	// public so can be recalculated on download success
	public void DisplayStars() {
		int number = rating();
		// full visible/invisible stuff since can be done multiple times
		ImageView star1 = (ImageView)findViewById(R.id.imageViewStar1);
		ImageView star2 = (ImageView)findViewById(R.id.imageViewStar2);
		ImageView star3 = (ImageView)findViewById(R.id.imageViewStar3);
		ImageView star4 = (ImageView)findViewById(R.id.imageViewStar4);
		ImageView star5 = (ImageView)findViewById(R.id.imageViewStar5);
		if (number >= 1) {star1.setVisibility(View.VISIBLE);} else {star1.setVisibility(View.INVISIBLE);}
		if (number >= 2) {star2.setVisibility(View.VISIBLE);} else {star2.setVisibility(View.INVISIBLE);}
		if (number >= 3) {star3.setVisibility(View.VISIBLE);} else {star3.setVisibility(View.INVISIBLE);}
		if (number >= 4) {star4.setVisibility(View.VISIBLE);} else {star4.setVisibility(View.INVISIBLE);}
		if (number >= 5) {star5.setVisibility(View.VISIBLE);} else {star5.setVisibility(View.INVISIBLE);}
	}
	
	// displays an image taken from Foursquare of the place
	// public so can be recalculated on download success
	public void DisplayImage() {
		Drawable image = _place.getImage();
		ImageView imagePlace = (ImageView)findViewById(R.id.imageViewImage);
		if (image == null)
		{
			imagePlace.setVisibility(View.INVISIBLE);
		}
		else
		{
			imagePlace.setImageDrawable(image);
			imagePlace.setVisibility(View.VISIBLE);
		}
	}
	

	
	
	
	
	

}
