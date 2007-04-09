package us.k5n.ical;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Rrule.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class RruleTest extends TestCase implements Constants {

	public void setUp () {
	}

	public void testOne () {
		String str = "RRULE:FREQ=MONTHLY;BYMONTH=10;BYDAY=2MO";
		try {
			Rrule x = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", x );
			// TODO: add more tests...
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( RruleTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( RruleTest.class );
	}

}
