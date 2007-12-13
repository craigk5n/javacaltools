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

import java.util.Vector;

/**
 * iCalendar Event class that corresponds to the VEVENT iCalendarendar object.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Event implements Constants {
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
	/** Comment */
	protected Comment comment = null;
	/** Classification (PUBLIC, CONFIDENTIAL, PRIVATE) */
	protected Classification classification = null;
	/** List of categories (comma-separated) */
	protected Categories categories = null;
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
	/** Participants for the event (Vector of Attendee) */
	protected Vector attendees = null;
	/** Recurrance rule (RRULE) */
	protected Rrule rrule = null;
	/** URL */
	protected URL url = null;
	/** Location */
	protected Location location = null;
	/** Event status */
	protected int status = STATUS_UNDEFINED;
	/** Attachments */
	protected Vector attachments = null;
	/** Private user object for caller to set/get */
	private Object userData = null;

	// TODO: multiple summaries, descriptions with different LANGUAGE values
	// TODO: alarms/triggers
	// TODO: RDATE - recurrance dates
	// TODO: EXDATE - exception dates
	// TODO: EXRULE - exception rule
	// TODO: RELATED-TO

	/**
	 * Create an Event object based on the specified iCalendar data
	 * 
	 * @param parser
	 *          The IcalParser object
	 * @param initialLine
	 *          The starting line number
	 * @Param textLines
	 *          Vector of iCalendar text lines
	 */
	public Event(CalendarParser parser, int initialLine, Vector textLines) {
		for ( int i = 0; i < textLines.size (); i++ ) {
			String line = (String) textLines.elementAt ( i );
			try {
				parseLine ( line, parser.getParseMethod () );
			} catch ( BogusDataException bde ) {
				parser.reportParseError ( new ParseError ( initialLine + i, bde.error,
				    line ) );
			} catch ( ParseException pe ) {
				parser.reportParseError ( new ParseError ( initialLine + i, pe.error,
				    line ) );
			}
		}
		// must have UID
		if ( uid == null )
			uid = new Uid ();
		// create a sequence if not specified
		if ( sequence == null )
			sequence = new Sequence ( 0 );
	}

	/**
	 * Create an event that is timeless (no duration)
	 * 
	 * @param summary
	 *          Brief summary of event
	 * @param description
	 *          Full description of the event
	 * @param startDate
	 *          The date of the event in ISO 8601 format (19991231,
	 *          199912310T115900, etc.)
	 */
	public Event(String summary, String description, Date startDate) {
		this ( summary, description, startDate, 0 );
	}

	/**
	 * Create an event with the specified duration
	 * 
	 * @param summary
	 *          Brief summary of event
	 * @param description
	 *          Full description of the event
	 * @param startDate
	 *          The date of the event in ISO 8601 format (19991231,
	 *          199912310T115900, etc.)
	 * @param duration
	 *          The event duration in seconds
	 */
	public Event(String summary, String description, Date startDate, int duration) {
		uid = new Uid (); // Generate unique Id
		this.summary = new Summary ();
		this.summary.value = summary;
		this.description = new Description ();
		this.description.value = description;
		this.startDate = startDate;
		this.duration = new Duration ( duration );
		// TODO: calculate endDate from duration
	}

	/**
	 * Was enough information parsed for this Event to be valid? Note: if an event
	 * is parsed by ICalendarParser and it is found to have an invalid date, no
	 * Event object will be created for it.
	 */
	public boolean isValid () {
		// Must have at least a start date and a summary
		return ( startDate != null && summary != null && uid != null );
	}

	/**
	 * Parse a line of iCalendar test.
	 * 
	 * @param line
	 *          The line of text
	 * @param parseMethod
	 *          PARSE_STRICT or PARSE_LOOSE
	 */
	public void parseLine ( String icalStr, int parseMethod )
	    throws ParseException, BogusDataException {
		String up = icalStr.toUpperCase ();
		if ( up.equals ( "BEGIN:VEVENT" ) || up.equals ( "END:VEVENT" ) ) {
			// ignore
		} else if ( up.trim ().length () == 0 ) {
			// ignore empty lines
		} else if ( up.startsWith ( "DESCRIPTION" ) ) {
			description = new Description ( icalStr );
		} else if ( up.startsWith ( "SUMMARY" ) ) {
			summary = new Summary ( icalStr );
		} else if ( up.startsWith ( "COMMENT" ) ) {
			comment = new Comment ( icalStr );
		} else if ( up.startsWith ( "DTSTART" ) ) {
			startDate = new Date ( icalStr );
		} else if ( up.startsWith ( "DTEND" ) ) {
			endDate = new Date ( icalStr );
		} else if ( up.startsWith ( "DTSTAMP" ) ) {
			dtstamp = new Date ( icalStr );
		} else if ( up.startsWith ( "DURATION" ) ) {
			duration = new Duration ( icalStr );
		} else if ( up.startsWith ( "LAST-MODIFIED" ) ) {
			lastModified = new Date ( icalStr );
		} else if ( up.startsWith ( "CLASS" ) ) {
			classification = new Classification ( icalStr );
		} else if ( up.startsWith ( "CATEGORIES" ) ) {
			categories = new Categories ( icalStr );
		} else if ( up.startsWith ( "UID" ) ) {
			uid = new Uid ( icalStr );
		} else if ( up.startsWith ( "SEQUENCE" ) ) {
			sequence = new Sequence ( icalStr );
		} else if ( up.startsWith ( "RRULE" ) ) {
			rrule = new Rrule ( icalStr, parseMethod );
		} else if ( up.startsWith ( "STATUS" ) ) {
			status = StringUtils.parseStatus ( icalStr, parseMethod );
			// Only allow VEVENT status types
			if ( status != STATUS_TENTATIVE && status != STATUS_CONFIRMED
			    && status != STATUS_CANCELLED ) {
				if ( parseMethod == PARSE_STRICT ) {
					throw new BogusDataException ( "Status type not allowed in VEVENT",
					    icalStr );
				}
			}
		} else if ( up.startsWith ( "URL" ) ) {
			url = new URL ( icalStr );
		} else if ( up.startsWith ( "LOCATION" ) ) {
			location = new Location ( icalStr );
		} else if ( up.startsWith ( "ATTACH" ) ) {
			Attachment attach = new Attachment ( icalStr );
			if ( this.attachments == null )
				this.attachments = new Vector ();
			this.attachments.addElement ( attach );
		} else {
			System.out.println ( "Ignoring: " + icalStr );
		}
	}

	/**
	 * Get the event summary
	 */
	public Summary getSummary () {
		return getSummary ( "EN" );
	}

	/**
	 * Get the event summary for the specified language. If not available, then
	 * the primary summary will be returned.
	 * 
	 * @param langage
	 *          The language ("EN", "FR", etc.)
	 */
	public Summary getSummary ( String language ) {
		// TODO: handle language param
		return summary;
	}

	public Vector getAttendees () {
		return attendees;
	}

	public void setAttendees ( Vector attendees ) {
		this.attendees = attendees;
	}

	public Categories getCategories () {
		return categories;
	}

	public void setCategories ( Categories categories ) {
		this.categories = categories;
	}

	public Classification getClassification () {
		return classification;
	}

	public void setClassification ( Classification classification ) {
		this.classification = classification;
	}

	public Description getDescription () {
		return description;
	}

	public void setDescription ( Description description ) {
		this.description = description;
	}

	public Comment getComment () {
		return comment;
	}

	public void setComment ( Comment comment ) {
		this.comment = comment;
	}

	public Date getDtstamp () {
		return dtstamp;
	}

	public void setDtstamp ( Date dtstamp ) {
		this.dtstamp = dtstamp;
	}

	public Duration getDuration () {
		return duration;
	}

	public void setDuration ( Duration duration ) {
		this.duration = duration;
	}

	public Date getEndDate () {
		return endDate;
	}

	public void setEndDate ( Date endDate ) {
		this.endDate = endDate;
	}

	public Date getLastModified () {
		return lastModified;
	}

	public void setLastModified ( Date lastModified ) {
		this.lastModified = lastModified;
	}

	public Rrule getRrule () {
		return rrule;
	}

	public void setRrule ( Rrule rrule ) {
		this.rrule = rrule;
	}

	public Sequence getSequence () {
		return sequence;
	}

	public void setSequence ( Sequence sequence ) {
		this.sequence = sequence;
	}

	public Date getStartDate () {
		return startDate;
	}

	public void setStartDate ( Date startDate ) {
		this.startDate = startDate;
	}

	public Uid getUid () {
		return uid;
	}

	public void setUid ( Uid uid ) {
		this.uid = uid;
	}

	public URL getUrl () {
		return url;
	}

	public void setUrl ( URL url ) {
		this.url = url;
	}

	public Location getLocation () {
		return location;
	}

	public void setLocation ( Location location ) {
		this.location = location;
	}

	/**
	 * Get the event status
	 * 
	 * @return the event status (STATUS_TENTATIVE, STATUS_CONFIRMED or
	 *         STATUS_CANCELLED)
	 */
	public int getStatus () {
		return status;
	}

	/**
	 * Set the status
	 * 
	 * @param status
	 *          The new status (STATUS_TENTATIVE, STATUS_CONFIRMED or
	 *          STATUS_CANCELLED)
	 */
	public void setStatus ( int status ) {
		this.status = status;
	}

	public void setSummary ( Summary summary ) {
		this.summary = summary;
	}

	public Vector getAttachments () {
  	return attachments;
  }

	public void setAttachments ( Vector attachments ) {
  	this.attachments = attachments;
  }

	public Object getUserData () {
		return userData;
	}

	public void setUserData ( Object userData ) {
		this.userData = userData;
	}

	/**
	 * Get a Vector of Date objects that contain the recurrance dates (or null if
	 * there are none).
	 * 
	 * @return
	 */
	public Vector getRecurranceDates () {
		String tzid = null;
		// TODO: add support for exceptions
		if ( this.rrule == null )
			return null;
		if ( this.startDate == null )
			return null;
		tzid = this.startDate.tzid;
		return rrule.generateRecurrances ( this.startDate, tzid );
	}

	/**
	 * Convert this Event into iCalendar text
	 */
	public String toICalendar () {
		StringBuffer ret = new StringBuffer ( 128 );
		ret.append ( "BEGIN:VEVENT" );
		ret.append ( CRLF );

		if ( uid != null )
			ret.append ( uid.toICalendar () );
		if ( sequence != null )
			ret.append ( sequence.toICalendar () );
		if ( summary != null )
			ret.append ( summary.toICalendar () );
		if ( description != null )
			ret.append ( description.toICalendar () );
		if ( startDate != null )
			ret.append ( startDate.toICalendar () );
		if ( endDate != null )
			ret.append ( endDate.toICalendar () );
		if ( dtstamp != null )
			ret.append ( dtstamp.toICalendar () );
		if ( lastModified != null )
			ret.append ( lastModified.toICalendar () );
		if ( rrule != null )
			ret.append ( rrule.toICalendar () );
		if ( classification != null )
			ret.append ( classification.toICalendar () );
		if ( categories != null )
			ret.append ( categories.toICalendar () );
		if ( url != null )
			ret.append ( url.toICalendar () );
		if ( location != null )
			ret.append ( location.toICalendar () );
		if ( this.attachments != null ) {
			for ( int i = 0; i < this.attachments.size (); i++ ) {
				Attachment attach = (Attachment) this.attachments.elementAt ( i );
				ret.append ( attach.toICalendar () );
			}
		}
		if ( status != STATUS_UNDEFINED ) {
			switch ( status ) {
				case STATUS_CONFIRMED:
					ret.append ( "STATUS:CONFIRMED" );
					ret.append ( CRLF );
					break;
				case STATUS_TENTATIVE:
					ret.append ( "STATUS:TENTATIVE" );
					ret.append ( CRLF );
					break;
				case STATUS_CANCELLED:
					ret.append ( "STATUS:CANCELLED" );
					ret.append ( CRLF );
					break;
			}
		}

		ret.append ( "END:VEVENT" );
		ret.append ( CRLF );

		return ret.toString ();
	}

}
