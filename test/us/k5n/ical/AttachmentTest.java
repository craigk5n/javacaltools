package us.k5n.ical;

import java.io.File;

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

	public void testOne () {
		try {
			Attachment x = new Attachment ( new File ( "test/data/plaintext.txt" ) );
			System.out.println ( "Attachment:\n\n" + x.toICalendar () );
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
			System.out.println ( "\n\nDecoded:\n" + decStr );
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
