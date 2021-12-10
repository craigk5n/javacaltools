package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Attachment.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class AttachmentTest implements Constants {

  @Test
  public void test01() {
    try {
      Attachment x = new Attachment(new File("src/test/resources/plaintext.txt"), "text/plain");
      // System.out.println ( "Attachment:\n\n" + x.toICalendar () );
      String val = x.getValue();
      assertTrue(val != null);
      assertTrue(val.length() > 0);
      byte[] bytes = new byte[val.length()];
      char[] chars = val.toCharArray();
      for (int i = 0; i < chars.length; i++) {
        bytes[i] = (byte) chars[i];
      }
      byte[] decoded = Base64.decodeBase64(bytes);
      String decStr = new String(decoded);
      assertTrue(decStr.indexOf("How now brown cow") >= 0);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  @Test
  public void test02() {
    ICalendarParser parser;
    DataStore ds;
    File f = new File("src/test/resources/Attachment1.ics");
    try {
      parser = new ICalendarParser(PARSE_STRICT);
      ds = parser.getDataStoreAt(0);
      BufferedReader reader = null;
      if (f.exists()) {
        try {
          reader = new BufferedReader(new FileReader(f));
          parser.parse(reader);
          reader.close();
        } catch (IOException e) {
          System.err.println("Error opening " + f + ": " + e);
          fail("Error opening " + f + ": " + e);
        }
      } else {
        System.err.println("Could not find test file: " + f);
        fail("Could not find test file: " + f);
      }
      Event event = (Event) ds.getAllEvents().get(0);
      assertTrue(event.getAttachments() != null);
      Attachment x = (Attachment) event.getAttachments().get(0);
      String val = x.getValue();
      assertTrue(val != null);
      assertTrue(val.length() > 0);
      byte[] bytes = new byte[val.length()];
      char[] chars = val.toCharArray();
      for (int i = 0; i < chars.length; i++) {
        bytes[i] = (byte) chars[i];
      }
      byte[] decoded = Base64.decodeBase64(bytes);
      String decStr = new String(decoded);
      // System.out.println ( "\n\nDecoded:\n" + decStr );
      assertTrue(decStr.indexOf("How now brown cow") >= 0);
      // Get binary data
      bytes = x.getBytes();
      assertNotNull(bytes);
      decStr = new String(bytes);
      assertTrue(decStr.indexOf("How now brown cow") >= 0);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

}
