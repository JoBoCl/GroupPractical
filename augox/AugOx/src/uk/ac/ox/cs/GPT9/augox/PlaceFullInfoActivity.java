package uk.ac.ox.cs.GPT9.augox;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PlaceFullInfoActivity extends Activity {
	/*
	 * Intent Constants
	 */
	public final static String EXTRA_PLACE = "uk.ac.ox.cs.GPT9.augox.PLACE";
	public final static String EXTRA_BACKGROUND = "uk.ac.ox.cs.GPT9.augox.BACKGROUND";
	public final static String EXTRA_DISTANCE = "uk.ac.ox.cs.GPT9.augox.DISTANCE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_full_info);
		
		// load place from intent
		Intent intent = getIntent();
		int _placeid = intent.getIntExtra(EXTRA_PLACE, 0); 
		_place = MainScreenActivity.getPlacesDatabase().getPlaceByID(_placeid);
		// todo: check it isn't null before continuing
		_distance = intent.getDoubleExtra(EXTRA_DISTANCE, 0.0d);
		
		// display name
		TextView nameView = (TextView)findViewById(R.id.textViewName);
		nameView.setText(name());
		
		// display distance
		String distanceString = new DecimalFormat("#.#").format(distance());
		TextView distanceView = (TextView)findViewById(R.id.textViewDistance);
		distanceView.setText(distanceString + " " + getResources().getText(R.string.fullinfo_distanceaway));
		
		// display rating
		displayStars(rating());
		
		// display description
		TextView descriptionView = (TextView)findViewById(R.id.textViewDescription);
		descriptionView.setText(description());
		
		// display feed
		String[] exampleTweets = new String[] {"this is an example tweet", "#yolo #omg #iluvhashtags", "look, ma, I'm on tv!",
				"#bestpubever @MatthewsAwesomePub", "example #takinggroupprojectseriously", "seriously why would people want a list of tweets",
				"having a really great party in here", "tweet number 8 coming right up", "I need a lot of tweets so I can test scrolling, before you ask",
				"so shut up about it", "is that enough?", "I hope so.", "but better add another one", "because #YOLO"};
		ListView feedView = (ListView)findViewById(R.id.listViewFeed);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.news_feed_item_1, exampleTweets);
		feedView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_full_info, menu);
		return true;
	}
	
	private PlaceData _place; // = new PlaceData("Matthew's Awesome Pub", 0.0, 0.0, 5, false, PlaceCategory.BAR, "This isn't the greatest pub in the world.  This is a tribute.  I'm also going to try to make this description long so I can make sure it doesn't go too far to the right and wraps around properly, kind of ruining the preceding one-liner.  Which is a great shame, really.  At some point I'm going to have to make a way of sourcing the description from the Internet, which is going to be annoying and hard and stuff but at least for now I can get this prototype working.  And hey, getting the pretty layout is what really matters.  Having the most up-to-date data is not as important, as xkcd 937 tells us (there's an xkcd for everything)", new OpeningHours(new ArrayList<OpeningHours.Period>()));
	private double _distance;
	
	private String name() {return _place.getName();}
	private double distance() {return _distance;}
	private String description() {return _place.getDescription();}
	private PlaceCategory category() {return _place.getCategory();}
	private int rating() {return _place.getRating();}
	private boolean visited() {return _place.getVisited();}
	
	private void displayStars(int number)
	{
		// this is just for the sake of rapid prototyping, okay?
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
	

}
