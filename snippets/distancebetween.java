/*
 * Calculate the distance between two world coordinates, in km
 */
public double getDistanceBetween(	double lat1, double long1,
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