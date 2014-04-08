package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts places that have been clicked.
 */
public class ClickedQuery implements DatabaseQuery {
	/*
	 * Constructor
	 */
	public ClickedQuery() {
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return place.getClicked();
	}
}