package uk.ac.ox.cs.GPT9.augox;

import java.io.*;

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
	private String twitterhandle;

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
	 * Constructor
	 */
	public PlaceData(	String name, double latitude, double longitude,
						int rating, boolean visited, PlaceCategory category,
						String description, OpeningHours openinghours,
						String twitterhandle) {
		// Initialise permanent data
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.rating = rating;
		this.visited = visited;
		this.category = category;
		this.description = description;
		this.openinghours = openinghours;
		this.twitterhandle = twitterhandle;
		
		// Initialise semi-persistent data
		// image = null;
		
		// Initialise session data
		clicked = false;
	}

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
	public String getTwitterHandle() { return twitterhandle; }
	//public SOMEIMAGETYPE getImage();
	public boolean getClicked() { return clicked; }
	// social caching getters

	/*
	 * Updaters (or 'setters', but 'updaters' covers their intended use better)
	 */
	public void updateRating(int rating) { this.rating = rating; }
	public void updateVisited(boolean visited) { this.visited = visited; }
	public void updateClicked(boolean clicked) { this.clicked = clicked; }
	
	/*
	 * Write the place into the given data stream
	 */
	public void writeToStream(DataOutputStream dstream) throws IOException {
		PlacesDatabase.writeStringToStream(dstream, name);
		dstream.writeDouble(latitude);
		dstream.writeDouble(longitude);
		dstream.writeInt(rating);
		dstream.writeBoolean(visited);
		dstream.writeInt(category.getID());
		PlacesDatabase.writeStringToStream(dstream, description);
		openinghours.writeToStream(dstream);
		PlacesDatabase.writeStringToStream(dstream, twitterhandle);
	}
	
	/*
	 * Create and return a PlaceData object from the given data stream
	 */
	public static PlaceData buildPlaceDataFromStream(DataInputStream dstream)
			throws IOException {
		// Load values from stream
		String name = PlacesDatabase.loadStringFromStream(dstream);
		double latitude = dstream.readDouble();
		double longitude = dstream.readDouble();
		int rating = dstream.readInt();
		boolean visited = dstream.readBoolean();
		PlaceCategory category = PlaceCategory.getCategoryByID(
				dstream.readInt());
		String description = PlacesDatabase.loadStringFromStream(dstream);
		OpeningHours openinghours = OpeningHours.buildOpeningHoursFromStream(
				dstream);
		String twitterhandle = PlacesDatabase.loadStringFromStream(dstream);
		
		// Check for invalid data
		if(openinghours == null) return null;
		
		// Build and return object
		PlaceData place = new PlaceData(name, latitude, longitude, rating,
				visited, category, description, openinghours, twitterhandle);
		return place;
	}
}