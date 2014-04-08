package uk.ac.ox.cs.GPT9.augox.dbsort;

import java.util.Comparator;

/**
 * All sorter classes must implement this.
 */
public interface DatabaseSorter {
	/*
	 * Return a Comparator for Integers that compares the objects represented
	 * by the given database primary keys in a particular way.
	 */
	public Comparator<Integer> getComparator();
}