package us.k5n.ical;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Sequence.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class SequenceTest extends TestCase implements Constants {

	public void setUp () {
	}

	public void testOne () {
		String str = "SEQUENCE:1";
		try {
			Sequence x = new Sequence ( str, PARSE_STRICT );
			assertNotNull ( "Null Sequence", x );
			assertTrue ( "Incorrect num", x.num == 1 );
			x.increment ();
			assertTrue ( "Incorrect num after increment", x.num == 2 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( SequenceTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( SequenceTest.class );
	}

}
