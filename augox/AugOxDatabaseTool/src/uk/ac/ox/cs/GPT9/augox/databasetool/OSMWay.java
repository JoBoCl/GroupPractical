package uk.ac.ox.cs.GPT9.augox.databasetool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a parsed OSM Way
 */
public class OSMWay {
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
	 * Fetch the value of the given tag, or null if the gag does not exist
	 */
	public String getTagValue(String key) {
		return tags.get(key);
	}
}