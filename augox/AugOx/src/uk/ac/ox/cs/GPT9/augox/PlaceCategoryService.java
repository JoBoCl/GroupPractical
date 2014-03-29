package uk.ac.ox.cs.GPT9.augox;

/**
 * Provides services related to place categories. A singleton.
 */
public class PlaceCategoryService {
	/*
	 * Singleton Mechanism 
	 */
	private static final PlaceCategoryService INSTANCE = new PlaceCategoryService();
	
	public static PlaceCategoryService getInstance() {
		return INSTANCE;
	}
	
	/*
	 * Constructor
	 */
	private PlaceCategoryService() {
	}
	
	/*
	 * Return the human-readable name of the given Category.
	 */
	public String getName(PlaceCategory cat) { return ""; }

	/*
	 * Return the icon representing the given Category.
	 */
	//public SOMEIMAGETYPE getIcon(PlaceCategory cat);
}