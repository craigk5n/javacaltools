package us.k5n.ical.extensions.rfc9073;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import us.k5n.ical.*;

/**
 * RFC 9073: CALENDAR-ADDRESS Property Tests
 *
 * Tests for the CALENDAR-ADDRESS property as defined in RFC 9073, Section 6.4.
 * CALENDAR-ADDRESS provides calendar user addresses for participants.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 9073 Section 6.4: CALENDAR-ADDRESS Property")
public class CalendarAddressPropertyTest {

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 9073 Section 6.4: should parse basic CALENDAR-ADDRESS")
    void should_parseBasic_when_basicAddressProvided() {
      String icalStr = "CALENDAR-ADDRESS:mailto:test@example.com";

      try {
        CalendarAddress ca = new CalendarAddress(icalStr);
        assertEquals("mailto:test@example.com", ca.getValue());
      } catch (Exception e) {
        fail("CALENDAR-ADDRESS should parse: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 9073 Section 6.4: should parse CALENDAR-ADDRESS with parameters")
    void should_parseParameters_when_parametersProvided() {
      String icalStr = "CALENDAR-ADDRESS;TYPE=INDIVIDUAL:mailto:john@example.com";

      try {
        CalendarAddress ca = new CalendarAddress(icalStr);
        assertEquals("mailto:john@example.com", ca.getValue());
        assertNotNull(ca.getNamedAttribute("TYPE"));
      } catch (Exception e) {
        fail("CALENDAR-ADDRESS with parameters should parse: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 9073 Section 6.4: should parse complex URI")
    void should_parseComplexUri_when_urnUuidProvided() {
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
    @DisplayName("RFC 9073 Section 6.4: should parse nonstandard parameters in loose mode")
    void should_parseNonstandardParams_when_looseModeUsed() {
      String icalStr = "CALENDAR-ADDRESS;X-CUSTOM=value:mailto:test@example.com";

      try {
        CalendarAddress ca = new CalendarAddress(icalStr, ICalendarParser.PARSE_LOOSE);
        assertEquals("mailto:test@example.com", ca.getValue());
      } catch (Exception e) {
        fail("CALENDAR-ADDRESS with nonstandard parameters should parse in loose mode: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Component Integration Tests")
  class ComponentIntegrationTests {

    @Test
    @DisplayName("RFC 9073 Section 6.4: should parse CALENDAR-ADDRESS in PARTICIPANT")
    void should_parseInParticipant_when_participantComponentProvided() {
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
        assertTrue(parser.getAllErrors().isEmpty(),
            "CALENDAR-ADDRESS in PARTICIPANT should parse without errors");

        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Participant participant = ds.getAllParticipants().get(0);
        assertNotNull(participant.getCalendarAddress(), "Participant should have CALENDAR-ADDRESS");
        assertEquals("mailto:test@example.com", participant.getCalendarAddress().getValue());
      } catch (Exception e) {
        fail("CALENDAR-ADDRESS in PARTICIPANT should parse: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 9073 Section 6.4: should parse multiple CALENDAR-ADDRESS instances")
    void should_parseMultiple_when_multipleAddressesProvided() {
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
        assertTrue(parser.getAllErrors().isEmpty(),
            "Multiple CALENDAR-ADDRESS should parse without errors");

        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Participant participant = ds.getAllParticipants().get(0);
        assertNotNull(participant.getCalendarAddress(), "Participant should have CALENDAR-ADDRESS");
      } catch (Exception e) {
        fail("Multiple CALENDAR-ADDRESS should parse: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 9073 Section 6.4: should serialize CALENDAR-ADDRESS")
    void should_serialize_when_toICalendarCalled() {
      CalendarAddress ca = new CalendarAddress();
      ca.setValue("mailto:test@example.com");

      String icalOutput = ca.toICalendar();
      assertTrue(icalOutput.contains("CALENDAR-ADDRESS:mailto:test@example.com"),
          "Serialized output should contain the calendar address");
    }

    @Test
    @DisplayName("RFC 9073 Section 6.4: should serialize empty value")
    void should_serializeEmpty_when_emptyValueProvided() {
      CalendarAddress ca = new CalendarAddress();
      ca.setValue("");

      String icalOutput = ca.toICalendar();
      assertTrue(icalOutput.contains("CALENDAR-ADDRESS:"), "Empty calendar address should serialize");
    }
  }
}
