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
 * iCalendar Todo class that corresponds to a VTODO iCalendar object.
 * A VTODO component describes a to-do item, such as a work item or a project.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class Todo implements Constants {
	/** Unique Id */
	protected Uid uid = null;
	/** Sequence number (0 is first version) */
	protected Sequence sequence = null;
	/** Brief summary */
	protected Summary summary = null;
	/** Full description */
	protected Description description = null;
	/** Comment */
	protected Comment comment = null;
	/** Classification (PUBLIC, CONFIDENTIAL, PRIVATE) */
	protected Classification classification = null;
	/** List of categories (comma-separated) */
	protected Categories categories = null;
	/** Date created */
	protected Date createdDate = null;
	/** Primary start date */
	protected Date startDate = null;
	/** End date */
	protected Date endDate = null;
	/** Due date */
	protected Date dueDate = null;
	/** Time created */
	protected Date dtstamp = null;
	/** Time last modified */
	protected Date lastModified = null;
	/** Priority (1-9, 0 is undefined) */
	protected Integer priority = null;
	/** Percentage complete (0-100) */
	protected Integer percentComplete = null;
	/** Date completed */
	protected Date completed = null;
	/** Event status */
	protected int status = STATUS_UNDEFINED;
	/** URL */
	protected URL url = null;
	/** Location */
	protected Location location = null;
	/** Geographic position */
	protected String geo = null;
	/** Participants for todo (List of Attendee) */
	protected List<Attendee> attendees = null;
	/** Recurrence rule (RRULE) */
	protected Rrule rrule = null;
	/** Exception dates (EXDATE) */
	protected List<Date> exdates = null;
	/** Inclusion dates (RDATE) */
	protected List<Date> rdates = null;
	/** Attachments */
	protected List<Attachment> attachments = null;
	/** Alarms (List of Valarm objects) */
	protected List<Valarm> alarms = null;
	/** Parser reference for sub-component parsing */
	private CalendarParser parser = null;
	/** VALARM parsing state */
	private boolean inValarm = false;
	private List<String> valarmLines = null;
	/** Private user object for caller to set/get */
	private Object userData = null;

	/**
	 * Create a Todo object based on specified iCalendar data
	 * 
	 * @param parser
	 *                    The ICalendarParser object
	 * @param initialLine
	 *                    The starting line number
	 * @param textLines
	 *        List of iCalendar text lines
	 */
	public Todo(CalendarParser parser, int initialLine, List<String> textLines) {
		this.parser = parser;
		attendees = new ArrayList<Attendee>();
		exdates = new ArrayList<Date>();
		rdates = new ArrayList<Date>();
		attachments = new ArrayList<Attachment>();
		alarms = new ArrayList<Valarm>();
		alarms = new ArrayList<Valarm>();
		
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
		
		if (uid == null) {
			uid = new Uid();
			uid.value = Utils.generateUniqueId("JAVACALTOOLS");
		}
		if (sequence == null)
			sequence = new Sequence(0);
	}

	/**
	 * Create a simple todo object programmatically
	 * 
	 * @param summary
	 *                    Brief summary of todo
	 * @param description
	 *                    Full description of todo
	 */
	public Todo(String summary, String description) {
		uid = new Uid();
		uid.value = Utils.generateUniqueId("JAVACALTOOLS");
		this.summary = new Summary();
		this.summary.value = summary;
		if (description != null) {
			this.description = new Description();
			this.description.value = description;
		}
		this.sequence = new Sequence(0);
		this.createdDate = Date.getCurrentDateTime("CREATED");
	}

	/**
	 * Create a todo object programmatically
	 * 
	 * @param summary
	 *                    Brief summary of todo
	 * @param description
	 *                    Full description of todo
	 * @param startDate
	 *                    Start date
	 * @param dueDate
	 *                    Due date
	 * @param priority
	 *                    Priority (1-9)
	 */
	public Todo(String summary, String description, Date startDate, Date dueDate,
			Integer priority) {
		uid = new Uid();
		uid.value = Utils.generateUniqueId("JAVACALTOOLS");
		this.summary = new Summary();
		this.summary.value = summary;
		if (description != null) {
			this.description = new Description();
			this.description.value = description;
		}
		this.startDate = startDate;
		this.dueDate = dueDate;
		this.priority = priority;
		this.sequence = new Sequence(0);
		this.createdDate = Date.getCurrentDateTime("CREATED");
	}

	/**
	 * Parse a line of iCalendar text
	 * 
	 * @param line
	 *                    The line of text
	 * @param parseMethod
	 *                    PARSE_STRICT or PARSE_LOOSE
	 */
	public void parseLine(String icalStr, int parseMethod)
			throws ParseException, BogusDataException {
		String up = icalStr.toUpperCase();
		if (up.equals("BEGIN:VTODO") || up.equals("END:VTODO")) {
			// ignore
		} else if (up.equals("BEGIN:VALARM")) {
			inValarm = true;
			valarmLines = new ArrayList<String>();
			valarmLines.add(icalStr);
		} else if (up.equals("END:VALARM")) {
			if (inValarm && valarmLines != null) {
				valarmLines.add(icalStr);
				// Create the VALARM object
				Valarm alarm = new Valarm(parser, 0, valarmLines);
				if (alarm.isValid()) {
					if (this.alarms == null)
						this.alarms = new ArrayList<Valarm>();
					this.alarms.add(alarm);
				}
				inValarm = false;
				valarmLines = null;
			}
		} else if (inValarm) {
			// We're inside a VALARM, collect the lines
			if (valarmLines != null) {
				valarmLines.add(icalStr);
			}
		} else if (up.trim().length() == 0) {
			// ignore empty lines
		} else if (up.startsWith("DESCRIPTION")) {
			description = new Description(icalStr);
		} else if (up.startsWith("SUMMARY")) {
			summary = new Summary(icalStr);
		} else if (up.startsWith("COMMENT")) {
			comment = new Comment(icalStr);
		} else if (up.startsWith("CREATED")) {
			createdDate = new Date(icalStr);
		} else if (up.startsWith("DTSTART")) {
			startDate = new Date(icalStr);
		} else if (up.startsWith("DTEND")) {
			endDate = new Date(icalStr);
		} else if (up.startsWith("DUE")) {
			dueDate = new Date(icalStr);
		} else if (up.startsWith("DTSTAMP")) {
			dtstamp = new Date(icalStr);
		} else if (up.startsWith("DURATION")) {
			// TODO: implement duration for VTODO
		} else if (up.startsWith("LAST-MODIFIED")) {
			lastModified = new Date(icalStr);
		} else if (up.startsWith("CLASS")) {
			classification = new Classification(icalStr);
		} else if (up.startsWith("CATEGORIES")) {
			categories = new Categories(icalStr);
		} else if (up.startsWith("UID")) {
			uid = new Uid(icalStr);
		} else if (up.startsWith("SEQUENCE")) {
			sequence = new Sequence(icalStr);
		} else if (up.startsWith("RRULE")) {
			rrule = new Rrule(icalStr, parseMethod);
		} else if (up.startsWith("EXDATE")) {
			String[] args = icalStr.split(":");
			if (args.length != 2) {
				if (parseMethod == PARSE_STRICT) {
					throw new BogusDataException("Invalid EXDATE", icalStr);
				}
			} else {
				String[] dateVals = args[1].split(",");
				for (int i = 0; i < dateVals.length; i++) {
					String newIcalStr = args[0] + ':' + dateVals[i];
					Date exdate = new Date(newIcalStr);
					exdates.add(exdate);
				}
			}
		} else if (up.startsWith("RDATE")) {
			String[] args = icalStr.split(":");
			if (args.length != 2) {
				if (parseMethod == PARSE_STRICT) {
					throw new BogusDataException("Invalid RDATE", icalStr);
				}
			} else {
				String[] dateVals = args[1].split(",");
				for (int i = 0; i < dateVals.length; i++) {
					String newIcalStr = args[0] + ':' + dateVals[i];
					Date rdate = new Date(newIcalStr);
					rdates.add(rdate);
				}
			}
		} else if (up.startsWith("STATUS")) {
			String statusStr = up.substring(7);
			if (statusStr.equals("NEEDS-ACTION"))
				status = STATUS_NEEDS_ACTION;
			else if (statusStr.equals("COMPLETED"))
				status = STATUS_COMPLETED;
			else if (statusStr.equals("IN-PROCESS"))
				status = STATUS_IN_PROCESS;
			else if (statusStr.equals("CANCELLED"))
				status = STATUS_CANCELLED;
		} else if (up.startsWith("COMPLETED")) {
			completed = new Date(icalStr);
		} else if (up.startsWith("PRIORITY")) {
			Property p = new Property(icalStr);
			try {
				priority = Integer.parseInt(p.value);
			} catch (NumberFormatException e) {
				if (parseMethod == PARSE_STRICT) {
					throw new ParseException("Invalid PRIORITY: " + p.value, icalStr);
				}
			}
		} else if (up.startsWith("PERCENT-COMPLETE")) {
			Property p = new Property(icalStr);
			try {
				percentComplete = Integer.parseInt(p.value);
			} catch (NumberFormatException e) {
				if (parseMethod == PARSE_STRICT) {
					throw new ParseException("Invalid PERCENT-COMPLETE: " + p.value, icalStr);
				}
			}
		} else if (up.startsWith("URL")) {
			url = new URL(icalStr);
		} else if (up.startsWith("LOCATION")) {
			location = new Location(icalStr);
		} else if (up.startsWith("GEO")) {
			geo = new Property(icalStr).value;
		} else if (up.startsWith("ATTENDEE")) {
			Attendee attendee = new Attendee(icalStr);
			if (this.attendees == null)
				this.attendees = new ArrayList<Attendee>();
			this.attendees.add(attendee);
		} else if (up.startsWith("ATTACH")) {
			Attachment attachment = new Attachment(icalStr);
			if (this.attachments == null)
				this.attachments = new ArrayList<Attachment>();
			this.attachments.add(attachment);
		} else {
			System.out.println("Ignoring VTODO line: " + icalStr);
		}
	}

	/**
	 * Was enough information parsed for this Todo to be valid?
	 * 
	 * @return true if todo has required information
	 */
	public boolean isValid() {
		return (summary != null || description != null);
	}

	/**
	 * Get a List of all attendees
	 * 
	 * @return List of Attendee objects
	 */
	public List<Attendee> getAttendees() {
		return attendees;
	}

	/**
	 * Get recurrence rule
	 * 
	 * @return: RRULE object
	 */
	public Rrule getRrule() {
		return rrule;
	}

	/**
	 * Get exception dates
	 * 
	 * @return list of Date objects
	 */
	public List<Date> getExdates() {
		return exdates;
	}

	/**
	 * Get recurrence dates
	 * 
	 * @return list of Date objects
	 */
	public List<Date> getRdates() {
		return rdates;
	}

	/**
	 * Get a List of all attachments
	 * 
	 * @return List of Attachment objects
	 */
	public List<Attachment> getAttachments() {
		return attachments;
	}

	/**
	 * Get alarms
	 *
	 * @return list of alarms
	 */
	public List<Valarm> getAlarms() {
		return alarms;
	}

	/**
	 * Get user data object
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
	 * Get todo summary
	 */
	public Summary getSummary() {
		return getSummary("EN");
	}

	/**
	 * Get todo summary for specified language. If not available, then
	 * primary summary will be returned.
	 * 
	 * @param langage
	 *                The language ("EN", "FR", etc.)
	 */
	public Summary getSummary(String language) {
		if (summary != null && summary != null)
			return summary;
		return null;
	}

	/**
	 * Get description
	 * 
	 * @return: description object
	 */
	public Description getDescription() {
		return description;
	}

	/**
	 * Get comment
	 * 
	 * @return: comment object
	 */
	public Comment getComment() {
		return comment;
	}

	/**
	 * Get classification
	 * 
	 * @return: classification object
	 */
	public Classification getClassification() {
		return classification;
	}

	/**
	 * Get categories
	 * 
	 * @return: categories object
	 */
	public Categories getCategories() {
		return categories;
	}

	/**
	 * Get created date
	 * 
	 * @return: Date object
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Get start date
	 * 
	 * @return: start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Get end date
	 * 
	 * @return: end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Get due date
	 * 
	 * @return: due date
	 */
	public Date getDueDate() {
		return dueDate;
	}

	/**
	 * Get dtstamp
	 * 
	 * @return: dtstamp
	 */
	public Date getDtstamp() {
		return dtstamp;
	}

	/**
	 * Get last modified
	 * 
	 * @return: last modified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * Get URL
	 * 
	 * @return: URL object
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Get location
	 * 
	 * @return: location object
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Get geographic position
	 * 
	 * @return: geographic position string
	 */
	public String getGeo() {
		return geo;
	}

	/**
	 * Get Unique Id
	 * 
	 * @return: UID object
	 */
	public Uid getUid() {
		return uid;
	}

	/**
	 * Get Sequence number
	 * 
	 * @return: Sequence object
	 */
	public Sequence getSequence() {
		return sequence;
	}

	/**
	 * Convert this Todo object to iCalendar format
	 * 
	 * @return iCalendar string representation
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(1024);
		ret.append("BEGIN:VTODO");
		ret.append(CRLF);

		if (uid != null) {
			ret.append(uid.toICalendar());
			ret.append(CRLF);
		}

		if (sequence != null) {
			ret.append(sequence.toICalendar());
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

		if (comment != null) {
			ret.append(comment.toICalendar());
			ret.append(CRLF);
		}

		if (createdDate != null) {
			ret.append(createdDate.toICalendar());
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

		if (dueDate != null) {
			ret.append(dueDate.toICalendar());
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

		if (classification != null) {
			ret.append(classification.toICalendar());
			ret.append(CRLF);
		}

		if (categories != null) {
			ret.append(categories.toICalendar());
			ret.append(CRLF);
		}

		if (rrule != null) {
			ret.append(rrule.toICalendar());
			ret.append(CRLF);
		}

		if (this.exdates != null && this.exdates.size() > 0) {
			if (this.exdates.size() == 1) {
				ret.append(this.exdates.get(0).toICalendar());
			} else {
				StringBuffer sb = new StringBuffer(25);
				sb.append(this.exdates.get(0).toICalendar().trim());
				for (int i = 1; i < this.exdates.size(); i++) {
					sb.append(',');
					String s = this.exdates.get(i).toICalendar().trim();
					String[] args = s.split(":");
					sb.append(args[1]);
				}
				sb.append(CRLF);
				ret.append(sb.toString());
			}
		}

		if (this.rdates != null && this.rdates.size() > 0) {
			if (this.rdates.size() == 1) {
				ret.append(this.rdates.get(0).toICalendar());
			} else {
				StringBuffer sb = new StringBuffer(25);
				sb.append(this.rdates.get(0).toICalendar().trim());
				for (int i = 1; i < this.rdates.size(); i++) {
					sb.append(',');
					String s = this.rdates.get(i).toICalendar().trim();
					String[] args = s.split(":");
					sb.append(args[1]);
				}
				sb.append(CRLF);
				ret.append(sb.toString());
			}
		}

		if (this.attachments != null) {
			for (int i = 0; i < this.attachments.size(); i++) {
				Attachment attach = (Attachment) this.attachments.get(i);
				ret.append(attach.toICalendar());
				ret.append(CRLF);
			}
		}

		if (priority != null) {
			ret.append("PRIORITY:" + priority);
			ret.append(CRLF);
		}

		if (percentComplete != null) {
			ret.append("PERCENT-COMPLETE:" + percentComplete);
			ret.append(CRLF);
		}

		if (url != null) {
			ret.append(url.toICalendar());
			ret.append(CRLF);
		}

		if (location != null) {
			ret.append(location.toICalendar());
			ret.append(CRLF);
		}

		if (this.attendees != null) {
			for (int i = 0; i < this.attendees.size(); i++) {
				Attendee attendee = (Attendee) this.attendees.get(i);
				ret.append(attendee.toICalendar());
				ret.append(CRLF);
			}
		}

		if (status != STATUS_UNDEFINED) {
			switch (status) {
			case STATUS_NEEDS_ACTION:
				ret.append("STATUS:NEEDS-ACTION");
				ret.append(CRLF);
				break;
			case STATUS_IN_PROCESS:
				ret.append("STATUS:IN-PROCESS");
				ret.append(CRLF);
				break;
			case STATUS_COMPLETED:
				ret.append("STATUS:COMPLETED");
				ret.append(CRLF);
				break;
			case STATUS_CANCELLED:
				ret.append("STATUS:CANCELLED");
				ret.append(CRLF);
				break;
			}
		}

		if (completed != null) {
			ret.append(completed.toICalendar());
			ret.append(CRLF);
		}

		if (this.alarms != null) {
			for (int i = 0; i < this.alarms.size(); i++) {
				Valarm alarm = (Valarm) this.alarms.get(i);
				ret.append(alarm.toICalendar());
			}
		}

		ret.append("END:VTODO");
		ret.append(CRLF);
		return ret.toString();
	}

	/**
	 * Get a string representation of this Todo
	 * 
	 * @return string representation
	 */
	public String toString() {
		StringBuffer ret = new StringBuffer(128);
		ret.append("Todo[");
		if (summary != null && summary.value != null)
			ret.append(summary.value);
		if (uid != null && uid.value != null)
			ret.append(" uid=" + uid.value);
		ret.append("]");
		return ret.toString();
	}
}
