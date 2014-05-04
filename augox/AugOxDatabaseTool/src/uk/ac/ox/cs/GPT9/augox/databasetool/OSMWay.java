package uk.ac.ox.cs.GPT9.augox.databasetool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a parsed OSM Way
 */
public class OSMWay implements OSMItem {
	/*
	 * Variables
	 */
	private List<OSMNode> nodes = new ArrayList<OSMNode>();
	private Map<String, String> tags = new HashMap<String, String>();
	
	/*
	 * Constructor
	 */
	public OSMWay() {
	}
	
	/*
	 * Get latitude and longitude
	 */
	public double getLatitude() {
		double latitude = 0;
		for(OSMNode node : nodes) {
			latitude += node.getLatitude();
		}
		latitude /= nodes.size();
		return latitude;
	}
	
	public double getLongitude() {
		double longitude = 0;
		for(OSMNode node : nodes) {
			longitude += node.getLongitude();
		}
		longitude /= nodes.size();
		return longitude;
	}
	
	/*
	 * Add a node
	 */
	public void addNode(OSMNode node) {
		nodes.add(node);
	}
	
	/*
	 * Add a tag
	 */
	public void addTag(String key, String value) {
		tags.put(key, value);
	}
	
	/*
	 * Fetch the value of the given tag - the string "null" (rather than a null
	 * object) is returned if the tag does not exist
	 */
	public String getTagValue(String key) {
		String v = tags.get(key);
		if(v == null) return "null";
		return v;
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