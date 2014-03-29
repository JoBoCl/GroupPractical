package uk.ac.ox.cs.GPT9.augox;

import java.util.Date;

/**
 * Represents the times that some place is open. Given that there are complex
 * ways this information can be defined, a basic abstraction from the raw data
 * is provided.
 */
public class OpeningHours {
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
}