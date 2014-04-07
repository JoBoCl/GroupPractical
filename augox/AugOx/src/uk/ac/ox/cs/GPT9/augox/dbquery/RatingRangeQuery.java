package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts places with a rating in the given range.
 */
public class RatingRangeQuery implements DatabaseQuery {
	/*
	 * Variables
	 */
	int min, max;
	
	/*
	 * Constructor
	 */
	public RatingRangeQuery(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return min <= place.getRating() && place.getRating() <= max;
	}
}