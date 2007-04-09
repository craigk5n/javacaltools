package us.k5n.ical;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Attendee.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class AttendeeTest extends TestCase implements Constants {

	public void setUp () {
	}

	/**
	 * Test valid detection of no time specified.
	 */
	public void testOne () {
		String attendeeStr = "ATTENDEE;ROLE=CHAIR;CN=Joe Smith:MAILTO:joe@xxx.com";
		try {
			Attendee a = new Attendee ( attendeeStr, PARSE_STRICT );
			assertTrue ( "Attendee has wrong role", a.role == Attendee.ROLE_CHAIR );
			assertNotNull ( "Null CN", a.cn );
			//assertNotNull ( "Null name", a.name );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( AttendeeTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( AttendeeTest.class );
	}

}
