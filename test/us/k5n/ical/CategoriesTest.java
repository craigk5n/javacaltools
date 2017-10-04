package us.k5n.ical;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Categories.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class CategoriesTest extends TestCase implements Constants {

	public void setUp () {
	}

	public void testOne () {
		String str = "CATEGORIES:Home,Personal";
		try {
			Categories c = new Categories ( str, PARSE_STRICT );
			System.out.println ( "ICAL: " + c.toICalendar () );
			assertNotNull ( "Null categories", c );
			// TODO: add code for getting categories once it
			// is added to Categories.java.
		} catch ( ParseException e ) {
			fail ( "ParseException: " + e.getMessage () );
		}
	}

	public static Test suite () {
		return new TestSuite ( CategoriesTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( CategoriesTest.class );
	}

}
