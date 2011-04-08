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
 * iCalendar Timezone class NOTE: not yet implemented!
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Timezone {
	public String tzid = null;
	public String lastMod = null;
	public String tzOffsetFrom = null;
	public String tzOffsetTo = null;
	public String tzName = null;

	public Timezone(CalendarParser parser, int initialLine, Vector<String> textLines) {
		// TODO
	}

	public void parseLine ( String icalStr, int parseMethod )
	    throws ParseException, BogusDataException {
		// TODO

		// so we can compile :-)
		boolean x = false;
		if ( x )
			throw new ParseException ( "TODO!", "XXX" );
		if ( x )
			throw new BogusDataException ( "TODO!", "XXX" );
	}

	public boolean isValid () {
		return false;
	}

}
