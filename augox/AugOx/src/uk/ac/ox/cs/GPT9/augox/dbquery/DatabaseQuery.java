package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * DatabaseQuery Interface
 * All query classes must implement this.
 * 
 * @see PlacesDatabase
 */
public interface DatabaseQuery {
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place);
}
