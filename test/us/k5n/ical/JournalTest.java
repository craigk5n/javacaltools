package us.k5n.ical;

import java.io.StringReader;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Journal.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class JournalTest extends TestCase implements Constants {
	ICalendarParser parser;
	DataStore ds;
	String header = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//FOO//BAR//EN\n"
	    + "METHOD:PUBLISH\nBEGIN:VJOURNAL\n";
	String trailer = "END:VJOURNAL\nEND:VCALENDAR\n";

	public void setUp () {
		parser = new ICalendarParser ( PARSE_STRICT );
		ds = parser.getDataStoreAt ( 0 );
	}

	public void testOne () {
		String x = header
		    + "SUMMARY:Test Journal Entry\nDESCRIPTION:This is the description\n"
		    + "DTSTAMP:20060501\nUID:journaltest1@k5n.us\n" + trailer;
		StringReader reader = new StringReader ( x );
		try {
			boolean ret = parser.parse ( reader );
			assertTrue ( "Failed to parse", ret );
			Vector journals = ds.getAllJournals ();
			assertTrue ( "Journal not found in db", journals.size () > 0 );
			Journal j = (Journal) journals.elementAt ( 0 );
			assertTrue ( "Journal entry not valid", j.isValid () );
			Date d = j.dtstamp;
			assertTrue ( "Journal has time", d.isDateOnly () );
			assertTrue ( "Incorrect year", d.year == 2006 );
			assertTrue ( "Incorrect month", d.month == 5 );
			assertTrue ( "Incorrect day", d.day == 1 );
			assertTrue ( "Null summary", j.summary != null );
			assertTrue ( "Incorrect summary", j.summary.value
			    .equals ( "Test Journal Entry" ) );
			assertTrue ( "Null description", j.description != null );
			assertTrue ( "Incorrect description", j.description.value
			    .equals ( "This is the description" ) );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( JournalTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( JournalTest.class );
	}

}
