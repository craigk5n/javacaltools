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
 * iCalendar RELATED-TO class - This object represents a relationship to another
 * calendar component and corresponds to the RELATED-TO iCalendar property. <br/>
 * From RFC 5545: <br/>
 * The property can be specified in several components and implies
 * there is a relationship of some kind (for example, "child" or "sibling")
 * between the different calendar components specified by each UID parameter value.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class RelatedTo extends Property {
	/**
	 * Constructor
	 */
	public RelatedTo() {
		super ( "RELATED-TO", "" );
	}

	/**
	 * Constructor
	 *
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies a related component
	 */
	public RelatedTo(String icalStr) throws ParseException {
		this ( icalStr, PARSE_LOOSE );
	}

	/**
	 * Constructor
	 *
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies the related component
	 * @param parseMode
	 *          PARSE_STRICT or PARSE_LOOSE
	 */
	public RelatedTo(String icalStr, int parseMode) throws ParseException {
		super ( icalStr, parseMode );
	}

	/**
	 * Get the related component UID
	 *
	 * @return the UID of the related component
	 */
	public String getRelatedTo() {
		return value;
	}

	/**
	 * Set the related component UID
	 *
	 * @param uid the UID of the related component
	 */
	public void setRelatedTo(String uid) {
		this.value = uid;
	}
}