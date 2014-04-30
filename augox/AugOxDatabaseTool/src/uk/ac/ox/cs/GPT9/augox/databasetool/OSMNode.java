package uk.ac.ox.cs.GPT9.augox.databasetool;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a parsed OSM Node
 */
public class OSMNode {
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
	 * Getters
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
	 * Fetch the value of the given tag, or null if the gag does not exist
	 */
	public String getTagValue(String key) {
		return tags.get(key);
	}
}