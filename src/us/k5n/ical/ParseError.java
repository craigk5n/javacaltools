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
 * A single parse error.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class ParseError {
	/** Line number (if available) */
	public int lineNo;
	/** Error message */
	public String error;
	/** Offending iCalendar data */
	public String icalStr;

	/**
	 * Create a parse error
	 * 
	 * @param lineNo
	 *          Line number of error (if available)
	 * @param error
	 *          Error message
	 * @param icalStr
	 *          Offending iCalendar data
	 */
	public ParseError(int lineNo, String error, String icalStr) {
		this.lineNo = lineNo;
		this.error = error;
		this.icalStr = icalStr;
	}

	/**
	 * Convert the error to a String.
	 */
	public String toString () {
		return toString ( 0 );
	}

	/**
	 * Convert the error to a String.
	 * 
	 * @param indent
	 *          The number of spaces to indent each line
	 */
	public String toString ( int indent ) {
		StringBuffer ret = new StringBuffer ();
		String ind = "";
		for ( int i = 0; i < indent; i++ )
			ind += " ";

		ret.append ( ind );
		ret.append ( "Error  : " );
		ret.append ( error );
		ret.append ( "\n" );

		ret.append ( ind );
		ret.append ( "Line No: " );
		ret.append ( lineNo );
		ret.append ( "\n" );

		ret.append ( ind );
		ret.append ( "Input  : " );
		ret.append ( icalStr );
		ret.append ( "\n" );

		return ret.toString ();

	}

}
