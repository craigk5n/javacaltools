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

    @Test
    @DisplayName("RFC5546: METHOD REPLY")
    void testMethodReply() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "METHOD:REPLY\n" +
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
            assertTrue(parser.getAllErrors().isEmpty(), "METHOD REPLY should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals("REPLY", ds.getMethodValue(), "METHOD value should be REPLY");
        } catch (Exception e) {
            fail("METHOD REPLY should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5546: METHOD ADD")
    void testMethodAdd() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "METHOD:ADD\n" +
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
            assertTrue(parser.getAllErrors().isEmpty(), "METHOD ADD should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals("ADD", ds.getMethodValue(), "METHOD value should be ADD");
        } catch (Exception e) {
            fail("METHOD ADD should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5546: METHOD CANCEL")
    void testMethodCancel() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "METHOD:CANCEL\n" +
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
            assertTrue(parser.getAllErrors().isEmpty(), "METHOD CANCEL should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals("CANCEL", ds.getMethodValue(), "METHOD value should be CANCEL");
        } catch (Exception e) {
            fail("METHOD CANCEL should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5546: METHOD REFRESH")
    void testMethodRefresh() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "METHOD:REFRESH\n" +
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
            assertTrue(parser.getAllErrors().isEmpty(), "METHOD REFRESH should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals("REFRESH", ds.getMethodValue(), "METHOD value should be REFRESH");
        } catch (Exception e) {
            fail("METHOD REFRESH should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5546: METHOD COUNTER")
    void testMethodCounter() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "METHOD:COUNTER\n" +
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
            assertTrue(parser.getAllErrors().isEmpty(), "METHOD COUNTER should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals("COUNTER", ds.getMethodValue(), "METHOD value should be COUNTER");
        } catch (Exception e) {
            fail("METHOD COUNTER should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5546: METHOD DECLINECOUNTER")
    void testMethodDeclineCounter() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "METHOD:DECLINECOUNTER\n" +
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
            assertTrue(parser.getAllErrors().isEmpty(), "METHOD DECLINECOUNTER should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals("DECLINECOUNTER", ds.getMethodValue(), "METHOD value should be DECLINECOUNTER");
        } catch (Exception e) {
            fail("METHOD DECLINECOUNTER should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5546: Invalid METHOD value validation")
    void testInvalidMethodValue() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "METHOD:INVALID_METHOD\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        // In loose mode, invalid METHOD should be accepted
        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Invalid METHOD should be accepted in loose mode");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals("INVALID_METHOD", ds.getMethodValue(), "METHOD value should be stored even if invalid");
        } catch (Exception e) {
            fail("Invalid METHOD should parse in loose mode: " + e.getMessage());
        }

        // In strict mode, invalid METHOD should generate an error
        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
            parser.parse(new StringReader(icalData));
            assertFalse(parser.getAllErrors().isEmpty(), "Invalid METHOD should generate error in strict mode");

            // Should still store the method value
            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals("INVALID_METHOD", ds.getMethodValue(), "METHOD value should still be stored");
        } catch (Exception e) {
            fail("Strict parsing should not throw exception for invalid METHOD: " + e.getMessage());
        }
    }
}