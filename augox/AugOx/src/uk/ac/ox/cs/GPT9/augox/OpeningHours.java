package uk.ac.ox.cs.GPT9.augox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.util.Log;

/**
 * Represents the times that some place is open. Given that there are complex
 * ways this information can be defined, a basic abstraction from the raw data
 * is provided.
 * 
 * NOTE: Features that would use this class were cut during development, so
 * some more complex features were ultimately not implemented (namely checking
 * when next open/closed) or have broken implementations (namely bulletproof
 * OSM data mining). The core functionality is fully functional, however.
 */
public class OpeningHours {
	/*
	 * Representation:
	 * List of periods in priority order (highest first).
	 * Each period represents a recurring block of time, and whether the thing
	 * is open/closed during that block.
	 * 
	 * Other notes:
	 * All times local.
	 */
	
	/*
	 * Variables
	 */
	private List<Period> periods;
	
	/*
	 * Constructor
	 */
	public OpeningHours(List<Period> periods) {
		this.periods = periods;
	}
	
	/*
	 * Empty constructor
	 */
	public OpeningHours() {
		this.periods = new ArrayList<Period>();
	}
	
	/*
	 * Construct from OSM-style string
	 */
	public OpeningHours(String oh) {
		// Prepare periods list
		this.periods = new ArrayList<Period>();
		
		// Loop through each section of the input, building period
		String seg;
		int sd1, sd2;
		boolean[] isOpen = {false, false, false, false, false, false, false};
		int[] openHour = {0, 0, 0, 0, 0, 0, 0};
		int[] openMinute = {0, 0, 0, 0, 0, 0, 0};
		int[] closeHour = {0, 0, 0, 0, 0, 0, 0};
		int[] closeMinute = {0, 0, 0, 0, 0, 0, 0};
		while(oh.length() > 0) {
			Log.d("DGBJames", oh);
			// Fetch next segment
			int nextsemi = oh.indexOf(";");
			if(nextsemi == -1) {
				seg = oh;
				oh = "";
			} else {
				seg = oh.substring(0, nextsemi);
				oh = oh.substring(nextsemi + 2);
			}
			Log.d("DGBJames", seg);
			
			// Extract segment start and end days
			sd1 = getShortDayNumber(seg.substring(0,2));
			if(seg.substring(2,3).equals(" ")) {
				sd2 = sd1;
				seg = seg.substring(3);
			} else {
				sd2 = getShortDayNumber(seg.substring(3,5));
				seg = seg.substring(6);
			}
			if(sd1 == -1 || sd2 == -1) return;
			
			Log.d("DGBJames", seg);
			
			// Apply opening times to each day in the segment range
			int r = (sd1 < sd2) ? sd2 - sd1 : sd1 - sd2;
			for(int i = 0; i <= r; i++) {
				int d = (sd1 + i) % 7;
				isOpen[d] = true;
				try {
					openHour[d] = Integer.parseInt(seg.substring(0,2));
					openMinute[d] = Integer.parseInt(seg.substring(3,5));
					closeHour[d] = Integer.parseInt(seg.substring(6,8));
					closeMinute[d] = Integer.parseInt(seg.substring(9,11));
				} catch(Exception e) {
					return;
				}
			}
		}
		
		// Add period to list
		periods.add(new Period(false, 0, 0, 0, 0, false, isOpen, openHour,
				openMinute, closeHour, closeMinute));
		
		Log.d("DBGJames", getOpeningSummary());
	}
	
	/*
	 * Helper function for string constructor
	 */
	private int getShortDayNumber(String sd) {
		if(sd.equals("Su")) return 0;
		if(sd.equals("Mo")) return 1;
		if(sd.equals("Tu")) return 2;
		if(sd.equals("We")) return 3;
		if(sd.equals("Th")) return 4;
		if(sd.equals("Fr")) return 5;
		if(sd.equals("Sa")) return 6;
		return -1;
	}
	
	/*
	 * Return if the thing is open at give time and date.
	 */
	public boolean isOpenAt(LocalTime date) {
		// Consider each period in priority order
		for(Period p : periods) {
			// What is the state of this period on the given date?
			Period.PeriodOpenState state = p.isOpenAt(date);
			if(state == Period.PeriodOpenState.OPEN) return true;
			if(state == Period.PeriodOpenState.CLOSED) return false;
		}
		
		// No periods applicable - assume closed
		return false;
	}

	/* ***NOT IMPLEMENTED***
	 * Return the time and date at which the thing next transitions to being
	 * open after the given time and date (or null if it will never open).
	 * Note that if it is already open, this returns the *next* time it
	 * *becomes* open.
	 */
	public LocalTime nextOpen(LocalTime date) {
		return null;
	}
	
	/* ***NOT IMPLEMENTED***
	 * Return the time and date at which the thing next transitions to being
	 * closed after the given time and date (or null if it will never close).
	 * Note that if it is already closed, this returns the *next* time it
	 * *becomes* closed.
	 */
	public LocalTime nextClosed(LocalTime date) {
		return null;
	}

