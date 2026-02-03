package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Attribute;
import us.k5n.ical.Constants;
import us.k5n.ical.DefaultDataStore;
import us.k5n.ical.Event;
import us.k5n.ical.ICalendarParser;
import us.k5n.ical.Organizer;
import us.k5n.ical.ParseException;

/**
 * RFC 5545 Section 3.8.4.3: ORGANIZER Property Tests
 *
 * Tests for the ORGANIZER property as defined in RFC 5545.
 * The ORGANIZER property defines the organizer for a calendar component.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.4.3: ORGANIZER Property")
public class OrganizerPropertyTest implements Constants {

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should parse simple organizer with mailto")
    void should_parseSimpleOrganizer_when_mailtoProvided() throws Exception {
      String orgStr = "ORGANIZER:mailto:organizer@example.com";
      Organizer org = new Organizer(orgStr, PARSE_STRICT);

      assertNotNull(org, "Organizer should not be null");
      assertEquals("mailto:organizer@example.com", org.getOrganizer());
      assertEquals("mailto:organizer@example.com", org.getValue());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should parse organizer with CN parameter")
    void should_parseCN_when_cnParameterProvided() throws Exception {
      String orgStr = "ORGANIZER;CN=John Smith:mailto:john@example.com";
      Organizer org = new Organizer(orgStr, PARSE_STRICT);

      assertEquals("mailto:john@example.com", org.getOrganizer());

      Attribute cnAttr = org.getNamedAttribute("CN");
      assertNotNull(cnAttr, "CN attribute should exist");
      assertEquals("John Smith", cnAttr.value);
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should parse organizer with quoted CN parameter")
    void should_parseQuotedCN_when_cnContainsComma() throws Exception {
      String orgStr = "ORGANIZER;CN=\"Smith, John\":mailto:john@example.com";
      Organizer org = new Organizer(orgStr, PARSE_LOOSE);

      assertEquals("mailto:john@example.com", org.getOrganizer());

      Attribute cnAttr = org.getNamedAttribute("CN");
      assertNotNull(cnAttr, "CN attribute should exist");
      assertEquals("Smith, John", cnAttr.value);
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should parse organizer with DIR parameter")
    void should_parseDir_when_dirParameterProvided() throws Exception {
      String orgStr = "ORGANIZER;DIR=\"ldap://example.com/uid=john\":mailto:john@example.com";
      Organizer org = new Organizer(orgStr, PARSE_LOOSE);

      assertEquals("mailto:john@example.com", org.getOrganizer());

      Attribute dirAttr = org.getNamedAttribute("DIR");
      assertNotNull(dirAttr, "DIR attribute should exist");
      assertEquals("ldap://example.com/uid=john", dirAttr.value);
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should parse organizer with SENT-BY parameter")
    void should_parseSentBy_when_sentByParameterProvided() throws Exception {
      String orgStr = "ORGANIZER;SENT-BY=\"mailto:assistant@example.com\":mailto:boss@example.com";
      Organizer org = new Organizer(orgStr, PARSE_LOOSE);

      assertEquals("mailto:boss@example.com", org.getOrganizer());

      Attribute sentByAttr = org.getNamedAttribute("SENT-BY");
      assertNotNull(sentByAttr, "SENT-BY attribute should exist");
      assertEquals("mailto:assistant@example.com", sentByAttr.value);
    }
  }

  @Nested
  @DisplayName("RFC 9073 PARTICIPANT-ID Tests")
  class ParticipantIdTests {

    @Test
    @DisplayName("RFC 9073: should parse organizer with PARTICIPANT-ID parameter")
    void should_parseParticipantId_when_participantIdProvided() throws Exception {
      String orgStr = "ORGANIZER;PARTICIPANT-ID=participant-123:mailto:organizer@example.com";
      Organizer org = new Organizer(orgStr, PARSE_LOOSE);

      assertEquals("mailto:organizer@example.com", org.getOrganizer());
      assertEquals("participant-123", org.getParticipantId());
    }

    @Test
    @DisplayName("RFC 9073: should return null when PARTICIPANT-ID not provided")
    void should_returnNull_when_noParticipantId() throws Exception {
      String orgStr = "ORGANIZER:mailto:organizer@example.com";
      Organizer org = new Organizer(orgStr, PARSE_LOOSE);

      assertNull(org.getParticipantId());
    }

    @Test
    @DisplayName("RFC 9073: should parse organizer with multiple parameters including PARTICIPANT-ID")
    void should_parseAllParameters_when_multipleParametersProvided() throws Exception {
      String orgStr = "ORGANIZER;CN=John Smith;PARTICIPANT-ID=p-456:mailto:john@example.com";
      Organizer org = new Organizer(orgStr, PARSE_LOOSE);

      assertEquals("mailto:john@example.com", org.getOrganizer());
      assertEquals("p-456", org.getParticipantId());

      Attribute cnAttr = org.getNamedAttribute("CN");
      assertNotNull(cnAttr);
      assertEquals("John Smith", cnAttr.value);
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should round-trip simple organizer")
    void should_roundTrip_when_simpleOrganizer() throws Exception {
      String orgStr = "ORGANIZER:mailto:test@example.com";
      Organizer org = new Organizer(orgStr, PARSE_LOOSE);

      String output = org.toICalendar();
      assertNotNull(output);
      assertTrue(output.contains("mailto:test@example.com") ||
          output.contains("MAILTO:test@example.com"));
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should round-trip organizer with attributes")
    void should_roundTrip_when_organizerHasAttributes() throws Exception {
      String orgStr = "ORGANIZER;CN=Test User:mailto:test@example.com";
      Organizer org = new Organizer(orgStr, PARSE_LOOSE);

      String output = org.toICalendar();
      assertNotNull(output);
      assertTrue(output.contains("ORGANIZER"));
    }
  }

  @Nested
  @DisplayName("Event Integration Tests")
  class EventIntegrationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should parse event with organizer")
    void should_parseOrganizer_when_eventContainsOrganizer() throws Exception {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Test//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:test-123@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240115T140000Z\n" +
          "SUMMARY:Test Event\n" +
          "ORGANIZER;CN=Meeting Organizer:mailto:organizer@example.com\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      ICalendarParser parser = new ICalendarParser(PARSE_LOOSE);
      parser.parse(new StringReader(icalData));

      DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
      assertNotNull(ds.getAllEvents());
      assertEquals(1, ds.getAllEvents().size());

      Event event = ds.getAllEvents().get(0);
      assertNotNull(event.getOrganizer());
      assertEquals("mailto:organizer@example.com", event.getOrganizer().getOrganizer());
    }

    @Test
    @DisplayName("RFC 5546: should parse iTIP REQUEST with organizer")
    void should_parseOrganizer_when_itipRequest() throws Exception {
      String icalData = "BEGIN:VCALENDAR\n" +
          "METHOD:REQUEST\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Test//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:meeting-456@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240115T140000Z\n" +
          "SUMMARY:Meeting Request\n" +
          "ORGANIZER;CN=Boss;SENT-BY=\"mailto:assistant@example.com\":mailto:boss@example.com\n" +
          "ATTENDEE;PARTSTAT=NEEDS-ACTION:mailto:attendee@example.com\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      ICalendarParser parser = new ICalendarParser(PARSE_LOOSE);
      parser.parse(new StringReader(icalData));

      DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
      Event event = ds.getAllEvents().get(0);

      assertNotNull(event.getOrganizer());
      assertEquals("mailto:boss@example.com", event.getOrganizer().getOrganizer());
    }
  }

  @Nested
  @DisplayName("Error Handling Tests")
  class ErrorHandlingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should accept various formats in LOOSE mode")
    void should_parseVariousFormats_when_looseModeEnabled() {
      String[] testCases = {
          "ORGANIZER:mailto:test@example.com",
          "ORGANIZER:MAILTO:TEST@EXAMPLE.COM",
          "ORGANIZER;CN=Test:mailto:test@example.com"
      };

      for (String testCase : testCases) {
        try {
          Organizer org = new Organizer(testCase, PARSE_LOOSE);
          assertNotNull(org);
        } catch (Exception e) {
          fail("LOOSE mode should accept: " + testCase + " but got: " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.3: should handle empty organizer value")
    void should_handleEmptyValue_when_noValueProvided() {
      try {
        Organizer org = new Organizer("ORGANIZER:", PARSE_LOOSE);
        assertEquals("", org.getValue());
      } catch (ParseException e) {
        // This is also acceptable behavior
      }
    }
  }
}
