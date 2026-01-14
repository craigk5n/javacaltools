/*
 * Copyright (C) 2005-2006 Craig Knudsen and other authors
 * (see AUTHORS for a complete list)
 *
 * JavaCalTools is free software; you can redistribute it and/or modify
 * it under the terms of GNU Lesser General Public License as
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
 * iCalendar Valarm class that corresponds to a VALARM iCalendar object.
 * A VALARM component defines an alarm or reminder for a calendar component.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class Valarm implements Constants {
	/** Alarm action (AUDIO, DISPLAY, EMAIL, PROCEDURE) */
	protected String action = null;
	/** Trigger for the alarm */
	protected String trigger = null;
	/** Alarm summary */
	protected Summary summary = null;
	/** Alarm description */
	protected Description description = null;
	/** Duration of the alarm */
	protected Duration duration = null;
	/** Number of times to repeat */
	protected Integer repeat = null;
	/** Attendees to notify */
	protected List<Attendee> attendees = null;
	/** Attachments for the alarm */
	protected List<Attachment> attachments = null;
	/** Proximity trigger */
	protected String proximity = null;
	/** Structured data */
	protected String structuredData = null;

	/**
	 * Create a Valarm object based on specified iCalendar data
	 * 
	 * @param parser
	 *                    The ICalendarParser object
	 * @param initialLine
	 *                    The starting line number
	 * @param textLines
	 *        List of iCalendar text lines
	 */
	public Valarm(CalendarParser parser, int initialLine, List<String> textLines) {
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
	 * Create a Valarm object from a single iCalendar string
	 * 
	 * @param icalStr
	 *        iCalendar string
	 */
	public Valarm(String icalStr) throws ParseException, BogusDataException {
		this(icalStr, PARSE_LOOSE);
	}

	/**
	 * Create a Valarm object from a single iCalendar string with specified parse mode
	 * 
	 * @param icalStr
	 *        iCalendar string
	 * @param parseMode
	 *        PARSE_STRICT or PARSE_LOOSE
	 */
	public Valarm(String icalStr, int parseMode) throws ParseException, BogusDataException {
		String[] lines = icalStr.split("\\r?\\n");
		for (int i = 0; i < lines.length; i++) {
			parseLine(lines[i], parseMode);
		}
	}

	/**
	 * Parse a line of iCalendar text
	 * 
	 * @param line
	 *               The line of text
	 * @param parseMethod
	 *               PARSE_STRICT or PARSE_LOOSE
	 */
	public void parseLine(String icalStr, int parseMethod)
			throws ParseException, BogusDataException {
		String up = icalStr.toUpperCase();
		if (up.equals("BEGIN:VALARM") || up.equals("END:VALARM")) {
			// ignore begin/end markers
		} else if (up.trim().length() == 0) {
			// ignore empty lines
		} else if (up.startsWith("ACTION:")) {
			Property p = new Property(icalStr);
			action = p.value;
		} else if (up.startsWith("TRIGGER:")) {
			Property p = new Property(icalStr);
			trigger = p.value;
		} else if (up.startsWith("SUMMARY:")) {
			summary = new Summary(icalStr);
		} else if (up.startsWith("DESCRIPTION:")) {
			description = new Description(icalStr);
		} else if (up.startsWith("DURATION:")) {
			duration = new Duration(icalStr);
		} else if (up.startsWith("REPEAT:")) {
			Property p = new Property(icalStr);
			try {
				repeat = Integer.parseInt(p.value);
			} catch (NumberFormatException e) {
				if (parseMethod == PARSE_STRICT) {
					throw new ParseException("Invalid REPEAT value: " + p.value, icalStr);
				}
			}
		} else if (up.startsWith("ATTENDEE:")) {
			Attendee attendee = new Attendee(icalStr);
			if (attendees == null)
				attendees = new ArrayList<Attendee>();
			attendees.add(attendee);
		} else if (up.startsWith("ATTACH:")) {
			Attachment attach = new Attachment(icalStr);
			if (attachments == null)
				attachments = new ArrayList<Attachment>();
			attachments.add(attach);
		} else if (up.startsWith("PROXIMITY:")) {
			Property p = new Property(icalStr);
			proximity = p.value;
		} else if (up.startsWith("STRUCTURED-DATA:")) {
			Property p = new Property(icalStr);
			structuredData = p.value;
		} else if (isParseStrict(parseMethod)) {
			throw new ParseException("Unrecognized data in VALARM: " + icalStr, icalStr);
		}
	}

	/**
	 * Check if parse method is strict
	 */
	private boolean isParseStrict(int parseMethod) {
		return parseMethod == PARSE_STRICT;
	}

	/**
	 * Was enough information parsed for this Valarm to be valid?
	 * 
	 * @return true if valarm has required information
	 */
	public boolean isValid() {
		// ACTION is required
		return action != null && action.length() > 0;
	}

	/**
	 * Get the alarm action
	 * 
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Get the trigger
	 * 
	 * @return the trigger
	 */
	public String getTrigger() {
		return trigger;
	}

	/**
	 * Get the summary
	 * 
	 * @return: summary object
	 */
	public Summary getSummary() {
		return summary;
	}

	/**
	 * Get the description
	 * 
	 * @return: description object
	 */
	public Description getDescription() {
		return description;
	}

	/**
	 * Get the duration
	 * 
	 * @return: duration
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * Get the repeat count
	 * 
	 * @return: repeat count
	 */
	public Integer getRepeat() {
		return repeat;
	}

	/**
	 * Get the attendees
	 *
	 * @return: list of attendee objects
	 */
	public List<Attendee> getAttendees() {
		return attendees;
	}

	/**
	 * Get the attachments
	 *
	 * @return: list of attachment objects
	 */
	public List<Attachment> getAttachments() {
		return attachments;
	}

	/**
	 * Convert this Valarm object to iCalendar format
	 * 
	 * @return iCalendar string representation
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(512);
		ret.append("BEGIN:VALARM");
		ret.append(CRLF);
		
		if (action != null) {
			ret.append("ACTION:" + action);
			ret.append(CRLF);
		}
		
		if (trigger != null) {
			ret.append("TRIGGER:" + trigger);
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
		
		if (duration != null) {
			ret.append(duration.toICalendar());
			ret.append(CRLF);
		}
		
		if (repeat != null) {
			ret.append("REPEAT:" + repeat);
			ret.append(CRLF);
		}
		
		if (attendees != null) {
			for (Attendee attendee : attendees) {
				ret.append(attendee.toICalendar());
				ret.append(CRLF);
			}
		}

		if (attachments != null) {
			for (Attachment attachment : attachments) {
				ret.append(attachment.toICalendar());
				ret.append(CRLF);
			}
		}

		if (proximity != null) {
			ret.append("PROXIMITY:").append(proximity).append(CRLF);
		}
		if (structuredData != null) {
			ret.append("STRUCTURED-DATA:").append(structuredData).append(CRLF);
		}

		ret.append("END:VALARM");
		ret.append(CRLF);
		return ret.toString();
	}

	/**
	 * Get a string representation of this Valarm
	 * 
	 * @return string representation
	 */
	public String toString() {
		return "Valarm [action=" + action + ", trigger=" + trigger + "]";
	}
}
