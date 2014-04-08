package uk.ac.ox.cs.GPT9.augox.dbsort;

import java.util.Comparator;

import uk.ac.ox.cs.GPT9.augox.MainScreenActivity;
import uk.ac.ox.cs.GPT9.augox.PlacesDatabase;

public class RatingSorter implements DatabaseSorter {
	/*
	 * Variables
	 */
	final SortOrder order;
	
	/*
	 * Constructor
	 */
	public RatingSorter(SortOrder order) {
		this.order = order;
	}
	
	/*
	 * Return a Comparator for Integers that compares the objects represented
	 * by the given database primary keys in a particular way.
	 */
	public Comparator<Integer> getComparator() {
		return new Comparator<Integer>() {
			public int compare(Integer pid1, Integer pid2) {
				// Fetch ratings to compare
				PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
				int rating1 = db.getPlaceByID(pid1).getRating();
				int rating2 = db.getPlaceByID(pid2).getRating();
				
				// Compare ratings
				int result = 0;
				if(rating1 < rating2) result = -1;
				if(rating1 > rating2) result = 1;
				return (order == SortOrder.ASC) ? result : -result;
			}
			
			public boolean equals(Object obj) {
				return false;
			}
		};
	}
}