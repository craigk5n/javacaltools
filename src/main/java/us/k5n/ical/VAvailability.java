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

import java.util.List;

/**
 * iCalendar VAVAILABILITY class that corresponds to the VAVAILABILITY iCalendar component.
 * A VAVAILABILITY component defines periods of time when a calendar user is generally
 * available or unavailable for appointments.
 *
 * From RFC 5545: The "VAVAILABILITY" component defines an availability period
 * for a calendar user. It can be used to specify periods of time when the user
 * is generally available or unavailable.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class VAvailability implements Constants {
	/** Unique Id */
	protected Uid uid = null;
	/** Date/time stamp */
	protected Date dtstamp = null;
	/** Start date/time */
	protected Date dtstart = null;
	/** End date/time */
	protected Date dtend = null;
	/** Duration */
	protected Duration duration = null;
	/** Busy type */
	protected String busyType = null;
	/** Participant type */
	protected String participantType = null;
	/** Summary */
	protected Summary summary = null;
	/** Description */
	protected Description description = null;
	/** Categories */
	protected Categories categories = null;
	/** Created date */
	protected Date createdDate = null;
	/** Last modified date */
	protected Date lastModified = null;
	/** Calendar address (RFC 9073) */
	protected CalendarAddress calendarAddress = null;
	/** Private user object for caller to set/get */
	private Object userData = null;

	/**
	 * Create a VAvailability object based on specified iCalendar data
	 *
	 * @param parser
	 *                    The ICalendarParser object
	 * @param initialLine
	 *                    The starting line number
	 * @param textLines
	 *        List of iCalendar text lines
	 */
	public VAvailability(CalendarParser parser, int initialLine, List<String> textLines) {
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
		// VAVAILABILITY requires UID to be explicitly provided (unlike other components)
		// Don't auto-generate UID
	}

	/**
	 * Was enough information parsed for this VAvailability to be valid?
	 */
	public boolean isValid() {
		// Must have at least a UID and DTSTAMP
		return (uid != null && dtstamp != null);
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
		if (up.equals("BEGIN:VAVAILABILITY") || up.equals("END:VAVAILABILITY")) {
			// ignore begin/end markers
		} else if (up.trim().length() == 0) {
			// ignore empty lines
		} else if (up.startsWith("UID:")) {
			uid = new Uid(icalStr);
		} else if (up.startsWith("DTSTAMP:")) {
			dtstamp = new Date(icalStr);
		} else if (up.startsWith("DTSTART:")) {
			dtstart = new Date(icalStr);
		} else if (up.startsWith("DTEND:")) {
			dtend = new Date(icalStr);
		} else if (up.startsWith("DURATION:")) {
			duration = new Duration(icalStr);
		} else if (up.startsWith("BUSYTYPE:")) {
			busyType = icalStr.substring(icalStr.indexOf(':') + 1);
		} else if (up.startsWith("SUMMARY:")) {
			summary = new Summary(icalStr);
		} else if (up.startsWith("DESCRIPTION:")) {
			description = new Description(icalStr);
		} else if (up.startsWith("CATEGORIES:")) {
			categories = new Categories(icalStr);
		} else if (up.startsWith("CREATED:")) {
			createdDate = new Date(icalStr);
		} else if (up.startsWith("LAST-MODIFIED:")) {
			lastModified = new Date(icalStr);
		} else if (up.startsWith("PARTICIPANT-TYPE:")) {
			participantType = icalStr.substring(icalStr.indexOf(':') + 1);
		} else if (up.startsWith("CALENDAR-ADDRESS:")) {
			try {
				calendarAddress = new CalendarAddress(icalStr);
			} catch (ParseException pe) {
				throw new ParseException("Invalid CALENDAR-ADDRESS: " + pe.getMessage(), icalStr);
			}
		} else {
			System.err.println("Ignoring VAVAILABILITY line: " + icalStr);
		}
	}

	/**
	 * Convert this VAvailability object to iCalendar format
	 *
	 * @return iCalendar string representation
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(512);
		ret.append("BEGIN:VAVAILABILITY");
		ret.append(CRLF);

		if (uid != null) {
			ret.append(uid.toICalendar());
			ret.append(CRLF);
		}

		if (dtstamp != null) {
			ret.append(dtstamp.toICalendar());
			ret.append(CRLF);
		}

		if (dtstart != null) {
			ret.append(dtstart.toICalendar());
			ret.append(CRLF);
		}

		if (dtend != null) {
			ret.append(dtend.toICalendar());
			ret.append(CRLF);
		}

		if (duration != null) {
			ret.append(duration.toICalendar());
			ret.append(CRLF);
		}

		if (busyType != null) {
			ret.append("BUSYTYPE:");
			ret.append(busyType);
			ret.append(CRLF);
		}

		if (summary != null) {
			ret.append(summary.toICalendar());
			ret.append(CRLF);
		}

		if (description != null) {
			ret.append(description.toICalendar());
			ret.append(CRLF);
		}

		if (categories != null) {
			ret.append(categories.toICalendar());
			ret.append(CRLF);
		}

		if (createdDate != null) {
			ret.append(createdDate.toICalendar());
			ret.append(CRLF);
		}

		if (lastModified != null) {
			ret.append(lastModified.toICalendar());
			ret.append(CRLF);
		}

		if (participantType != null) {
			ret.append("PARTICIPANT-TYPE:").append(participantType).append(CRLF);
		}

		if (calendarAddress != null) {
			ret.append(calendarAddress.toICalendar());
			ret.append(CRLF);
		}

		ret.append("END:VAVAILABILITY");
		ret.append(CRLF);
		return ret.toString();
	}

	// Getter and setter methods

	public Uid getUid() {
		return uid;
	}

	public void setUid(Uid uid) {
		this.uid = uid;
	}

	public Date getDtstamp() {
		return dtstamp;
	}

	public void setDtstamp(Date dtstamp) {
		this.dtstamp = dtstamp;
	}

	public Date getDtstart() {
		return dtstart;
	}

	public void setDtstart(Date dtstart) {
		this.dtstart = dtstart;
	}

	public Date getDtend() {
		return dtend;
	}

	public void setDtend(Date dtend) {
		this.dtend = dtend;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getBusyType() {
		return busyType;
	}

	public void setBusyType(String busyType) {
		this.busyType = busyType;
	}

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public Categories getCategories() {
		return categories;
	}

	public void setCategories(Categories categories) {
		this.categories = categories;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public CalendarAddress getCalendarAddress() {
		return calendarAddress;
	}

	public void setCalendarAddress(CalendarAddress calendarAddress) {
		this.calendarAddress = calendarAddress;
	}

	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}
}