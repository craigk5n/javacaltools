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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

/**
 * iCalendar Utility class
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Utils implements Constants {

	static public String getFileContents ( File f ) throws IOException,
	    FileNotFoundException {
		StringBuffer ret = new StringBuffer ();
		BufferedReader input = null;
		input = new BufferedReader ( new FileReader ( f ) );
		String line = null;
		while ( ( line = input.readLine () ) != null ) {
			ret.append ( line );
			ret.append ( CRLF );
		}
		if ( input != null ) {
			input.close ();
		}
		return ret.toString ();
	}

	/**
	 * Convert a java.util.Calendar object to a YYYYMMDD String.
	 * 
	 * @param inDate
	 *          Date to convert
	 * @return The Date as a String in YYYYMMDD format
	 */
	public static String CalendarToYYYYMMDD ( Calendar inDate ) {
		StringBuffer ret = new StringBuffer ( 8 );
		ret.append ( inDate.get ( Calendar.YEAR ) );
		if ( inDate.get ( Calendar.MONTH ) + 1 < 10 )
			ret.append ( '0' );
		ret.append ( ( inDate.get ( Calendar.MONTH ) + 1 ) );
		if ( inDate.get ( Calendar.DAY_OF_MONTH ) < 10 )
			ret.append ( '0' );
		ret.append ( inDate.get ( Calendar.DAY_OF_MONTH ) );
		return ret.toString ();
	}

	/**
	 * Convert a Date object to a YYYYMMDD String.
	 * 
	 * @param inDate
	 *          Date to convert
	 * @return The Date as a String in YYYYMMDD format
	 */
	public static String DateToYYYYMMDD ( Date inDate ) {
		StringBuffer ret = new StringBuffer ( 8 );
		ret.append ( inDate.year );
		if ( inDate.month < 10 )
			ret.append ( '0' );
		ret.append ( inDate.month );
		if ( inDate.day < 10 )
			ret.append ( '0' );
		ret.append ( inDate.day );
		return ret.toString ();
	}

	/**
	 * Convert a YYYYMMDD String into a java.util.Calendar object. Possible
	 * formats include:
	 * <ul>
	 * <li> 20001231 (date only, no time) </li>
	 * <li> 20001231T235900 (date with local time) </li>
	 * <li> 20001231T235900Z (date with utc time) </li>
	 * </ul>
	 * 
	 * @param inDate
	 *          Date to convert
	 * @return The Date as a Calendar
	 */
	public static Calendar YYYYMMDDToCalendar ( String inDate )
	    throws BogusDataException, ParseException {
		Calendar ret = Calendar.getInstance ();
		if ( inDate.length () < 8 )
			throw new BogusDataException ( "Invalid date format '" + inDate + "'",
			    inDate );
		int year = Integer.parseInt ( inDate.substring ( 0, 4 ) );
		int month = Integer.parseInt ( inDate.substring ( 4, 6 ) );
		int day = Integer.parseInt ( inDate.substring ( 6, 8 ) );
		// TODO: validate for each month and leap years, too
		// TODO: can java.util.Calendar handle dates before 1970 and after 2038?
		if ( day < 1 || day > 31 || month < 1 || month > 12 )
			throw new BogusDataException ( "Invalid date '" + inDate + "'", inDate );
		// TODO: parse time, handle localtime, handle timezone
		if ( inDate.length () > 8 ) {
			if ( inDate.charAt ( 8 ) == 'T' ) {
				try {
					int hour = Integer.parseInt ( inDate.substring ( 9, 11 ) );
					int minute = Integer.parseInt ( inDate.substring ( 11, 13 ) );
					int second = Integer.parseInt ( inDate.substring ( 13, 15 ) );
					ret.set ( year, month, day, hour, minute, second );
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
			ret.set ( year, month, day );
		}
		return ret;
	}

	/**
	 * Get the date for the first day of the week for the specified date.
	 */
	public static Calendar startOfWeek ( Calendar cal, boolean sundayStartsWeek ) {
		int dow = sundayStartsWeek ? Calendar.SUNDAY : Calendar.MONDAY;
		Calendar ret = (Calendar) cal.clone ();
		while ( ret.get ( Calendar.DAY_OF_WEEK ) != dow ) {
			ret.add ( Calendar.DATE, -1 );
		}
		return ret;
	}

	/**
	 * Get the date for the first day of the week for the specified date.
	 */
	public static Calendar endOfWeek ( Calendar cal, boolean sundayStartsWeek ) {
		int dow = sundayStartsWeek ? Calendar.SATURDAY : Calendar.SUNDAY;
		Calendar ret = (Calendar) cal.clone ();
		while ( ret.get ( Calendar.DAY_OF_WEEK ) != dow ) {
			ret.add ( Calendar.DATE, 1 );
		}
		return ret;
	}

	/**
	 * Get the day of the week (0=Sun to 6=Sat) for any specified date.
	 * 
	 * @param y
	 *          year (NNNN format, > 0)
	 * @param m
	 *          month (1=Jan)
	 * @param d
	 *          Day of month
	 * @return
	 */
	public static int getDayOfWeek ( int y, int m, int d ) {
		int[] month = { -1, 0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4 };

		if ( m == 1 )
			y--;
		if ( m == 2 )
			y--;

		int wday = ( ( d + month[m] + y + ( y / 4 ) - ( y / 100 ) + ( y / 400 ) ) % 7 );
		return wday;
	}

	/**
	 * Get the weekday for the first day of the year (Jan 1)
	 * 
	 * @param y
	 *          The 4-digit year
	 * @return The weekday (0=sunday)
	 */
	public static int getFirstDayOfWeekForYear ( int y ) {
		try {
			Date d = new Date ( "FOOBAR", y, 1, 1 );
			return getDayOfWeek ( d.getYear (), d.getMonth (), d.getDay () );
		} catch ( BogusDataException e1 ) {
			e1.printStackTrace ();
			return -1;
		}
	}

	/**
	 * Get the weekday for the last day of the year (Dec 31)
	 * 
	 * @param y
	 *          The 4-digit year
	 * @return The weekday (0=sunday)
	 */
	public static int getLastDayOfWeekForYear ( int y ) {
		try {
			Date d = new Date ( "FOOBAR", y, 12, 31 );
			return getDayOfWeek ( d.getYear (), d.getMonth (), d.getDay () );
		} catch ( BogusDataException e1 ) {
			e1.printStackTrace ();
			return -1;
		}
	}

}
