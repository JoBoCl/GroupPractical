package uk.ac.ox.cs.GPT9.augox;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.GPT9.augox.dbquery.*;

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
		
		PlaceData p1 = new PlaceData("Dorridge Station", 52.372167, -1.752943,
				5, false, PlaceCategory.RESTAURANT, "TRAINS", test);
		db.add(p1);
	}
	
	/*
	 * Populate the database from the given stream.
	 * Note that the database is cleared first, and opening/closing the stream
	 * should be handled by the calling method.
	 */
	public void loadFromStream(InputStream stream) throws IOException {
		// Reinitialise database
		db = new ArrayList<PlaceData>();
		
		// Create data input stream
		DataInputStream dstream = new DataInputStream(stream);
		
		// Read all places in the stream into the database
		try {
			while(true) {
				PlaceData place = PlaceData.buildPlaceDataFromStream(dstream);
				if(place != null) {
					db.add(place);
				}
			}
		} catch(EOFException e) {
			// End of file reached - good!
		}
	}
	
	/*
	 * Dump the database into the given stream.
	 */
	public void writeToStream(OutputStream stream) throws IOException {
		// Create data output stream
		DataOutputStream dstream = new DataOutputStream(stream);
		
		// Write all places in the database to the stream
		for(PlaceData place : db) {
			place.writeToStream(dstream);
		}
	}
	
	/*
	 * Load a null-terminated string from the given data stream
	 */
	public static String loadStringFromStream(DataInputStream dstream)
			throws IOException {
		// Prepare string to build
		String result = "";
		
		// Loop through, reading bytes, until the null character is reached
		char c;
		do {
			c = dstream.readChar();
			if(c != 0) result += c;
		} while(c != 0);
		
		// Return result
		return result;
	}
	
	/*
	 * Write a null-terminated string to the given data stream
	 */
	public static void writeStringToStream(DataOutputStream dstream,
			String str) throws IOException {
		dstream.writeChars(str);
		dstream.writeChar(0);
	}
	
	/*
	 * Process a query on the database
	 */
	public List<PlaceData> query(DatabaseQuery q) {
		// Prepare result list
		List<PlaceData> result = new ArrayList<PlaceData>();
		
		// Populate result list with places that are accepted by the query
		for(PlaceData p : db) {
			if(q.accepts(p)) result.add(p);
		}
		
		// Return result list
		return result;
	}
}