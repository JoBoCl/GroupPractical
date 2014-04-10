package uk.ac.ox.cs.GPT9.augox;

/**
 * Local Time Datatype
 * Represents a date and time, in the 'local timezone' of the program, for use
 * with the OpeningHours class.
 * 
 * @see OpeningHours
 */
public class LocalTime {
	/*
	 * Variables
	 */
	private int year, month, day;
	private int hour, minute;
	
	/*
	 * Constructor
	 */
	public LocalTime(int year, int month, int day, int hour, int minute) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}
	
	/*
	 * Getters
	 */
	public int getYear() { return year; }
	public int getMonth() { return month; }
	public int getDay() { return day; }
	public int getHour() { return hour; }
	public int getMinute() { return minute; }
	
	/*
	 * Does this time occur before the given time?
	 */
	public boolean isBefore(LocalTime rhs) {
		if(year == rhs.getYear()) {
			if(month == rhs.getMonth()) {
				if(day == rhs.getDay()) {
					if(hour == rhs.getHour()) {
						return minute < rhs.getMinute();
					} else {
						return hour < rhs.getHour();
					}
				} else {
					return day < rhs.getDay();
				}
			} else {
				return month < rhs.getMonth();
			}
		} else {
			return year < rhs.getYear();
		}
	}
	
	/*
	 * Is this time the same as the given time?
	 */
	public boolean isEqualTo(LocalTime rhs) {
		return year == rhs.getYear() && month == rhs.getMonth()
				&& day == rhs.getDay() && hour == rhs.getHour()
				&& minute == rhs.getMinute();
	}
}