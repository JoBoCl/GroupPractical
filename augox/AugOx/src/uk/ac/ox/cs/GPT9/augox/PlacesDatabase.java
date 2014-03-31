package uk.ac.ox.cs.GPT9.augox;

import java.util.List;
import java.util.ArrayList;

/**
 * A database of all Places known to the program.
 */
public class PlacesDatabase {
	/*
	 * Constructor
	 */
	public PlacesDatabase() {
	}
	
	/*
	 * Return list of all Places within the given locus (radius in km), in
	 * order of distance from centre.
	 */
	public List<PlaceData> getPlacesInLocus(double latitude, double longitude, double radius) {
		return new ArrayList<PlaceData>();
	}

	/* Add other getters / setters as is deemed necessary later */
}