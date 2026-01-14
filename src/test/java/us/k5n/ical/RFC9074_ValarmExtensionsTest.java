package us.k5n.ical;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;

/**
 * RFC 9074: VALARM Extensions Test
 *
 * Tests for RFC 9074 VALARM extensions:
 * - PROXIMITY property for location-based triggers
 * - STRUCTURED-DATA property for VALARM
 */
public class RFC9074_ValarmExtensionsTest {

    @Test
    @DisplayName("RFC 9074: VALARM with PROXIMITY trigger")
    void testValarmWithProximityTrigger() {
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
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with PROXIMITY should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            Valarm alarm = event.getAlarms().get(0);
            assertNotNull(alarm, "Event should have VALARM");
            // Note: PROXIMITY getter may not be implemented yet, but parsing should succeed

        } catch (Exception e) {
            fail("VALARM with PROXIMITY should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9074: VALARM with STRUCTURED-DATA")
    void testValarmWithStructuredData() {
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
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with STRUCTURED-DATA should parse without errors");

        } catch (Exception e) {
            fail("VALARM with STRUCTURED-DATA should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9074: PROXIMITY values validation")
    void testProximityValues() {
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
                assertTrue(parser.getAllErrors().isEmpty(), "PROXIMITY=" + proximity + " should be valid");

            } catch (Exception e) {
                fail("PROXIMITY=" + proximity + " should parse: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("RFC 9074: VALARM with both PROXIMITY and STRUCTURED-DATA")
    void testValarmWithBothExtensions() {
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
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with both PROXIMITY and STRUCTURED-DATA should parse without errors");

        } catch (Exception e) {
            fail("VALARM with both extensions should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9074: Multiple VALARMs with different triggers")
    void testMultipleValarmsWithDifferentTriggers() {
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
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple VALARMs with different triggers should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            assertEquals(2, event.getAlarms().size(), "Event should have 2 VALARMs");

        } catch (Exception e) {
            fail("Multiple VALARMs should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9074: VALARM serialization with extensions")
    void testValarmSerializationWithExtensions() {
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

            // Parse the serialized output again
            ICalendarParser parser2 = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser2.parse(new StringReader(serialized));
            assertTrue(parser2.getAllErrors().isEmpty(), "Serialized output should parse without errors");

        } catch (Exception e) {
            fail("Serialization round-trip should succeed: " + e.getMessage());
        }
    }
}