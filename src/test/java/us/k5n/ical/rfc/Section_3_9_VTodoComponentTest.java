package us.k5n.ical.rfc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.k5n.ical.ICalendarParser;

/**
 * RFC 5545 Section 3.9: VTODO Component
 *
 * Tests for the VTODO calendar component, including required properties,
 * optional properties, and component-specific validation rules.
 */
public class Section_3_9_VTodoComponentTest {

    @Test
    @DisplayName("RFC5545-3.9: VTODO with required properties")
    void testVTodoRequiredProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-required@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "SUMMARY:Test Todo\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTODO with required properties should parse without errors");
        } catch (Exception e) {
            fail("VTODO with required properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.9: VTODO with DTSTART and DUE")
    void testVTodoWithDtStartAndDue() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-dates@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DUE:20230101T110000Z\n" +
            "SUMMARY:Todo with dates\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTODO with DTSTART and DUE should parse without errors");
        } catch (Exception e) {
            fail("VTODO with DTSTART and DUE should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.9: VTODO with DURATION")
    void testVTodoWithDuration() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-duration@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DURATION:PT2H\n" +
            "SUMMARY:Todo with duration\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTODO with DURATION should parse without errors");
        } catch (Exception e) {
            fail("VTODO with DURATION should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.9: VTODO with recurrence")
    void testVTodoWithRecurrence() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-recur@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "RRULE:FREQ=WEEKLY;COUNT=4\n" +
            "SUMMARY:Recurring Todo\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTODO with recurrence should parse without errors");
        } catch (Exception e) {
            fail("VTODO with recurrence should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.9: VTODO with alarms")
    void testVTodoWithAlarms() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-alarms@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Todo with Alarms\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT15M\n" +
            "DESCRIPTION:Todo reminder\n" +
            "END:VALARM\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTODO with alarms should parse without errors");
        } catch (Exception e) {
            fail("VTODO with alarms should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.9: VTODO with attendees")
    void testVTodoWithAttendees() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-attendees@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE;CN=Assignee;ROLE=REQ-PARTICIPANT:mailto:assignee@example.com\n" +
            "SUMMARY:Todo with attendees\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTODO with attendees should parse without errors");
        } catch (Exception e) {
            fail("VTODO with attendees should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.9: VTODO with completion properties")
    void testVTodoWithCompletionProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-complete@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DUE:20230102T100000Z\n" +
            "COMPLETED:20230101T120000Z\n" +
            "PERCENT-COMPLETE:100\n" +
            "STATUS:COMPLETED\n" +
            "SUMMARY:Completed Todo\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTODO with completion properties should parse without errors");
        } catch (Exception e) {
            fail("VTODO with completion properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.9: VTODO with all optional properties")
    void testVTodoWithAllOptionalProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-complete@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DUE:20230102T100000Z\n" +
            "SUMMARY:Complete Todo\n" +
            "DESCRIPTION:Detailed todo description\n" +
            "LOCATION:Office\n" +
            "PRIORITY:1\n" +
            "STATUS:IN-PROCESS\n" +
            "SEQUENCE:1\n" +
            "CATEGORIES:WORK,URGENT\n" +
            "URL:http://example.com/todo\n" +
            "COMMENT:Additional comment\n" +
            "CONTACT:Phone: 555-1234\n" +
            "RELATED-TO:parent-todo@example.com\n" +
            "RRULE:FREQ=DAILY\n" +
            "EXDATE:20230103T100000Z\n" +
            "RDATE:20230105T100000Z\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTODO with all optional properties should parse without errors");
        } catch (Exception e) {
            fail("VTODO with all optional properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.9: VTODO priority validation")
    void testVTodoPriorityValidation() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-priority@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "PRIORITY:5\n" +
            "SUMMARY:Priority Todo\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTODO with valid priority should parse without errors");
        } catch (Exception e) {
            fail("VTODO with valid priority should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.9: VTODO status transitions")
    void testVTodoStatusTransitions() {
        // Test different valid status values for VTODO
        String[] validStatuses = {"NEEDS-ACTION", "IN-PROCESS", "COMPLETED", "CANCELLED"};

        for (String status : validStatuses) {
            String icalData = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VTODO\n" +
                "UID:todo-status-" + status.toLowerCase() + "@example.com\n" +
                "DTSTAMP:20230101T100000Z\n" +
                "DTSTART:20230101T100000Z\n" +
                "STATUS:" + status + "\n" +
                "SUMMARY:Todo with " + status + " status\n" +
                "END:VTODO\n" +
                "END:VCALENDAR";

            try {
                ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
                parser.parse(new StringReader(icalData));
                assertTrue(parser.getAllErrors().isEmpty(), "VTODO with status " + status + " should parse without errors");
            } catch (Exception e) {
                fail("VTODO with status " + status + " should parse: " + e.getMessage());
            }
        }
    }
}