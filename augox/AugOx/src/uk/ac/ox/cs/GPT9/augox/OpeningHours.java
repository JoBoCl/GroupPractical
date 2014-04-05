package uk.ac.ox.cs.GPT9.augox;

import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

	/*
	 * Return the time and date at which the thing next transitions to being
	 * open after the given time and date (or null if it will never open).
	 */
	public LocalTime nextOpen(LocalTime date) {
		return null;
	}
	
	/*
	 * Return the time and date at which the thing next transitions to being
	 * closed after the given time and date (or null if it will never close).
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