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
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
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
			String aname = a.name.toUpperCase ();
			if ( aname.equals ( "LANGUAGE" ) ) {
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

}
