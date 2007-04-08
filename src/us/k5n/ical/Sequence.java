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
 * iCalendar Sequence class - This object represents a uid and corresponds to
 * the SEQUENCE iCalendar property.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Sequence extends Property {
	int num = 0;

	/**
	 * Constructor
	 */
	public Sequence() {
		super ( "SEQUENCE", "" );
	}

	/**
	 * Constructor
	 * 
	 * @param num
	 *          Initial sequence number (typically 0)
	 */
	public Sequence(int num) {
		super ( "SEQUENCE", "" );
		this.num = num;
		this.value = "" + num;
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies an event/todo uid
	 */
	public Sequence(String icalStr) throws ParseException {
		this ( icalStr, PARSE_LOOSE );
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies the unique
	 *          identifier
	 * @param parseMode
	 *          PARSE_STRICT or PARSE_LOOSE
	 */
	public Sequence(String icalStr, int parseMode) throws ParseException {
		super ( icalStr, parseMode );

		// SEQUENCE cannot have any attributes
		for ( int i = 0; i < attributeList.size (); i++ ) {
			Attribute a = attributeAt ( i );
			// Only generate exception if strict parsing
			if ( parseMode == PARSE_STRICT ) {
				throw new ParseException ( "Invalid SEQUENCE attribute '" + a.name
				    + "'", icalStr );
			}
		}
		try {
			num = Integer.parseInt ( value );
		} catch ( NumberFormatException e ) {
			throw new ParseException ( "Invalid SEQUENCE value '" + value + "'",
			    icalStr );
		}
	}

	/**
	 * Increment the sequence number.
	 */
	public void increment () {
		num++;
		value = "" + num;
	}

	// Test routine - will parse input string and then export back
	// into ical format.
	// Usage: java Sequence "SEQUENCE:1"
	//   
	public static void main ( String args[] ) {
		for ( int i = 0; i < args.length; i++ ) {
			try {
				java.io.File f = new java.io.File ( args[i] );
				Sequence a = null;
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
				a = new Sequence ( input, PARSE_STRICT );
				System.out.println ( "Sequence input:\n  " + args[i] );
				System.out.println ( "\nSequence text:\n" + a.value );
				System.out.println ( "\nSequence output:\n  " + a.toIcal () );
			} catch ( ParseException e ) {
				System.err.println ( "iCalendar Parse Exception: " + e );
			}
		}
	}

}
