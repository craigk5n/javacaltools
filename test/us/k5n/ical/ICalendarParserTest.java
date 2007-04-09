package us.k5n.ical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for ICalendarParser.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class ICalendarParserTest extends TestCase implements Constants {
	ICalendarParser parser;
	DataStore ds;

	public void setUp () {
		parser = new ICalendarParser ( PARSE_STRICT );
		ds = parser.getDataStoreAt ( 0 );
	}

	private void showErrors ( Vector errors ) {
		for ( int i = 0; i < errors.size (); i++ ) {
			ParseError err = (ParseError) errors.elementAt ( i );
			System.err.println ( "Error #" + ( i + 1 ) + " at line " + err.lineNo
			    + ": " + err.error + "\n  Data: " + err.icalStr );
		}
	}

	private void parseIcsFile ( File f ) {
		BufferedReader reader = null;
		if ( f.exists () ) {
			try {
				reader = new BufferedReader ( new FileReader ( f ) );
				parser.parse ( reader );
				reader.close ();
			} catch ( IOException e ) {
				System.err.println ( "Error opening " + f + ": " + e );
				fail ( "Error opening " + f + ": " + e );
			}
		} else {
			System.err.println ( "Could not find test file: " + f );
			fail ( "Could not find test file: " + f );
		}
	}

	public void testOne () {
		parseIcsFile ( new File ( "test/data/BadDate1.ics" ) );
		Vector errors = parser.getAllErrors ();
		assertTrue ( "Did not find bad date", errors.size () > 0 );
		Vector events = ds.getAllEvents ();
		assertTrue ( "Loaded bad event", events.size () == 0 );
	}

	public void testTwo () {
		parseIcsFile ( new File ( "test/data/SimpleDate1.ics" ) );
		Vector errors = parser.getAllErrors ();
		Vector events = ds.getAllEvents ();
		if ( errors.size () > 0 ) {
			showErrors ( errors );
			fail ( "Found errors in valid ICS file" );
		}
		assertTrue ( "Did not load valid event", events.size () == 1 );
		Event e = (Event) events.elementAt ( 0 );
		assertNotNull ( "Null event start date", e.startDate );
		assertTrue ( "Wrong event year: " + e.startDate.year,
		    e.startDate.year == 2007 );
		assertTrue ( "Wrong event month: " + e.startDate.month,
		    e.startDate.month == 4 );
		assertTrue ( "Wrong event day: " + e.startDate.day, e.startDate.day == 1 );
		assertTrue ( "Did not set date-only correctly",
		    e.startDate.hasTime () == false );
	}

	public static Test suite () {
		return new TestSuite ( ICalendarParserTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( ICalendarParserTest.class );
	}

}
