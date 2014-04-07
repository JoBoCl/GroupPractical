package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts if the given query does not accept.
 */
public class NotQuery implements DatabaseQuery {
	/*
	 * Variables
	 */
	DatabaseQuery q;
	
	/*
	 * Constructor
	 */
	public NotQuery(DatabaseQuery q) {
		this.q = q;
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return !q.accepts(place);
	}
}