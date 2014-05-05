package uk.ac.ox.cs.GPT9.augox.databasetool;

/**
 * Any parsed item from OSM data must implement this
 */
interface OSMItem {
	/*
	 * Get latitude and longitude
	 */
	public double getLatitude();
	public double getLongitude();
	
	/*
	 * Add a tag
	 */
	public void addTag(String key, String value);
	
	/*
	 * Fetch the value of the given tag - the string "null" (rather than a null
	 * object) is returned if the tag does not exist
	 */
	public String getTagValue(String key);
	
	/*
	 * Does a tag with the given key exist at all?
	 */
	public boolean hasTag(String key);
	
	/*
	 * Does a tag with the given key-value pair exist?
	 */
	public boolean hasTag(String key, String value);
}