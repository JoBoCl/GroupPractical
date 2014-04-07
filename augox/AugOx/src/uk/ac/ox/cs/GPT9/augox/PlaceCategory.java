package uk.ac.ox.cs.GPT9.augox;

/**
 * Possible types of places the program can represent.
 */
public enum PlaceCategory {
	/*
	 * Enumerations
	 */
	UNKNOWN (0, "*UNKNOWN*"),
	MUSEUM (1, "Museum"),
	COLLEGE (2, "College"),
	RESTAURANT (3, "Restaurant"),
	BAR (4, "Bar");
	
	/*
	 * Member Data
	 */
	private final int id;
	private final String name;

	/*
	 * Constructor
	 */
	PlaceCategory(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/*
	 * Fetch properties of an enumeration
	 */
	public int getID() { return id; }
	public String getName() { return name; }
	
	/*
	 * Return the category with the given ID
	 */
	public static PlaceCategory getCategoryByID(int id) {
		for(PlaceCategory cat : values()) {
			if(cat.id == id) return cat;
		}
		
		return UNKNOWN;
	}
}
