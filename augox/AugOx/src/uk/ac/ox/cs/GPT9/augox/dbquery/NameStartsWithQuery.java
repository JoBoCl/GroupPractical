package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts places with names starting with the given substring.
 * Case insensitive.
 */
public class NameStartsWithQuery implements DatabaseQuery {
	/*
	 * Variables
	 */
	String sub;
	
	/*
	 * Constructor
	 */
	public NameStartsWithQuery(String sub) {
		this.sub = sub;
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return place.getName().toLowerCase().startsWith(sub.toLowerCase());
	}
}