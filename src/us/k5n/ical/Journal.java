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
 * iCalendar Journal class: VJOURNAL components describe a journal entry. They
 * simply attach descriptive text notes with a particular calendar date, and
 * might be used to record a daily record of activities or accomplishments. A
 * "VJOURNAL" calendar component does not take up time on, so it has no affect
 * on free or busy time (just like TRANSPARENT entries).
 * 
 * @version $Id$
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
	/** Date created */
	protected Date createdDate = null;
	/** Primary start date */
	protected Date startDate = null;
	/** Time created */
	protected Date dtstamp = null;
	/** Time last modified */
	protected Date lastModified = null;
	/** Participants for the journal entry (Vector of Attendee) */
	protected Vector attendees = null;
	/** Recurrence rule (RRULE) */
	protected Rrule rrule = null;
	/** URL */
	protected URL url = null;
	/** Attachments */
	protected Vector attachments = null;
	/** Journal status */
	protected int status = STATUS_UNDEFINED;
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
	 *          The IcalParser object
	 * @param initialLine
	 *          The starting line number
	 * @Param textLines
	 *          Vector of iCalendar text lines
	 */
	public Journal(CalendarParser parser, int initialLine, Vector textLines) {
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
	 * Create a journal entry
	 * 
	 * @param summary
	 *          Brief summary of journal entry
	 * @param description
	 *          Full description of the journal entry
	 * @param startDate
	 *          The date of the journal entry in ISO 8601 format (19991231,
	 *          199912310T115900, etc.)
	 */
	public Journal(String summary, String description, Date startDate) {
		uid = new Uid (); // Generate unique Id
		this.summary = new Summary ();
		this.summary.value = summary;
		this.description = new Description ();
		this.description.value = description;
		this.startDate = startDate;
		// create a sequence if not specified
		if ( sequence == null )
			sequence = new Sequence ( 0 );
		// Set DTSTAMP, which is the original creation date
		this.dtstamp = Date.getCurrentDateTime ( "DTSTAMP" );
		this.createdDate = Date.getCurrentDateTime ( "CREATED" );
	}

	/**
	 * Was enough information parsed for this Journal to be valid?
	 */
	public boolean isValid () {
		// Must have at least a start date and a summary
		// return ( startDate != null && summary != null );
		return true;
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
		if ( up.equals ( "BEGIN:VJOURNAL" ) || up.equals ( "END:VJOURNAL" ) ) {
			// ignore
		} else if ( up.trim ().length () == 0 ) {
			// ignore empty lines
		} else if ( up.startsWith ( "DESCRIPTION" ) ) {
			description = new Description ( icalStr );
		} else if ( up.startsWith ( "SUMMARY" ) ) {
			summary = new Summary ( icalStr );
		} else if ( up.startsWith ( "CREATED" ) ) {
			createdDate = new Date ( icalStr );
		} else if ( up.startsWith ( "DTSTART" ) ) {
			startDate = new Date ( icalStr );
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
		} else if ( up.startsWith ( "ATTACH" ) ) {
			Attachment attach = new Attachment ( icalStr );
			if ( this.attachments == null )
				this.attachments = new Vector ();
			this.attachments.addElement ( attach );
		} else if ( up.startsWith ( "STATUS" ) ) {
			status = StringUtils.parseStatus ( icalStr, parseMethod );
			// Only allow VJOURNAL status types
			if ( status != STATUS_DRAFT && status != STATUS_FINAL ) {
				if ( parseMethod == PARSE_STRICT ) {
					throw new BogusDataException ( "Status type not allowed in VJOURNAL",
					    icalStr );
				}
			}
		} else if ( up.startsWith ( "URL" ) ) {
			url = new URL ( icalStr, parseMethod );
		} else {
			System.err.println ( "Ignoring: " + icalStr );
		}
	}

	/**
	 * Get the journal entry summary
	 */
	public Summary getSummary () {
		return getSummary ( "EN" );
	}

	/**
	 * Get the journal entry summary for the specified language. If not available,
	 * then the primary summary will be returned.
	 * 
	 * @param langage
	 *          The language ("EN", "FR", etc.)
	 */
	public Summary getSummary ( String language ) {
		// TODO: handle language param
		return summary;
	}

	/**
	 * Convert this Journal into iCalendar text
	 */
	public String toICalendar () {
		StringBuffer ret = new StringBuffer ( 128 );
		ret.append ( "BEGIN:VJOURNAL" );
		ret.append ( CRLF );

		if ( uid != null )
			ret.append ( uid.toICalendar () );
		if ( sequence != null )
			ret.append ( sequence.toICalendar () );
		if ( summary != null )
			ret.append ( summary.toICalendar () );
		if ( description != null )
			ret.append ( description.toICalendar () );
		if ( createdDate != null )
			ret.append ( createdDate.toICalendar () );
		if ( startDate != null )
			ret.append ( startDate.toICalendar () );
		if ( dtstamp != null )
			ret.append ( dtstamp.toICalendar () );
		if ( lastModified != null )
			ret.append ( lastModified.toICalendar () );
		if ( classification != null )
			ret.append ( classification.toICalendar () );
		if ( categories != null )
			ret.append ( categories.toICalendar () );
		if ( url != null )
			ret.append ( url.toICalendar () );
		if ( this.attachments != null ) {
			for ( int i = 0; i < this.attachments.size (); i++ ) {
				Attachment attach = (Attachment) this.attachments.elementAt ( i );
				ret.append ( attach.toICalendar () );
			}
		}
		if ( status != STATUS_UNDEFINED ) {
			switch ( status ) {
				case STATUS_DRAFT:
					ret.append ( "STATUS:DRAFT" );
					ret.append ( CRLF );
					break;
				case STATUS_FINAL:
					ret.append ( "STATUS:FINAL" );
					ret.append ( CRLF );
					break;
			}
		}

		ret.append ( "END:VJOURNAL" );
		ret.append ( CRLF );

		return ret.toString ();
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

	public Date getDtstamp () {
		return dtstamp;
	}

	public void setDtstamp ( Date dtstamp ) {
		this.dtstamp = dtstamp;
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

	public Date getCreatedDate () {
  	return createdDate;
  }

	public void setCreatedDate ( Date createdDate ) {
  	this.createdDate = createdDate;
  }

	public Date getStartDate () {
		return startDate;
	}

	public void setStartDate ( Date startDate ) {
		this.startDate = startDate;
	}

	public void setSummary ( Summary summary ) {
		this.summary = summary;
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

	public Vector getAttachments () {
  	return attachments;
  }

	public void setAttachments ( Vector attachments ) {
  	this.attachments = attachments;
  }

	public int getStatus () {
  	return status;
  }

	public void setStatus ( int status ) {
  	this.status = status;
  }

	public Object getUserData () {
		return userData;
	}

	public void setUserData ( Object userData ) {
		this.userData = userData;
	}

}
