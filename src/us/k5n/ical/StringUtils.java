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
 * iCalendar StringFormatter utility class
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class StringUtils implements Constants {

	/**
	 * Format a string into ical folded-line format (which states that lines
	 * should not be longer than 75 characters, excluding the line break.)
	 * 
	 * @param x
	 *          The input string to be formatted
	 * @return The properly formatted string (includes newline)
	 */
	static public String foldLine ( String x ) {
		int len = 0;
		StringBuffer ret = new StringBuffer ( x.length () );
		for ( int i = 0; i < x.length (); i++ ) {
			if ( len >= MAX_LINE_LENGTH - 1 ) {
				ret.append ( CRLF );
				ret.append ( ' ' );
				len = 0;
			}
			if ( x.charAt ( i ) == CR ) {
				// Ignore
			} else if ( x.charAt ( i ) == LF ) {
				ret.append ( "\\n" );
				len += 2;
			} else {
				ret.append ( x.charAt ( i ) );
				len++;
			}
		}
		String r = ret.toString ();
		if ( !r.endsWith ( CRLF ) )
			r += CRLF;
		return r;
	}

	/**
	 * Unfold a multi-line iCalendar String into a single line. New line escape
	 * sequences will be converted back to CRLF (015 012). Rather than throwing an
	 * ParseException when lines longer than 75 characters are encountered, the
	 * error will be ignored for greater compatibility. Additionally, non-ASCII
	 * characters will be converted from their quoted-printable format.
	 * 
	 * @param line
	 *          Input iCalendar line
	 * @return The "unfolded" line
	 * @see foldLine
	 */
	static public String unfoldLine ( String line ) throws ParseException {
		return unfoldLine ( line, PARSE_LOOSE );
	}

	/**
	 * Unfold a multi-line iCalendar String into a single line. New line escape
	 * sequences will be converted back to CRLF (015 012). If a line is longer
	 * than 75 characters, a ParseException will be generated if parseMode is set
	 * to PARSE_STRICT. Additionally, non-ASCII characters will be converted from
	 * their quoted-printable format. If parseMode is not set to PARSE_STRICT,
	 * escaped commas "\," will be unescaped. (Mozilla Sunbird does this escaping
	 * which is not part of the RFC 2445 specification.)
	 * 
	 * @param line
	 *          Input iCalendar line
	 * @param parseMode
	 *          PARSE_STRICT or PARSE_LOOSE; PARSE_STRICT will generate an
	 *          exception on lines too long.
	 * @return The "unfolded" line
	 * @see foldLine
	 */
	// TODO: convert quotable-printable format
	static public String unfoldLine ( String line, int parseMode )
	    throws ParseException {
		int len = 0;
		StringBuffer ret = new StringBuffer ( line.length () );
		for ( int i = 0; i < line.length (); i++ ) {
			// check line length
			if ( len > MAX_LINE_LENGTH ) {
				if ( parseMode == PARSE_STRICT ) {
					throw new ParseException ( "Found line longer than "
					    + MAX_LINE_LENGTH + " limit", line );
				}
			}
			char ch = line.charAt ( i );
			char ch2 = ( i + 1 < line.length () ? line.charAt ( i + 1 ) : 0 );
			char ch3 = ( i + 2 < line.length () ? line.charAt ( i + 2 ) : 0 );
			if ( ch == '\\' && ch2 == 'n' && ch == '=' && parseMode == PARSE_LOOSE ) {
				// Convert "\\\n=" into CRLF
				// This is another Mozilla Sunbird hack
				ret.append ( CRLF );
				i++;
				len = 0;
			}
			if ( ch == '\\' && ch2 == 'n' ) {
				// Convert "\\\n" into CRLF
				ret.append ( CRLF );
				i++;
				len = 0;
			} else if ( ch == LF && ( ch2 == SPACE || ch2 == TAB ) ) {
				// unfold this...
				i++;
			} else if ( ch == '\\' && ch2 == ',' && parseMode == PARSE_LOOSE ) {
				// this is Mozilla's incorrect way of handling commas :-(
				ret.append ( ',' );
				i++;
			} else if ( ch == LF && ch2 == 0 ) {
				// end of data
			} else if ( ch == LF ) {
				// This should be a parse error. We should always find either
				// a space/tab after LF (folding) or it should be the end of
				// the data line.
				if ( parseMode == PARSE_STRICT ) {
					throw new ParseException ( "Invalid line termination at char " + i,
					    line );
				} else {
					// Oh, well... ignore it. Assume they forgot the space that
					// starts the next line.
				}
			} else if ( ch == CR ) {
				// ignore CR. Should be a LF next.
			} else if ( ch == '\\' && ch2 == 'r' ) {
				// ignore CR. Should be a LF next.
			} else {
				ret.append ( ch );
				len++;
			}
		}
		return ret.toString ();
	}

	/**
	 * Determine if a String is a whole number
	 */
	static public boolean isNumber ( String str, boolean allowSign ) {
		int i = 0;
		if ( str.length () == 0 )
			return false;

		if ( str.charAt ( 0 ) == '+' || str.charAt ( 0 ) == '-' )
			i = 1;
		for ( ; i < str.length (); i++ ) {
			if ( str.charAt ( i ) < '0' || str.charAt ( i ) > '9' )
				return false;
		}
		return true;
	}

	/**
	 * Determine if a String is a whole number
	 */
	static public boolean isNumber ( String str ) {
		return isNumber ( str, false );
	}
}
