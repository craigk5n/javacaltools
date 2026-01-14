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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.ical.iter.RecurrenceIterator;
import com.google.ical.iter.RecurrenceIteratorFactory;
import com.google.ical.values.DateTimeValueImpl;
import com.google.ical.values.DateValueImpl;
import com.google.ical.values.Frequency;
import com.google.ical.values.Weekday;
import com.google.ical.values.WeekdayNum;

class RruleByday {
	public boolean positive;
	public int weekday; // (0=Sun, etc.)
	public int number;
	public boolean valid = false;

	public RruleByday(String str) {
		int i = 0;
		weekday = -1;
		positive = true;
		if (str.charAt(i) == '+') {
			positive = true;
			i++;
		} else if (str.charAt(i) == '-') {
			positive = false;
			i++;
		}
		if (str.charAt(i) >= '0' && str.charAt(i) <= '9'
				&& str.charAt(i + 1) >= '0' && str.charAt(i + 1) <= '9') {
			number = (int) (str.charAt(i) - '0') * 10
					+ (int) (str.charAt(i + 1) - '0');
			i += 2;
		} else if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
			number = (int) (str.charAt(i) - '0');
			i++;
		}
		String sub = str.substring(i, i + 2);
		if (sub.equals("SU"))
			weekday = 0;
		else if (sub.equals("MO"))
			weekday = 1;
		else if (sub.equals("TU"))
			weekday = 2;
		else if (sub.equals("WE"))
			weekday = 3;
		else if (sub.equals("TH"))
			weekday = 4;
		else if (sub.equals("FR"))
			weekday = 5;
		else if (sub.equals("SA"))
			weekday = 6;
		if (weekday >= 0 && str.length() == i + 2)
			valid = true;
	}

	/**
	 * Convert the a google-compatible WeekdayNum object
	 */
	public WeekdayNum toWeekdayNum() {
		Weekday w;
		switch (weekday) {
			case 0:
				w = Weekday.SU;
				break;
			case 1:
				w = Weekday.MO;
				break;
			case 2:
				w = Weekday.TU;
				break;
			case 3:
				w = Weekday.WE;
				break;
			case 4:
				w = Weekday.TH;
				break;
			case 5:
				w = Weekday.FR;
				break;
			case 6:
			default:
				w = Weekday.SA;
				break;
		}
		WeekdayNum ret = new WeekdayNum(positive ? number : -number, w);
		return ret;
	}

	public String toICalendar() {
		StringBuffer ret = new StringBuffer();
		if (!positive)
			ret.append('-');
		ret.append(number);
		switch (weekday) {
			case 0:
				ret.append("SU");
				break;
			case 1:
				ret.append("MO");
				break;
			case 2:
				ret.append("TU");
				break;
			case 3:
				ret.append("WE");
				break;
			case 4:
				ret.append("TH");
				break;
			case 5:
				ret.append("FR");
				break;
			case 6:
				ret.append("SA");
				break;
		}
		return ret.toString();
	}
}

