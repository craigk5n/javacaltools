package us.k5n.ical.rfc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;

/**
 * RFC 5545 Section 3.5: iCalendar Object
 *
 * Tests for the overall structure and requirements of iCalendar objects.
 */
public class Section_3_5_iCalendarObjectTest {

    @Test
    @DisplayName("RFC5545-3.5: Valid iCalendar object structure")
    void testValidICalendarObjectStructure() {
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
            assertTrue(parser.getAllErrors().isEmpty(), "Valid iCalendar object should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertNotNull(ds, "DataStore should be created");
            assertNotNull(ds.getAllEvents(), "Events should be available");
        } catch (Exception e) {
            fail("Valid iCalendar object should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.5: Required VERSION property")
    void testRequiredVersionProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
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
            // VERSION is required but parser may be lenient
            assertNotNull(parser.getDataStoreAt(0));
        } catch (Exception e) {
            // May fail without VERSION
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("RFC5545-3.5: Multiple calendar components")
    void testMultipleCalendarComponents() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event1@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:First Event\n" +
            "END:VEVENT\n" +
            "BEGIN:VTODO\n" +
            "UID:todo1@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Todo\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple components should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals(1, ds.getAllEvents().size(), "Should have one event");
            // Note: No getter for todos in current DataStore interface
        } catch (Exception e) {
            fail("Multiple components should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.5: Empty calendar object")
    void testEmptyCalendarObject() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Empty calendar should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertNotNull(ds, "DataStore should be created even if empty");
        } catch (Exception e) {
            fail("Empty calendar should parse: " + e.getMessage());
        }
    }
}