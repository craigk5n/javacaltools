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
import java.util.List;

/**
 * iCalendar Timezone DAYLIGHT sub-component.
 * The DAYLIGHT sub-component defines daylight saving time observance within a VTIMEZONE component.
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class TimezoneDaylight implements Constants {
	/** Start date/time for this observance */
	protected Date dtstart = null;
	/** Offset from UTC when this observance begins */
	protected String tzOffsetFrom = null;
	/** Offset to UTC when this observance is in effect */
	protected String tzOffsetTo = null;
	/** Timezone name for this observance */
	protected String tzName = null;
	/** Recurrence rule for this observance */
	protected Rrule rrule = null;
	/** Recurrence dates for this observance */
	protected List<Date> rdates = null;
	/** Comment for this observance */
	protected Comment comment = null;

	/**
	 * Create a TimezoneDaylight object based on specified iCalendar data
	 * 
	 * @param parser
	 *                    The ICalendarParser object
	 * @param initialLine
	 *                    The starting line number
	 * @param textLines
	 *        List of iCalendar text lines
	 */
	public TimezoneDaylight(CalendarParser parser, int initialLine, List<String> textLines) {
		rdates = new ArrayList<Date>();
		
		for (int i = 0; i < textLines.size(); i++) {
			String line = textLines.get(i);
			try {
				parseLine(line, parser.getParseMethod());
			} catch (BogusDataException bde) {
				parser.reportParseError(new ParseError(initialLine + i, bde.error,
						line));
			} catch (ParseException pe) {
				parser.reportParseError(new ParseError(initialLine + i, pe.error,
						line));
			}
		}
	}

	/**
	 * Parse a line of iCalendar text
	 * 
	 * @param icalStr
	 *               The line of text
	 * @param parseMethod
	 *               PARSE_STRICT or PARSE_LOOSE
	 */
	public void parseLine(String icalStr, int parseMethod)
			throws ParseException, BogusDataException {
		String up = icalStr.toUpperCase();
		if (up.equals("BEGIN:DAYLIGHT") || up.equals("END:DAYLIGHT")) {
			// ignore begin/end markers
		} else if (up.trim().length() == 0) {
			// ignore empty lines
		} else if (up.startsWith("DTSTART:")) {
			dtstart = new Date(icalStr);
		} else if (up.startsWith("TZOFFSETFROM:")) {
			Property p = new Property(icalStr);
			tzOffsetFrom = p.value;
		} else if (up.startsWith("TZOFFSETTO:")) {
			Property p = new Property(icalStr);
			tzOffsetTo = p.value;
		} else if (up.startsWith("TZNAME:")) {
			Property p = new Property(icalStr);
			tzName = p.value;
		} else if (up.startsWith("RRULE:")) {
			rrule = new Rrule(icalStr, parseMethod);
		} else if (up.startsWith("RDATE:")) {
			Property p = new Property(icalStr);
			String[] dates = p.value.split(",");
			for (int i = 0; i < dates.length; i++) {
				Date date = new Date(dates[i].trim());
				rdates.add(date);
			}
		} else if (up.startsWith("COMMENT:")) {
			comment = new Comment(icalStr);
		} else if (isParseStrict(parseMethod)) {
			throw new ParseException("Unrecognized data in DAYLIGHT: " + icalStr, icalStr);
		}
	}

	/**
	 * Check if parse method is strict
	 */
	private boolean isParseStrict(int parseMethod) {
		return parseMethod == PARSE_STRICT;
	}

	/**
	 * Was enough information parsed for this TimezoneDaylight to be valid?
	 * 
	 * @return true if required properties are present
	 */
	public boolean isValid() {
		return isValid(null);
	}

	/**
	 * Check if this TimezoneDaylight is valid with optional error details
	 *
	 * @param errors List to collect validation error messages (can be null)
	 * @return true if the timezone daylight is valid
	 */
	public boolean isValid(List<String> errors) {
		boolean valid = true;

		// Required fields
		if (dtstart == null) {
			valid = false;
			if (errors != null) errors.add("TimezoneDaylight must have DTSTART");
		}

		if (tzOffsetFrom == null) {
			valid = false;
			if (errors != null) errors.add("TimezoneDaylight must have TZOFFSETFROM");
		}

		if (tzOffsetTo == null) {
			valid = false;
			if (errors != null) errors.add("TimezoneDaylight must have TZOFFSETTO");
		}

		// TZOFFSET validation
		if (tzOffsetFrom != null && !isValidTimezoneOffset(tzOffsetFrom)) {
			valid = false;
			if (errors != null) errors.add("TimezoneDaylight TZOFFSETFROM is invalid: " + tzOffsetFrom);
		}

		if (tzOffsetTo != null && !isValidTimezoneOffset(tzOffsetTo)) {
			valid = false;
			if (errors != null) errors.add("TimezoneDaylight TZOFFSETTO is invalid: " + tzOffsetTo);
		}

		// RRULE validation for recurrence (basic check)
		if (rrule != null && rrule.value == null) {
			valid = false;
			if (errors != null) errors.add("TimezoneDaylight RRULE is malformed");
		}

		return valid;
	}

	/**
	 * Validate timezone offset format (e.g., +0500, -0800)
	 */
	private boolean isValidTimezoneOffset(String offset) {
		if (offset == null || offset.length() != 5) {
			return false;
		}
		return offset.matches("[+-][0-9]{4}");
	}

	/**
	 * Get the start date/time
	 * 
	 * @return start date
	 */
	public Date getDtstart() {
		return dtstart;
	}

	/**
	 * Get the offset from UTC
	 * 
	 * @return offset from UTC
	 */
	public String getTzOffsetFrom() {
		return tzOffsetFrom;
	}

	/**
	 * Get the offset to UTC
	 * 
	 * @return offset to UTC
	 */
	public String getTzOffsetTo() {
		return tzOffsetTo;
	}

	/**
	 * Get the timezone name
	 * 
	 * @return timezone name
	 */
	public String getTzName() {
		return tzName;
	}

	/**
	 * Get the recurrence rule
	 * 
	 * @return recurrence rule
	 */
	public Rrule getRrule() {
		return rrule;
	}

	/**
	 * Get the recurrence dates
	 * 
	 * @return list of recurrence dates
	 */
	public List<Date> getRdates() {
		return rdates;
	}

	/**
	 * Convert this TimezoneDaylight to iCalendar format
	 * 
	 * @return iCalendar string representation
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(1024);
		ret.append("BEGIN:DAYLIGHT");
		ret.append(CRLF);
		
		if (dtstart != null) {
			ret.append(dtstart.toICalendar());
			ret.append(CRLF);
		}
		
		if (tzOffsetFrom != null) {
			ret.append("TZOFFSETFROM:" + tzOffsetFrom);
			ret.append(CRLF);
		}
		
		if (tzOffsetTo != null) {
			ret.append("TZOFFSETTO:" + tzOffsetTo);
			ret.append(CRLF);
		}
		
		if (tzName != null) {
			ret.append("TZNAME:" + tzName);
			ret.append(CRLF);
		}
		
		if (rrule != null) {
			ret.append(rrule.toICalendar());
			ret.append(CRLF);
		}
		
		// Add RDATE components
		if (rdates != null && rdates.size() > 0) {
			StringBuffer rdatesStr = new StringBuffer();
			for (int i = 0; i < rdates.size(); i++) {
				if (i > 0) {
					rdatesStr.append(",");
				}
				rdatesStr.append(rdates.get(i).toICalendar());
			}
			ret.append("RDATE:" + rdatesStr.toString());
			ret.append(CRLF);
		}
		
		if (comment != null) {
			ret.append(comment.toICalendar());
			ret.append(CRLF);
		}
		
		ret.append("END:DAYLIGHT");
		return ret.toString();
	}

	/**
	 * Get a string representation of this TimezoneDaylight
	 * 
	 * @return string representation
	 */
	public String toString() {
		return "TimezoneDaylight [dtstart=" + dtstart + 
			   ", offsetFrom=" + tzOffsetFrom + ", offsetTo=" + tzOffsetTo + "]";
	}
}