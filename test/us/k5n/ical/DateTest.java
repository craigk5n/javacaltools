package us.k5n.ical;

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
			assertFalse ( "Date has time", d.hasTime () );
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
		String dateStr = "DATE:20070131T132047";
		try {
			Date d = new Date ( dateStr, PARSE_STRICT );
			assertTrue ( "Date has no time", d.hasTime () );
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
	 * Make sure invalid dates are caught. TODO: add support for leap year
	 * checking once error-checking is added to Date.
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

	public static Test suite () {
		return new TestSuite ( DateTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( DateTest.class );
	}

}
