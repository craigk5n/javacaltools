package us.k5n.ical;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Summary.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class SummaryTest extends TestCase implements Constants {

	public void setUp () {
	}

	public void testOne () {
		String str = "SUMMARY;LANGUAGE=EN:This is\\na summary.";
		try {
			Description x = new Description ( str, PARSE_STRICT );
			assertNotNull ( "Null Description", x );
			assertNotNull ( "Null langauge", x.language );
			assertNotNull ( "Null text", x.value );
			assertTrue ( "Wrong text", x.value.startsWith ( "This is" ) );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( SummaryTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( SummaryTest.class );
	}

}
