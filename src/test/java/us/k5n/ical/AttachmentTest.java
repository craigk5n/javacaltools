package us.k5n.ical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Attachment.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class AttachmentTest implements Constants {

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void test01() {
        try {
            // Load the file from the resources directory
            File file = new File(getClass().getClassLoader().getResource("plaintext.txt").getFile());
            // Create an Attachment object using the file
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

    @Test
    public void test02() {
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