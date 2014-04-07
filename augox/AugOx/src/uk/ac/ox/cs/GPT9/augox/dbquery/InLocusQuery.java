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
		return getDistanceBetween(place.getLatitude(), place.getLongitude(),
				latitude, longitude) <= radius;
	}
	
	/*
	 * Calculate the distance between two world coordinates, in km
	 */
	private double getDistanceBetween(	double lat1, double long1,
										double lat2, double long2	) {
		// Formula based on spherical law of cosines
		// http://www.movable-type.co.uk/scripts/latlong.html
		double lat1r = Math.toRadians(lat1);
		double lat2r = Math.toRadians(lat2);
		double dlongr = Math.toRadians(long2 - long1);
		double earthrad = 6371;		// Radius of earth (km)
		double dist = Math.acos(
						Math.sin(lat1r) * Math.sin(lat2r)
						+ Math.cos(lat1r) * Math.cos(lat2r) * Math.cos(dlongr)
					) * earthrad;
		return dist;
	}
}