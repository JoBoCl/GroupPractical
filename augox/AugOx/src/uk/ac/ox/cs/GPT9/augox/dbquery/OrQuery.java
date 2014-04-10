package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts if one or both of the given queries accept.
 * Implicitly implements short-circuiting (since || does).
 */
public class OrQuery implements DatabaseQuery {
	/*
	 * Variables
	 */
	DatabaseQuery q1, q2;
	
	/*
	 * Constructor
	 */
	public OrQuery(DatabaseQuery q1, DatabaseQuery q2) {
		this.q1 = q1;
		this.q2 = q2;
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return q1.accepts(place) || q2.accepts(place);
	}
}