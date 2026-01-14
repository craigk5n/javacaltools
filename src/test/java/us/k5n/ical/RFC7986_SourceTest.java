 package us.k5n.ical;

 import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 7986: SOURCE Property Tests
 *
 * Tests for the SOURCE property as defined in RFC 7986, Section 5.8.
 * SOURCE identifies a URI where calendar data can be refreshed from.
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
 public class RFC7986_SourceTest {

     @Test
     @DisplayName("RFC 7986: SOURCE basic parsing")
     void testSourceBasic() {
         String icalStr = "SOURCE;VALUE=URI:https://example.com/holidays.ics";

         try {
             Source source = new Source(icalStr);
             assertEquals("https://example.com/holidays.ics", source.getValue());
             assertTrue(source.isValidSource());
             assertEquals("https://example.com/holidays.ics", source.getUri());
         } catch (Exception e) {
             fail("SOURCE should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: SOURCE with different URI schemes")
     void testSourceDifferentSchemes() {
         String[] uris = {
             "https://example.com/calendar.ics",
             "http://example.com/calendar.ics",
             "ftp://example.com/calendar.ics",
             "file:///path/to/calendar.ics",
             "mailto:user@example.com",
             "data:text/plain;base64,SGVsbG8gV29ybGQ=",
             "urn:ietf:rfc:7986"
         };

         for (String uri : uris) {
             String icalStr = "SOURCE;VALUE=URI:" + uri;
             try {
                 Source source = new Source(icalStr);
                 assertEquals(uri, source.getValue(), "Should parse URI: " + uri);
                 assertTrue(source.isValidSource(), "Should be valid source: " + uri);
             } catch (Exception e) {
                 fail("SOURCE with URI '" + uri + "' should parse: " + e.getMessage());
             }
         }
     }

     @Test
     @DisplayName("RFC 7986: SOURCE serialization")
     void testSourceSerialization() {
         Source source = new Source();
         source.setValue("https://example.com/holidays.ics");

         String icalOutput = source.toICalendar();
         assertTrue(icalOutput.contains("SOURCE;VALUE=URI:https://example.com/holidays.ics"),
                   "Serialized output should contain the source URI");
     }

     @Test
     @DisplayName("RFC 7986: SOURCE in VCALENDAR")
     void testSourceInCalendar() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "SOURCE;VALUE=URI:https://example.com/holidays.ics\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "SOURCE in VCALENDAR should parse without errors");
         } catch (Exception e) {
             fail("SOURCE in VCALENDAR should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: SOURCE with parameters")
     void testSourceWithParameters() {
         String icalStr = "SOURCE;VALUE=URI;X-CUSTOM=value:https://example.com/calendar.ics";

         try {
             Source source = new Source(icalStr, ICalendarParser.PARSE_LOOSE);
             assertEquals("https://example.com/calendar.ics", source.getValue());
             assertTrue(source.isValidSource());
         } catch (Exception e) {
             fail("SOURCE with parameters should parse in loose mode: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: SOURCE invalid URI")
     void testSourceInvalidUri() {
         String icalStr = "SOURCE;VALUE=URI:invalid-uri";

         try {
             Source source = new Source(icalStr);
             assertEquals("invalid-uri", source.getValue());
             // Should not be valid since it doesn't have a proper scheme
             assertFalse(source.isValidSource());
         } catch (Exception e) {
             fail("SOURCE with invalid URI should still parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: SOURCE empty value")
     void testSourceEmptyValue() {
         Source source = new Source();
         source.setValue("");

         String icalOutput = source.toICalendar();
         assertTrue(icalOutput.contains("SOURCE;VALUE=URI:"),
                   "Empty source should serialize");
         assertFalse(source.isValidSource(), "Empty value should not be valid");
     }

     @Test
     @DisplayName("RFC 7986: SOURCE with complex URI")
     void testSourceComplexUri() {
         String complexUri = "https://example.com/calendars/user123/events.ics?token=abc123&refresh=true";

         String icalStr = "SOURCE;VALUE=URI:" + complexUri;

         try {
             Source source = new Source(icalStr);
             assertEquals(complexUri, source.getValue());
             assertTrue(source.isValidSource());
         } catch (Exception e) {
             fail("SOURCE with complex URI should parse: " + e.getMessage());
         }
     }
 }