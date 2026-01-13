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
 * iCalendar Event class that corresponds to the VEVENT iCalendarendar object.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class Event implements Constants {
	// TODO: handle multiple instances of summary/description since
	// there can be more than one if LANGUAGE attribute is specified
	private boolean inValarm = false;
	private List<String> valarmLines = null;
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
	protected Date endDate = null; // may be derived from duration if not
	// specified
	/** Time created */
	protected Date dtstamp = null;
	/** Time last modified */
	protected Date lastModified = null;
	/** Event duration (in seconds) */
	protected Duration duration;
	/** Contact for the event */
	// public Individual contact;
	/** Participants for the event (List of Attendee) */
	protected List<Attendee> attendees = null;
	/** Recurrence rule (RRULE) */
	protected Rrule rrule = null;
	/** Exception dates (EXDATE) */
	protected List<Date> exdates = null;
	/** Inclusion dates (RDATE) */
	protected List<Date> rdates = null;
	/** Attachments */
	protected List<Attachment> attachments = null;
	/** URL */
	protected URL url = null;
	/** Location */
	protected Location location = null;
	/** Location reference (UID of VLOCATION) */
	protected String locationId = null;
	/** Resource references (List of VRESOURCE UIDs) */
	protected List<String> resourceIds = null;
	/** Geographic position */
	protected String geo = null;
	/** TRANSP (TRANSPARENT or OPAQUE) */
	protected int transp = TRANSP_OPAQUE;
	/** Event status */
	protected int status = STATUS_UNDEFINED;
	/** Alarms (List of Valarm objects) */
	protected List<Valarm> alarms = null;
	/** Parser reference for sub-component parsing */
	private CalendarParser parser = null;
	/** Organizer */
	protected Organizer organizer = null;
	/** Contact */
	protected Contact contact = null;
	/** Priority (1-9, 0 is undefined) */
	protected Integer priority = null;
	/** Request status */
	protected int requestStatus = STATUS_UNDEFINED;
	/** Private user object for caller to set/get */
	private Object userData = null;

	// TODO: multiple summaries, descriptions with different LANGUAGE values
	// TODO: auto-change transp if either all-day or no duration
	// TODO: alarms/triggers
	// TODO: support VALUE=PERIOD types (RDATE, EXDATE)
	// TODO: EXRULE - exception rule
	// TODO: RELATED-TO

	/**
	 * Create an Event object based on the specified iCalendar data
	 * 
	 * @param parser
	 *                    The IcalParser object
	 * @param initialLine
	 *                    The starting line number
	 * @Param textLines
	 *        List of iCalendar text lines
	 */
	public Event(CalendarParser parser, int initialLine, List<String> textLines) {
		this.parser = parser;
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
		// Note: UID validation is now handled in isValid() method
		// Auto-generation moved to toICalendar() for better validation
		// create a sequence if not specified
		if (sequence == null)
			sequence = new Sequence(0);
	}

	/**
	 * Create an event that is timeless (no duration)
	 * 
	 * @param summary
	 *                    Brief summary of event
	 * @param description
	 *                    Full description of the event
	 * @param startDate
	 *                    The date of the event in ISO 8601 format (19991231,
	 *                    199912310T115900, etc.)
	 */
	public Event(String summary, String description, Date startDate) {
		this(summary, description, startDate, 0);
	}

	/**
	 * Create an event with the specified duration
	 * 
	 * @param summary
	 *                    Brief summary of event
	 * @param description
	 *                    Full description of the event
	 * @param startDate
	 *                    The date of the event in ISO 8601 format (19991231,
	 *                    199912310T115900, etc.)
	 * @param duration
	 *                    The event duration in seconds
	 */
	public Event(String summary, String description, Date startDate, int duration) {
		uid = new Uid(); // Generate unique Id
		this.summary = new Summary();
		this.summary.value = summary;
		this.description = new Description();
		this.description.value = description;
		this.startDate = startDate;
		this.duration = new Duration(duration);
		// TODO: calculate endDate from duration
		this.createdDate = Date.getCurrentDateTime("CREATED");
	}

	/**
	 * Was enough information parsed for this Event to be valid? Note: if an event
	 * is parsed by ICalendarParser and it is found to have an invalid date, no
	 * Event object will be created for it.
	 */
	public boolean isValid() {
		return isValid(null);
	}

	/**
	 * Check if this Event is valid with optional error details
	 *
	 * @param errors List to collect validation error messages (can be null)
	 * @return true if the event is valid
	 */
	public boolean isValid(List<String> errors) {
		boolean valid = true;

		// Required fields
		if (uid == null) {
			valid = false;
			if (errors != null) errors.add("Event must have a UID");
		}

		if (summary == null) {
			valid = false;
			if (errors != null) errors.add("Event must have a SUMMARY");
		}

		if (startDate == null) {
			valid = false;
			if (errors != null) errors.add("Event must have a DTSTART");
		}

		// Date validation
		if (startDate != null && endDate != null) {
			if (startDate.compareTo(endDate) > 0) {
				valid = false;
				if (errors != null) errors.add("Event DTEND must be after DTSTART");
			}
		}

		// Duration validation (if duration is specified, end date should not be)
		if (duration != null && endDate != null) {
			valid = false;
			if (errors != null) errors.add("Event cannot have both DURATION and DTEND");
		}

		// Alarm validation
		if (alarms != null) {
			for (int i = 0; i < alarms.size(); i++) {
				Valarm alarm = alarms.get(i);
				if (alarm != null && !alarm.isValid()) {
					valid = false;
					if (errors != null) errors.add("Event alarm " + i + " is invalid");
				}
			}
		}

		// Recurrence validation
		if (rrule != null && exdates != null) {
			// Check if exdates are within recurrence range
			// This is a basic check - more complex validation could be added
		}

		// Status validation
		if (status != STATUS_UNDEFINED && status != STATUS_TENTATIVE &&
			status != STATUS_CONFIRMED && status != STATUS_CANCELLED) {
			valid = false;
			if (errors != null) errors.add("Invalid event STATUS value: " + status);
		}

		// Priority validation
		if (priority != null && (priority < 0 || priority > 9)) {
			valid = false;
			if (errors != null) errors.add("Event PRIORITY must be between 0 and 9");
		}

		return valid;
	}

	/**
	 * Parse a line of iCalendar test.
	 * 
	 * @param line
	 *                    The line of text
	 * @param parseMethod
	 *                    PARSE_STRICT or PARSE_LOOSE
	 */
	public void parseLine(String icalStr, int parseMethod)
			throws ParseException, BogusDataException {
		String up = icalStr.toUpperCase();
		if (up.equals("BEGIN:VEVENT") || up.equals("END:VEVENT")) {
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
		} else if (up.startsWith("DTSTAMP")) {
			dtstamp = new Date(icalStr);
		} else if (up.startsWith("DURATION")) {
			duration = new Duration(icalStr);
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
			// We could implement a class for EXDATE, but it's really just a Date
			// modified to have mulitple date values.
			// Since EXDATE supports multiple date values, we will create temporary
			// iCalendar string values for each date.
			// Note: this could will allow for multiple EXDATE lines.
			String[] args = icalStr.split(":");
			if (args.length != 2) {
				if (parseMethod == PARSE_STRICT) {
					throw new BogusDataException("Invalid EXDATE", icalStr);
				}
			} else {
				if (this.exdates == null)
					this.exdates = new ArrayList<Date>();
				String[] dateVals = args[1].split(",");
				for (int i = 0; i < dateVals.length; i++) {
					String newIcalStr = args[0] + ':' + dateVals[i];
					Date exdate = new Date(newIcalStr);
					this.exdates.add(exdate);
				}
			}
		} else if (up.startsWith("RDATE")) {
			// Handle this the same way we handled EXDATE
			String[] args = icalStr.split(":");
			if (args.length != 2) {
				if (parseMethod == PARSE_STRICT) {
					throw new BogusDataException("Invalid RDATE", icalStr);
				}
			} else {
				String[] dateVals = args[1].split(",");
				if (this.rdates == null)
					this.rdates = new ArrayList<Date>();
				for (int i = 0; i < dateVals.length; i++) {
					String newIcalStr = args[0] + ':' + dateVals[i];
					System.out.println("Temp RDATE str=" + newIcalStr);
					Date rdate = new Date(newIcalStr);
					this.rdates.add(rdate);
				}
			}
		} else if (up.startsWith("TRANSP")) {
			transp = StringUtils.parseStatus(icalStr, parseMethod);
		} else if (up.startsWith("STATUS")) {
			status = StringUtils.parseStatus(icalStr, parseMethod);
			// Only allow VEVENT status types
			if (status != STATUS_TENTATIVE && status != STATUS_CONFIRMED
					&& status != STATUS_CANCELLED) {
				if (parseMethod == PARSE_STRICT) {
					throw new BogusDataException("Status type not allowed in VEVENT",
							icalStr);
				}
			}
		} else if (up.startsWith("URL")) {
			url = new URL(icalStr);
		} else if (up.startsWith("LOCATION")) {
			location = new Location(icalStr);
		} else if (up.startsWith("ATTACH")) {
			Attachment attach = new Attachment(icalStr);
			if (this.attachments == null)
				this.attachments = new ArrayList<Attachment>();
			this.attachments.add(attach);
		} else if (up.startsWith("ATTENDEE")) {
			Attendee attendee = new Attendee(icalStr);
			if (this.attendees == null)
				this.attendees = new ArrayList<Attendee>();
			this.attendees.add(attendee);
		} else if (up.startsWith("ORGANIZER")) {
			organizer = new Organizer(icalStr);
		} else if (up.startsWith("CONTACT")) {
			contact = new Contact(icalStr);
		} else if (up.startsWith("PRIORITY")) {
			Property p = new Property(icalStr);
			try {
				int pri = Integer.parseInt(p.value);
				if (pri < 0 || pri > 9) {
					throw new ParseException("PRIORITY must be between 0 and 9, got: " + pri, icalStr);
				}
				priority = pri;
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid PRIORITY value (must be integer 0-9): " + p.value, icalStr);
			}
		} else if (up.startsWith("REQUEST-STATUS")) {
			Property p = new Property(icalStr);
			if (p.value.equals("NEEDS-ACTION"))
				requestStatus = STATUS_NEEDS_ACTION;
			else if (p.value.equals("ACCEPTED"))
				requestStatus = 3; // STATUS_IN_PROCESS for accepted
			else if (p.value.equals("DECLINED"))
				requestStatus = 7; // STATUS_CANCELLED for declined
			else if (p.value.equals("TENTATIVE"))
				requestStatus = 8; // STATUS_TENTATIVE for tentative
			else if (p.value.equals("COMPLETED"))
				requestStatus = STATUS_COMPLETED;
			else if (p.value.equals("IN-PROCESS"))
				requestStatus = STATUS_IN_PROCESS;
		} else if (up.startsWith("GEO")) {
			Property p = new Property(icalStr);
			geo = p.value;
		} else {
			System.out.println("Ignoring VEVENT line: " + icalStr);
		}
	}

	/**
	 * Get the event summary
	 */
	public Summary getSummary() {
		return getSummary("EN");
	}

	/**
	 * Get the event summary for the specified language. If not available, then
	 * the primary summary will be returned.
	 * 
	 * @param langage
	 *                The language ("EN", "FR", etc.)
	 */
	public Summary getSummary(String language) {
		// TODO: handle language param
		return summary;
	}

	public List<Attendee> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<Attendee> attendees) {
		this.attendees = attendees;
	}

	public Categories getCategories() {
		return categories;
	}

	public void setCategories(Categories categories) {
		this.categories = categories;
	}

	public Classification getClassification() {
		return classification;
	}

	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getDtstamp() {
		return dtstamp;
	}

	public void setDtstamp(Date dtstamp) {
		this.dtstamp = dtstamp;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Rrule getRrule() {
		return rrule;
	}

	public void setRrule(Rrule rrule) {
		this.rrule = rrule;
	}

	public Sequence getSequence() {
		return sequence;
	}

	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Uid getUid() {
		return uid;
	}

	public void setUid(Uid uid) {
		this.uid = uid;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Get location reference (UID of VLOCATION)
	 *
	 * @return location UID reference
	 */
	public String getLocationId() {
		return locationId;
	}

	/**
	 * Set location reference (UID of VLOCATION)
	 *
	 * @param locationId the UID of the referenced VLOCATION
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * Get resource references (UIDs of VRESOURCE objects)
	 *
	 * @return list of resource UIDs
	 */
	public List<String> getResourceIds() {
		return resourceIds;
	}

	/**
	 * Set resource references (UIDs of VRESOURCE objects)
	 *
	 * @param resourceIds the list of UIDs of referenced VRESOURCE objects
	 */
	public void setResourceIds(List<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	/**
	 * Set the TRANSP setting, which determines if the event shows up in a
	 * freebusy search.
	 * 
	 * @return Either TRANSP_OPAQUE or TRANSP_TRANSPARENT
	 */
	public int getTransp() {
		return transp;
	}

	/**
	 * Set the TRANSP settings, which determines if the event shows up in a
	 * freebusy search.
	 * 
	 * @param transp
	 *               The new TRANSP setting (TRANSP_OPAQUE, TRANSP_TRANSPARENT)
	 */
	public void setTransp(int transp) {
		this.transp = transp;
	}

	/**
	 * Get the event status
	 * 
	 * @return the event status (STATUS_TENTATIVE, STATUS_CONFIRMED or
	 *         STATUS_CANCELLED)
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set the status
	 * 
	 * @param status
	 *               The new status (STATUS_TENTATIVE, STATUS_CONFIRMED or
	 *               STATUS_CANCELLED)
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * Get alarms
	 *
	 * @return list of alarms
	 */
	public List<Valarm> getAlarms() {
		return alarms;
	}

	public void addException(Date date) {
		if (this.exdates != null)
			this.exdates = new ArrayList<Date>();
		date.setName("EXDATE"); // make sure the user had it correct
		this.exdates.add(date);
	}

	public void removeException(Date date) {
		for (int i = 0; this.exdates != null && i < this.exdates.size(); i++) {
			Date d = this.exdates.get(i);
			if (d.compareTo(date) == 0) {
				this.exdates.remove(i);
				return;
			}
		}
		// not found
	}

	public List<Date> getExceptions() {
		return this.exdates;
	}

	public void addRdate(Date date) {
		if (this.rdates != null)
			this.rdates = new ArrayList<Date>();
		date.setName("RDATE"); // make sure the user had it correct
		this.rdates.add(date);
	}

	public void removeRdate(Date date) {
		for (int i = 0; this.rdates != null && i < this.rdates.size(); i++) {
			Date d = this.rdates.get(i);
			if (d.compareTo(date) == 0) {
				this.rdates.remove(i);
				return;
			}
		}
		// not found
	}

	public List<Date> getRdates() {
		return this.rdates;
	}

	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}

	/**
	 * Get organizer
	 *
	 * @return organizer object
	 */
	public Organizer getOrganizer() {
		return organizer;
	}

	/**
	 * Get contact
	 *
	 * @return contact object
	 */
	public Contact getContact() {
		return contact;
	}

	/**
	 * Get priority
	 *
	 * @return priority (1-9, 0 undefined)
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * Get request status
	 *
	 * @return request status
	 */
	public int getRequestStatus() {
		return requestStatus;
	}

	/**
	 * Get geographic position
	 *
	 * @return geo string (latitude;longitude)
	 */
	public String getGeo() {
		return geo;
	}

	/**
	 * Get a List of Date objects that contain the recurrance dates (or null if
	 * there are none).
	 * 
	 * @return
	 */
	public List<Date> getRecurranceDates() {
		String tzid = null;
		// TODO: add support for exceptions
		if (this.rrule == null)
			return null;
		if (this.startDate == null)
			return null;
		tzid = this.startDate.tzid;
		return rrule.generateRecurrances(this.startDate, tzid, this.exdates,
				this.rdates);
	}

	/**
	 * Convert this Event into iCalendar text
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(128);
		ret.append("BEGIN:VEVENT");
		ret.append(CRLF);

		if (uid != null) {
			ret.append(uid.toICalendar());
		} else {
			// Auto-generate UID for serialization
			Uid autoUid = new Uid();
			autoUid.value = Utils.generateUniqueId("JAVACALTOOLS");
			ret.append(autoUid.toICalendar());
		}
		if (sequence != null)
			ret.append(sequence.toICalendar());
		if (summary != null)
			ret.append(summary.toICalendar());
		if (description != null)
			ret.append(description.toICalendar());
		if (createdDate != null)
			ret.append(createdDate.toICalendar());
		if (startDate != null)
			ret.append(startDate.toICalendar());
		if (endDate != null)
			ret.append(endDate.toICalendar());
		if (dtstamp != null)
			ret.append(dtstamp.toICalendar());
		if (lastModified != null)
			ret.append(lastModified.toICalendar());
		if (rrule != null)
			ret.append(rrule.toICalendar());
		if (rdates != null && rdates.size() > 0) {
			ret.append("RDATE:");
			for ( int i = 0; i < rdates.size(); i++ ) {
				if ( i > 0 ) {
					ret.append(',');
				}
				ret.append(Utils.DateToYYYYMMDD(rdates.get(i)));
			}
			ret.append(CRLF);
		}
		if (classification != null)
			ret.append(classification.toICalendar());
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
			}
		}
		if (categories != null)
			ret.append(categories.toICalendar());
		if (url != null)
			ret.append(url.toICalendar());
		if (location != null)
			ret.append(location.toICalendar());
		if (this.attachments != null) {
			for (int i = 0; i < this.attachments.size(); i++) {
				Attachment attach = (Attachment) this.attachments.get(i);
				ret.append(attach.toICalendar());
			}
		}
		ret.append(transp == TRANSP_OPAQUE ? "TRANSP:OPAQUE"
				: "TRANSP:TRANSPARENT");
		ret.append(CRLF);
		if (this.attendees != null) {
			for (int i = 0; i < this.attendees.size(); i++) {
				Attendee attendee = (Attendee) this.attendees.get(i);
				ret.append(attendee.toICalendar());
			}
		}
		if (status != STATUS_UNDEFINED) {
			switch (status) {
				case STATUS_CONFIRMED:
					ret.append("STATUS:CONFIRMED");
					ret.append(CRLF);
					break;
				case STATUS_TENTATIVE:
					ret.append("STATUS:TENTATIVE");
					ret.append(CRLF);
					break;
				case STATUS_CANCELLED:
					ret.append("STATUS:CANCELLED");
					ret.append(CRLF);
					break;
			}
		}

		if (this.alarms != null) {
			for (int i = 0; i < this.alarms.size(); i++) {
				Valarm alarm = (Valarm) this.alarms.get(i);
				ret.append(alarm.toICalendar());
			}
		}

		ret.append("END:VEVENT");
		ret.append(CRLF);

		return ret.toString();
	}

}
