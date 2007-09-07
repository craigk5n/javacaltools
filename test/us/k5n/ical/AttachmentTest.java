package us.k5n.ical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.codec.binary.Base64;

/**
 * Test cases for Attachment.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class AttachmentTest extends TestCase implements Constants {

	public void setUp () {
	}

	public void test01 () {
		try {
			Attachment x = new Attachment ( new File ( "test/data/plaintext.txt" ),
			    "text/plain" );
			//System.out.println ( "Attachment:\n\n" + x.toICalendar () );
			String val = x.getValue ();
			assertTrue ( "getValue is null", val != null );
			assertTrue ( "getValue is length 0", val.length () > 0 );
			byte[] bytes = new byte[val.length ()];
			char[] chars = val.toCharArray ();
			for ( int i = 0; i < chars.length; i++ ) {
				bytes[i] = (byte) chars[i];
			}
			byte[] decoded = Base64.decodeBase64 ( bytes );
			String decStr = new String ( decoded );
			//System.out.println ( "\n\nDecoded:\n" + decStr );
			assertTrue ( "\"How now brown cow\" not found", decStr
			    .indexOf ( "How now brown cow" ) >= 0 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public void test02 () {
		ICalendarParser parser;
		DataStore ds;
		File f = new File ( "test/data/Attachment1.ics" );
		try {
			parser = new ICalendarParser ( PARSE_STRICT );
			ds = parser.getDataStoreAt ( 0 );
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
			Event event = (Event) ds.getAllEvents ().elementAt ( 0 );
			assertTrue ( "Attachment not found", event.getAttachments () != null );
			Attachment x = (Attachment) event.getAttachments ().elementAt ( 0 );
			String val = x.getValue ();
			assertTrue ( "getValue is null", val != null );
			assertTrue ( "getValue is length 0", val.length () > 0 );
			byte[] bytes = new byte[val.length ()];
			char[] chars = val.toCharArray ();
			for ( int i = 0; i < chars.length; i++ ) {
				bytes[i] = (byte) chars[i];
			}
			byte[] decoded = Base64.decodeBase64 ( bytes );
			String decStr = new String ( decoded );
			//System.out.println ( "\n\nDecoded:\n" + decStr );
			assertTrue ( "\"How now brown cow\" not found", decStr
			    .indexOf ( "How now brown cow" ) >= 0 );
			// Get binary data
			bytes = x.getBytes ();
			assertNotNull ( "Null attachment bytes", bytes );
			decStr = new String ( bytes );
			assertTrue ( "\"How now brown cow\" not found", decStr
			    .indexOf ( "How now brown cow" ) >= 0 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( AttachmentTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( AttachmentTest.class );
	}

}
