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

/**
 * Base class for use with a variety of date-related iCalendar fields including
 * LAST-MODIFIED, DTSTAMP, DTSTART, etc. This can represent both a date and a
 * date-time.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Date extends Property implements Constants {
	int year, month, day;
	int hour, minute, second;
	boolean isUTC = false;
	boolean dateOnly = false; // is date only (rather than date-time)?

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies a date. Dates must
	 *          be of one of the following formats:
	 *          <ul>
	 *          <li> 19991231 (date only, no time) </li>
	 *          <li> 19991231T115900 (date with local time) </li>
	 *          <li> 19991231T115900Z (date and time UTC) </li>
	 *          </ul>
	 *          (This format is a based on the ISO 8601 standard.)
	 */
	public Date(String icalStr) throws ParseException, BogusDataException {
		this ( icalStr, PARSE_LOOSE );
	}

	/**
	 * Constructor: create a date based on the specified year, month and day.
	 * 
	 * @param dateType
	 *          Type of date; this should be an ical property name like DTSTART,
	 *          DTEND or DTSTAMP.
	 * @param year
	 *          The 4-digit year
	 * @param month
	 *          The month (1-12)
	 * @param day
	 *          The day of the month (1-31)
	 */

	public Date(String dateType, int year, int month, int day)
	    throws ParseException, BogusDataException {
		super ( dateType, "" );

		this.year = year;
		this.month = month;
		this.day = day;
		hour = minute = second = 0;
		dateOnly = true;

		String yearStr, monthStr, dayStr;

		yearStr = "" + year;
		monthStr = "" + month;
		dayStr = "" + day;
		while ( yearStr.length () < 4 )
			yearStr = '0' + yearStr;
		if ( monthStr.length () < 2 )
			monthStr = '0' + monthStr;
		if ( dayStr.length () < 2 )
			dayStr = '0' + dayStr;
		value = yearStr + monthStr + dayStr;

		// Add attribute that says date-only
		addAttribute ( "VALUE", "DATE" );
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies a date
	 * @param parseMode
	 *          PARSE_STRICT or PARSE_LOOSE
	 */
	public Date(String icalStr, int parseMode) throws ParseException,
	    BogusDataException {
		super ( icalStr, parseMode );

		year = month = day = 0;
		hour = minute = second = 0;

		for ( int i = 0; i < attributeList.size (); i++ ) {
			Attribute a = attributeAt ( i );
			String aname = a.name.toUpperCase ();
			String aval = a.value.toUpperCase ();
			// TODO: not sure if any attributes are allowed here...
			// Look for VALUE=DATE or VALUE=DATE-TIME
			// DATE means untimed for the event
			if ( aname.equals ( "VALUE" ) ) {
				if ( aval.equals ( "DATE" ) ) {
					dateOnly = true;
				} else if ( aval.equals ( "DATE-TIME" ) ) {
					dateOnly = false;
				} else {
					if ( parseMode == PARSE_STRICT ) {
						throw new ParseException ( "Unknown date VALUE '" + a.value + "'",
						    icalStr );
					}
				}
			} else {
				// TODO: anything else allowed here?
			}
		}

		String inDate = value;

		if ( inDate.length () < 8 ) {
			// Invalid format
			throw new ParseException ( "Invalid date format '" + inDate + "'", inDate );
		}

		// Make sure all parts of the year are numeric.
		for ( int i = 0; i < 8; i++ ) {
			char ch = inDate.charAt ( i );
			if ( ch < '0' || ch > '9' ) {
				throw new ParseException ( "Invalid date format '" + inDate + "'",
				    inDate );
			}
		}
		year = Integer.parseInt ( inDate.substring ( 0, 4 ) );
		month = Integer.parseInt ( inDate.substring ( 4, 6 ) );
		day = Integer.parseInt ( inDate.substring ( 6, 8 ) );
		// TODO: validate for each month and leap years, too
		if ( day < 1 || day > 31 || month < 1 || month > 12 )
			throw new BogusDataException ( "Invalid date '" + inDate + "'", inDate );
		// TODO: parse time, handle localtime, handle timezone
		if ( inDate.length () > 8 ) {
			// TODO make sure dateOnly == false
			if ( inDate.charAt ( 8 ) == 'T' ) {
				try {
					hour = Integer.parseInt ( inDate.substring ( 9, 11 ) );
					minute = Integer.parseInt ( inDate.substring ( 11, 13 ) );
					second = Integer.parseInt ( inDate.substring ( 13, 15 ) );
					if ( hour > 23 || minute > 59 || second > 59 ) {
						throw new BogusDataException ( "Invalid time in date string '"
						    + inDate + "'", inDate );
					}
					if ( inDate.length () > 15 ) {
						isUTC = inDate.charAt ( 15 ) == 'Z';
					}
				} catch ( NumberFormatException nef ) {
					throw new BogusDataException ( "Invalid time in date string '"
					    + inDate + "' - " + nef, inDate );
				}
			} else {
				// Invalid format
				throw new ParseException ( "Invalid date format '" + inDate + "'",
				    inDate );
			}
		} else {
			// Just date, no time
			dateOnly = true;
		}
	}

	/**
	 * Does the date contain a time components?
	 * 
	 * @return true if the Date contains a time components
	 */
	public boolean hasTime () {
		return ( dateOnly == false );
	}

	/**
	 * Generate the iCalendar string for this Date.
	 */
	public String toIcal () {
		StringBuffer sb = new StringBuffer ( dateOnly ? 8 : 15 );
		sb.append ( year );
		if ( month < 10 )
			sb.append ( '0' );
		sb.append ( month );
		if ( day < 10 )
			sb.append ( '0' );
		sb.append ( day );

		if ( !dateOnly ) {
			sb.append ( 'T' );
			if ( hour < 10 )
				sb.append ( '0' );
			sb.append ( hour );
			if ( minute < 10 )
				sb.append ( '0' );
			sb.append ( minute );
			if ( second < 10 )
				sb.append ( '0' );
			sb.append ( second );
			if ( isUTC )
				sb.append ( 'Z' );
		}
		value = sb.toString ();
		return super.toIcal ();
	}

	// Test routine - will parse input string and then export back
	// into ical format.
	// Usage: java Date "DTSTAMP;20030701T000000Z"
	//   
	public static void main ( String args[] ) {
		for ( int i = 0; i < args.length; i++ ) {
			try {
				java.io.File f = new java.io.File ( args[i] );
				Date a = null;
				String input = null;
				if ( f.exists () ) {
					try {
						input = Utils.getFileContents ( f );
					} catch ( Exception e ) {
						System.err.println ( "Error opening " + f + ": " + e );
						System.exit ( 1 );
					}
				} else {
					input = args[i];
				}
				a = new Date ( input, PARSE_STRICT );
				System.out.println ( "Date input:\n  " + args[i] );
				System.out.println ( "\nDate text:\n" + a.value );
				System.out.println ( "\nDate output:\n  " + a.toIcal () );
			} catch ( ParseException e ) {
				System.err.println ( "iCalendar Parse Exception: " + e );
			} catch ( BogusDataException e2 ) {
				System.err.println ( "iCalendar Data Exception: " + e2 );
			}
		}
	}

}
