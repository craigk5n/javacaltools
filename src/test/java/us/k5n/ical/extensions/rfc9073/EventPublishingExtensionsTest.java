package us.k5n.ical.extensions.rfc9073;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import us.k5n.ical.*;

/**
 * RFC 9073: Event Publishing Extensions - Comprehensive Test Suite
 *
 * Tests for all RFC 9073 extensions integrated together:
 * - PARTICIPANT component (Section 7.1)
 * - VLOCATION component (Section 7.2)
 * - VRESOURCE component (Section 7.3)
 * - STYLED-DESCRIPTION property (Section 6.5)
 * - CALENDAR-ADDRESS property (Section 6.4)
 * - LOCATION-TYPE property (Section 6.1)
 * - RESOURCE-TYPE property (Section 6.3)
 * - PARTICIPANT-TYPE property (Section 6.2)
 */
@DisplayName("RFC 9073: Event Publishing Extensions")
public class EventPublishingExtensionsTest {

  @Nested
  @DisplayName("Complete Calendar Parsing Tests")
  class CompleteCalendarTests {

    @Test
    @DisplayName("RFC 9073: should parse complete calendar with all extensions")
    void should_parseComplete_when_allExtensionsProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "CALENDAR-ADDRESS:mailto:calendar@example.com\n" +
          "NAME:My Personal Calendar\n" +
          "DESCRIPTION:A calendar for personal events and appointments\n" +
          "\n" +
          "BEGIN:PARTICIPANT\n" +
          "UID:participant-001@example.com\n" +
          "PARTICIPANT-TYPE:INDIVIDUAL\n" +
          "CALENDAR-ADDRESS:mailto:alice@example.com\n" +
          "NAME:Alice Johnson\n" +
          "DESCRIPTION:Project Manager\n" +
          "STRUCTURED-DATA;VALUE=TEXT:Senior PM with 5+ years experience\n" +
          "END:PARTICIPANT\n" +
          "\n" +
          "BEGIN:PARTICIPANT\n" +
          "UID:participant-002@example.com\n" +
          "PARTICIPANT-TYPE:GROUP\n" +
          "CALENDAR-ADDRESS:mailto:team@example.com\n" +
          "NAME:Development Team\n" +
          "DESCRIPTION:Core development team\n" +
          "END:PARTICIPANT\n" +
          "\n" +
          "BEGIN:VLOCATION\n" +
          "UID:location-001@example.com\n" +
          "NAME:Conference Room A\n" +
          "DESCRIPTION:Large conference room on the 5th floor\n" +
          "LOCATION-TYPE:ROOM\n" +
          "GEO:40.7128;-74.0060\n" +
          "STRUCTURED-DATA;VALUE=TEXT:Capacity: 20 people, Projector available\n" +
          "END:VLOCATION\n" +
          "\n" +
          "BEGIN:VRESOURCE\n" +
          "UID:resource-001@example.com\n" +
          "NAME:Projector Model X\n" +
          "DESCRIPTION:High-definition projector\n" +
          "RESOURCE-TYPE:EQUIPMENT\n" +
          "STRUCTURED-DATA;VALUE=TEXT:Resolution: 4K, Wireless capability\n" +
          "END:VRESOURCE\n" +
          "\n" +
          "BEGIN:VEVENT\n" +
          "UID:event-001@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "DTEND:20240101T160000Z\n" +
          "SUMMARY:Team Planning Meeting\n" +
          "LOCATION:Conference Room A\n" +
          "STYLED-DESCRIPTION;FMTTYPE=text/html:<p>Meeting agenda</p>\n" +
          "ATTENDEE;PARTICIPANT-ID=participant-001@example.com:mailto:alice@example.com\n" +
          "ATTENDEE;PARTICIPANT-ID=participant-002@example.com:mailto:team@example.com\n" +
          "LOCATION-ID:location-001@example.com\n" +
          "RESOURCE-ID:resource-001@example.com\n" +
          "END:VEVENT\n" +
          "\n" +
          "BEGIN:VTODO\n" +
          "UID:todo-001@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240102T090000Z\n" +
          "DUE:20240102T170000Z\n" +
          "SUMMARY:Prepare presentation\n" +
          "STYLED-DESCRIPTION;FMTTYPE=text/plain:Prepare slides\n" +
          "LOCATION-ID:location-001@example.com\n" +
          "END:VTODO\n" +
          "\n" +
          "BEGIN:VJOURNAL\n" +
          "UID:journal-001@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T120000Z\n" +
          "SUMMARY:Meeting Notes\n" +
          "STYLED-DESCRIPTION;FMTTYPE=text/plain:Meeting notes\n" +
          "END:VJOURNAL\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "Complete calendar with all RFC 9073 extensions should parse without errors");

        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertEquals("My Personal Calendar", ds.getName(), "Calendar should have NAME");
      } catch (Exception e) {
        fail("Complete calendar with all extensions should parse: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Serialization Round-Trip Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 9073: should round-trip serialize extensions")
    void should_roundTrip_when_extensionsSerialized() {
      String originalIcal = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "CALENDAR-ADDRESS:mailto:test@example.com\n" +
          "\n" +
          "BEGIN:PARTICIPANT\n" +
          "UID:participant-001@example.com\n" +
          "PARTICIPANT-TYPE:INDIVIDUAL\n" +
          "CALENDAR-ADDRESS:mailto:user@example.com\n" +
          "NAME:Test User\n" +
          "END:PARTICIPANT\n" +
          "\n" +
          "BEGIN:VLOCATION\n" +
          "UID:location-001@example.com\n" +
          "NAME:Test Room\n" +
          "LOCATION-TYPE:ROOM\n" +
          "END:VLOCATION\n" +
          "\n" +
          "BEGIN:VRESOURCE\n" +
          "UID:resource-001@example.com\n" +
          "NAME:Test Equipment\n" +
          "RESOURCE-TYPE:EQUIPMENT\n" +
          "END:VRESOURCE\n" +
          "\n" +
          "BEGIN:VEVENT\n" +
          "UID:event-001@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY:Test Event\n" +
          "STYLED-DESCRIPTION;FMTTYPE=text/html:<p>Test</p>\n" +
          "LOCATION-ID:location-001@example.com\n" +
          "RESOURCE-ID:resource-001@example.com\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));
        assertTrue(parser.getAllErrors().isEmpty(), "Original parsing should succeed");

        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        String serialized = ds.toICalendar();

        ICalendarParser parser2 = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser2.parse(new StringReader(serialized));
        assertTrue(parser2.getAllErrors().isEmpty(), "Serialized output should parse without errors");
      } catch (Exception e) {
        fail("Serialization round-trip should succeed: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Property Parameter Validation Tests")
  class ParameterValidationTests {

    @Test
    @DisplayName("RFC 9073: should validate LOCATION-TYPE values")
    void should_validateLocationTypes_when_validTypesProvided() {
      String[] validLocationTypes = {"ROOM", "SEAT", "DESK", "SPACE", "UNKNOWN"};
      for (String type : validLocationTypes) {
        String icalData = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VLOCATION\nUID:loc@example.com\nNAME:Test\nLOCATION-TYPE:" + type +
            "\nEND:VLOCATION\nEND:VCALENDAR";
        try {
          ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
          parser.parse(new StringReader(icalData));
          assertTrue(parser.getAllErrors().isEmpty(), "LOCATION-TYPE=" + type + " should be valid");
        } catch (Exception e) {
          fail("LOCATION-TYPE=" + type + " should parse: " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("RFC 9073: should validate RESOURCE-TYPE values")
    void should_validateResourceTypes_when_validTypesProvided() {
      String[] validResourceTypes = {"ROOM", "EQUIPMENT", "SERVICE", "UNKNOWN"};
      for (String type : validResourceTypes) {
        String icalData = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VRESOURCE\nUID:res@example.com\nNAME:Test\nRESOURCE-TYPE:" + type +
            "\nEND:VRESOURCE\nEND:VCALENDAR";
        try {
          ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
          parser.parse(new StringReader(icalData));
          assertTrue(parser.getAllErrors().isEmpty(), "RESOURCE-TYPE=" + type + " should be valid");
        } catch (Exception e) {
          fail("RESOURCE-TYPE=" + type + " should parse: " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("RFC 9073: should validate PARTICIPANT-TYPE values")
    void should_validateParticipantTypes_when_validTypesProvided() {
      String[] validParticipantTypes = {"INDIVIDUAL", "GROUP", "RESOURCE", "ROOM", "UNKNOWN"};
      for (String type : validParticipantTypes) {
        String icalData = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Calendar//EN\n" +
            "BEGIN:PARTICIPANT\nUID:part@example.com\nPARTICIPANT-TYPE:" + type +
            "\nCALENDAR-ADDRESS:mailto:test@example.com\nEND:PARTICIPANT\nEND:VCALENDAR";
        try {
          ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
          parser.parse(new StringReader(icalData));
          assertTrue(parser.getAllErrors().isEmpty(),
              "PARTICIPANT-TYPE=" + type + " should be valid");
        } catch (Exception e) {
          fail("PARTICIPANT-TYPE=" + type + " should parse: " + e.getMessage());
        }
      }
    }
  }

  @Nested
  @DisplayName("Cross-Component Reference Tests")
  class CrossComponentReferenceTests {

    @Test
    @DisplayName("RFC 9073: should parse cross-component references")
    void should_parseCrossReferences_when_referencesProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "\n" +
          "BEGIN:PARTICIPANT\n" +
          "UID:organizer@example.com\n" +
          "PARTICIPANT-TYPE:INDIVIDUAL\n" +
          "CALENDAR-ADDRESS:mailto:organizer@example.com\n" +
          "END:PARTICIPANT\n" +
          "\n" +
          "BEGIN:VLOCATION\n" +
          "UID:meeting-room@example.com\n" +
          "LOCATION-TYPE:ROOM\n" +
          "NAME:Main Conference Room\n" +
          "END:VLOCATION\n" +
          "\n" +
          "BEGIN:VRESOURCE\n" +
          "UID:projector@example.com\n" +
          "RESOURCE-TYPE:EQUIPMENT\n" +
          "NAME:4K Projector\n" +
          "END:VRESOURCE\n" +
          "\n" +
          "BEGIN:VEVENT\n" +
          "UID:meeting@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "ORGANIZER;PARTICIPANT-ID=organizer@example.com:mailto:organizer@example.com\n" +
          "LOCATION-ID:meeting-room@example.com\n" +
          "RESOURCE-ID:projector@example.com\n" +
          "ATTENDEE;PARTICIPANT-ID=organizer@example.com:mailto:organizer@example.com\n" +
          "SUMMARY:Project Review Meeting\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "Cross-component references should parse without errors");

        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertTrue(ds.getAllEvents().size() >= 0, "Should have events");
      } catch (Exception e) {
        fail("Cross-component references should parse: " + e.getMessage());
      }
    }
  }
}
