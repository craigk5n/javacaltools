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
 * iCalendar Freebusy class that corresponds to a VFREEBUSY iCalendar object.
 * A VFREEBUSY component represents a time period when the user is busy,
 * without revealing the reason for the busy time.
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class Freebusy implements Constants {
	/** Unique Id */
	protected Uid uid = null;
	/** Sequence number (0 is first version) */
	protected Sequence sequence = null;
	/** Brief summary */
	protected Summary summary = null;
	/** Contact */
	protected Contact contact = null;
	/** Time created */
	protected Date dtstamp = null;
	/** Time last modified */
	protected Date lastModified = null;
	/** Primary start date */
	protected Date startDate = null;
	/** End date */
	protected Date endDate = null;
	/** Duration */
	protected Duration duration = null;
	/** URL */
	protected URL url = null;
	/** Comment */
	protected Comment comment = null;
	/** Organizer (Attendee) */
	protected Attendee organizer = null;
	/** Busy periods - List of FreebusyPeriod objects */
	protected List<FreebusyPeriod> busyPeriods = null;
	/** Private user object for caller to set/get */
	private Object userData = null;

	/**
	 * Create a Freebusy object based on specified iCalendar data
	 * 
	 * @param parser
	 *                    The ICalendarParser object
	 * @param initialLine
	 *                    The starting line number
	 * @param textLines
	 *        List of iCalendar text lines
	 */
	public Freebusy(CalendarParser parser, int initialLine, List<String> textLines) {
		busyPeriods = new ArrayList<FreebusyPeriod>();
		
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
		
		// must have UID
		if (uid == null) {
			uid = new Uid();
			uid.value = Utils.generateUniqueId("JAVACALTOOLS");
		}
		// create a sequence if not specified
		if (sequence == null)
			sequence = new Sequence(0);
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
		if (up.equals("BEGIN:VFREEBUSY") || up.equals("END:VFREEBUSY")) {
			// ignore begin/end markers
		} else if (up.trim().length() == 0) {
			// ignore empty lines
		} else if (up.startsWith("UID")) {
			uid = new Uid(icalStr);
		} else if (up.startsWith("DTSTAMP")) {
			dtstamp = new Date(icalStr);
		} else if (up.startsWith("LAST-MODIFIED")) {
			lastModified = new Date(icalStr);
		} else if (up.startsWith("DTSTART")) {
			startDate = new Date(icalStr);
		} else if (up.startsWith("DTEND")) {
			endDate = new Date(icalStr);
		} else if (up.startsWith("DURATION")) {
			duration = new Duration(icalStr);
		} else if (up.startsWith("URL")) {
			url = new URL(icalStr);
		} else if (up.startsWith("COMMENT")) {
			comment = new Comment(icalStr);
		} else if (up.startsWith("SUMMARY")) {
			summary = new Summary(icalStr);
		} else if (up.startsWith("CONTACT")) {
			contact = new Contact(icalStr);
		} else if (up.startsWith("ORGANIZER")) {
			organizer = new Attendee(icalStr);
		} else if (up.startsWith("FREEBUSY")) {
			int colonIdx = icalStr.indexOf(':');
			if (colonIdx == -1) {
				if (parseMethod == PARSE_STRICT) {
					throw new ParseException("Invalid FREEBUSY format: " + icalStr, icalStr);
				}
			} else {
				String value = icalStr.substring(colonIdx + 1).trim();
				String[] periods = value.split(",");
				for (int i = 0; i < periods.length; i++) {
					try {
						FreebusyPeriod period = new FreebusyPeriod("FREEBUSY:" + periods[i].trim());
						if (period.isValid()) {
							busyPeriods.add(period);
						}
					} catch (Exception e) {
						// skip invalid periods
					}
				}
			}
		} else if (up.startsWith("SEQUENCE")) {
			sequence = new Sequence(icalStr);
		} else if (isParseStrict(parseMethod)) {
			throw new ParseException("Unrecognized data in VFREEBUSY: " + icalStr, icalStr);
		}
	}

	/**
	 * Check if parse method is strict
	 */
	private boolean isParseStrict(int parseMethod) {
		return parseMethod == PARSE_STRICT;
	}

	/**
	 * Was enough information parsed for this Freebusy to be valid?
	 * Note: if a freebusy is parsed by ICalendarParser and it is found to have an
	 * invalid date, no Freebusy object will be created for it.
	 * 
	 * @return true if freebusy has required information
	 */
	public boolean isValid() {
		// Must have DTSTART and either DTEND or DURATION
		if (startDate == null)
			return false;
		if (endDate == null && duration == null)
			return false;
		// If endDate is specified, duration should be null and vice versa
		if (endDate != null && duration != null)
			return false;
		return true;
	}

	/**
	 * Get busy periods
	 * 
	 * @return list of FreebusyPeriod objects
	 */
	public List<FreebusyPeriod> getBusyPeriods() {
		return busyPeriods;
	}

	/**
	 * Add a busy period
	 * 
	 * @param period
	 *               FreebusyPeriod to add
	 */
	public void addBusyPeriod(FreebusyPeriod period) {
		if (busyPeriods == null)
			busyPeriods = new ArrayList<FreebusyPeriod>();
		busyPeriods.add(period);
	}

	/**
	 * Get the user data object
	 * 
	 * @return user data object
	 */
	public Object getUserData() {
		return userData;
	}

	/**
	 * Set the user data object
	 * 
	 * @param userData
	 *                User data object
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}

	/**
	 * Convert this Freebusy object to iCalendar format
	 * 
	 * @return iCalendar string representation
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(1024);
		ret.append("BEGIN:VFREEBUSY");
		ret.append(CRLF);
		
		if (uid != null) {
			ret.append(uid.toICalendar());
			ret.append(CRLF);
		}
		
		if (sequence != null) {
			ret.append(sequence.toICalendar());
			ret.append(CRLF);
		}
		
		if (dtstamp != null) {
			ret.append(dtstamp.toICalendar());
			ret.append(CRLF);
		}
		
		if (lastModified != null) {
			ret.append(lastModified.toICalendar());
			ret.append(CRLF);
		}
		
		if (startDate != null) {
			ret.append(startDate.toICalendar());
			ret.append(CRLF);
		}
		
		if (endDate != null) {
			ret.append(endDate.toICalendar());
			ret.append(CRLF);
		}
		
		if (duration != null) {
			ret.append(duration.toICalendar());
			ret.append(CRLF);
		}
		
		if (url != null) {
			ret.append(url.toICalendar());
			ret.append(CRLF);
		}
		
		if (comment != null) {
			ret.append(comment.toICalendar());
			ret.append(CRLF);
		}
		
		if (summary != null) {
			ret.append(summary.toICalendar());
			ret.append(CRLF);
		}
		
		if (contact != null) {
			ret.append(contact.toICalendar());
			ret.append(CRLF);
		}
		
		if (organizer != null) {
			ret.append(organizer.toICalendar());
			ret.append(CRLF);
		}
		
		// Add FREEBUSY periods
		for (int i = 0; i < busyPeriods.size(); i++) {
			FreebusyPeriod period = busyPeriods.get(i);
			ret.append(period.toICalendar());
			ret.append(CRLF);
		}
		
		ret.append("END:VFREEBUSY");
		ret.append(CRLF);
		return ret.toString();
	}

	/**
	 * Get a string representation of this Freebusy
	 * 
	 * @return string representation
	 */
	public String toString() {
		return "Freebusy [uid=" + (uid != null ? uid.value : "null") + 
			   ", periods=" + busyPeriods.size() + "]";
	}

	/**
	 * Get summary
	 * 
	 * @return summary object
	 */
	public Summary getSummary() {
		return summary;
	}

	/**
	 * Get UID
	 * 
	 * @return UID object
	 */
	public Uid getUid() {
		return uid;
	}

	/**
	 * Get start date
	 * 
	 * @return start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Get end date
	 * 
	 * @return end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Get duration
	 * 
	 * @return duration
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * Get organizer
	 * 
	 * @return organizer
	 */
	public Attendee getOrganizer() {
		return organizer;
	}

	/**
	 * Get contact
	 * 
	 * @return contact
	 */
	public Contact getContact() {
		return contact;
	}

	/**
	 * Get dtstamp
	 * 
	 * @return dtstamp
	 */
	public Date getDtstamp() {
		return dtstamp;
	}

	/**
	 * Get last modified
	 * 
	 * @return last modified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * Get URL
	 * 
	 * @return URL
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Get comment
	 * 
	 * @return comment
	 */
	public Comment getComment() {
		return comment;
	}

	/**
	 * Get sequence
	 * 
	 * @return sequence object
	 */
	public Sequence getSequence() {
		return sequence;
	}
}
