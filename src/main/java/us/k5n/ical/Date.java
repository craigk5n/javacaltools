/*
 * Copyright (C) 2005-2006 Craig Knudsen and other authors
 * (see AUTHORS for a complete list)
 *
 * JavaCalTools is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * A copy of the GNU Lesser General Public License is included in the Wine
 * distribution in the file COPYING.LIB. If you did not receive this copy,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 */

package us.k5n.ical;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;

/**
 * Base class for use with a variety of date-related iCalendar fields including
 * LAST-MODIFIED, DTSTAMP, DTSTART, etc. This can represent both a date and a
 * date-time.
 * 
 * According to RFC2445, date-time values can either "float" (so they are the
 * same time in every timezone), or they must have a timezone specified. This
 * class will assume the user's local timezone unless they specifically set the
 * object to be floating.
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class Date extends Property implements Comparable {
	int year, month, day;
	int hour, minute, second;
	boolean dateOnly = false; // is date only (rather than date-time)?
	static int[] monthDays = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	static int[] leapMonthDays = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	String tzid = null;
	public static final int SUNDAY = 0;
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;
	private boolean floating = false;

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *                One or more lines of iCalendar that specifies a date. Dates
	 *                must
	 *                be of one of the following formats:
	 *                <ul>
	 *                <li>19991231 (date only, no time)</li>
	 *                <li>19991231T115900 (date with local time)</li>
	 *                <li>19991231T115900Z (date and time UTC)</li>
	 *                </ul>
	 *                (This format is a based on the ISO 8601 standard.)
	 */
	public Date(String icalStr) throws ParseException, BogusDataException {
		this(icalStr, PARSE_LOOSE);
	}

	/**
	 * Constructor: create a date based on the specified year, month and day. This
	 * Date object will be considered "untimed" so that there is no need for
	 * timezone information.
	 * 
	 * @param dateType
	 *                 Type of date; this should be an ical property name like
	 *                 DTSTART,
	 *                 DTEND or DTSTAMP.
	 * @param year
	 *                 The 4-digit year
	 * @param month
	 *                 The month (1-12)
	 * @param day
	 *                 The day of the month (1-31)
	 */
	public Date(String dateType, int year, int month, int day)
			throws BogusDataException {
		super(dateType, "");

		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = this.minute = this.second = 0;
		this.dateOnly = true;
		this.floating = false;

		String yearStr, monthStr, dayStr;

		yearStr = "" + year;
		monthStr = "" + month;
		dayStr = "" + day;
		while (yearStr.length() < 4)
			yearStr = '0' + yearStr;
		if (monthStr.length() < 2)
			monthStr = '0' + monthStr;
		if (dayStr.length() < 2)
			dayStr = '0' + dayStr;
		value = yearStr + monthStr + dayStr;

		// Add attribute that says date-only
		addAttribute("VALUE", "DATE");
	}

	/**
	 * Constructor: create a date based on the specified year, month, day, hour,
	 * minute and second. The date-time value will be in the current user's
	 * timezone (retrieved from system settings). If the Date object should be
	 * "floating" (not a different time in each timezone), then the caller should
	 * call Date.setFloating after the constructor.
	 * 
	 * @param dateType
	 *                 Type of date; this should be an ical property name like
	 *                 DTSTART,
	 *                 DTEND or DTSTAMP.
	 * @param year
	 *                 The 4-digit year
	 * @param month
	 *                 The month (1-12)
	 * @param day
	 *                 The day of the month (1-31)
	 * @param hour
	 *                 The hour of day (0-23)
	 * @param min
	 *                 minute of hour (0-59(
	 * @param sec
	 *                 seconds (0-59)
	 */
	public Date(String dateType, int year, int month, int day, int hour, int min,
			int sec) throws BogusDataException {
		super(dateType, "");

		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = min;
		this.second = sec;
		this.dateOnly = false;
		this.floating = false;

		String yearStr, monthStr, dayStr, hourStr, minStr, secStr;

		yearStr = "" + year;
		monthStr = "" + month;
		dayStr = "" + day;
		hourStr = "" + hour;
		minStr = "" + min;
		secStr = "" + sec;

		while (yearStr.length() < 4)
			yearStr = '0' + yearStr;
		if (monthStr.length() < 2)
			monthStr = '0' + monthStr;
		if (dayStr.length() < 2)
			dayStr = '0' + dayStr;
		if (hourStr.length() < 2)
			hourStr = '0' + hourStr;
		if (minStr.length() < 2)
			minStr = '0' + minStr;
		if (secStr.length() < 2)
			secStr = '0' + secStr;

		// TODO: validate values
		value = yearStr + monthStr + dayStr + 'T' + hourStr + minStr + secStr;

		// Add attribute that says date has a time
		addAttribute("VALUE", "DATE-TIME");
		// Assume local system's current timezone
		this.tzid = java.util.TimeZone.getDefault().getID();
		addAttribute("TZID", this.tzid);
	}

	/**
	 * Constructor from iCalendar-formated line. The format can be either a date
	 * or a date-time value. If it is a date-time value, it should have a timezone
	 * setting. If it has no timezone setting, it will be considered a "floating"
	 * time.
	 * 
	 * @param icalStr
	 *                  One or more lines of iCalendar that specifies a date
	 * @param parseMode
	 *                  PARSE_STRICT or PARSE_LOOSE
	 */
	public Date(String icalStr, int parseMode) throws ParseException,
			BogusDataException {
		super(icalStr, parseMode);

		year = month = day = 0;
		hour = minute = second = 0;
		this.tzid = null;

		for (int i = 0; i < attributeList.size(); i++) {
			Attribute a = attributeAt(i);
			String aname = a.name.toUpperCase();
			String aval = a.value.toUpperCase();
			// Look for VALUE=DATE or VALUE=DATE-TIME
			// DATE means untimed for the event
			if (aname.equals("VALUE")) {
				if (aval.equals("DATE")) {
					dateOnly = true;
				} else if (aval.equals("DATE-TIME")) {
					dateOnly = false;
				} else {
					if (parseMode == PARSE_STRICT) {
						throw new ParseException("Unknown date VALUE '" + a.value + "'",
								icalStr);
					}
				}
			} else if (aname.equals("TZID")) {
				this.tzid = a.value;
				// Validate timezone
				try {
					ZoneId timezone = ZoneId.of(tzid);
					if (timezone == null) {
						System.err.println("Ignoring unrecognized timezone '" + tzid
								+ "' in Date " + this.getName());
					}
				} catch (Exception e1) {
					System.err.println("Ignoring unrecognized timezone '" + tzid
							+ "' in Date " + this.getName());
				}
			} else {
				System.out.println("Ignoring unknown date attribute " + a.name
						+ " in Date " + this.getName());
				// TODO: anything else allowed here?
			}
		}

		String inDate = value;
		boolean isUTC = false;

		if (inDate.length() < 8) {
			// Invalid format
			throw new ParseException("Invalid date format '" + inDate + "'", inDate);
		}

		// Make sure all parts of the year are numeric.
		for (int i = 0; i < 8; i++) {
			char ch = inDate.charAt(i);
			if (ch < '0' || ch > '9') {
				throw new ParseException("Invalid date format '" + inDate + "'",
						inDate);
			}
		}
		year = Integer.parseInt(inDate.substring(0, 4));
		month = Integer.parseInt(inDate.substring(4, 6));
		day = Integer.parseInt(inDate.substring(6, 8));
		if (day < 1 || day > 31 || month < 1 || month > 12)
			throw new BogusDataException("Invalid date '" + inDate + "'", inDate);
		// Make sure day of month is valid for specified month
		if (year % 4 == 0) {
			// leap year
			if (day > leapMonthDays[month - 1]) {
				throw new BogusDataException("Invalid day of month '" + inDate + "'",
						inDate);
			}
		} else {
			if (day > monthDays[month - 1]) {
				throw new BogusDataException("Invalid day of month '" + inDate + "'",
						inDate);
			}
		}
		// TODO: parse time, handle localtime, handle timezone
		if (inDate.length() > 8) {
			// TODO make sure dateOnly == false
			if (inDate.charAt(8) == 'T') {
				try {
					hour = Integer.parseInt(inDate.substring(9, 11));
					minute = Integer.parseInt(inDate.substring(11, 13));
					second = Integer.parseInt(inDate.substring(13, 15));
					if (hour > 23 || minute > 59 || second > 59) {
						throw new BogusDataException("Invalid time in date string '"
								+ inDate + "'", inDate);
					}
					if (inDate.length() > 15) {
						isUTC = inDate.charAt(15) == 'Z';
					}
				} catch (NumberFormatException nef) {
					throw new BogusDataException("Invalid time in date string '"
							+ inDate + "' - " + nef, inDate);
				}
			} else {
				// Invalid format
				throw new ParseException("Invalid date format '" + inDate + "'",
						inDate);
			}
		} else {
			// Just date, no time
			dateOnly = true;
		}

		if (isUTC && !dateOnly) {
			// Convert UTC to localtime
			ZonedDateTime utcDateTime = ZonedDateTime.of(year, month, day, hour,
					minute, second, 0, ZoneOffset.UTC);
			ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId
					.systemDefault());
			year = localDateTime.getYear();
			month = localDateTime.getMonthValue();
			day = localDateTime.getDayOfMonth();
			hour = localDateTime.getHour();
			minute = localDateTime.getMinute();
			second = localDateTime.getSecond();
			// Now set timezone to local since we converted.
			this.tzid = ZoneId.systemDefault().getId();
			this.addAttribute("TZID", this.tzid);
		} else if (this.tzid != null) {
			ZoneId tz = null;
			try {
				tz = ZoneId.of(this.tzid);
			} catch (IllegalArgumentException e1) {
				if (parseMode == PARSE_STRICT)
					throw new BogusDataException(
							"Invalid timezone '" + this.tzid + "'", icalStr);
			}
			if (tz == null && parseMode == PARSE_STRICT) {
				throw new BogusDataException("Invalid timezone '" + this.tzid + "'",
						icalStr);
			}
			if (tz != null) {
				// Convert to localtime
				ZonedDateTime utcDateTime = ZonedDateTime.of(year, month, day,
						hour, minute, second, 0, tz);
				ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId
						.systemDefault());
				year = localDateTime.getYear();
				month = localDateTime.getMonthValue();
				day = localDateTime.getDayOfMonth();
				hour = localDateTime.getHour();
				minute = localDateTime.getMinute();
				second = localDateTime.getSecond();
				// Since we have converted to localtime, remove the TZID attribute
				// and replace with our own timezone
				this.removeNamedAttribute("TZID");
				this.tzid = ZoneId.systemDefault().getId();
				this.addAttribute("TZID", this.tzid);
			}
		} else if (!isUTC && this.tzid == null) {
			// No timezone specified. This is a "floating" time. So, if the
			// time is 3PM, then it's 3PM EST and 3PM PST, etc.
			this.floating = true;
		}

		// Add attribute that says date-only or date with time
		if (dateOnly)
			addAttribute("VALUE", "DATE");
		else
			addAttribute("VALUE", "DATE-TIME");
	}

	/**
	 * Is the time for the Date object "floating"? (If so, then the time specified
	 * is valid for ALL timezones.)
	 * 
	 * @return
	 */
	public boolean isFloating() {
		return floating;
	}

	/**
	 * Set whether this Date object is "floating". If it is floating, then the
	 * same time should be used for all timezones. If this is set to true, all
	 * timezone info will be removed from the Date object.
	 * 
	 * @param floating
	 *                 The new floating value
	 */
	public void setFloating(boolean floating) {
		this.floating = floating;
	}

	/**
	 * Get a Data object that represents the current date and has no time
	 * information.
	 * 
	 * @param dateType
	 *                 Type of date; this should be an ical property name like
	 *                 DTSTART,
	 *                 DTEND or DTSTAMP.
	 * @return A Date object set to the current date
	 */
	public static Date getCurrentDate(String dateType) {
		Date d = null;
		Calendar c = Calendar.getInstance();
		try {
			d = new Date(dateType, c.get(Calendar.YEAR),
					c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
		} catch (BogusDataException e2) {
			// This should never happen since we're setting the m/d/y
			System.err.println(e2.toString());
			e2.printStackTrace();
		}
		return d;
	}

	/**
	 * Get a Data object that represents the current date and time information.
	 * 
	 * @param dateType
	 *                 Type of date; this should be an ical property name like
	 *                 DTSTART,
	 *                 DTEND or DTSTAMP.
	 * @return A Date object set to the current date
	 */
	public static Date getCurrentDateTime(String dateType) {
		Date d = null;
		Calendar c = Calendar.getInstance();
		try {
			d = new Date(dateType, c.get(Calendar.YEAR),
					c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
			d.setHour(c.get(Calendar.HOUR_OF_DAY));
			d.setMinute(c.get(Calendar.MINUTE));
			d.setSecond(c.get(Calendar.SECOND));
			d.setDateOnly(false);
			d.addAttribute("TZID", ZoneId.systemDefault().getId());
		} catch (BogusDataException e2) {
			// This should never happen since we're setting the m/d/y
			System.err.println(e2.toString());
			e2.printStackTrace();
		}
		return d;
	}

	public Calendar toCalendar() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c;
	}

	/**
	 * Generate the iCalendar string for this Date.
	 */
	public String toICalendar() {
		// We don't need to worry about timezone if it is date-only.
		// If there is a time, convert to GMT.
		StringBuffer sb = new StringBuffer(dateOnly ? 8 : 15);
		if (dateOnly) {
			sb.append(year);
			if (month < 10)
				sb.append('0');
			sb.append(month);
			if (day < 10)
				sb.append('0');
			sb.append(day);
			value = sb.toString();
			return super.toICalendar();
		}

		// Convert from timezone specified to GMT
		String timezoneId = this.tzid;
		if (timezoneId == null)
			timezoneId = ZoneId.systemDefault().getId();
		ZoneId tz = null;
		try {
			tz = ZoneId.of(this.tzid);
		} catch (Exception e1) {
			// Invalid timezone
		}
		if (tz != null) {
			ZonedDateTime dt = ZonedDateTime.of(year, month, day, hour, minute, second, 0,
					tz);
			ZonedDateTime utc = dt.withZoneSameInstant(ZoneOffset.UTC);
			sb.append(utc.getYear());
			if (utc.getMonthValue() < 10)
				sb.append('0');
			sb.append(utc.getMonthValue());
			if (utc.getDayOfMonth() < 10)
				sb.append('0');
			sb.append(utc.getDayOfMonth());

			sb.append('T');
			if (utc.getHour() < 10)
				sb.append('0');
			sb.append(utc.getHour());
			if (utc.getMinute() < 10)
				sb.append('0');
			sb.append(utc.getMinute());
			if (utc.getSecond() < 10)
				sb.append('0');
			sb.append(utc.getSecond());
			sb.append('Z');
			value = sb.toString();
			return super.toICalendar();
		}

		sb.append(year);
		if (month < 10)
			sb.append('0');
		sb.append(month);
		if (day < 10)
			sb.append('0');
		sb.append(day);

		sb.append('T');
		if (hour < 10)
			sb.append('0');
		sb.append(hour);
		if (minute < 10)
			sb.append('0');
		sb.append(minute);
		if (second < 10)
			sb.append('0');
		sb.append(second);
		value = sb.toString();
		return super.toICalendar();
	}

	public boolean isDateOnly() {
		return dateOnly;
	}

	public void setDateOnly(boolean dateOnly) {
		this.dateOnly = dateOnly;
		this.addAttribute("VALUE", dateOnly ? "DATE" : "DATE-TIME");
		if (!dateOnly && this.tzid == null) {
			this.tzid = java.util.TimeZone.getDefault().getID();
			addAttribute("TZID", this.tzid);
		} else if (dateOnly) {
			this.tzid = null;
			this.removeNamedAttribute("TZID");
		}
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Get the day of the week where 0=Sunday, 1=Monday, etc.
	 * 
	 * @return
	 */
	public int getDayOfWeek() {
		return Utils.getDayOfWeek(year, month, day);
	}

	public int getDayOfYear() {
		int[] days = { -1, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int[] ldays = { -1, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

		int ret = 0;
		for (int i = 1; i < this.month; i++) {
			ret += (this.year % 4 == 0 ? ldays[i] : days[i]);
		}
		ret += this.day;
		return ret;
	}

	/**
	 * Get the ISO8601 week of year. NOTE: This is DIFFERENT than the
	 * java.util.Calendar week number. The first week of the year is the week that
	 * contains that year's first Thursday (='First 4-day week').
	 * 
	 * @return
	 */
	public int getWeekOfYear() {
		int dayOfYear = getDayOfYear();
		int jan1DayOfWeek = Utils.getDayOfWeek(getYear(), 1, 1);

		// Calculate the offset of the first week according to ISO 8601
		int firstWeekOffset = (jan1DayOfWeek <= 4) ? jan1DayOfWeek - 1 : jan1DayOfWeek - 8;

		// Adjust day of the year to calculate the correct week number
		int adjustedDayOfYear = dayOfYear + firstWeekOffset;
		int weekNumber = adjustedDayOfYear / 7 + 1;

		// Handle the case where adjustedDayOfYear results in 0 or negative, indicating
		// that it belongs to the last week of the previous year
		if (adjustedDayOfYear <= 0) {
			try {
				return (new Date("DTSTART", getYear() - 1, 12, 31)).getWeekOfYear();
			} catch (BogusDataException e) {
				e.printStackTrace();
			}
		}

		// Handle the case where the week number should actually belong to the next year
		if (weekNumber > 52) {
			int nextYearJan1DayOfWeek = Utils.getDayOfWeek(getYear() + 1, 1, 1);
			if (nextYearJan1DayOfWeek <= 3) {
				return 1;
			}
		}

		return weekNumber;
	}

	/**
	 * Get the number of days in the current month.
	 * 
	 * @return
	 */
	public int getDaysInMonth() {
		if (this.year % 4 == 0)
			return leapMonthDays[this.month - 1];
		else
			return monthDays[this.month - 1];
	}

	/**
     * Convert this Date object into a date-time format used in ISO 8601.
     * Example output: "20230101T000000Z" (for UTC) or "20230101T000000" (for floating/local).
     * * @return String formatted in ISO 8601
     */
    public String toISO8601String() {
        StringBuilder sb = new StringBuilder(dateOnly ? 8 : 16);

        // Format Date Part: YYYYMMDD
        sb.append(year);
        if (month < 10) sb.append('0');
        sb.append(month);
        if (day < 10) sb.append('0');
        sb.append(day);

        // If it's a date-time, append the time part: THHMMSS
        if (!dateOnly) {
            sb.append('T');
            if (hour < 10) sb.append('0');
            sb.append(hour);
            if (minute < 10) sb.append('0');
            sb.append(minute);
            if (second < 10) sb.append('0');
            sb.append(second);

            // If it is not floating and we have a timezone, 
            // the toICalendar logic suggests appending 'Z' for UTC/GMT.
            if (!floating && (tzid == null || tzid.equalsIgnoreCase("GMT") || tzid.equalsIgnoreCase("UTC"))) {
                sb.append('Z');
            }
        }

        return sb.toString();
    }

	public boolean equals(Object o) {
		if (o instanceof Date) {
			return (this.compareTo(o) == 0);
		} else {
			return false;
		}
	}

	public Date clone() {
		Date ret = null;
		try {
			if (this.dateOnly)
				ret = new Date(this.name, this.year, this.month, this.day);
			else
				ret = new Date(this.name, this.year, this.month, this.day, this.hour,
						this.minute, this.second);
		} catch (BogusDataException e1) {
			// TODO
		}
		for (int i = 0; i < this.attributeList.size(); i++) {
			Attribute a = this.attributeAt(i);
			ret.addAttribute(a.name, a.value);
		}
		return ret;
	}

	/**
	 * Is the date/time before the specified date?
	 * 
	 * @param d2
	 *           The Date object to compare against
	 * @return
	 */
	public boolean isBefore(Date d2) {
		int ret = this.compareTo(d2);
		return ret < 0;
	}

	/**
	 * Is the date/time after the specified date?
	 * 
	 * @param d2
	 *           The Date object to compare against
	 * @return
	 */
	public boolean isAfter(Date d2) {
		int ret = this.compareTo(d2);
		return ret > 0;
	}

	public int compareTo(Object anotherDate) throws ClassCastException {
		Date d2 = (Date) anotherDate;
		if (this.year < d2.year)
			return -1;
		if (this.year > d2.year)
			return 1;
		if (this.month < d2.month)
			return -1;
		if (this.month > d2.month)
			return 1;
		if (this.day < d2.day)
			return -1;
		if (this.day > d2.day)
			return 1;
		if (this.dateOnly && d2.dateOnly)
			return 0;
		if (!this.dateOnly && d2.dateOnly)
			return -1;
		if (this.dateOnly && !d2.dateOnly)
			return -1;
		if (this.hour < d2.hour)
			return -1;
		if (this.hour > d2.hour)
			return 1;
		if (this.minute < d2.minute)
			return -1;
		if (this.minute > d2.minute)
			return 1;
		if (this.second < d2.second)
			return -1;
		if (this.second > d2.second)
			return 1;
		return 0;
	}

}
