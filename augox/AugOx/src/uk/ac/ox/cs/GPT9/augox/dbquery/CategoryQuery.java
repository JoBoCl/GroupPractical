package uk.ac.ox.cs.GPT9.augox.dbquery;

import java.util.List;

import uk.ac.ox.cs.GPT9.augox.PlaceCategory;
import uk.ac.ox.cs.GPT9.augox.PlaceData;

/**
 * Query accepts places with one of the given categories.
 */
public class CategoryQuery implements DatabaseQuery {
	/*
	 * Variables
	 */
	List<PlaceCategory> cats;
	
	/*
	 * Constructor
	 */
	public CategoryQuery(List<PlaceCategory> cats) {
		this.cats = cats;
	}
	
	/*
	 * Does the given PlaceData match the criteria for this query?
	 */
	public boolean accepts(PlaceData place) {
		return cats.contains(place.getCategory());
	}
}