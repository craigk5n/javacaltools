 package us.k5n.ical;

 import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 9073: VLOCATION Component Tests
 *
 * Tests for the VLOCATION component as defined in RFC 9073, Section 7.2.
 * VLOCATION provides rich information about event locations using structured data.
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
 public class RFC9073_VLocationTest {

     @Test
     @DisplayName("RFC 9073: VLOCATION basic parsing")
     void testVLocationBasic() {
         String icalData = "BEGIN:VLOCATION\n" +
             "UID:123456-abcdef-98765432\n" +
             "NAME:The venue\n" +
             "STRUCTURED-DATA;VALUE=URI:http://dir.example.com/venues/big-hall.vcf\n" +
             "END:VLOCATION";

         try {
             VLocation vloc = new VLocation(icalData);
             assertEquals("123456-abcdef-98765432", vloc.getUid());
             assertEquals("The venue", vloc.getName());
             assertTrue(vloc.isValid());
             assertEquals(1, vloc.getStructuredDataList().size());
             assertEquals("http://dir.example.com/venues/big-hall.vcf", vloc.getStructuredDataList().get(0));
         } catch (Exception e) {
             fail("VLOCATION should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: VLOCATION with location type")
     void testVLocationWithLocationType() {
         String icalData = "BEGIN:VLOCATION\n" +
             "UID:venue-001\n" +
             "NAME:Main Auditorium\n" +
             "LOCATION-TYPE:ROOM\n" +
             "END:VLOCATION";

         try {
             VLocation vloc = new VLocation(icalData);
             assertEquals("venue-001", vloc.getUid());
             assertEquals("Main Auditorium", vloc.getName());
             assertNotNull(vloc.getLocationType());
             assertEquals("ROOM", vloc.getLocationType().getValue());
         } catch (Exception e) {
             fail("VLOCATION with location type should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: VLOCATION serialization")
     void testVLocationSerialization() {
         VLocation vloc = new VLocation();
         vloc.setUid("venue-001");
         vloc.setName("Conference Room A");
         vloc.addStructuredData("http://example.com/venue.vcf");

         String icalOutput = vloc.toICalendar();
         assertTrue(icalOutput.contains("BEGIN:VLOCATION"));
         assertTrue(icalOutput.contains("UID:venue-001"));
         assertTrue(icalOutput.contains("NAME:Conference Room A"));
         assertTrue(icalOutput.contains("STRUCTURED-DATA;VALUE=URI:http://example.com/venue.vcf"));
         assertTrue(icalOutput.contains("END:VLOCATION"));
     }

     @Test
     @DisplayName("RFC 9073: VLOCATION in VEVENT")
     void testVLocationInEvent() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:event-001@example.com\n" +
             "DTSTAMP:20230101T100000Z\n" +
             "DTSTART:20230101T110000Z\n" +
             "SUMMARY:Meeting\n" +
             "BEGIN:VLOCATION\n" +
             "UID:loc-001\n" +
             "NAME:Conference Room\n" +
             "END:VLOCATION\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "VLOCATION in VEVENT should parse without errors");
         } catch (Exception e) {
             fail("VLOCATION in VEVENT should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: VLOCATION validation")
     void testVLocationValidation() {
         VLocation vloc = new VLocation();
         assertFalse(vloc.isValid(), "VLOCATION without UID should be invalid");

         vloc.setUid("test-uid");
         assertTrue(vloc.isValid(), "VLOCATION with UID should be valid");
     }
 }