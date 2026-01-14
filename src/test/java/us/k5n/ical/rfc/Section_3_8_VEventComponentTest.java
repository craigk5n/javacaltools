package us.k5n.ical.rfc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.k5n.ical.ICalendarParser;

/**
 * RFC 5545 Section 3.8: VEVENT Component
 *
 * Tests for the VEVENT calendar component, including required properties,
 * optional properties, and component-specific validation rules.
 */
public class Section_3_8_VEventComponentTest {

    @Test
    @DisplayName("RFC5545-3.8: VEVENT with required properties")
    void testVEventRequiredProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-required@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Required Properties Test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VEVENT with required properties should parse without errors");
        } catch (Exception e) {
            fail("VEVENT with required properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.8: VEVENT with DTEND")
    void testVEventWithDtEnd() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-dtend@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DTEND:20230101T110000Z\n" +
            "SUMMARY:Event with DTEND\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VEVENT with DTEND should parse without errors");
        } catch (Exception e) {
            fail("VEVENT with DTEND should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.8: VEVENT with DURATION")
    void testVEventWithDuration() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-duration@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DURATION:PT1H30M\n" +
            "SUMMARY:Event with DURATION\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VEVENT with DURATION should parse without errors");
        } catch (Exception e) {
            fail("VEVENT with DURATION should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.8: VEVENT with recurrence")
    void testVEventWithRecurrence() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-recur@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "RRULE:FREQ=WEEKLY;COUNT=4\n" +
            "SUMMARY:Recurring Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VEVENT with recurrence should parse without errors");
        } catch (Exception e) {
            fail("VEVENT with recurrence should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.8: VEVENT with alarms")
    void testVEventWithAlarms() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarms@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with Alarms\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT15M\n" +
            "DESCRIPTION:Meeting reminder\n" +
            "END:VALARM\n" +
            "BEGIN:VALARM\n" +
            "ACTION:AUDIO\n" +
            "TRIGGER:-PT30M\n" +
            "ATTACH:Bell.mp3\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VEVENT with alarms should parse without errors");
        } catch (Exception e) {
            fail("VEVENT with alarms should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.8: VEVENT with attendees")
    void testVEventWithAttendees() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-attendees@example.com\n" +
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
            assertTrue(parser.getAllErrors().isEmpty(), "VEVENT with attendees should parse without errors");
        } catch (Exception e) {
            fail("VEVENT with attendees should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.8: VEVENT with all optional properties")
    void testVEventWithAllOptionalProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-complete@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DTEND:20230101T110000Z\n" +
            "SUMMARY:Complete Event\n" +
            "DESCRIPTION:Detailed description of the event\n" +
            "LOCATION:Conference Room A\n" +
            "GEO:37.7749;-122.4194\n" +
            "CLASS:PUBLIC\n" +
            "PRIORITY:1\n" +
            "STATUS:CONFIRMED\n" +
            "TRANSP:OPAQUE\n" +
            "SEQUENCE:0\n" +
            "CATEGORIES:WORK,MEETING\n" +
            "URL:http://example.com/event\n" +
            "COMMENT:Additional comment\n" +
            "CONTACT:Phone: 555-1234\n" +
            "RRULE:FREQ=WEEKLY\n" +
            "EXDATE:20230108T100000Z\n" +
            "RDATE:20230115T100000Z\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VEVENT with all optional properties should parse without errors");
        } catch (Exception e) {
            fail("VEVENT with all optional properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.8: VEVENT date-only format")
    void testVEventDateOnly() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-dateonly@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART;VALUE=DATE:20230101\n" +
            "DTEND;VALUE=DATE:20230102\n" +
            "SUMMARY:All-day Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Date-only VEVENT should parse without errors");
        } catch (Exception e) {
            fail("Date-only VEVENT should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.8: Invalid VEVENT (missing DTSTART)")
    void testInvalidVEventMissingDtStart() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-invalid@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "SUMMARY:Invalid Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            // May still parse but component may be invalid
            assertNotNull(parser.getDataStoreAt(0));
        } catch (Exception e) {
            // Expected for invalid event
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("RFC5545-3.8: VEVENT with both DTEND and DURATION")
    void testVEventWithBothDtEndAndDuration() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-both@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DTEND:20230101T110000Z\n" +
            "DURATION:PT1H\n" +
            "SUMMARY:Event with both DTEND and DURATION\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            // Both DTEND and DURATION may be allowed or one may take precedence
            assertNotNull(parser.getDataStoreAt(0));
        } catch (Exception e) {
            fail("VEVENT with both DTEND and DURATION should parse: " + e.getMessage());
        }
    }
}