package uk.ac.ox.cs.GPT9.augox.route;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.MainScreenActivity;
import uk.ac.ox.cs.GPT9.augox.PlaceData;
import uk.ac.ox.cs.GPT9.augox.PlacesDatabase;

// Check IRoute for more information.  Basic implementation using list.
// Probably shouldn't need to change to other implementation
public class Route implements IRoute {
	
	private List<Integer> route;
	private PlacesDatabase database;
	
	// Get id of place at index
	public int idAtIndex(int index) {
		return route.get(index); 
	}
	
	// Is this place included in the current route?
	public boolean contains(PlaceData placeData) {
		return getRouteAsList().contains(placeData);
	}
	
	public boolean contains(int id) {
		return route.contains(id);
	}
	
	// Changes position in list of places - either for place to be found or place in position
	private void trueChangePosition(int id, int start, int end) {
		route.remove(start);
		route.add(end, id);
	}
	
	public void changePosition(PlaceData place, int index) {
		for (int i = 0; i < route.size(); i++) {
			if (getRouteAsList().get(i) == place) {
				trueChangePosition(route.get(i), i, index);
				return;
			}
		}
	}
	
	public void changePosition(int start, int end) {
		trueChangePosition(route.get(start), start, end);
	}
	
	// Removes a place from the list from scanning or by position
	public void removePlace(PlaceData place) {
		PlaceData[] places = getRouteAsArray();
		for (int i = 0; i < route.size(); i++) {
			if (places[i] == place) {
				removePlace(i);
				return;
			}
		}
	}
	
	public void removePlace(int index) {
		route.remove(index);
	}
	
	// Refreshes the list of places in the route, either through a list or an array (because I'm nice)
	public void setList(List<Integer> places) {
		route.clear();
		for (int i = 0; i < places.size(); i++) route.add(places.get(i));
	}
	
	public void setList(Integer[] places) {
		route.clear();
		for (int i = 0; i < places.length; i++) route.add(places[i]);
	}
	
	// Adds a place to the end of the route
	public void addEnd(Integer id) {
		route.add(id);
	}
	
	// Adds a place at the front of the route (next to be visited)
	public void addNext(Integer id) {
		route.add(0, id);
	}
	
	// Simply returns the next place to go to
	// should return error if empty as their responsibility to check before demanding
	public PlaceData getNext() {
		return database.getPlaceByID(route.get(0));
	}
	
	public int getNextAsID() {
		return route.get(0);
	}
	
	// Removes the first element of the list (move along to next point on route)
	// If we've reached the end returns true, else return false
	public boolean moveOn() {
		if (route.size() > 0) route.remove(0);
		return !(route.size() > 0);
	}
	
	// tells us whether the route is empty or not
	public boolean empty() {
		return route.size() == 0;
	}
	
	// Miscellaneous useful
	
	// Clears the list totally
	public void clear() {
		route.clear();
	}
	
	// Gets the entire route as a list
	public List<PlaceData> getRouteAsList() {
		// hmm, don't want their changes to affect this one.
		ArrayList<PlaceData> newList = new ArrayList<PlaceData>();
		for (int i = 0; i < route.size(); i++) newList.add(database.getPlaceByID(route.get(i)));
		return newList;
	}
	
	// Gets the entire route as a list
	public List<Integer> getRouteAsIDList() {
		// hmm, don't want their changes to affect this one.
		ArrayList<Integer> newList = new ArrayList<Integer>();
		for (int i = 0; i < route.size(); i++) newList.add(route.get(i));
		return newList;
	}
	
	// Gets the entire route as an array
	public PlaceData[] getRouteAsArray() {
		return getRouteAsList().toArray(new PlaceData[route.size()]);
	}
	
	// Gets the entire route as an array of ids
	public Integer[] getRouteAsIDArray() {
		return route.toArray(new Integer[route.size()]);
	}
	
	public Route() {
		route = new ArrayList<Integer>();
		database = MainScreenActivity.getPlacesDatabase();
	}
}