package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.LocalTime;
import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts places that are open at the given time and date.
 */
public class OpenAtQuery implements DatabaseQuery {
	/*
	 * Variables
	 */
	LocalTime date;
	
	/*
	 * Constructor
	 */
	public OpenAtQuery(LocalTime date) {
		this.date = date;
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return place.getOpeningHours().isOpenAt(date);
	}
}