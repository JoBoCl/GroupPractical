package uk.ac.ox.cs.GPT9.augox;

/**
 * Possible types of places the program can represent.
 */
public enum PlaceCategory {
	/*
	 * Enumerations
	 */
	UNKNOWN (0, "*UNKNOWN*","",0,0),
	MUSEUM (1, "Museum","filter_museums",R.drawable.museumicon,R.drawable.museumicontick),
	COLLEGE (2, "College","filter_colleges",R.drawable.collegeicon,R.drawable.collegeicontick),
	RESTAURANT (3, "Restaurant","filter_restaurants",R.drawable.restauranticon,R.drawable.restauranticontick),
	BAR (4, "Bar","filter_bars",R.drawable.baricon,R.drawable.baricontick);
	
	/*
	 * Member Data
	 */
	private final int id;
	private final String name;
	private final String filterName;
	private final int imageRef;
	private final int imageRefTick;

	/*
	 * Constructor
	 */
	PlaceCategory(int id, String name, String filterName, int imageRef, int imageRefTick) {
		this.id = id;
		this.name = name;
		this.filterName = filterName;
		this.imageRef = imageRef;
		this.imageRefTick = imageRefTick;
	}
	
	/*
	 * Fetch properties of an enumeration
	 */
	public int getID() { return id; }
	public String getName() { return name; }
	public String getFilter() {return filterName;}
	public int getImageRef(boolean visited) {
		if(visited) return imageRefTick; else return imageRef;}
	
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
