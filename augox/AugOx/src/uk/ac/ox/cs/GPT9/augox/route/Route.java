package uk.ac.ox.cs.GPT9.augox.route;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.MainScreenActivity;
import uk.ac.ox.cs.GPT9.augox.PlaceData;
import uk.ac.ox.cs.GPT9.augox.PlacesDatabase;

// Check IRoute for more information.  Basic implementation using list.
// Probably shouldn't need to change to other implementation
public class Route implements IRoute {
	
	private List<Integer> _route;
	private PlacesDatabase _database;
	
	// Get id of place at index
	public int idAtIndex(int index) {
		return _route.get(index); 
	}
	
	// Is this place included in the current route?
	public boolean contains(PlaceData placeData) {
		return getRouteAsList().contains(placeData);
	}
	
	public boolean contains(int id) {
		return _route.contains(id);
	}
	
	// Changes position in list of places - either for place to be found or place in position
	private void trueChangePosition(int id, int start, int end) {
		_route.remove(start);
		_route.add(end, id);
	}
	
	public void changePosition(PlaceData place, int index) {
		for (int i = 0; i < _route.size(); i++) {
			if (getRouteAsList().get(i) == place) {
				trueChangePosition(_route.get(i), i, index);
				return;
			}
		}
	}
	
	public void changePosition(int start, int end) {
		trueChangePosition(_route.get(start), start, end);
	}
	
	// Removes a place from the list from scanning or by position
	public void removePlace(PlaceData place) {
		PlaceData[] places = getRouteAsArray();
		for (int i = 0; i < _route.size(); i++) {
			if (places[i] == place) {
				removePlace(i);
				return;
			}
		}
	}
	
	public void removePlace(int index) {
		_route.remove(index);
	}
	
	// Refreshes the list of places in the route, either through a list or an array (because I'm nice)
	public void setList(List<Integer> places) {
		_route.clear();
		for (int i = 0; i < places.size(); i++) _route.add(places.get(i));
	}
	
	public void setList(Integer[] places) {
		_route.clear();
		for (int i = 0; i < places.length; i++) _route.add(places[i]);
	}
	
	// Adds a place to the end of the route
	public void addEnd(Integer id) {
		_route.add(id);
	}
	
	// Adds a place at the front of the route (next to be visited)
	public void addNext(Integer id) {
		_route.add(0, id);
	}
	
	// Simply returns the next place to go to
	public PlaceData getNext() {
		return _database.getPlaceByID(_route.get(0));
	}
	
	// Removes the first element of the list (move along to next point on route)
	// If we've reached the end returns true, else return false
	public boolean moveOn() {
		if (_route.size() > 0) {_route.remove(0); return true;}
		return false;
	}
	
	// Miscellaneous useful
	
	// Clears the list totally
	public void clear() {
		_route.clear();
	}
	
	// Gets the entire route as a list
	public List<PlaceData> getRouteAsList() {
		// hmm, don't want their changes to affect this one.
		ArrayList<PlaceData> newList = new ArrayList<PlaceData>();
		for (int i = 0; i < _route.size(); i++) newList.add(_database.getPlaceByID(_route.get(i)));
		return newList;
	}
	
	// Gets the entire route as a list
	public List<Integer> getRouteAsIDList() {
		// hmm, don't want their changes to affect this one.
		ArrayList<Integer> newList = new ArrayList<Integer>();
		for (int i = 0; i < _route.size(); i++) newList.add(_route.get(i));
		return newList;
	}
	
	// Gets the entire route as an array
	public PlaceData[] getRouteAsArray() {
		return getRouteAsList().toArray(new PlaceData[_route.size()]);
	}
	
	// Gets the entire route as an array of ids
	public Integer[] getRouteAsIDArray() {
		return _route.toArray(new Integer[_route.size()]);
	}
	
	public Route() {
		_route = new ArrayList<Integer>();
		_database = MainScreenActivity.getPlacesDatabase();
	}
}