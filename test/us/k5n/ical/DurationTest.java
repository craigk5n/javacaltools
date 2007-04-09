package us.k5n.ical;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Duration.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class DurationTest extends TestCase implements Constants {

	public void setUp () {
	}

	// 1 hour test
	public void test001 () {
		String str = "DURATION:+PT1H";
		try {
			Duration x = new Duration ( str, PARSE_STRICT );
			assertNotNull ( "Null Duration", x );
			assertTrue ( "Incorrect value: " + x.duration, x.duration == 3600 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// 90 minute test
	public void test002 () {
		String str = "DURATION:+PT1H30M";
		try {
			Duration x = new Duration ( str, PARSE_STRICT );
			assertNotNull ( "Null Duration", x );
			assertTrue ( "Incorrect value: " + x.duration, x.duration == 5400 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// 1hr, 30 min, 15 sec minute test
	public void test003 () {
		String str = "DURATION:+PT1H30M15S";
		try {
			Duration x = new Duration ( str, PARSE_STRICT );
			assertNotNull ( "Null Duration", x );
			assertTrue ( "Incorrect value: " + x.duration, x.duration == 5415 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// 1 day
	public void test004 () {
		String str = "DURATION:+P1D";
		try {
			Duration x = new Duration ( str, PARSE_STRICT );
			assertNotNull ( "Null Duration", x );
			assertTrue ( "Incorrect value: " + x.duration, x.duration == 86400 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// 1 day, 2 hours
	public void test005 () {
		String str = "DURATION:+P1DT2H";
		try {
			Duration x = new Duration ( str, PARSE_STRICT );
			assertNotNull ( "Null Duration", x );
			assertTrue ( "Incorrect value: " + x.duration, x.duration == 93600 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// 1 week, 1 day
	public void test006 () {
		String str = "DURATION:+P1W2D";
		try {
			Duration x = new Duration ( str, PARSE_STRICT );
			assertNotNull ( "Null Duration", x );
			assertTrue ( "Incorrect value: " + x.duration, x.duration == 777600 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// 15 days, 5 hours and 20 seconds
	// (example from RFC 2445 spec)
	public void test007 () {
		String str = "DURATION:P15DT5H0M20S";
		try {
			Duration x = new Duration ( str, PARSE_STRICT );
			assertNotNull ( "Null Duration", x );
			assertTrue ( "Incorrect value: " + x.duration, x.duration == 1314020 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( DurationTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( DurationTest.class );
	}

}
