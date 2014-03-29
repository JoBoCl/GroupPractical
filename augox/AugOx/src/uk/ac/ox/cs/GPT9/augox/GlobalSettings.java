package uk.ac.ox.cs.GPT9.augox;

/**
 * Storage and access to persistent settings data. A singleton.
 */
public class GlobalSettings {
	/*
	 * Singleton Mechanism 
	 */
	private static final GlobalSettings INSTANCE = new GlobalSettings();
	
	public static GlobalSettings getInstance() {
		return INSTANCE;
	}
	
	/*
	 * Constructor
	 */
	private GlobalSettings() {
	}
	
	// TBA
}