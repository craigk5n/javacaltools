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
	public Uid uid = null;
	/** Sequence number (0 is first version) */
	public Sequence sequence = null;
	/** Brief summary */
	public Summary summary = null;
	/** Full description */
	public Description description = null;
	/** Classification (PUBLIC, CONFIDENTIAL, PRIVATE) */
	public Classification classification = null;
	/** List of categories (comma-separated) */
	public Categories categories = null;
	/** Primary start date */
	public Date startDate = null;
	/** End date */
	public Date endDate = null; // may be derived from duration if not specified
	/** Time created */
	public Date dtstamp = null;
	/** Time last modified */
	public Date lastModified = null;
	/** Event duration (in seconds) */
	public Duration duration;
	/** Contact for the event */
	// public Individual contact;
	/** Participants for the event (Vector of Attendee) */
	public Vector attendees = null;
	/** Recurrence rule (RRULE) */
	public Rrule rrule = null;

	// TODO: multiple summaries, descriptions with different LANGUAGE values
	// TODO: alarms/triggers
	// TODO: RDATE - recurrance dates
	// TODO: EXDATE - exception dates
	// TODO: EXRULE - exception rule
	// TODO: URL
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
	public Event(ICalendarParser parser, int initialLine, Vector textLines) {
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
		} else if ( up.startsWith ( "DTSTART" ) ) {
			startDate = new Date ( icalStr );
		} else if ( up.startsWith ( "DTEND" ) ) {
			endDate = new Date ( icalStr );
		} else if ( up.startsWith ( "DTSTAMP" ) ) {
			dtstamp = new Date ( icalStr );
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
		if ( classification != null )
			ret.append ( classification.toICalendar () );
		if ( categories != null )
			ret.append ( categories.toICalendar () );

		ret.append ( "END:VEVENT" );
		ret.append ( CRLF );

		return ret.toString ();
	}

}
