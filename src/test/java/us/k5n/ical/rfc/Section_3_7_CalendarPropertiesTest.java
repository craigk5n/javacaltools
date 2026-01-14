package us.k5n.ical.rfc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.k5n.ical.ICalendarParser;

/**
 * RFC 5545 Section 3.7: Calendar Properties
 *
 * Tests for properties that can appear within calendar components
 * (descriptive, date/time, relationship, recurrence, alarm, change management, miscellaneous).
 */
public class Section_3_7_CalendarPropertiesTest {

    @Test
    @DisplayName("RFC5545-3.7: Descriptive properties (SUMMARY, DESCRIPTION)")
    void testDescriptiveProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-desc@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Meeting with Team\n" +
            "DESCRIPTION:Discuss Q4 goals and objectives for the project.\n" +
            "LOCATION:Conference Room A\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Descriptive properties should parse without errors");
        } catch (Exception e) {
            fail("Descriptive properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.7: Date and Time properties")
    void testDateTimeProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-datetime@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DTEND:20230101T110000Z\n" +
            "DURATION:PT1H\n" +
            "CREATED:20230101T090000Z\n" +
            "LAST-MODIFIED:20230101T091500Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Date/time properties should parse without errors");
        } catch (Exception e) {
            fail("Date/time properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.7: Relationship properties (UID, RELATED-TO)")
    void testRelationshipProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-rel@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "RELATED-TO:parent-event@example.com\n" +
            "SUMMARY:Related Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Relationship properties should parse without errors");
        } catch (Exception e) {
            fail("Relationship properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.7: Recurrence properties (RRULE, EXDATE, RDATE)")
    void testRecurrenceProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-recur@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "RRULE:FREQ=WEEKLY;COUNT=5\n" +
            "EXDATE:20230108T100000Z\n" +
            "RDATE:20230115T100000Z\n" +
            "SUMMARY:Recurring Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Recurrence properties should parse without errors");
        } catch (Exception e) {
            fail("Recurrence properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.7: Alarm properties (VALARM sub-component)")
    void testAlarmProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with Alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:AUDIO\n" +
            "TRIGGER:-PT15M\n" +
            "ATTACH:Bell.mp3\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Alarm properties should parse without errors");
        } catch (Exception e) {
            fail("Alarm properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.7: Change management properties (SEQUENCE)")
    void testChangeManagementProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-sequence@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SEQUENCE:1\n" +
            "SUMMARY:Updated Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Change management properties should parse without errors");
        } catch (Exception e) {
            fail("Change management properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.7: Miscellaneous properties (CATEGORIES, RESOURCES)")
    void testMiscellaneousProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-misc@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "CATEGORIES:WORK,MEETING\n" +
            "RESOURCES:Projector,Laptop\n" +
            "PRIORITY:1\n" +
            "CLASS:PUBLIC\n" +
            "STATUS:CONFIRMED\n" +
            "SUMMARY:Complete Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Miscellaneous properties should parse without errors");
        } catch (Exception e) {
            fail("Miscellaneous properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.7: Attendee properties (ATTENDEE, ORGANIZER)")
    void testAttendeeProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-attendee@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE;CN=John Doe;ROLE=REQ-PARTICIPANT:mailto:john@example.com\n" +
            "ATTENDEE;CN=Jane Smith;ROLE=OPT-PARTICIPANT:mailto:jane@example.com\n" +
            "SUMMARY:Meeting with Attendees\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Attendee properties should parse without errors");
        } catch (Exception e) {
            fail("Attendee properties should parse: " + e.getMessage());
        }
    }
}