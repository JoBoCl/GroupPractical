package uk.ac.ox.cs.GPT9.augox;

/**
 * Wrapper for GPS system. A singleton.
 */
public class GPSService {
	/*
	 * Singleton Mechanism 
	 */
	private static final GPSService INSTANCE = new GPSService();
	
	public static GPSService getInstance() {
		return INSTANCE;
	}
	
	/*
	 * Constructor
	 */
	private GPSService() {
	}
	
	/*
	 * Retrieve location.
	 */
	public double getLatitude() { return 0.0d; }
	public double getLongitude() { return 0.0d; }
	
	/*
	 * Calculate the distance between two world coordinates, in km
	 */
	public double getDistanceBetween(double lat1, double long1, double lat2, double long2) {
		return 0.0d;
	}
}