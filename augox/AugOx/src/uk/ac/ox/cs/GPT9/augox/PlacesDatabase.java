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
	}
	
	/*
	 * Construct database from list of PlaceData
	 */
	public PlacesDatabase(List<PlaceData> places) {
		for(PlaceData place : places) {
			addEntry(place);
		}
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
	 * Dump the database into the given stream as human-readable XML.
	 */
	public void writeToStreamAsXML(OutputStream stream) throws IOException {
		// Create data output stream
		DataOutputStream dstream = new DataOutputStream(stream);
		
		// Write all places in the database to the stream
		dstream.writeChars("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		dstream.writeChars("<database>\n");
		for(Map.Entry<Integer, PlaceData> entry : db.entrySet()) {
			entry.getValue().writeToStreamAsXML(dstream, entry.getKey());
		}
		dstream.writeChars("</database>");
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