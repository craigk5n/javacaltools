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
 * iCalendar Timezone class that corresponds to the VTIMEZONE iCalendar object.
 * A VTIMEZONE component defines a time zone and typically contains one or more
 * sub-components that describe daylight saving time observances within the time zone.
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class Timezone implements Constants {
	/** Timezone identifier (required) */
	protected String tzid = null;
	/** Last modification timestamp */
	protected Date lastModified = null;
	/** List of STANDARD sub-components */
	protected List<TimezoneStandard> standards = null;
	/** List of DAYLIGHT sub-components */
	protected List<TimezoneDaylight> daylight = null;
	/** URL for timezone definition */
	protected URL url = null;

	/**
	 * Create a Timezone object based on specified iCalendar data
	 * 
	 * @param parser
	 *                    The ICalendarParser object
	 * @param initialLine
	 *                    The starting line number
	 * @param textLines
	 *        List of iCalendar text lines
	 */
	public Timezone(CalendarParser parser, int initialLine, List<String> textLines) {
		standards = new ArrayList<TimezoneStandard>();
		daylight = new ArrayList<TimezoneDaylight>();
		
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
		
		// must have TZID
		if (tzid == null) {
			tzid = "UTC"; // default if not specified
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
		if (up.equals("BEGIN:VTIMEZONE") || up.equals("END:VTIMEZONE")) {
			// ignore begin/end markers
		} else if (up.trim().length() == 0) {
			// ignore empty lines
		} else if (up.startsWith("TZID:")) {
			Property p = new Property(icalStr);
			tzid = p.value;
		} else if (up.startsWith("LAST-MODIFIED:")) {
			lastModified = new Date(icalStr);
		} else if (up.startsWith("BEGIN:STANDARD")) {
			// This shouldn't happen here - standard components should be parsed separately
			throw new ParseException("STANDARD component found outside VTIMEZONE context", icalStr);
		} else if (up.startsWith("BEGIN:DAYLIGHT")) {
			// This shouldn't happen here - daylight components should be parsed separately
			throw new ParseException("DAYLIGHT component found outside VTIMEZONE context", icalStr);
		} else if (up.startsWith("TZURL:")) {
			url = new URL(icalStr);
		} else if (isParseStrict(parseMethod)) {
			throw new ParseException("Unrecognized data in VTIMEZONE: " + icalStr, icalStr);
		}
	}

	/**
	 * Check if parse method is strict
	 * 
	 * @param parseMethod
	 *               PARSE_STRICT or PARSE_LOOSE
	 * @return true if parse method is PARSE_STRICT
	 */
	private boolean isParseStrict(int parseMethod) {
		return parseMethod == PARSE_STRICT;
	}

	/**
	 * Add a STANDARD sub-component to this timezone
	 * 
	 * @param standard
	 *               The TimezoneStandard component
	 */
	public void addStandard(TimezoneStandard standard) {
		standards.add(standard);
	}

	/**
	 * Add a DAYLIGHT sub-component to this timezone
	 * 
	 * @param daylight
	 *               The TimezoneDaylight component
	 */
	public void addDaylight(TimezoneDaylight daylight) {
		this.daylight.add(daylight);
	}

	/**
	 * Was enough information parsed for this Timezone to be valid?
	 * 
	 * @return true if timezone has required information
	 */
	public boolean isValid() {
		return isValid(null);
	}

	/**
	 * Check if this Timezone is valid with optional error details
	 *
	 * @param errors List to collect validation error messages (can be null)
	 * @return true if the timezone is valid
	 */
	public boolean isValid(List<String> errors) {
		boolean valid = true;

		// TZID validation
		if (tzid == null || tzid.trim().length() == 0) {
			valid = false;
			if (errors != null) errors.add("Timezone must have a TZID");
		} else if (tzid.length() > 255) {
			valid = false;
			if (errors != null) errors.add("Timezone TZID is too long (max 255 characters)");
		} else if (!tzid.matches("[A-Za-z0-9_/+-]+")) {
			valid = false;
			if (errors != null) errors.add("Timezone TZID contains invalid characters");
		}

		// Validate STANDARD components if they exist
		if (standards != null) {
			// Validate STANDARD components
			for (int i = 0; i < standards.size(); i++) {
				TimezoneStandard std = standards.get(i);
				if (std != null && !std.isValid()) {
					valid = false;
					if (errors != null) errors.add("Timezone STANDARD component " + i + " is invalid");
				}
			}
		}

		// Validate DAYLIGHT components if they exist
		if (daylight != null) {
			for (int i = 0; i < daylight.size(); i++) {
				TimezoneDaylight dl = daylight.get(i);
				if (dl != null && !dl.isValid()) {
					valid = false;
					if (errors != null) errors.add("Timezone DAYLIGHT component " + i + " is invalid");
				}
			}
		}

		// URL validation if present
		if (url != null && url.value != null) {
			try {
				new java.net.URL(url.value); // Basic URL validation
			} catch (java.net.MalformedURLException e) {
				valid = false;
				if (errors != null) errors.add("Timezone URL is not a valid URL: " + url.value);
			}
		}

		return valid;
	}

	/**
	 * Get the timezone identifier
	 * 
	 * @return the timezone ID
	 */
	public String getTimezoneId() {
		return tzid;
	}

	/**
	 * Get the list of STANDARD components
	 * 
	 * @return list of TimezoneStandard objects
	 */
	public List<TimezoneStandard> getStandards() {
		return standards;
	}

	/**
	 * Get the list of DAYLIGHT components
	 * 
	 * @return list of TimezoneDaylight objects
	 */
	public List<TimezoneDaylight> getDaylight() {
		return daylight;
	}

	/**
	 * Convert this Timezone object to iCalendar format
	 * 
	 * @return iCalendar string representation
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(1024);
		ret.append("BEGIN:VTIMEZONE");
		ret.append(CRLF);
		
		if (tzid != null) {
			ret.append("TZID:" + tzid);
			ret.append(CRLF);
		}
		
		if (lastModified != null) {
			ret.append(lastModified.toICalendar());
			ret.append(CRLF);
		}
		
		if (url != null) {
			ret.append(url.toICalendar());
			ret.append(CRLF);
		}
		
		// Add STANDARD components
		for (int i = 0; i < standards.size(); i++) {
			TimezoneStandard standard = standards.get(i);
			ret.append(standard.toICalendar());
			ret.append(CRLF);
		}
		
		// Add DAYLIGHT components
		for (int i = 0; i < daylight.size(); i++) {
			TimezoneDaylight day = daylight.get(i);
			ret.append(day.toICalendar());
			ret.append(CRLF);
		}
		
		ret.append("END:VTIMEZONE");
		ret.append(CRLF);
		return ret.toString();
	}

	/**
	 * Get the last modified date
	 * 
	 * @return the last modified date
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * Get the URL
	 * 
	 * @return the URL
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Get a string representation of this Timezone
	 * 
	 * @return string representation
	 */
	public String toString() {
		return "Timezone [tzid=" + tzid + ", standards=" + standards.size() + 
			   ", daylight=" + daylight.size() + "]";
	}
}