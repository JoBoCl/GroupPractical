package uk.ac.ox.cs.GPT9.augox.databasetool;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a parsed OSM Node
 */
public class OSMNode implements OSMItem {
	/*
	 * Variables
	 */
	private double latitude, longitude;
	private Map<String, String> tags = new HashMap<String, String>();
	
	/*
	 * Constructor
	 */
	public OSMNode(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/*
	 * Get latitude and longitude
	 */
	public double getLatitude() { return latitude; }
	public double getLongitude() { return longitude; }
	
	/*
	 * Add a tag
	 */
	public void addTag(String key, String value) {
		tags.put(key, value);
	}
	
	/*
	 * Fetch the value of the given tag - the string "" (rather than a null
	 * object) is returned if the tag does not exist
	 */
	public String getTagValue(String key) {
		String v = tags.get(key);
		if(v == null) return "";
		return v;
	}
	
	/*
	 * Does a tag with the given key exist at all?
	 */
	public boolean hasTag(String key) {
		return tags.get(key) != null;
	}
	
	/*
	 * Does a tag with the given key-value pair exist?
	 */
	public boolean hasTag(String key, String value) {
		String v = tags.get(key);
		if(v == null) return false;
		return value.equalsIgnoreCase(v);
	}
}