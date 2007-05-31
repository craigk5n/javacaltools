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
 * iCalendar Location class - This object represents a URL and corresponds to
 * the URL iCalendar property. <br/> From the RFC: <br/> <quote>
 * Purpose: The property defines the intended venue for the activity
 * defined by a calendar component.
 * <br/>
 * Value Type: TEXT
 * <br/>
 * Property Parameters: Non-standard, alternate text representation and
 * language property parameters can be specified on this property.
 * <br/>
 * Conformance: This property can be specified in "VEVENT" or "VTODO"
 * calendar component.
 * <br/>
 * Description: Specific venues such as conference or meeting rooms may
 * be explicitly specified using this property. An alternate
 * representation may be specified that is a URI that points to
 * directory information with more structured specification of the
 * location. For example, the alternate representation may specify
 * either an LDAP URI pointing to an LDAP server entry or a CID URI
 * pointing to a MIME body part containing a vCard [RFC 2426] for the
 * location.
 * </quote>
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Location extends Property {
	/**
	 * Constructor
	 */
	public Location() {
		super ( "LOCATION", "" );
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies an event/todo URL
	 */
	public Location(String icalStr) throws ParseException {
		this ( icalStr, PARSE_LOOSE );
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies the unique
	 *          identifier.
	 * @param parseMode
	 *          PARSE_STRICT or PARSE_LOOSE
	 */
	public Location(String icalStr, int parseMode) throws ParseException {
		super ( icalStr, parseMode );
	}
}
