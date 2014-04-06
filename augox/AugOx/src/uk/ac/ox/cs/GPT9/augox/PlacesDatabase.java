package uk.ac.ox.cs.GPT9.augox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A database of all Places known to the program.
 */
public class PlacesDatabase {
	/*
	 * Database Variables
	 */
	List<PlaceData> db = new ArrayList<PlaceData>();
	
	/*
	 * Constructor
	 */
	public PlacesDatabase() {
		// debuggery
		/*
		List<OpeningHours.Period> periods = new ArrayList<OpeningHours.Period>();
		boolean[] isOpen2 = {false};
		int[] openHour2 = {0};
		int[] openMinute2 = {0};
		int[] closeHour2 = {0};
		int[] closeMinute2 = {0};
		periods.add(new OpeningHours.Period(true, 12, 25, 12, 25, true, isOpen2, openHour2, openMinute2, closeHour2, closeMinute2));
		boolean[] isOpen3 = {true};
		int[] openHour3 = {7};
		int[] openMinute3 = {30};
		int[] closeHour3 = {19};
		int[] closeMinute3 = {0};
		periods.add(new OpeningHours.Period(true, 9, 1, 2, 29, true, isOpen3, openHour3, openMinute3, closeHour3, closeMinute3));
		boolean[] isOpen = {true, false, true, true, true, true, true};
		int[] openHour = {10, 0, 9, 9, 9, 9, 9};
		int[] openMinute = {0, 0, 0, 0, 0, 0, 0};
		int[] closeHour = {16, 0, 18, 18, 18, 18, 18};
		int[] closeMinute = {0, 0, 0, 0, 0, 0, 0};
		periods.add(new OpeningHours.Period(true, 3, 1, 8, 31, false, isOpen, openHour, openMinute, closeHour, closeMinute));
		OpeningHours test = new OpeningHours(periods);
		Log.d("DBG_Database", test.getOpeningSummary());
		Log.d("DBG_Database", test.isOpenAt(new LocalTime(2014,12,25,23,59))?"Open":"Closed");
		*/
	}
	
	/*
	 * Populate the database from the given stream.
	 * Note that the database is cleared first, and opening/closing the stream
	 * should be handled by the calling method.
	 */
	public void loadFromStream(InputStream stream) {
		// Reinitialise database
		db = new ArrayList<PlaceData>();
		
		// Create data input stream
		DataInputStream dstream = new DataInputStream(stream);
	}
	
	/*
	 * Dump the database into the given stream.
	 */
	public void writeToStream(OutputStream stream) {
		// Create data output stream
		DataOutputStream dstream = new DataOutputStream(stream);
	}
	
	/*
	 * Return list of all Places within the given locus (radius in km), in
	 * order of distance from centre.
	 */
	public List<PlaceData> getPlacesInLocus(double latitude, double longitude, double radius) {
		return new ArrayList<PlaceData>();
	}

	/* Add other getters / setters as is deemed necessary later */
}