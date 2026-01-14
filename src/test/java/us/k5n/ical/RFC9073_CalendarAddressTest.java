 package us.k5n.ical;

 import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 9073: CALENDAR-ADDRESS Property Tests
 *
 * Tests for the CALENDAR-ADDRESS property as defined in RFC 9073, Section 6.4.
 * CALENDAR-ADDRESS provides calendar user addresses for participants.
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
 public class RFC9073_CalendarAddressTest {

     @Test
     @DisplayName("RFC 9073: CALENDAR-ADDRESS basic parsing")
     void testCalendarAddressBasic() {
         String icalStr = "CALENDAR-ADDRESS:mailto:test@example.com";

         try {
             CalendarAddress ca = new CalendarAddress(icalStr);
             assertEquals("mailto:test@example.com", ca.getValue());
         } catch (Exception e) {
             fail("CALENDAR-ADDRESS should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: CALENDAR-ADDRESS with parameters")
     void testCalendarAddressWithParameters() {
         String icalStr = "CALENDAR-ADDRESS;TYPE=INDIVIDUAL:mailto:john@example.com";

         try {
             CalendarAddress ca = new CalendarAddress(icalStr);
             assertEquals("mailto:john@example.com", ca.getValue());
             // Check if parameters are parsed (basic check)
             assertNotNull(ca.attributeList);
         } catch (Exception e) {
             fail("CALENDAR-ADDRESS with parameters should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: CALENDAR-ADDRESS in PARTICIPANT component")
     void testCalendarAddressInParticipant() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:PARTICIPANT\n" +
             "UID:participant-001@example.com\n" +
             "PARTICIPANT-TYPE:INDIVIDUAL\n" +
             "CALENDAR-ADDRESS:mailto:test@example.com\n" +
             "END:PARTICIPANT\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "CALENDAR-ADDRESS in PARTICIPANT should parse without errors");

             DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
             Participant participant = ds.getAllParticipants().get(0);
             assertNotNull(participant.getCalendarAddress(), "Participant should have CALENDAR-ADDRESS");
             assertEquals("mailto:test@example.com", participant.getCalendarAddress().getValue());
         } catch (Exception e) {
             fail("CALENDAR-ADDRESS in PARTICIPANT should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: CALENDAR-ADDRESS serialization")
     void testCalendarAddressSerialization() {
         CalendarAddress ca = new CalendarAddress();
         ca.setValue("mailto:test@example.com");

         String icalOutput = ca.toICalendar();
         assertTrue(icalOutput.contains("CALENDAR-ADDRESS:mailto:test@example.com"),
                   "Serialized output should contain the calendar address");
     }

     @Test
     @DisplayName("RFC 9073: CALENDAR-ADDRESS with complex URI")
     void testCalendarAddressComplexUri() {
         String complexUri = "urn:uuid:12345678-1234-1234-1234-123456789012";

         String icalStr = "CALENDAR-ADDRESS:" + complexUri;

         try {
             CalendarAddress ca = new CalendarAddress(icalStr);
             assertEquals(complexUri, ca.getValue());
         } catch (Exception e) {
             fail("CALENDAR-ADDRESS with complex URI should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: CALENDAR-ADDRESS multiple instances")
     void testMultipleCalendarAddresses() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:PARTICIPANT\n" +
             "UID:participant-001@example.com\n" +
             "PARTICIPANT-TYPE:INDIVIDUAL\n" +
             "CALENDAR-ADDRESS:mailto:work@example.com\n" +
             "CALENDAR-ADDRESS:mailto:home@example.com\n" +
             "END:PARTICIPANT\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "Multiple CALENDAR-ADDRESS should parse without errors");

             DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
             Participant participant = ds.getAllParticipants().get(0);
             // For now, we support a single calendar address
             assertNotNull(participant.getCalendarAddress(), "Participant should have CALENDAR-ADDRESS");
         } catch (Exception e) {
             fail("Multiple CALENDAR-ADDRESS should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: CALENDAR-ADDRESS empty value")
     void testCalendarAddressEmptyValue() {
         CalendarAddress ca = new CalendarAddress();
         ca.setValue("");

         String icalOutput = ca.toICalendar();
         assertTrue(icalOutput.contains("CALENDAR-ADDRESS:"), "Empty calendar address should serialize");
     }

     @Test
     @DisplayName("RFC 9073: CALENDAR-ADDRESS with nonstandard parameters")
     void testCalendarAddressNonstandardParameters() {
         String icalStr = "CALENDAR-ADDRESS;X-CUSTOM=value:mailto:test@example.com";

         try {
             CalendarAddress ca = new CalendarAddress(icalStr, ICalendarParser.PARSE_LOOSE);
             assertEquals("mailto:test@example.com", ca.getValue());
             // In loose mode, nonstandard parameters should be accepted
         } catch (Exception e) {
             fail("CALENDAR-ADDRESS with nonstandard parameters should parse in loose mode: " + e.getMessage());
         }
     }
 }