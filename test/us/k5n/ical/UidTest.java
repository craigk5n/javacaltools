package us.k5n.ical;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Uid.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class UidTest extends TestCase implements Constants {

	public void setUp () {
	}

	public void testOne () {
		String str = "UID:SFSDFSDFSDF-SDFDFSDFSD@xxx.com";
		try {
			Uid x = new Uid ( str, PARSE_STRICT );
			assertNotNull ( "Null Uid", x );
			assertTrue ( "Wrong value", x.value
			    .equals ( "SFSDFSDFSDF-SDFDFSDFSD@xxx.com" ) );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( UidTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( UidTest.class );
	}

}
