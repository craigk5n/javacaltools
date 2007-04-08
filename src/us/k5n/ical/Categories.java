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
 * iCalendar Categories class - This object represents a category list and
 * corresponds to the CATEGORIES iCalendar property.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Categories extends Property {
	/** Language specification */
	public String language = null;

	// TODO provide an API to parse through the comma-separated list
	// of category name

	/**
	 * Constructor
	 */
	public Categories() {
		super ( "CATEGORIES", "" );
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies an event/todo
	 *          description
	 */
	public Categories(String icalStr) throws ParseException {
		this ( icalStr, PARSE_LOOSE );
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies a category list
	 *          (comma separated)
	 * @param parseMode
	 *          PARSE_STRICT or PARSE_LOOSE
	 */
	public Categories(String icalStr, int parseMode) throws ParseException {
		super ( icalStr, parseMode );

		for ( int i = 0; i < attributeList.size (); i++ ) {
			Attribute a = attributeAt ( i );
			String aval = a.value.toUpperCase ();
			if ( aval.equals ( "LANGUAGE" ) ) {
				// Can only have one of these
				if ( language != null && parseMode == PARSE_STRICT ) {
					throw new ParseException ( "More than one LANGUAGE found", icalStr );
				}
				language = a.value;
			} else {
				// Only generate exception if strict parsing
				if ( parseMode == PARSE_STRICT ) {
					throw new ParseException ( "Invalid CATEGORIES attribute '" + a.name
					    + "'", icalStr );
				}
			}
		}
	}

	// Test routine - will parse input string and then export back
	// into ical format.
	// Usage: java Categories "DESCRIPTION;LANGUAGE=EN:This is\\na test."
	//   
	public static void main ( String args[] ) {
		for ( int i = 0; i < args.length; i++ ) {
			try {
				java.io.File f = new java.io.File ( args[i] );
				Categories a = null;
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
				a = new Categories ( input, PARSE_STRICT );
				System.out.println ( "Categories input:\n  " + args[i] );
				System.out.println ( "\nCategories text:\n" + a.value );
				System.out.println ( "\nCategories output:\n  " + a.toICalendar () );
			} catch ( ParseException e ) {
				System.err.println ( "iCalendar Parse Exception: " + e );
			}
		}
	}

}
