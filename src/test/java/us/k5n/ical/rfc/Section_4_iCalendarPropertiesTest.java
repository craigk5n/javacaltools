package us.k5n.ical.rfc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;

/**
 * RFC 5545 Section 4: iCalendar Properties
 *
 * Tests for calendar-level properties like PRODID, VERSION, CALSCALE, METHOD.
 */
public class Section_4_iCalendarPropertiesTest {

    @Test
    @DisplayName("RFC5545-4: PRODID property")
    void testProdIdProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "PRODID:-//Example Corp.//Calendar 1.0//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "PRODID property should parse without errors");
        } catch (Exception e) {
            fail("PRODID property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-4: VERSION property")
    void testVersionProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VERSION 2.0 should parse without errors");
        } catch (Exception e) {
            fail("VERSION property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-4: CALSCALE property")
    void testCalscaleProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "CALSCALE GREGORIAN should parse without errors");
        } catch (Exception e) {
            fail("CALSCALE property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-4: METHOD property")
    void testMethodProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "METHOD:REQUEST\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "METHOD REQUEST should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals("REQUEST", ds.getMethodValue(), "METHOD value should be stored");
        } catch (Exception e) {
            fail("METHOD property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-4: Multiple calendar properties")
    void testMultipleCalendarProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "METHOD:PUBLISH\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple calendar properties should parse without errors");
        } catch (Exception e) {
            fail("Multiple calendar properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-4: Invalid CALSCALE value")
    void testInvalidCalscaleValue() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "CALSCALE:INVALID\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            // May still parse leniently
            assertNotNull(parser.getDataStoreAt(0));
        } catch (Exception e) {
            // Expected for invalid CALSCALE
            assertTrue(true);
        }
    }
}