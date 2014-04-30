package uk.ac.ox.cs.GPT9.augox.route;

import java.util.List;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

// Interface representing a current route.  Add to it, take away from it
// Basically just a wrapper for a list
// But an interface in case we need to suddenly swap out implementations
// Also so that I can copy and paste it for the spec
// Functions are listed according to expected users
public interface IRoute {
	
	// Functions used probably exclusively by manual route planner
	
	// Is this place included in the current route?
	boolean contains(PlaceData place);
	// no version with id included as easier to get from activity than some poor wittle cwass in a different package
	
	// Changes position in list of places - either for place to be found or place in position
	void changePosition(PlaceData place, int index);
	void changePosition(int start, int end);
	
	// Removes a place from the list from scanning or by position
	void removePlace(PlaceData place);
	void removePlace(int index);
	
	// Used by manual and auto route planners
	
	// Refreshes the list of places in the route, either through a list or an array (because I'm nice)
	void setList(List<PlaceData> places);
	void setList(PlaceData[] places);
	
	// Used by manual route planner and fullscreeninfo (and possibly mainscreen activity)
	
	// Adds a place to the end of the route
	void addEnd(PlaceData place);
	
	// Used by fullscreeninfo (and possibly mainscreen activity)
	
	// Adds a place at the front of the route (next to be visited)
	void addNext(PlaceData place);
	
	// Used by MainScreenActivity
	
	// Simply returns the next place to go to
	PlaceData getNext();
	
	// Removes the first element of the list (move along to next point on route)
	// If we've reached the end returns true, else return false
	boolean moveOn();
	
	// Miscellaneous useful
	
	// Clears the list totally
	void clear();
	
	// Gets the entire route as a list
	List<PlaceData> getRouteAsList();
	
	// Gets the entire route as an array
	PlaceData[] getRouteAsArray();
}