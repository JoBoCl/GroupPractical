package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts places that have been visited.
 */
public class VisitedQuery implements DatabaseQuery {
	/*
	 * Constructor
	 */
	public VisitedQuery() {
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return place.getVisited();
	}
}