	/*
	 * Return a summary of the opening times as a human-readable String.
	 */
	public String getOpeningSummary() {
		// Prepare result to build
		String result = "";
		
		// Loop through periods in priority order, adding summaries
		for(Period p : periods) {
			result += p.getSummary();
		}
		
		// Return result
		return result;
	}
	
	/*
	 * Write the opening hours into the given data stream
	 */
	public void writeToStream(DataOutputStream dstream) throws IOException {
		// Write number of periods to stream
		dstream.writeInt(periods.size());
		
		// Write periods to stream
		for(Period p : periods) {
			p.writeToStream(dstream);
		}
	}
	
	/*
	 * Create and return an OpeningHours object from the given data stream
	 */
	public static OpeningHours buildOpeningHoursFromStream(DataInputStream
			dstream) throws IOException {
		// Load number of periods from stream
		int n = dstream.readInt();
		
		// Load all periods
		List<Period> periods = new ArrayList<Period>();
		for(int i = 0; i < n; i++) {
			Period p = Period.buildPeriodFromStream(dstream);
			if(p != null) periods.add(p);
		}
		
		// Build and return object
		OpeningHours openinghours = new OpeningHours(periods);
		return openinghours;
	}
	
	/*
	 * Period Class
	 */
	public static class Period {
		/*
		 * Variables
		 * Notes: First day of week is Sunday - value 0
		 * Day and month numberings start from 1
		 */
		private boolean hasGeneralSpan;		// False: applies all year
		private int startMonth, startDay;	// hasGeneralSpan => set these to
		private int endMonth, endDay;		// the general period this applies
		private boolean useGenericTimes;	// True: use a single open/close
											// time; False: use one open/close
											// time per day
		private boolean[] isOpen;			// Open/closed in general/per day
		private int[] openHour, openMinute;	// Opening times
		private int[] closeHour, closeMinute;	// Closing times
		
		/*
		 * Enumerations
		 */
		public enum PeriodOpenState {
			NA,
			OPEN,
			CLOSED
		}
		
		/*
		 * Constructor
		 */
		public Period(	boolean hasGeneralSpan, int startMonth, int startDay,
						int endMonth, int endDay, boolean useGenericTimes,
						boolean[] isOpen, int[] openHour, int[] openMinute,
						int[] closeHour, int[] closeMinute	) {
			this.hasGeneralSpan = hasGeneralSpan;
			this.startMonth = startMonth;
			this.startDay = startDay;
			this.endMonth = endMonth;
			this.endDay = endDay;
			this.useGenericTimes = useGenericTimes;
			this.isOpen = isOpen;
			this.openHour = openHour;
			this.openMinute = openMinute;
			this.closeHour = closeHour;
			this.closeMinute = closeMinute;
		}
		
		/*
		 * Does this period cover the given date?
		 */
		public boolean isApplicable(LocalTime date) {
			// No general span specified => always applicable
			if(!hasGeneralSpan) return true;
			
			// Find latest start date before given date
			LocalTime periodstart = new LocalTime(date.getYear(), startMonth,
					startDay, 0, 0);
			if(date.isBefore(periodstart)) periodstart = new LocalTime(
					date.getYear() - 1, startMonth, startDay, 0, 0);
			
			// Find matching end date
			LocalTime periodend = new LocalTime(periodstart.getYear(),
					endMonth, endDay, 23, 59);
			if(periodend.isBefore(periodstart)) periodend = new LocalTime(
					periodstart.getYear() + 1, endMonth, endDay, 23, 59);
			
			// We already know that date occurs on or after periodstart
			// Hence period is applicable if date occurs before or on periodend
			return !periodend.isBefore(date);
		}
		
		/*
		 * Does this period cover the given date, and if so what is its state?
		 */
		public PeriodOpenState isOpenAt(LocalTime date) {
			// If not applicable, return not applicable
			if(!isApplicable(date)) return PeriodOpenState.NA;
			
			// Calculate which day data to use
			int dayid;
			if(useGenericTimes) {
				dayid = 0;
			} else {
				Calendar cal = new GregorianCalendar(date.getYear(),
						date.getMonth() - 1, date.getDay());
				dayid = cal.get(Calendar.DAY_OF_WEEK) - 1;
			}
			
			// If the period is closed on this day, return closed
			if(!isOpen[dayid]) return PeriodOpenState.CLOSED;
			
			// Calculate opening and closing times for this date
			LocalTime opentime = new LocalTime(date.getYear(), date.getMonth(),
					date.getDay(), openHour[dayid], openMinute[dayid]);
			LocalTime closetime = new LocalTime(date.getYear(), date.getMonth(),
					date.getDay(), closeHour[dayid], closeMinute[dayid]);
			
			// Does the given date fall within the opening times?
			if(date.isBefore(opentime) || closetime.isBefore(date)) {
				// No
				return PeriodOpenState.CLOSED;
			} else {
				// Yes
				return PeriodOpenState.OPEN;
			}
		}
		
