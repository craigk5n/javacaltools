package us.k5n.ical;

import java.util.Calendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Utils.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class UtilsTest extends TestCase implements Constants {

	public void setUp () {
	}

	public void testDayOfWeek () {
		int[] wdays = { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
		    Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY,
		    Calendar.SATURDAY };
		Calendar c = Calendar.getInstance ();
		c.setLenient ( true );
		// test date ranges of 1700 through 2200
		c.set ( Calendar.YEAR, 1700 );
		while ( true ) {
			int y = c.get ( Calendar.YEAR );
			int m = c.get ( Calendar.MONTH ) + 1;
			int d = c.get ( Calendar.DAY_OF_MONTH );
			int javaWeekday = c.get ( Calendar.DAY_OF_WEEK );
			int wday = Utils.getDayOfWeek ( y, m, d );
			assertTrue ( "Weekday mismatch for " + m + "/" + d + "/" + y + ": "
			    + "java weekday=" + javaWeekday + ", Utils weekday=" + wday,
			    javaWeekday == wdays[wday] );
			// increment date
			c.set ( Calendar.DAY_OF_YEAR, c.get ( Calendar.DAY_OF_YEAR ) + 1 );
			if ( y >= 2201 )
				break;
		}
	}
	
	public static Test suite () {
		return new TestSuite ( UtilsTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( UtilsTest.class );
	}

}
