package uk.ac.ox.cs.GPT9.augox;

import java.io.*;
import java.util.*;

import uk.ac.ox.cs.GPT9.augox.dbquery.DatabaseQuery;
import uk.ac.ox.cs.GPT9.augox.dbsort.DatabaseSorter;

/**
 * A database of all Places known to the program.
 */
public class PlacesDatabase {
	/*
	 * Database Variables
	 */
	private int nextKey = 0;
	private Map<Integer, PlaceData> db = new HashMap<Integer, PlaceData>();
	
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
		
		PlaceData p1 = new PlaceData("Matthew's Awesome Pub", 0.0, 0.0, 5, false, PlaceCategory.BAR, "This isn't the greatest pub in the world.  This is a tribute.  I'm also going to try to make this description long so I can make sure it doesn't go too far to the right and wraps around properly, kind of ruining the preceding one-liner.  Which is a great shame, really.  At some point I'm going to have to make a way of sourcing the description from the Internet, which is going to be annoying and hard and stuff but at least for now I can get this prototype working.  And hey, getting the pretty layout is what really matters.  Having the most up-to-date data is not as important, as xkcd 937 tells us (there's an xkcd for everything)", test, "SjoHo");
		addEntry(p1);
	}
	
	/*
	 * Add a new entry to the database
	 */
	private void addEntry(PlaceData place) {
		db.put(nextKey, place);
		nextKey++;
	}
	
	/*
	 * Reset the database
	 */
	private void resetDatabase() {
		nextKey = 0;
		db = new HashMap<Integer, PlaceData>();
	}
	
	/*
	 * Populate the database from the given stream.
	 * Note that the database is cleared first, and opening/closing the stream
	 * should be handled by the calling method.
	 */
	public void loadFromStream(InputStream stream) throws IOException {
		// Reinitialise database
		resetDatabase();
		
		// Create data input stream
		DataInputStream dstream = new DataInputStream(stream);
		
		// Read all places in the stream into the database
		try {
			while(true) {
				PlaceData place = PlaceData.buildPlaceDataFromStream(dstream);
				if(place != null) {
					addEntry(place);
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
		for(Map.Entry<Integer, PlaceData> entry : db.entrySet()) {
			entry.getValue().writeToStream(dstream);
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
	 * Fetch a specific Place by its id
	 */
	public PlaceData getPlaceByID(int id) {
		return db.get(id);
	}
	
	/*
	 * Process a query on the database - return unique ids
	 */
	public List<Integer> query(DatabaseQuery q, DatabaseSorter s) {
		// Prepare order result builder
		SortedSet<Integer> builder = new TreeSet<Integer>(s.getComparator());
		
		// Populate result list with places that are accepted by the query
		for(Map.Entry<Integer, PlaceData> entry : db.entrySet()) {
			if(q.accepts(entry.getValue())) builder.add(entry.getKey());
		}
		
		// Create and return result list
		List<Integer> result = new ArrayList<Integer>(builder);
		return result;
	}
}