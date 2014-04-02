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
	public String getOpeningSummary() { return ""; }
	
	/*
	 * Period Class
	 */
	public class Period {
		/*
		 * Variables
		 */
		private boolean hasGeneralSpan;		// False: applies all year
		private int startMonth, startDay;	// hasGeneralSpan => set these to
		private int endMonth, endDay;		// the general period this applies
		private boolean[] daysApplied;		// Day names this period applies on
		private boolean isOpen;				// False: closed over this period
		private int openHour, openMinute;	// isOpen => set these to opening
		private int closeHour, closeMinute;	// and closing times in this period
		
		/*
		 * Constructor
		 */
		public Period(	boolean hasGeneralSpan, int startMonth, int startDay,
						int endMonth, int endDay, boolean[] daysApplied,
						boolean isOpen, int openHour, int openMinute,
						int closeHour, int closeMinute	) {
			this.hasGeneralSpan = hasGeneralSpan;
			this.startMonth = startMonth;
			this.startDay = startDay;
			this.endMonth = endMonth;
			this.endDay = endDay;
			this.daysApplied = daysApplied;
			this.isOpen = isOpen;
			this.openHour = openHour;
			this.openMinute = openMinute;
			this.closeHour = closeHour;
			this.closeMinute = closeMinute;
		}
	}
}