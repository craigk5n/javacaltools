package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Attachment;
import us.k5n.ical.Constants;
import us.k5n.ical.DataStore;
import us.k5n.ical.Event;
import us.k5n.ical.ICalendarParser;

/**
 * RFC 5545 Section 3.8.1.1: ATTACH Property Tests
 *
 * Tests for the ATTACH property as defined in RFC 5545.
 * The ATTACH property provides the capability to associate a document object
 * with a calendar component.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.1.1: ATTACH Property")
public class AttachmentPropertyTest implements Constants {

  @Nested
  @DisplayName("File-based Attachment Tests")
  class FileBasedAttachmentTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.1: should create attachment from file with Base64 encoding")
    void should_createBase64Attachment_when_fileProvided() {
      try {
        File file = new File(getClass().getClassLoader().getResource("plaintext.txt").getFile());
        Attachment x = new Attachment(file, "text/plain");
        String val = x.getValue();
        assertNotNull(val, "getValue is null");
        assertTrue(val.length() > 0, "getValue is length 0");

        byte[] bytes = new byte[val.length()];
        char[] chars = val.toCharArray();
        for (int i = 0; i < chars.length; i++) {
          bytes[i] = (byte) chars[i];
        }

        byte[] decoded = Base64.decodeBase64(bytes);
        String decStr = new String(decoded);
        assertTrue(decStr.contains("How now brown cow"), "\"How now brown cow\" not found");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("iCalendar Attachment Parsing Tests")
  class ICalendarParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.1: should parse inline Base64 attachment from ICS file")
    void should_parseInlineAttachment_when_icsFileProvided() {
      ICalendarParser parser;
      DataStore ds;
      File f = new File(getClass().getClassLoader().getResource("Attachment1.ics").getFile());
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

        Event event = ds.getAllEvents().get(0);
        assertNotNull(event.getAttachments(), "Attachment not found");

        Attachment x = event.getAttachments().get(0);
        String val = x.getValue();
        assertNotNull(val, "getValue is null");
        assertTrue(val.length() > 0, "getValue is length 0");

        byte[] bytes = new byte[val.length()];
        char[] chars = val.toCharArray();
        for (int i = 0; i < chars.length; i++) {
          bytes[i] = (byte) chars[i];
        }

        byte[] decoded = Base64.decodeBase64(bytes);
        String decStr = new String(decoded);
        assertTrue(decStr.contains("How now brown cow"), "\"How now brown cow\" not found");

        // Get binary data
        bytes = x.getBytes();
        assertNotNull(bytes, "Null attachment bytes");
        decStr = new String(bytes);
        assertTrue(decStr.contains("How now brown cow"), "\"How now brown cow\" not found");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed: " + e.toString());
      }
    }
  }
}
