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
 * iCalendar Journal class: VJOURNAL components describe a journal entry. They
 * simply attach descriptive text notes with a particular calendar date, and
 * might be used to record a daily record of activities or accomplishments. A
 * "VJOURNAL" calendar component does not take up time on, so it has no affect
 * on free or busy time (just like TRANSPARENT entries).
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class Journal implements Constants {
	// TODO: handle multiple instances of summary/description since
	// there can be more than one if LANGUAGE attribute is specified
	/** Unique Id */
	protected Uid uid = null;
	/** Sequence number (0 is first version) */
	protected Sequence sequence = null;
	/** Brief summary */
	protected Summary summary = null;
	/** Full description */
	protected Description description = null;
	/** Classification (PUBLIC, CONFIDENTIAL, PRIVATE) */
	protected Classification classification = null;
	/** List of categories (comma-separated) */
	protected Categories categories = null;
	/** Color */
	protected String color = null;
	/** Image URI */
	protected String imageUri = null;
	/** Date created */
	protected Date createdDate = null;
	/** Primary start date */
	protected Date startDate = null;
	/** Time created */
	protected Date dtstamp = null;
	/** Time last modified */
	protected Date lastModified = null;
	/** Participants for the journal entry (List of Attendee) */
	protected List<Attendee> attendees = null;
	/** Recurrence rule (RRULE) */
	protected Rrule rrule = null;
	/** URL */
	protected URL url = null;
	/** Attachments */
	protected List<Attachment> attachments = null;
	/** Journal status */
	protected int status = STATUS_UNDEFINED;
	/** Organizer */
	protected Organizer organizer = null;
	/** Comment */
	protected Comment comment = null;
	/** Contact */
	protected Contact contact = null;
	/** Exception dates */
	protected List<Date> exdates = null;
	/** Recurrence dates */
	protected List<Date> rdates = null;
	/** Related to */
	protected RelatedTo relatedTo = null;
	/** Alarms (List of Valarm objects) */
	protected List<Valarm> alarms = null;
	/** Parser reference for sub-component parsing */
	private CalendarParser parser = null;
	/** VALARM parsing state */
	private boolean inValarm = false;
	private List<String> valarmLines = null;
	/** Private user object for caller to set/get */
	private Object userData = null;

	// TODO: multiple summaries, descriptions with different LANGUAGE values
	// TODO: alarms/triggers
	// TODO: RDATE - recurrance dates
	// TODO: EXDATE - exception dates
	// TODO: EXRULE - exception rule
	// TODO: RELATED-TO

	/**
	 * Create n Journal object based on the specified iCalendar data
	 * 
	 * @param parser
	 *                    The IcalParser object
	 * @param initialLine
	 *                    The starting line number
	 * @param textLines List of iCalendar text lines
	 */
	public Journal(CalendarParser parser, int initialLine,
			List<String> textLines) {
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
	 * Create a journal entry
	 * 
	 * @param summary
	 *                    Brief summary of journal entry
	 * @param description
	 *                    Full description of the journal entry
	 * @param startDate
	 *                    The date of the journal entry in ISO 8601 format
	 *                    (19991231,
	 *                    199912310T115900, etc.)
	 */
	public Journal(String summary, String description, Date startDate) {
		uid = new Uid(); // Generate unique Id
		this.summary = new Summary();
		this.summary.value = summary;
		this.description = new Description();
		this.description.value = description;
		this.startDate = startDate;
		// create a sequence if not specified
		if (sequence == null)
			sequence = new Sequence(0);
		// Set DTSTAMP, which is the original creation date
		this.dtstamp = Date.getCurrentDateTime("DTSTAMP");
		this.createdDate = Date.getCurrentDateTime("CREATED");
	}

	/**
	 * Was enough information parsed for this Journal to be valid?
	 */
	public boolean isValid() {
		return isValid(null);
	}

	/**
	 * Check if this Journal is valid with optional error details
	 *
	 * @param errors List to collect validation error messages (can be null)
	 * @return true if the journal is valid
	 */
	public boolean isValid(List<String> errors) {
		boolean valid = true;

		// For journals, be lenient to maintain backward compatibility
		// Just require some basic content
		if (summary == null && description == null) {
			valid = false;
			if (errors != null) errors.add("Journal must have either SUMMARY or DESCRIPTION");
		}

		// Date validation
		if (startDate != null && createdDate != null) {
			if (startDate.compareTo(createdDate) < 0) {
				valid = false;
				if (errors != null) errors.add("Journal DTSTART should not be before CREATED date");
			}
		}

		// Alarm validation
		if (alarms != null) {
			for (int i = 0; i < alarms.size(); i++) {
				Valarm alarm = alarms.get(i);
				if (alarm != null && !alarm.isValid()) {
					valid = false;
					if (errors != null) errors.add("Journal alarm " + i + " is invalid");
				}
			}
		}

		// Status validation
		if (status != STATUS_UNDEFINED && status != STATUS_DRAFT && status != STATUS_FINAL) {
			valid = false;
			if (errors != null) errors.add("Invalid journal STATUS value: " + status);
		}

		return valid;
	}

	/**
	 * Parse a line of iCalendar test.
	 * 
	 * @param icalStr
	 *                    The line of text
	 * @param parseMethod
	 *                    PARSE_STRICT or PARSE_LOOSE
	 */
	public void parseLine(String icalStr, int parseMethod)
			throws ParseException, BogusDataException {
		String up = icalStr.toUpperCase();
		if (up.equals("BEGIN:VJOURNAL") || up.equals("END:VJOURNAL")) {
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
		} else if (up.startsWith("CREATED")) {
			createdDate = new Date(icalStr);
		} else if (up.startsWith("DTSTART")) {
			startDate = new Date(icalStr);
		} else if (up.startsWith("DTSTAMP")) {
			dtstamp = new Date(icalStr);
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
		} else if (up.startsWith("ATTACH")) {
			Attachment attach = new Attachment(icalStr);
			if (this.attachments == null)
				this.attachments = new ArrayList<Attachment>();
			this.attachments.add(attach);
		} else if (up.startsWith("STATUS")) {
			status = StringUtils.parseStatus(icalStr, parseMethod);
			// Only allow VJOURNAL status types
			if (status != STATUS_DRAFT && status != STATUS_FINAL) {
				if (parseMethod == PARSE_STRICT) {
					throw new BogusDataException("Status type not allowed in VJOURNAL",
							icalStr);
				}
			}
		} else if (up.startsWith("URL")) {
			url = new URL(icalStr, parseMethod);
		} else if (up.startsWith("ORGANIZER")) {
			organizer = new Organizer(icalStr);
		} else if (up.startsWith("COMMENT")) {
			comment = new Comment(icalStr);
		} else if (up.startsWith("CONTACT")) {
			contact = new Contact(icalStr);
		} else if (up.startsWith("ATTENDEE")) {
			Attendee attendee = new Attendee(icalStr);
			if (this.attendees == null)
				this.attendees = new ArrayList<Attendee>();
			this.attendees.add(attendee);
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
					Date rdate = new Date(newIcalStr);
					this.rdates.add(rdate);
				}
			}
		} else if (up.startsWith("RELATED-TO")) {
			relatedTo = new RelatedTo(icalStr);
		} else if (up.startsWith("COLOR")) {
			Property p = new Property(icalStr);
			color = p.value;
		} else if (up.startsWith("IMAGE")) {
			Property p = new Property(icalStr);
			imageUri = p.value;
		} else {
			System.err.println("Ignoring VJOURNAL line: " + icalStr);
		}
	}

	/**
	 * Get the journal entry summary
	 */
	public Summary getSummary() {
		return getSummary("EN");
	}

	/**
	 * Get the journal entry summary for the specified language. If not available,
	 * then the primary summary will be returned.
	 * 
	 * @param language
	 *                The language ("EN", "FR", etc.)
	 */
	public Summary getSummary(String language) {
		// TODO: handle language param
		return summary;
	}

	/**
	 * Convert this Journal into iCalendar text
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(128);
		ret.append("BEGIN:VJOURNAL");
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
		if (dtstamp != null)
			ret.append(dtstamp.toICalendar());
		if (lastModified != null)
			ret.append(lastModified.toICalendar());
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
		if (relatedTo != null)
			ret.append(relatedTo.toICalendar());
		if (categories != null)
			ret.append(categories.toICalendar());
		if (url != null)
			ret.append(url.toICalendar());
		if (organizer != null)
			ret.append(organizer.toICalendar());
		if (comment != null)
			ret.append(comment.toICalendar());
		if (contact != null)
			ret.append(contact.toICalendar());
		if (this.attachments != null) {
			for (int i = 0; i < this.attachments.size(); i++) {
				Attachment attach = (Attachment) this.attachments.get(i);
				ret.append(attach.toICalendar());
			}
		}
		if (status != STATUS_UNDEFINED) {
			switch (status) {
				case STATUS_DRAFT:
					ret.append("STATUS:DRAFT");
					ret.append(CRLF);
					break;
				case STATUS_FINAL:
					ret.append("STATUS:FINAL");
					ret.append(CRLF);
					break;
			}
		}
		if (this.attendees != null) {
			for (int i = 0; i < this.attendees.size(); i++) {
				Attendee attendee = (Attendee) this.attendees.get(i);
				ret.append(attendee.toICalendar());
			}
		}

		if (this.alarms != null) {
			for (int i = 0; i < this.alarms.size(); i++) {
				Valarm alarm = (Valarm) this.alarms.get(i);
				ret.append(alarm.toICalendar());
			}
		}

		if (color != null) {
			ret.append("COLOR:").append(color).append(CRLF);
		}
		if (imageUri != null) {
			ret.append("IMAGE:").append(imageUri).append(CRLF);
		}

		ret.append("END:VJOURNAL");
		ret.append(CRLF);

		return ret.toString();
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

	public Date getDtstamp() {
		return dtstamp;
	}

	public void setDtstamp(Date dtstamp) {
		this.dtstamp = dtstamp;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
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

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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
	 * Get comment
	 *
	 * @return comment object
	 */
	public Comment getComment() {
		return comment;
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
	 * Get exception dates
	 *
	 * @return list of exception dates
	 */
	public List<Date> getExceptions() {
		return this.exdates;
	}

	/**
	 * Get recurrence dates
	 *
	 * @return list of recurrence dates
	 */
	public List<Date> getRdates() {
		return this.rdates;
	}

	/**
	 * Get related to
	 *
	 * @return related to object
	 */
	public RelatedTo getRelatedTo() {
		return relatedTo;
	}

	/**
	 * Get alarms
	 *
	 * @return list of alarms
	 */
	public List<Valarm> getAlarms() {
		return alarms;
	}

	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}

}
