package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 7986: Calendar Extensions Test
 *
 * Tests for RFC 7986 calendar-level properties:
 * - NAME, DESCRIPTION, UID, URL, LAST-MODIFIED (Section 5)
 * - Additional properties: REFRESH-INTERVAL, SOURCE, COLOR
 */
public class RFC7986_CalendarExtensionsTest {

    @Test
    @DisplayName("RFC 7986: Calendar with all VCALENDAR-level properties")
    void testCalendarWithAllProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "NAME:My Personal Calendar\n" +
            "DESCRIPTION:A calendar for personal events and appointments\n" +
            "UID:calendar-123@example.com\n" +
            "URL:http://example.com/calendar\n" +
            "LAST-MODIFIED:20240101T100000Z\n" +
            "REFRESH-INTERVAL;VALUE=DURATION:P1W\n" +
            "SOURCE:http://example.com/calendars/personal.ics\n" +
            "COLOR:#FF0000\n" +
            "\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-001@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Calendar with RFC 7986 properties should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);

            // Verify calendar properties - check that parsing succeeded
            assertEquals("My Personal Calendar", ds.getName(), "Calendar should have NAME");

        } catch (Exception e) {
            fail("Calendar with RFC 7986 properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 7986: Calendar NAME property validation")
    void testCalendarNameProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "NAME:Work Calendar\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Calendar NAME should parse without errors");

        } catch (Exception e) {
            fail("Calendar NAME should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 7986: Calendar DESCRIPTION property")
    void testCalendarDescriptionProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "DESCRIPTION:This calendar contains all my important events\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Calendar DESCRIPTION should parse without errors");

        } catch (Exception e) {
            fail("Calendar DESCRIPTION should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 7986: Calendar UID property")
    void testCalendarUidProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "UID:unique-calendar-id@example.com\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Calendar UID should parse without errors");

        } catch (Exception e) {
            fail("Calendar UID should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 7986: Calendar URL property")
    void testCalendarUrlProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "URL:https://calendar.example.com/my-calendar\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Calendar URL should parse without errors");

        } catch (Exception e) {
            fail("Calendar URL should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 7986: Calendar LAST-MODIFIED property")
    void testCalendarLastModifiedProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "LAST-MODIFIED:20240101T120000Z\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Calendar LAST-MODIFIED should parse without errors");

        } catch (Exception e) {
            fail("Calendar LAST-MODIFIED should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 7986: Calendar REFRESH-INTERVAL property")
    void testCalendarRefreshIntervalProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "REFRESH-INTERVAL;VALUE=DURATION:P1D\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Calendar REFRESH-INTERVAL should parse without errors");

        } catch (Exception e) {
            fail("Calendar REFRESH-INTERVAL should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 7986: Calendar SOURCE property")
    void testCalendarSourceProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "SOURCE:http://calendar.example.com/feed.ics\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Calendar SOURCE should parse without errors");

        } catch (Exception e) {
            fail("Calendar SOURCE should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 7986: Calendar COLOR property")
    void testCalendarColorProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "COLOR:#00FF00\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Calendar COLOR should parse without errors");

        } catch (Exception e) {
            fail("Calendar COLOR should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 7986: Calendar extensions serialization")
    void testCalendarExtensionsSerialization() {
        String originalIcal = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "NAME:Test Calendar\n" +
            "DESCRIPTION:Test Description\n" +
            "UID:test-calendar@example.com\n" +
            "URL:http://example.com\n" +
            "LAST-MODIFIED:20240101T100000Z\n" +
            "COLOR:#FF0000\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test\n" +
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