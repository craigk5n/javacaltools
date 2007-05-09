package us.k5n.ical;

import java.util.Calendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Date.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class DateTest extends TestCase implements Constants {

	public void setUp () {
	}

	/**
	 * Test valid detection of no time specified.
	 */
	public void testOne () {
		String dateStr = "DATE:20070131";
		try {
			Date d = new Date ( dateStr, PARSE_STRICT );
			assertTrue ( "Date has time", d.isDateOnly () );
			assertTrue ( "Incorrect year", d.year == 2007 );
			assertTrue ( "Incorrect month", d.month == 1 );
			assertTrue ( "Incorrect day", d.day == 31 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	/**
	 * Test valid detection with time specified.
	 */
	public void testTwo () {
		String dateStr = "DTSTART:20070131T132047";
		try {
			Date d = new Date ( dateStr, PARSE_STRICT );
			assertFalse ( "Date has no time", d.isDateOnly () );
			assertTrue ( "Incorrect year", d.year == 2007 );
			assertTrue ( "Incorrect month", d.month == 1 );
			assertTrue ( "Incorrect day", d.day == 31 );
			assertTrue ( "Incorrect hour: " + d.hour, d.hour == 13 );
			assertTrue ( "Incorrect minute: " + d.minute, d.minute == 20 );
			assertTrue ( "Incorrect second: " + d.second, d.second == 47 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	/**
	 * Make sure invalid dates are caught.
	 */
	public void testThree () {
		String[] badDates = { "20011301", "20010132", "19990012", "19991200",
		    "19990132", "19990229", "20010230", "20010332", "20010431", "20010532",
		    "20010631", "20010732", "20010832", "20010931", "20021032", "20021131",
		    "20021232" };
		for ( int i = 0; i < badDates.length; i++ ) {
			String dStr = badDates[i];
			try {
				Date d = new Date ( "DATE:" + dStr );
				fail ( "Did not catch invalid date '" + dStr + "'" );
			} catch ( BogusDataException e ) {
				// passed!
			} catch ( Exception e2 ) {
				fail ( "Caught exception: " + e2.getMessage () );
			}
		}
	}

	/**
	 * Make sure all valid dates are allowed.
	 */
	public void testFour () {
		Calendar c = Calendar.getInstance ();

		c.set ( Calendar.YEAR, 2000 );
		c.set ( Calendar.MONTH, 1 );
		c.set ( Calendar.DAY_OF_MONTH, 1 );

		// leap year
		for ( int i = 1; i <= 365; i++ ) {
			c.set ( Calendar.DAY_OF_YEAR, i );
			c.getTimeInMillis ();
			String str = "DATE:" + Utils.CalendarToYYYYMMDD ( c );
			// System.out.println ( str );
			try {
				Date d = new Date ( str );
				// passed
			} catch ( BogusDataException e ) {
				fail ( "Did not allow valid date '" + str + "'" );
				// passed!
			} catch ( Exception e2 ) {
				fail ( "Caught exception: " + e2.getMessage () );
			}
		}

		// non-leap year
		c.set ( Calendar.YEAR, 2001 );
		c.set ( Calendar.MONTH, 1 );
		c.set ( Calendar.DAY_OF_MONTH, 1 );
		for ( int i = 1; i <= 366; i++ ) {
			c.set ( Calendar.DAY_OF_YEAR, i );
			c.getTimeInMillis ();
			String str = "DATE:" + Utils.CalendarToYYYYMMDD ( c );
			// System.out.println ( str );
			try {
				Date d = new Date ( str );
				// passed
			} catch ( BogusDataException e ) {
				fail ( "Did not allow valid date '" + str + "'" );
				// passed!
			} catch ( Exception e2 ) {
				fail ( "Caught exception: " + e2.getMessage () );
			}
		}
	}

	public void testFive () {
		String dateStr = "DATE:20070131";
		try {
			Date d = new Date ( dateStr, PARSE_STRICT );
			String ical = d.toICalendar ().trim ();
			assertTrue ( "Incorrect ical output: " + ical,
			    ical.indexOf ( "20070131" ) >= 0 );
			Date newDate = new Date ( d.toICalendar (), PARSE_STRICT );
			assertTrue ( "Dates not equal: " + d + " vs " + newDate, d
			    .equals ( newDate ) );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public void testSix () {
		String dateStr = "DATE:20070131T115959";
		try {
			Date d = new Date ( dateStr, PARSE_STRICT );
			Date newDate = new Date ( d.toICalendar (), PARSE_STRICT );
			assertTrue ( "Dates not equal: " + d + " vs " + newDate, d
			    .equals ( newDate ) );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public void testSeven () {
		String dateStr = "DTSTART;VALUE=\"DATE\":20070413T091945";
		try {
			Date d = new Date ( dateStr, PARSE_STRICT );
			Date newDate = new Date ( d.toICalendar (), PARSE_STRICT );
			assertTrue ( "Dates not equal: " + d.toICalendar () + " vs "
			    + newDate.toICalendar (), d.equals ( newDate ) );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public void testEight () {
		String dateStr = "DTSTART;VALUE=\"DATE\":20070413T091945";
		try {
			Date d = new Date ( "DTSTART", 1999, 12, 31 );
			assertTrue ( "Date has time", d.isDateOnly () );
			d = new Date ( "DTSTART", 1999, 12, 31, 23, 59, 59 );
			assertTrue ( "Date has no time", !d.isDateOnly () );
			assertTrue ( "Wrong time", d.getHour () == 23 && d.getMinute () == 59
			    && d.getSecond () == 59 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public void testTimeZone () {
		String dateStr = "DTSTART;VALUE=\"DATE\";TZID=\""
		    + java.util.TimeZone.getDefault ().getID () + "\":20070901T120000";
		try {
			// System.out.println ( "Input: " + dateStr );
			Date d = new Date ( dateStr, PARSE_STRICT );
			assertTrue ( "Date has time", d.isDateOnly () );
			assertTrue ( "Wrong time", d.getHour () == 12 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( DateTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( DateTest.class );
	}

}
