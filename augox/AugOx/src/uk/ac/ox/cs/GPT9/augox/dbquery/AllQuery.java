package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts all places.
 */
public class AllQuery implements DatabaseQuery {
	/*
	 * Constructor
	 */
	public AllQuery() {
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return true;
	}
}