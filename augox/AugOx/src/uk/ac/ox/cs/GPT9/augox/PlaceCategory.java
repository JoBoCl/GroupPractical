package uk.ac.ox.cs.GPT9.augox;

/**
 * Possible types of places the program can represent.
 */
public enum PlaceCategory {
	/*
	 * Enumerations
	 */
	//(ID,Name,FilterPreferenceString,Icon,IconForVisited)
	UNKNOWN (0, "*UNKNOWN*","",0,0,0,0,0,0),
	MUSEUM (1, "Museum","filter_museums",R.drawable.museumicon,R.drawable.museumicontick,R.drawable.museumiconnoborder,
			R.drawable.museumiconticknoborder,R.drawable.museumiconnext,R.drawable.museumiconticknext),
	COLLEGE (2, "College","filter_colleges",R.drawable.collegeicon,R.drawable.collegeicontick,R.drawable.collegeiconnoborder,
			R.drawable.collegeiconticknoborder,R.drawable.collegeiconnext,R.drawable.collegeiconticknext),
	RESTAURANT (3, "Restaurant","filter_restaurants",R.drawable.restauranticon,R.drawable.restauranticontick,R.drawable.restauranticonnoborder,
			R.drawable.restauranticonticknoborder,R.drawable.restauranticonnext,R.drawable.restauranticonticknext),
	BAR (4, "Bar","filter_bars",R.drawable.baricon,R.drawable.baricontick,R.drawable.bariconnoborder,
			R.drawable.bariconticknoborder,R.drawable.bariconnext,R.drawable.bariconticknext);
	
	/*
	 * Member Data
	 */
	private final int id;
	private final String name;
	private final String filterName;
	private final int imageRef;
	private final int imageRefTick;
	private final int imageRefNoBorder;
	private final int imageRefTickNoBorder;
	private final int imageRefNext;
	private final int imageRefNextTick;

	/*
	 * Constructor
	 */
	PlaceCategory(int id, String name, String filterName, int imageRef, int imageRefTick, int imageRefNoBorder, 
			int imageRefTickNoBorder, int imageRefNext, int imageRefNextTick) {
		this.id = id;
		this.name = name;
		this.filterName = filterName;
		this.imageRef = imageRef;
		this.imageRefTick = imageRefTick;
		this.imageRefNoBorder = imageRefNoBorder;
		this.imageRefTickNoBorder = imageRefTickNoBorder;
		this.imageRefNext = imageRefNext;
		this.imageRefNextTick = imageRefNextTick;
	}
	
	/*
	 * Fetch properties of an enumeration
	 */
	public int getID() { return id; }
	public String getName() { return name; }
	public String getFilter() {return filterName;}
	
	public int getImageRef(boolean visited, boolean nextInRoute) {
		if(visited) 
			if(nextInRoute) return imageRefNextTick; else return imageRefTick;
		else 
			if(nextInRoute) return imageRefNext; else return imageRef;
	}

	public int getImageRef(boolean visited) { return getImageRef(visited,false);}
	
	public int getImageRefNoBorder(boolean visited){ 
		if(visited) return imageRefNoBorder; else return imageRefTickNoBorder;}
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
