package uk.ac.ox.cs.GPT9.augox;

import java.util.Date;
import java.util.List;

/**
 * Represents the times that some place is open. Given that there are complex
 * ways this information can be defined, a basic abstraction from the raw data
 * is provided.
 */
public class OpeningHours {
	/*
	 * Representation:
	 * List of periods in priority order (highest first).
	 * Each period represents a recurring block of time, and whether the thing
	 * is open/closed during that block.
	 * 
	 * Other notes:
	 * All times UTC.
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
	 * Return if the thing is open at give time and date.
	 */
	public boolean isOpenAt(Date date) { return true; }

	/*
	 * Return the time and date at which the thing next transitions to being
	 * open / closed after the given time and date.
	 */
	public Date nextOpen(Date date) { return new Date(); }
	public Date nextClosed(Date date) { return new Date(); }

	/*
	 * Return a summary of the opening times as a human-readable String.
	 */
	public String getOpeningSummary() {
		String result = "";
		for(Period p : periods) {
			result += p.getSummary();
		}
		return result;
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
		 * Return a summary of this period as a human-readable String.
		 */
		public String getSummary() {
			// Prepare result to build
			String result = "";
			
			// Does this period have a general span?
			if(hasGeneralSpan) {
				// Describe the span
				if(!(startDay == endDay && startMonth == endMonth)) {
					result += String.format("%s%s %s - ", startDay,
							getOrdinal(startDay), getMonthName(startMonth));
				}
				result += String.format("%s%s %s :\n", endDay,
						getOrdinal(endDay), getMonthName(endMonth));
			}
			
			// Does this period have a generic open/closed state, or specify
			// on a per-day basis?
			if(useGenericTimes) {
				// Generic
				// Describe the opening times
				if(isOpen[0]) {
					if(!(openHour[0] == closeHour[0]
							&& openMinute[0] == closeMinute[0])) {
						result += String.format("%s:%s - ", openHour[0],
								openMinute[0]);
					}
					result += String.format("%s:%s : Open\n", closeHour[0],
							closeMinute[0]);
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
							result += String.format("%s:%s - ", openHour[i],
									openMinute[i]);
						}
						result += String.format("%s:%s : Open\n", closeHour[i],
								closeMinute[i]);
					} else {
						result += "Closed\n";
					}
				}
			}
			
			// Return result
			return result;
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