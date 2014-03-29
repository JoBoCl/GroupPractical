package uk.ac.ox.cs.GPT9.augox;

/**
 * Storage and access to persistent display filter configuration. A singleton.
 */
public class FilterSettings {
	/*
	 * Singleton Mechanism 
	 */
	private static final FilterSettings INSTANCE = new FilterSettings();
	
	public static FilterSettings getInstance() {
		return INSTANCE;
	}
	
	/*
	 * Constructor
	 */
	private FilterSettings() {
	}
	
	// TBA
}