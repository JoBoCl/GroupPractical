package uk.ac.ox.cs.GPT9.augox;

import java.io.*;

import android.graphics.drawable.Drawable;

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
	private String foursquareid;

	/*
	 * Semi-Persistent Data - initially null, can be set, but may be wiped at
	 * any time in the future to conserve space
	 */
	private Drawable image;

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
						String twitterhandle, String foursquareid) {
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
		this.foursquareid = foursquareid;
		
		// Initialise semi-persistent data
		image = null;
		
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
	public String getFourSquareID() { return foursquareid; }
	public Drawable getImage() { return image; }
	public boolean getClicked() { return clicked; }
	// social caching getters

	/*
	 * Updaters (or 'setters', but 'updaters' covers their intended use better)
	 */
	public void updateRating(int rating) { this.rating = rating; }
	public void updateVisited(boolean visited) { this.visited = visited; }
	public void updateImage(Drawable image) { this.image = image; }
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
		PlacesDatabase.writeStringToStream(dstream, foursquareid);
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
		String foursquareid = PlacesDatabase.loadStringFromStream(dstream);
		
		// Check for invalid data
		if(openinghours == null) return null;
		
		// Build and return object
		PlaceData place = new PlaceData(name, latitude, longitude, rating,
				visited, category, description, openinghours, twitterhandle,
				foursquareid);
		return place;
	}
	
	/*
	 * Calculate the distance between two world coordinates, in km
	 */
	public static double getDistanceBetween(double lat1, double long1,
			double lat2, double long2) {
		// Formula based on spherical law of cosines
		// http://www.movable-type.co.uk/scripts/latlong.html
		double lat1r = Math.toRadians(lat1);
		double lat2r = Math.toRadians(lat2);
		double dlongr = Math.toRadians(long2 - long1);
		double earthrad = 6371;		// Radius of earth (km)
		double dist = Math.acos(
						Math.sin(lat1r) * Math.sin(lat2r)
						+ Math.cos(lat1r) * Math.cos(lat2r) * Math.cos(dlongr)
					) * earthrad;
		return dist;
	}
}