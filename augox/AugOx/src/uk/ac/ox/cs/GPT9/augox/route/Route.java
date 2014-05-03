package uk.ac.ox.cs.GPT9.augox.route;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

// Check IRoute for more information.  Basic implementation using list.
// Probably shouldn't need to change to other implementation
public class Route implements IRoute {
	
	private List<PlaceData> _route;
	
	// Is this place included in the current route?
	public boolean contains(PlaceData place) {
		return _route.contains(place);
	}
	
	// Changes position in list of places - either for place to be found or place in position
	private void trueChangePosition(PlaceData place, int start, int end) {
		_route.remove(start);
		_route.add(end, place);
	}
	
	public void changePosition(PlaceData place, int index) {
		for (int i = 0; i < _route.size(); i++) {
			if (_route.get(i) == place) {
				trueChangePosition(place, i, index);
			}
		}
	}
	
	public void changePosition(int start, int end) {
		trueChangePosition(_route.get(start), start, end);
	}
	
	// Removes a place from the list from scanning or by position
	public void removePlace(PlaceData place) {
		_route.remove(place);
	}
	
	public void removePlace(int index) {
		_route.remove(index);
	}
	
	// Refreshes the list of places in the route, either through a list or an array (because I'm nice)
	public void setList(List<PlaceData> places) {
		_route = new ArrayList<PlaceData>();
		for (int i = 0; i < places.size(); i++) _route.add(places.get(i));
	}
	
	public void setList(PlaceData[] places) {
		_route = new ArrayList<PlaceData>();
		for (int i = 0; i < places.length; i++) _route.add(places[i]);
	}
	
	// Adds a place to the end of the route
	public void addEnd(PlaceData place) {
		_route.add(place);
	}
	
	// Adds a place at the front of the route (next to be visited)
	public void addNext(PlaceData place) {
		_route.add(0, place);
	}
	
	// Simply returns the next place to go to
	public PlaceData getNext() {
		if (_route.size() > 0) return _route.get(0);
		else return null; // better than an error message, I guess		
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
		for (int i = 0; i < _route.size(); i++) newList.add(_route.get(i));
		return newList;
	}
	
	// Gets the entire route as an array
	public PlaceData[] getRouteAsArray() {
		return _route.toArray(new PlaceData[_route.size()]);
	}
	
	public Route() {
		_route = new ArrayList<PlaceData>();
	}
}