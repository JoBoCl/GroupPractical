package uk.ac.ox.cs.GPT9.augox.dbquery;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts places within the given geographic locus.
 * Radius should be in kilometres.
 */
public class InLocusQuery implements DatabaseQuery {
	/*
	 * Variables
	 */
	double latitude, longitude, radius;
	
	/*
	 * Constructor
	 */
	public InLocusQuery(double latitude, double longitude, double radius) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return PlaceData.getDistanceBetween(place.getLatitude(),
				place.getLongitude(), latitude, longitude) <= radius;
	}
}