package uk.ac.ox.cs.GPT9.augox;

import java.util.Calendar;
import java.util.Date;

public class SessionHelper {
	public static Session getSessionAtTime(final int time) {
		switch (time) {
			case 8 * 60 + 0:
				return Session.NIGHT;
			case 9 * 60 + 0:
				return Session.BREAKFAST;
			case 12 * 60 + 30:
				return Session.MORNING;
			case 13 * 60 + 30:
				return Session.LUNCH;
			case 18 * 60:
				return Session.AFTERNOON;
			case 19 * 60:
				return Session.TEA;
			case 22 * 60:
				return Session.EVENING;
			case 24 * 60:
				return Session.NIGHT;
			default:
				return null;
		}
	}

	public static Session getSessionAtTime(int hour, int minute) {
		return getSessionAtTime(hour * 60 + minute);
	}

	public static PlaceCategory[] getCategoriesForSession(Session act) {
		switch (act) {
			case BREAKFAST:
			case LUNCH:
			case TEA:
				return new PlaceCategory[] { PlaceCategory.RESTAURANT };
			case MORNING:
			case AFTERNOON:
				return new PlaceCategory[] { PlaceCategory.COLLEGE,
						PlaceCategory.MUSEUM };
			case EVENING:
			case NIGHT:
				return new PlaceCategory[] { PlaceCategory.BAR };
			default:
				return null;
		}

	}

	public static LocalTime getStartTimeForSession(Session session) {
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		int hour;
		int minute;
		switch (session) {
			case BREAKFAST: {
				hour = 8;
				minute = 0;
				break;
			}
			case MORNING: {
				hour = 9;
				minute = 0;
				break;
			}
			case LUNCH: {
				hour = 12;
				minute = 30;
				break;
			}
			case AFTERNOON: {
				hour = 13;
				minute = 30;
				break;
			}
			case TEA: {
				hour = 18;
				minute = 0;
				break;
			}
			case EVENING: {
				hour = 19;
				minute = 0;
				break;
			}
			case NIGHT: {
				hour = 22;
				minute = 0;
				break;
			}
			default:
				return null;
		}
		return new LocalTime(year, month, day, hour, minute);
	}
}
