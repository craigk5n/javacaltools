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
 * iCal Journal class
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Journal implements Constants {
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
  /** List of categories (comma-separated) */
  public Categories categories = null;
  /** Primary start date */
  public Date startDate = null;
  /** Time created */
  public Date dtstamp = null;
  /** Time last modified */
  public Date lastModified = null;
  /** Contact for the entry */
  // public Individual contact;
  /** Participants for the journal entry (Vector of Attendee) */
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
   * Create n Journal object based on the specified iCal data
   * 
   * @param parser
   *          The IcalParser object
   * @param initialLine
   *          The starting line number
   * @Param textLines
   *          Vector of iCal text lines
   */
  public Journal ( IcalParser parser, int initialLine, Vector textLines ) {
    for (int i = 0; i < textLines.size (); i++) {
      String line = (String)textLines.elementAt ( i );
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
    if (uid == null)
      uid = new Uid ();
    // create a sequence if not specified
    if (sequence == null)
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
  public Journal ( String summary, String description, Date startDate ) {
    uid = new Uid (); // Generate unique Id
    this.summary = new Summary ();
    this.summary.value = summary;
    this.description = new Description ();
    this.description.value = description;
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
   * Parse a line of iCal test.
   * 
   * @param line
   *          The line of text
   * @param parseMethod
   *          PARSE_STRICT or PARSE_LOOSE
   */
  public void parseLine ( String icalStr, int parseMethod )
      throws ParseException, BogusDataException {
    String up = icalStr.toUpperCase ();
    if (up.equals ( "BEGIN:VEVENT" ) || up.equals ( "END:VEVENT" )) {
      // ignore
    } else if (up.trim ().length () == 0) {
      // ignore empty lines
    } else if (up.startsWith ( "DESCRIPTION" )) {
      description = new Description ( icalStr );
    } else if (up.startsWith ( "SUMMARY" )) {
      summary = new Summary ( icalStr );
    } else if (up.startsWith ( "DTSTART" )) {
      startDate = new Date ( icalStr );
    } else if (up.startsWith ( "DTSTAMP" )) {
      dtstamp = new Date ( icalStr );
    } else if (up.startsWith ( "LAST-MODIFIED" )) {
      lastModified = new Date ( icalStr );
    } else if (up.startsWith ( "CATEGORIES" )) {
      categories = new Categories ( icalStr );
    } else if (up.startsWith ( "UID" )) {
      uid = new Uid ( icalStr );
    } else if (up.startsWith ( "SEQUENCE" )) {
      sequence = new Sequence ( icalStr );
    } else if (up.startsWith ( "RRULE" )) {
      rrule = new Rrule ( icalStr, parseMethod );
    } else {
      System.out.println ( "Ignoring: " + icalStr );
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
   * Convert this Journal into iCal text
   */
  public String toIcal () {
    StringBuffer ret = new StringBuffer ( 128 );
    ret.append ( "BEGIN:VJOURNAL" );
    ret.append ( CRLF );

    if (uid != null)
      ret.append ( uid.toIcal () );
    if (sequence != null)
      ret.append ( sequence.toIcal () );
    if (summary != null)
      ret.append ( summary.toIcal () );
    if (description != null)
      ret.append ( description.toIcal () );
    if (startDate != null)
      ret.append ( startDate.toIcal () );
    if (dtstamp != null)
      ret.append ( dtstamp.toIcal () );
    if (lastModified != null)
      ret.append ( lastModified.toIcal () );

    ret.append ( "END:VJOURNAL" );
    ret.append ( CRLF );

    return ret.toString ();
  }
}
