package uk.ac.ox.cs.GPT9.augox.dbsort;

import java.text.Collator;
import java.util.Comparator;

import uk.ac.ox.cs.GPT9.augox.MainScreenActivity;
import uk.ac.ox.cs.GPT9.augox.PlacesDatabase;

/**
 * Sort query results alphabetically by name
 */
public class NameSorter implements DatabaseSorter {
	/*
	 * Variables
	 */
	final SortOrder order;
	
	/*
	 * Constructor
	 */
	public NameSorter(SortOrder order) {
		this.order = order;
	}
	
	/*
	 * Return a Comparator for Integers that compares the objects represented
	 * by the given database primary keys in a particular way.
	 */
	public Comparator<Integer> getComparator() {
		return new Comparator<Integer>() {
			public int compare(Integer pid1, Integer pid2) {
				// Fetch strings to compare
				PlacesDatabase db = MainScreenActivity.getPlacesDatabase();
				String name1 = db.getPlaceByID(pid1).getName();
				String name2 = db.getPlaceByID(pid2).getName();
				
				// Compare strings within locale
				Collator collator = Collator.getInstance();
				collator.setStrength(Collator.PRIMARY);
				int result = collator.compare(name1, name2); 
				return (order == SortOrder.ASC) ? result : -result;
			}
			
			public boolean equals(Object obj) {
				return false;
			}
		};
	}
}