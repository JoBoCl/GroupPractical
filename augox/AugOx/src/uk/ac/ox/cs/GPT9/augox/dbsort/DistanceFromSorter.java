package uk.ac.ox.cs.GPT9.augox.dbsort;

import java.util.Comparator;

import uk.ac.ox.cs.GPT9.augox.MainScreenActivity;
import uk.ac.ox.cs.GPT9.augox.PlaceData;
import uk.ac.ox.cs.GPT9.augox.PlacesDatabase;

/**
 * Sort query results by distance from given GPS coordinates
 */
public class DistanceFromSorter implements DatabaseSorter {
	/*
	 * Variables
	 */
	double latitude;
	double longitude;
	SortOrder order;
	
	/*
	 * Constructor
	 */
	public DistanceFromSorter(double latitude, double longitude,
			SortOrder order) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.order = order;
	}
	
	/*
	 * Return a Comparator for Integers that compares the objects represented
	 * by the given database primary keys in a particular way.
	 */
	public Comparator<Integer> getComparator() {
		return new Comparator<Integer>() {
			public int compare(Integer pid1, Integer pid2) {
				// Fetch locations
				PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
				double latitude1 = db.getPlaceByID(pid1).getLatitude();
				double longitude1 = db.getPlaceByID(pid1).getLongitude();
				double latitude2 = db.getPlaceByID(pid2).getLatitude();
				double longitude2 = db.getPlaceByID(pid2).getLongitude();
				
				// Calculate distances to compare
				double distance1 = PlaceData.getDistanceBetween(latitude,
						longitude, latitude1, longitude1);
				double distance2 = PlaceData.getDistanceBetween(latitude,
						longitude, latitude2, longitude2);
				
				// Compare ratings
				int result = 0;
				if(distance1 < distance2) result = -1;
				if(distance1 > distance2) result = 1;
				return (order == SortOrder.ASC) ? result : -result;
			}
			
			public boolean equals(Object obj) {
				return false;
			}
		};
	}
}