/**
 * Class for holding recurrence information for an event/todo as specified in
 * the iCalendar RRULE property.
 * 
 * This class does its own parsing of the RRULE values. However, the recurrance
 * dates are generating using Google's RFC2445 jar package. See the following
 * URL for more info: <a
 * href=
 * "http://code.google.com/p/google-rfc-2445/">http://code.google.com/p/google-rfc-2445/</a>
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class Rrule extends Property {
	/** Repeat frequency (required) */
	public int freq;
	/** Interval between recurrences (default is 1) */
	public int interval;
	/** Last day of recurrence (optional) */
	public Date untilDate;
	/** Count specifies the max number of recurrences */
	public int count;
	/** Second event falls on */
	public int[] bysecond = null;
	/** Minute event falls on */
	public int[] byminute = null;
	/** Hour event falls on */
	public int[] byhour = null;
	/** Day event falls on */
	public RruleByday[] byday = null;
	/** Day of month event falls on (1,2,-1,2, etc.) */
	public int[] bymonthday = null;
	/** Day of year event falls on (1,2,-1,2, etc.) */
	public int[] byyearday = null;
	/** Month event falls on (1,2 etc.) */
	public int[] bymonth = null;
	public int[] bysetpos = null;

	public static final int FREQ_NOT_SPECIFIED = -1;
	public static final int FREQ_YEARLY = 1;
	public static final int FREQ_MONTHLY = 2;
	public static final int FREQ_WEEKLY = 3;
	public static final int FREQ_DAILY = 4;
	public static final int FREQ_HOURLY = 5;
	public static final int FREQ_MINUTELY = 6;
	public static final int FREQ_SECONDLY = 7;

	/**
	 * Create an Rrule.
	 * 
	 * @param frequency
	 *                  The frequency of the recurrence (FREQ_DAILY, FREQ_WEEKLY,
	 *                  etc.)
	 */
	public Rrule(int frequency) {
		super("RRULE", "");
		this.freq = frequency;
	}

	/**
	 * Construct based on iCalendar RRULE text
	 * 
	 * @param icalStr
	 *                  The line(s) of iCalendar text (should be unfolded already)
	 * @param parseMode
	 *                  PARSE_STRICT or PARSE_LOOSE
	 */
	public Rrule(String icalStr, int parseMode) throws ParseException,
			BogusDataException {
		super(icalStr, parseMode);

		// Set defaults
		freq = FREQ_NOT_SPECIFIED;
		interval = 1; // default
		untilDate = null;
		count = -1; // not in use

		// No attributes allowed on RRULE
		for (int i = 0; i < attributeList.size(); i++) {
			Attribute a = attributeAt(i);
			if (parseMode == PARSE_STRICT) {
				throw new ParseException("No attribute '" + a.name
						+ "' allowed in RRULE", icalStr);
			}
		}

		String[] args = value.split(";");
		for (int i = 0; i < args.length; i++) {
			String[] param = args[i].split("=", 2);
			if (param.length != 2)
				throw new ParseException("Invalid RRULE '" + args[i] + "'", icalStr);
			String aname = param[0].toUpperCase();
			String aval = param[1].toUpperCase();
			if (aname.equals("FREQ")) {
				// only one allowed
				if (freq != FREQ_NOT_SPECIFIED)
					throw new BogusDataException(
							"More than one RRULE FREQ is not allowed", icalStr);
				if (aval.equals("YEARLY")) {
					freq = FREQ_YEARLY;
				} else if (aval.equals("MONTHLY")) {
					freq = FREQ_MONTHLY;
				} else if (aval.equals("WEEKLY")) {
					freq = FREQ_WEEKLY;
				} else if (aval.equals("DAILY")) {
					freq = FREQ_DAILY;
				} else if (aval.equals("HOURLY")) {
					freq = FREQ_HOURLY;
				} else if (aval.equals("MINUTELY")) {
					freq = FREQ_MINUTELY;
				} else if (aval.equals("SECONDLY")) {
					freq = FREQ_SECONDLY;
				} else {
					throw new BogusDataException("Invalid RRULE FREQ '" + aval + "'",
							icalStr);
				}
			} else if (aname.equals("INTERVAL")) {
				try {
					interval = Integer.parseInt(aval);
				} catch (NumberFormatException nef) {
					throw new BogusDataException("Invalid RRULE INTERVAL '" + aval
							+ "'", icalStr);
				}
			} else if (aname.equals("UNTIL")) {
				try {
					untilDate = new Date("XXX:" + aval);
				} catch (BogusDataException bde) {
					throw new BogusDataException("Invalid RRULE UNTIL date: "
							+ bde.error, icalStr);
				} catch (ParseException pe) {
					throw new BogusDataException("Invalid RRULE UNTIL date: "
							+ pe.error, icalStr);
				}
			} else if (aname.equals("COUNT")) {
				try {
					count = Integer.parseInt(aval);
				} catch (NumberFormatException nef) {
					throw new BogusDataException("Invalid RRULE COUNT '" + aval + "'",
							icalStr);
				}
			} else if (aname.equals("WKST")) {
				// TODO
			} else if (aname.equals("BYYEARNO")) {
				// TODO
			} else if (aname.equals("BYWEEKNO")) {
				// TODO
			} else if (aname.equals("BYSETPOS")) {
				String[] s = aval.split(",");
				bysetpos = new int[s.length];
				for (int j = 0; j < s.length; j++) {
					if (StringUtils.isNumber(s[j], true)) {
						bysetpos[j] = Integer.parseInt(s[j]);
						if (bysetpos[j] < -366 || bysetpos[j] > 366) {
							throw new BogusDataException("Invalid RRULE BYSETPOS (range) '"
									+ bysetpos[j] + "'", icalStr);
						}
					} else {
						throw new BogusDataException("Invalid RRULE BYSETPOS '" + s[j]
								+ "'", icalStr);
					}
				}
			} else if (aname.equals("BYMONTH")) {
				String[] s = aval.split(",");
				bymonth = new int[s.length];
				for (int j = 0; j < s.length; j++) {
					if (StringUtils.isNumber(s[j], true))
						bymonth[j] = Integer.parseInt(s[j]);
					else {
						throw new BogusDataException("Invalid RRULE BYMONTH '" + s[j]
								+ "'", icalStr);
					}
				}
			} else if (aname.equals("BYYEARDAY")) {
				String[] s = aval.split(",");
				byyearday = new int[s.length];
				for (int j = 0; j < s.length; j++) {
					if (StringUtils.isNumber(s[j], true)) {
						byyearday[j] = Integer.parseInt(s[j]);
						if (byyearday[j] < -366 || byyearday[j] > 366) {
							throw new BogusDataException("Invalid RRULE BYYEARDAY '" + s[j]
									+ "'", icalStr);
						}
					} else {
						throw new BogusDataException("Invalid RRULE BYYEARDAY '" + s[j]
								+ "'", icalStr);
					}
				}
			} else if (aname.equals("BYMONTHDAY")) {
				String[] s = aval.split(",");
				bymonthday = new int[s.length];
				for (int j = 0; j < s.length; j++) {
					if (StringUtils.isNumber(s[j], true)) {
						bymonthday[j] = Integer.parseInt(s[j]);
						if (bymonthday[j] < -31 || bymonthday[j] > 31) {
							throw new BogusDataException(
									"Invalid RRULE BYMONTHDAY (range) '" + bymonthday[j] + "'",
									icalStr);
						}
					} else {
						throw new BogusDataException("Invalid RRULE BYMONTHDAY '" + s[j]
								+ "'", icalStr);
					}
				}
			} else if (aname.equals("BYDAY")) {
				String[] bydaystr = aval.split(",");
				byday = new RruleByday[bydaystr.length];
				for (int j = 0; j < bydaystr.length; j++) {
					byday[j] = new RruleByday(bydaystr[j]);
					if (!byday[j].valid) {
						throw new BogusDataException("Invalid RRULE BYDAY '"
								+ bydaystr[j] + "'", icalStr);
					}
				}
			} else if (aname.equals("BYHOUR")) {
				String[] s = aval.split(",");
				byhour = new int[s.length];
				// validate
				for (int j = 0; j < s.length; j++) {
					if (!StringUtils.isNumber(s[j]) || Integer.parseInt(s[j]) > 23) {
						throw new BogusDataException("Invalid RRULE BYHOUR '" + s[j]
								+ "'", icalStr);
					} else {
						byhour[j] = Integer.parseInt(s[j]);
					}
				}
			} else if (aname.equals("BYMINUTE")) {
				String[] s = aval.split(",");
				byminute = new int[s.length];
				// validate
				for (int j = 0; j < s.length; j++) {
					if (!StringUtils.isNumber(s[j]) || Integer.parseInt(s[j]) > 59) {
						throw new BogusDataException("Invalid RRULE BYMINUTE '" + s[j]
								+ "'", icalStr);
					} else {
						byminute[j] = Integer.parseInt(s[j]);
					}
				}
			} else if (aname.equals("BYSECOND")) {
				String[] s = aval.split(",");
				bysecond = new int[s.length];
				// validate
				for (int j = 0; j < s.length; j++) {
					if (!StringUtils.isNumber(s[j]) || Integer.parseInt(s[j]) > 59) {
						throw new BogusDataException("Invalid RRULE BYSECOND '" + s[j]
								+ "'", icalStr);
					} else {
						bysecond[j] = Integer.parseInt(s[j]);
					}
				}
			} else if (parseMode == PARSE_STRICT) {
				// Only generate exception if strict parsing
				throw new ParseException("Invalid RRULE attribute '" + aname + "'",
						icalStr);
			}
		}

		// freq must be defined
		if (freq == FREQ_NOT_SPECIFIED) {
			throw new BogusDataException("No FREQ attribute found in RRULE",
					icalStr);
		}
	}

	/**
	 * Get the frequency of the recurrence. This can be any of the following:
	 * <ul>
	 * <li>FREQ_NOT_SPECIFIED</li>
	 * <li>FREQ_YEARLY</li>
	 * <li>FREQ_MONTHLY</li>
	 * <li>FREQ_WEEKLY</li>
	 * <li>FREQ_DAILY</li>
	 * <li>FREQ_HOURLY</li>
	 * <li>FREQ_MINUTELY</li>
	 * <li>FREQ_SECONDLY</li>
	 * </ul>
	 * 
	 * @return
	 */
	public int getFrequency() {
		return this.freq;
	}

	/**
	 * Convert to a RRULE iCalendar line
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer();
		// regenerate value in case anything was updated and so we can validate
		// parse was correct
		switch (freq) {
			case FREQ_YEARLY:
				ret.append("FREQ=YEARLY");
				break;
			case FREQ_MONTHLY:
				ret.append("FREQ=MONTHLY");
				break;
			case FREQ_WEEKLY:
				ret.append("FREQ=WEEKLY");
				break;
			case FREQ_DAILY:
				ret.append("FREQ=DAILY");
				break;
			case FREQ_HOURLY:
				ret.append("FREQ=HOURLY");
				break;
			case FREQ_MINUTELY:
				ret.append("FREQ=MINUTELY");
				break;
			case FREQ_SECONDLY:
				ret.append("FREQ=SECONDLY");
				break;
			default:
				ret.append("FREQ=UNKNOWN");
				break; // error
		}

		if (count > 0) {
			ret.append(";COUNT=");
			ret.append(count);
		}
		if (interval > 1) {
			ret.append(";INTERVAL=");
			ret.append(interval);
		}
		if (untilDate != null) {
			ret.append(";UNTIL=");
			ret.append(untilDate.value);
		}
		if (bysecond != null) {
			ret.append(";BYSECOND=");
			for (int i = 0; i < bysecond.length; i++) {
				if (i > 0)
					ret.append(',');
				ret.append(bysecond[i]);
			}
		}
		if (byminute != null) {
			ret.append(";BYMINUTE=");
			for (int i = 0; i < byminute.length; i++) {
				if (i > 0)
					ret.append(',');
				ret.append(byminute[i]);
			}
		}
		if (bymonthday != null) {
			ret.append(";BYMONTHDAY=");
			for (int i = 0; i < bymonthday.length; i++) {
				if (i > 0)
					ret.append(',');
				ret.append(bymonthday[i]);
			}
		}
		if (byyearday != null) {
			ret.append(";BYYEARDAY=");
			for (int i = 0; i < byyearday.length; i++) {
				if (i > 0)
					ret.append(',');
				ret.append(byyearday[i]);
			}
		}
		if (byday != null) {
			ret.append(";BYDAY=");
			for (int i = 0; i < byday.length; i++) {
				if (i > 0)
					ret.append(',');
				ret.append(byday[i].toICalendar());
			}
		}
		if (bymonth != null) {
			ret.append(";BYMONTH=");
			for (int i = 0; i < bymonth.length; i++) {
				if (i > 0)
					ret.append(',');
				ret.append(bymonth[i]);
			}
		}

		value = ret.toString();

		return super.toICalendar();
	}

	/**
	 * Generate a List of Date objects indicating when this event will repeat.
	 * This DOES NOT include the original event date specified by DTSTART. The
	 * Google RFC2445 package is used to generate recurrences. See the following
	 * URL for more info: <a
	 * href=
	 * "http://code.google.com/p/google-rfc-2445/">http://code.google.com/p/google-rfc-2445/</a>
	 * 
	 * @param startDate
	 *                  the start date of the recurrence
	 * @param tzid
	 *                  the timezone ID
	 */
	public List<Date> generateRecurrances(Date startDate, String tzid) {
		return generateRecurrances(startDate, tzid, null, null);
	}

	/**
	 * Generate a List of Date objects indicating when this event will repeat.
	 * This DOES NOT include the original event date specified by DTSTART. The
	 * Google RFC2445 package is used to generate recurrences. See the following
	 * URL for more info: <a
	 * href=
	 * "http://code.google.com/p/google-rfc-2445/">http://code.google.com/p/google-rfc-2445/</a>
	 * 
	 * @param startDate
	 *                  the start date of the recurrence
	 * @param tzid
	 *                  the timezone ID
	 * @param exdates
	 *                  exception dates that should not be included in the series
	 *                  (from
	 *                  the EXDATE iCalendar field)
	 * @param rdates
	 *                  inclusion dates that should be added to the series (from the
	 *                  RDATE
	 *                  iCalendar field)
	 */
	public List<Date> generateRecurrances(Date startDate, String tzid,
			List<Date> exdates, List<Date> rdates) {
		List<Date> ret = new ArrayList<Date>();
		com.google.ical.values.DateValue dtStart = null;
		if (startDate.dateOnly) {
			dtStart = new DateValueImpl(startDate.getYear(),
					startDate.getMonth(), startDate.getDay());
		} else {
			dtStart = new DateTimeValueImpl(startDate.getYear(),
					startDate.getMonth(), startDate.getDay(), startDate.getHour(),
					startDate.getMinute(), startDate.getSecond());
		}
		com.google.ical.values.RRule rrule = new com.google.ical.values.RRule();
		rrule.setName("RRULE");
		rrule.setInterval(this.interval);
		switch (this.freq) {
			case FREQ_YEARLY:
				rrule.setFreq(Frequency.YEARLY);
				break;
			case FREQ_MONTHLY:
				rrule.setFreq(Frequency.MONTHLY);
				break;
			case FREQ_WEEKLY:
				rrule.setFreq(Frequency.WEEKLY);
				break;
			case FREQ_DAILY:
				rrule.setFreq(Frequency.DAILY);
				break;
			case FREQ_HOURLY:
				rrule.setFreq(Frequency.HOURLY);
				break;
			case FREQ_MINUTELY:
				rrule.setFreq(Frequency.MINUTELY);
				break;
			case FREQ_SECONDLY:
				rrule.setFreq(Frequency.SECONDLY);
				break;
		}
		if (this.count > 0)
			rrule.setCount(this.count);
		if (this.byyearday != null && this.byyearday.length > 0) {
			rrule.setByYearDay(this.byyearday);
		}
		if (this.bymonth != null && this.bymonth.length > 0)
			rrule.setByMonth(this.bymonth);
		if (this.bymonthday != null && this.bymonthday.length > 0)
			rrule.setByMonthDay(this.bymonthday);
		if (this.byday != null && this.byday.length > 0) {
			List<WeekdayNum> weekdays = new ArrayList<WeekdayNum>();
			for (int i = 0; i < this.byday.length; i++) {
				WeekdayNum weekday = this.byday[i].toWeekdayNum();
				weekdays.add(weekday);
			}
			rrule.setByDay(weekdays);
		}
		if (this.byhour != null && this.byhour.length > 0)
			rrule.setByHour(this.byhour);
		if (this.byminute != null && this.byminute.length > 0)
			rrule.setByMinute(this.byminute);
		if (this.bysecond != null && this.bysecond.length > 0)
			rrule.setBySecond(this.bysecond);
		if (this.bysetpos != null && this.bysetpos.length > 0)
			rrule.setBySetPos(this.bysetpos);
		if (this.untilDate != null) {
			com.google.ical.values.DateValue rruleUntil = null;
			if (this.untilDate.dateOnly) {
				rruleUntil = new DateValueImpl(this.untilDate.getYear(),
						this.untilDate.getMonth(), this.untilDate.getDay());
			} else {
				rruleUntil = new DateTimeValueImpl(this.untilDate.getYear(),
						this.untilDate.getMonth(), this.untilDate.getDay(),
						this.untilDate.getHour(), this.untilDate.getMinute(),
						this.untilDate.getSecond());
			}
			rrule.setUntil(rruleUntil);
		}

		// TODO: does this conflict with Joda's own Timezone stuff?
		// should we be using a Joda timezone object here?
		if (tzid == null)
			tzid = "GMT";
		java.util.TimeZone timezone = java.util.TimeZone.getTimeZone(tzid);
		RecurrenceIterator iter = RecurrenceIteratorFactory
				.createRecurrenceIterator(rrule, dtStart, timezone);
		int num = 0;
		int thisYear = java.util.Calendar.getInstance().get(
				java.util.Calendar.YEAR);
		while (iter.hasNext() && num++ < 10000) {
			com.google.ical.values.DateValue d = iter.next();
			// Check if the generated date matches the original start date
			boolean isSameAsStartDate = d.year() == startDate.getYear() &&
					d.month() == startDate.getMonth() &&
					d.day() == startDate.getDay() &&
					(!startDate.dateOnly || (d instanceof com.google.ical.values.DateTimeValue &&
							((com.google.ical.values.DateTimeValue) d).hour() == startDate.getHour() &&
							((com.google.ical.values.DateTimeValue) d).minute() == startDate.getMinute() &&
							((com.google.ical.values.DateTimeValue) d).second() == startDate.getSecond()));

			if (isSameAsStartDate) {
				continue; // Skip adding the original start date
			}
			if (d instanceof com.google.ical.values.DateTimeValue) {
				com.google.ical.values.DateTimeValue dt = (com.google.ical.values.DateTimeValue) d;
				try {
					Date newDateTime = new Date("XXX", dt.year(), dt.month(),
							dt.day(), dt.hour(), dt.minute(), dt.second());
					boolean isException = false;
					for (int i = 0; exdates != null && i < exdates.size(); i++) {
						Date exdate = exdates.get(i);
						if (newDateTime.compareTo(exdate) == 0)
							isException = true;
					}
					if (!newDateTime.equals(startDate) && !isException)
						ret.add(newDateTime);
				} catch (BogusDataException e1) {
					e1.printStackTrace();
				}
			} else {
				try {
					Date newDate = new Date("EXDATE", d.year(), d.month(), d.day());
					boolean isException = false;
					for (int i = 0; exdates != null && i < exdates.size(); i++) {
						Date exdate = exdates.get(i);
						if (newDate.compareTo(exdate) == 0)
							isException = true;
					}
					if (!newDate.equals(startDate) && !isException)
						ret.add(newDate);
				} catch (BogusDataException e1) {
					e1.printStackTrace();
				}
			}
			// Max of 100 years from this year. (To avoid endless loop.)
			// TODO: make this configurable
			if (d.year() >= thisYear + 100)
				break;
		}
		// Add in inclusion dates (from RDATE field)
		// If the recurrence already has the date, don't re-add it (which could
		// happen if the RRULE was changed after the RDATE field was set).
		if (rdates != null) {
			for (int i = 0; i < rdates.size(); i++) {
				Date d = rdates.get(i);
				boolean alreadyThere = false;
				for (int j = 0; j < ret.size(); j++) {
					Date cur = ret.get(j);
					if (cur.compareTo(d) == 0)
						alreadyThere = true;
				}
				if (!alreadyThere)
					ret.add(d);
			}
			// Sort the List since they might be out of order now.
			Collections.sort(ret);
		}
		return ret;
	}

	/**
	 * Check if this RRULE is valid
	 *
	 * @return true if the RRULE is valid
	 */
	public boolean isValid() {
		return isValid(null);
	}

	/**
	 * Check if this RRULE is valid with optional error details
	 *
	 * @param errors List to collect validation error messages (can be null)
	 * @return true if the RRULE is valid
	 */
	public boolean isValid(List<String> errors) {
		boolean valid = true;

		// Frequency is required
		if (freq == FREQ_NOT_SPECIFIED || freq < FREQ_YEARLY || freq > FREQ_SECONDLY) {
			valid = false;
			if (errors != null) errors.add("RRULE must have a valid FREQ");
		}

		// Interval must be positive
		if (interval <= 0) {
			valid = false;
			if (errors != null) errors.add("RRULE INTERVAL must be positive");
		}

		// Count and Until are mutually exclusive
		// count >= 0 means COUNT was explicitly specified (count = -1 when not specified)
		if (count >= 0 && untilDate != null) {
			valid = false;
			if (errors != null) errors.add("RRULE cannot have both COUNT and UNTIL");
		}

		// Count must be positive if specified
		if (count >= 0 && count <= 0) {
			valid = false;
			if (errors != null) errors.add("RRULE COUNT must be positive");
		}

		// Validate BYxxx rules based on frequency
		if (freq == FREQ_YEARLY) {
			// BYMONTHDAY and BYYEARDAY are valid for YEARLY
		} else if (freq == FREQ_MONTHLY) {
			// BYMONTHDAY is valid for MONTHLY
			if (byyearday != null && byyearday.length > 0) {
				valid = false;
				if (errors != null) errors.add("RRULE BYYEARDAY not valid with MONTHLY frequency");
			}
		} else if (freq == FREQ_WEEKLY) {
			// BYMONTHDAY and BYYEARDAY not valid for WEEKLY
			if (bymonthday != null && bymonthday.length > 0) {
				valid = false;
				if (errors != null) errors.add("RRULE BYMONTHDAY not valid with WEEKLY frequency");
			}
			if (byyearday != null && byyearday.length > 0) {
				valid = false;
				if (errors != null) errors.add("RRULE BYYEARDAY not valid with WEEKLY frequency");
			}
		} else if (freq == FREQ_DAILY) {
			// Most BYxxx rules not valid for DAILY
			if (bymonthday != null && bymonthday.length > 0) {
				valid = false;
				if (errors != null) errors.add("RRULE BYMONTHDAY not valid with DAILY frequency");
			}
			if (byyearday != null && byyearday.length > 0) {
				valid = false;
				if (errors != null) errors.add("RRULE BYYEARDAY not valid with DAILY frequency");
			}
			if (byday != null && byday.length > 0) {
				valid = false;
				if (errors != null) errors.add("RRULE BYDAY not valid with DAILY frequency");
			}
		}

		// Validate BYSETPOS values (-53 to 53, excluding 0)
		if (bysetpos != null) {
			for (int pos : bysetpos) {
				if (pos == 0 || pos < -53 || pos > 53) {
					valid = false;
					if (errors != null) errors.add("RRULE BYSETPOS values must be between -53 and 53, excluding 0");
					break;
				}
			}
		}

		// Validate BYMONTH values (1-12)
		if (bymonth != null) {
			for (int month : bymonth) {
				if (month < 1 || month > 12) {
					valid = false;
					if (errors != null) errors.add("RRULE BYMONTH values must be between 1 and 12");
					break;
				}
			}
		}

		// Validate BYMONTHDAY values (-31 to 31, excluding 0)
		if (bymonthday != null) {
			for (int day : bymonthday) {
				if (day == 0 || day < -31 || day > 31) {
					valid = false;
					if (errors != null) errors.add("RRULE BYMONTHDAY values must be between -31 and 31, excluding 0");
					break;
				}
			}
		}

		// Validate BYYEARDAY values (-366 to 366, excluding 0)
		if (byyearday != null) {
			for (int day : byyearday) {
				if (day == 0 || day < -366 || day > 366) {
					valid = false;
					if (errors != null) errors.add("RRULE BYYEARDAY values must be between -366 and 366, excluding 0");
					break;
				}
			}
		}

		return valid;
	}
}
