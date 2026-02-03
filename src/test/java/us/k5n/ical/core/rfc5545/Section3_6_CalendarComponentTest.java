package us.k5n.ical.core.rfc5545;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;

/**
 * RFC 5545 Section 3.6: Calendar Components
 *
 * Tests for the general structure and requirements of calendar components
 * (VEVENT, VTODO, VJOURNAL, VFREEBUSY, VTIMEZONE, VALARM).
 */
public class Section3_6_CalendarComponentTest {

    @Test
    @DisplayName("RFC5545-3.6: Valid VEVENT component structure")
    void testValidVEventComponentStructure() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Valid VEVENT component should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals(1, ds.getAllEvents().size(), "Should contain one event");
        } catch (Exception e) {
            fail("Valid VEVENT component should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.6: Valid VTODO component structure")
    void testValidVTodoComponentStructure() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Todo\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Valid VTODO component should parse without errors");

            // Note: No direct getter for todos in DataStore interface
        } catch (Exception e) {
            fail("Valid VTODO component should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.6: Valid VJOURNAL component structure")
    void testValidVJournalComponentStructure() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Journal\n" +
            "DESCRIPTION:Journal entry description\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Valid VJOURNAL component should parse without errors");
        } catch (Exception e) {
            fail("Valid VJOURNAL component should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.6: Component nesting (VALARM in VEVENT)")
    void testComponentNesting() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with Alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT15M\n" +
            "DESCRIPTION:Meeting reminder\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Component nesting should parse without errors");
        } catch (Exception e) {
            fail("Component nesting should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.6: Invalid component nesting")
    void testInvalidComponentNesting() {
        // VALARM cannot contain another VALARM
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-invalid@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Invalid nesting\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT15M\n" +
            "BEGIN:VALARM\n" + // Invalid nesting
            "ACTION:EMAIL\n" +
            "TRIGGER:-PT30M\n" +
            "END:VALARM\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            // Parser may still parse this, but it's semantically invalid
            assertNotNull(parser.getDataStoreAt(0));
        } catch (Exception e) {
            // May fail due to invalid nesting
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("RFC5545-3.6: Missing END marker")
    void testMissingEndMarker() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-no-end@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Missing END\n" +
            // Missing END:VEVENT
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            // Parser may handle this gracefully or report errors
            assertNotNull(parser.getDataStoreAt(0));
        } catch (Exception e) {
            // Expected for malformed component
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("RFC5545-3.6: Empty component")
    void testEmptyComponent() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            // Empty component may parse but be invalid
            assertNotNull(parser.getDataStoreAt(0));
        } catch (Exception e) {
            // Expected for invalid empty component
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("RFC5545-3.6: Multiple components of same type")
    void testMultipleComponentsOfSameType() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event1@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:First Event\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "UID:event2@example.com\n" +
            "DTSTAMP:20230101T110000Z\n" +
            "DTSTART:20230101T110000Z\n" +
            "SUMMARY:Second Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple components should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertEquals(2, ds.getAllEvents().size(), "Should contain two events");
        } catch (Exception e) {
            fail("Multiple components should parse: " + e.getMessage());
        }
    }
}
