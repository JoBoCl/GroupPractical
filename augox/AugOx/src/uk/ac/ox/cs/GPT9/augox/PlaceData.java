package uk.ac.ox.cs.GPT9.augox;

/**
 * Represents a Place - a location in the world that the program will deal
 * with. The primary storage of these is the singleton PlacesDatabase object
 * that the program maintains.
 * 
 * @see PlacesDatabase
 */
public class PlaceData {
	/*
	 * Permanent Data - stored forever
	 */
	private String name;
	private double latitude;
	private double longitude;
	private int rating;
	private boolean visited;
	private PlaceCategory category;
	private String description;
	private OpeningHours openinghours;
	// social addresses; e.g. twitter handle; to be added once planned

	/*
	 * Semi-Persistent Data - initially null, can be set, but may be wiped at
	 * any time in the future to conserve space
	 */
	//private SOMEIMAGETYPE image;

	/*
	 * Session Data - initially takes a default / null value, which can be
	 * modified, but will be wiped between sessions.
	 */
	private boolean clicked;
	// social caching; to be added once planned

	/*
	 * Getters
	 */
	public String getName() { return name; }
	public double getLatitude() { return latitude; }
	public double getLongitude() { return longitude; }
	public int getRating() { return rating; }
	public boolean getVisited() { return visited; }
	public PlaceCategory getCategory() { return category; }
	public String getDescription() { return description; }
	public OpeningHours getOpeningHours() { return openinghours; }
	// social address getters
	//public SOMEIMAGETYPE getImage();
	public boolean getClicked() { return clicked; }
	// social caching getters

	/*
	 * Updaters (or 'setters', but 'updaters' covers their intended use better)
	 */
	public void updateRating(int rating) { this.rating = rating; }
	public void updateClicked(boolean clicked) { this.clicked = clicked; }
}