		/*
		 * Return a summary of this period as a human-readable String.
		 */
		public String getSummary() {
			// Prepare result to build
			String result = "";
			
			// Does this period have a general span?
			if(hasGeneralSpan) {
				// Describe the span
				if(!(startDay == endDay && startMonth == endMonth)) {
					result += getDateString(startMonth, startDay) + " - ";
				}
				result += getDateString(endMonth, endDay) + ":\n";
			}
			
			// Does this period have a generic open/closed state, or specify
			// on a per-day basis?
			if(useGenericTimes) {
				// Generic
				// Describe the opening times
				if(isOpen[0]) {
					if(!(openHour[0] == closeHour[0]
							&& openMinute[0] == closeMinute[0])) {
						result += getTimeString(openHour[0], openMinute[0])
								+ "-";
					}
					result += getTimeString(closeHour[0], closeMinute[0])
							+ " : Open\n";
				} else {
					result += "Closed\n";
				}
			} else {
				// Per-day
				// Describe the opening times
				for(int i = 0; i < 7; i++) {
					result += String.format("%s ", getDayName(i));
					if(isOpen[i]) {
						if(!(openHour[i] == closeHour[i]
								&& openMinute[i] == closeMinute[i])) {
							result += getTimeString(openHour[i], openMinute[i])
									+ "-";
						}
						result += getTimeString(closeHour[i], closeMinute[i])
								+ " : Open\n";
					} else {
						result += ": Closed\n";
					}
				}
			}
			
			// Return result
			return result;
		}
		
		/*
		 * Write the period into the given data stream
		 */
		public void writeToStream(DataOutputStream dstream) throws IOException {
			dstream.writeBoolean(hasGeneralSpan);
			if(hasGeneralSpan) {
				dstream.writeInt(startMonth);
				dstream.writeInt(startDay);
				dstream.writeInt(endMonth);
				dstream.writeInt(endDay);
			}
			dstream.writeBoolean(useGenericTimes);
			int n = useGenericTimes ? 1 : 7;
			for(int i = 0; i < n; i++) {
				dstream.writeBoolean(isOpen[i]);
				dstream.writeInt(openHour[i]);
				dstream.writeInt(openMinute[i]);
				dstream.writeInt(closeHour[i]);
				dstream.writeInt(closeMinute[i]);
			}
		}
		
		/*
		 * Create and return a Period object from the given data stream
		 */
		public static Period buildPeriodFromStream(DataInputStream dstream)
				throws IOException {
			// Load values from stream
			boolean hasGeneralSpan = dstream.readBoolean();
			int startMonth = 0;
			int startDay = 0;
			int endMonth = 0;
			int endDay = 0;
			if(hasGeneralSpan) {
				startMonth = dstream.readInt();
				startDay = dstream.readInt();
				endMonth = dstream.readInt();
				endDay = dstream.readInt();
			}
			boolean useGenericTimes = dstream.readBoolean();
			int n = useGenericTimes ? 1 : 7;
			boolean[] isOpen = new boolean[n];
			int[] openHour = new int[n];
			int[] openMinute = new int[n];
			int[] closeHour = new int[n];
			int[] closeMinute = new int[n];
			for(int i = 0; i < n; i++) {
				isOpen[i] = dstream.readBoolean();
				openHour[i] = dstream.readInt();
				openMinute[i] = dstream.readInt();
				closeHour[i] = dstream.readInt();
				closeMinute[i] = dstream.readInt();
			}
			
			// Build and return object
			Period period = new Period(hasGeneralSpan, startMonth, startDay,
					endMonth, endDay, useGenericTimes, isOpen, openHour,
					openMinute, closeHour, closeMinute);
			return period;
		}
		
		/*
		 * Get a string representing the given day/month combo
		 */
		private static String getDateString(int month, int day) {
			return String.format("%s%s %s", day, getOrdinal(day),
					getMonthName(month));
		}
		
		/*
		 * Get a string representing the given time
		 */
		private static String getTimeString(int hour, int minute) {
			if(minute < 10) {
				return String.format("%s:0%s", hour, minute);
			} else {
				return String.format("%s:%s", hour, minute);
			}
		}
		
		/*
		 * Get name of the given numbered day (starting with Sunday: 0)
		 */
		private static String getDayName(int n) {
			switch(n) {
			case 0: return "Sunday";
			case 1: return "Monday";
			case 2: return "Tuesday";
			case 3: return "Wednesday";
			case 4: return "Thursday";
			case 5: return "Friday";
			case 6: return "Saturday";
			default: return "INVALID DAY";
			}
		}
		
		/*
		 * Get name of the given numbered month
		 */
		private static String getMonthName(int n) {
			switch(n) {
			case 1: return "January";
			case 2: return "February";
			case 3: return "March";
			case 4: return "April";
			case 5: return "May";
			case 6: return "June";
			case 7: return "July";
			case 8: return "August";
			case 9: return "September";
			case 10: return "October";
			case 11: return "November";
			case 12: return "December";
			default: return "INVALID MONTH";
			}
		}
		
		/*
		 * Get the ordinal for the given number
		 */
		private static String getOrdinal(int n) {
			if(n >= 10 && n <= 19) return "th";
			if(n % 10 == 1) return "st";
			if(n % 10 == 2) return "nd";
			if(n % 10 == 3) return "rd";
			return "th";
		}
	}
}