package us.k5n.ical.extensions.rfc9074;

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
 * RFC 9074: VALARM Extensions Test
 *
 * Tests for RFC 9074 VALARM extensions:
 * - PROXIMITY property for location-based triggers
 * - STRUCTURED-DATA property for VALARM
 */
@DisplayName("RFC 9074: VALARM Extensions")
public class ValarmExtensionsTest {

  @Nested
  @DisplayName("PROXIMITY Trigger Tests")
  class ProximityTriggerTests {

    @Test
    @DisplayName("RFC 9074: should parse VALARM with PROXIMITY trigger")
    void should_parseProximityTrigger_when_proximityProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:event@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY:Meeting with proximity alarm\n" +
          "BEGIN:VALARM\n" +
          "ACTION:DISPLAY\n" +
          "DESCRIPTION:Meeting reminder\n" +
          "TRIGGER;VALUE=DURATION:-PT15M\n" +
          "PROXIMITY:DEPART\n" +
          "END:VALARM\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "VALARM with PROXIMITY should parse without errors");

        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);
        Valarm alarm = event.getAlarms().get(0);
        assertNotNull(alarm, "Event should have VALARM");
      } catch (Exception e) {
        fail("VALARM with PROXIMITY should parse: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 9074: should validate all PROXIMITY values")
    void should_validateAllValues_when_allProximityValuesProvided() {
      String[] proximityValues = {"ARRIVE", "DEPART", "CONNECT", "DISCONNECT"};

      for (String proximity : proximityValues) {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test Event\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "DESCRIPTION:Test alarm\n" +
            "TRIGGER;VALUE=DURATION:-PT5M\n" +
            "PROXIMITY:" + proximity + "\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
          ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
          parser.parse(new StringReader(icalData));
          assertTrue(parser.getAllErrors().isEmpty(),
              "PROXIMITY=" + proximity + " should be valid");
        } catch (Exception e) {
          fail("PROXIMITY=" + proximity + " should parse: " + e.getMessage());
        }
      }
    }
  }

  @Nested
  @DisplayName("STRUCTURED-DATA Tests")
  class StructuredDataTests {

    @Test
    @DisplayName("RFC 9074: should parse VALARM with STRUCTURED-DATA")
    void should_parseStructuredData_when_structuredDataProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:event@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY:Event with structured alarm data\n" +
          "BEGIN:VALARM\n" +
          "ACTION:DISPLAY\n" +
          "DESCRIPTION:Custom alarm\n" +
          "TRIGGER;VALUE=DURATION:-PT10M\n" +
          "STRUCTURED-DATA;VALUE=TEXT:Custom alarm configuration\n" +
          "END:VALARM\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "VALARM with STRUCTURED-DATA should parse without errors");
      } catch (Exception e) {
        fail("VALARM with STRUCTURED-DATA should parse: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Combined Extensions Tests")
  class CombinedExtensionsTests {

    @Test
    @DisplayName("RFC 9074: should parse VALARM with both PROXIMITY and STRUCTURED-DATA")
    void should_parseBothExtensions_when_bothProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:event@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY:Advanced alarm event\n" +
          "BEGIN:VALARM\n" +
          "ACTION:DISPLAY\n" +
          "DESCRIPTION:Location-based reminder\n" +
          "TRIGGER;VALUE=DURATION:-PT30M\n" +
          "PROXIMITY:ARRIVE\n" +
          "STRUCTURED-DATA;VALUE=TEXT:Location-based alarm data\n" +
          "END:VALARM\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "VALARM with both PROXIMITY and STRUCTURED-DATA should parse without errors");
      } catch (Exception e) {
        fail("VALARM with both extensions should parse: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 9074: should parse multiple VALARMs with different triggers")
    void should_parseMultipleAlarms_when_differentTriggersProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:event@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY:Event with multiple alarms\n" +
          "BEGIN:VALARM\n" +
          "ACTION:DISPLAY\n" +
          "DESCRIPTION:Time-based reminder\n" +
          "TRIGGER;VALUE=DURATION:-PT15M\n" +
          "END:VALARM\n" +
          "BEGIN:VALARM\n" +
          "ACTION:DISPLAY\n" +
          "DESCRIPTION:Location-based reminder\n" +
          "TRIGGER;VALUE=DURATION:-PT30M\n" +
          "PROXIMITY:ARRIVE\n" +
          "END:VALARM\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "Multiple VALARMs with different triggers should parse without errors");

        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);
        assertEquals(2, event.getAlarms().size(), "Event should have 2 VALARMs");
      } catch (Exception e) {
        fail("Multiple VALARMs should parse: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 9074: should round-trip serialize VALARM with extensions")
    void should_roundTrip_when_extensionsSerialized() {
      String originalIcal = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:event@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY:Serialization test\n" +
          "BEGIN:VALARM\n" +
          "ACTION:DISPLAY\n" +
          "DESCRIPTION:Test alarm\n" +
          "TRIGGER;VALUE=DURATION:-PT10M\n" +
          "PROXIMITY:DEPART\n" +
          "STRUCTURED-DATA;VALUE=TEXT:Test data\n" +
          "END:VALARM\n" +
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
        assertTrue(parser2.getAllErrors().isEmpty(),
            "Serialized output should parse without errors");
      } catch (Exception e) {
        fail("Serialization round-trip should succeed: " + e.getMessage());
      }
    }
  }
}
