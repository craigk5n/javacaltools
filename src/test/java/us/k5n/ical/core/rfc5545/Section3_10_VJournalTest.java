package us.k5n.ical.core.rfc5545;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.k5n.ical.ICalendarParser;

/**
 * RFC 5545 Section 3.10: VJOURNAL Component
 *
 * Tests for the VJOURNAL calendar component, including required properties,
 * optional properties, and component-specific validation rules.
 */
public class Section3_10_VJournalTest {

    @Test
    @DisplayName("RFC5545-3.10: VJOURNAL with required properties")
    void testVJournalRequiredProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal-required@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "SUMMARY:Test Journal Entry\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VJOURNAL with required properties should parse without errors");
        } catch (Exception e) {
            fail("VJOURNAL with required properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.10: VJOURNAL with DTSTART")
    void testVJournalWithDtStart() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal-dtstart@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Journal with start time\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VJOURNAL with DTSTART should parse without errors");
        } catch (Exception e) {
            fail("VJOURNAL with DTSTART should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.10: VJOURNAL with recurrence")
    void testVJournalWithRecurrence() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal-recur@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "RRULE:FREQ=DAILY;COUNT=7\n" +
            "SUMMARY:Daily Journal\n" +
            "DESCRIPTION:Daily journal entry\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VJOURNAL with recurrence should parse without errors");
        } catch (Exception e) {
            fail("VJOURNAL with recurrence should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.10: VJOURNAL with attendees")
    void testVJournalWithAttendees() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal-attendees@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "ORGANIZER:mailto:author@example.com\n" +
            "ATTENDEE;CN=Reviewer;ROLE=REQ-PARTICIPANT:mailto:reviewer@example.com\n" +
            "SUMMARY:Journal with attendees\n" +
            "DESCRIPTION:Journal entry for review\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VJOURNAL with attendees should parse without errors");
        } catch (Exception e) {
            fail("VJOURNAL with attendees should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.10: VJOURNAL with status")
    void testVJournalWithStatus() {
        String[] validStatuses = {"DRAFT", "FINAL", "CANCELLED"};

        for (String status : validStatuses) {
            String icalData = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VJOURNAL\n" +
                "UID:journal-status-" + status.toLowerCase() + "@example.com\n" +
                "DTSTAMP:20230101T100000Z\n" +
                "DTSTART:20230101T100000Z\n" +
                "STATUS:" + status + "\n" +
                "SUMMARY:Journal with " + status + " status\n" +
                "DESCRIPTION:Status test entry\n" +
                "END:VJOURNAL\n" +
                "END:VCALENDAR";

            try {
                ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
                parser.parse(new StringReader(icalData));
                assertTrue(parser.getAllErrors().isEmpty(), "VJOURNAL with status " + status + " should parse without errors");
            } catch (Exception e) {
                fail("VJOURNAL with status " + status + " should parse: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("RFC5545-3.10: VJOURNAL with all optional properties")
    void testVJournalWithAllOptionalProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal-complete@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Complete Journal Entry\n" +
            "DESCRIPTION:Detailed journal description\n" +
            "CLASS:PUBLIC\n" +
            "CATEGORIES:WORK,PERSONAL\n" +
            "URL:http://example.com/journal\n" +
            "COMMENT:Additional comment\n" +
            "CONTACT:Phone: 555-1234\n" +
            "ORGANIZER:mailto:author@example.com\n" +
            "ATTENDEE;CN=Reader;ROLE=OPT-PARTICIPANT:mailto:reader@example.com\n" +
            "RELATED-TO:parent-entry@example.com\n" +
            "RRULE:FREQ=WEEKLY\n" +
            "EXDATE:20230108T100000Z\n" +
            "RDATE:20230115T100000Z\n" +
            "SEQUENCE:1\n" +
            "STATUS:FINAL\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VJOURNAL with all optional properties should parse without errors");
        } catch (Exception e) {
            fail("VJOURNAL with all optional properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.10: VJOURNAL with attachments")
    void testVJournalWithAttachments() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal-attach@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Journal with attachments\n" +
            "DESCRIPTION:Journal entry with media\n" +
            "ATTACH:http://example.com/document.pdf\n" +
            "ATTACH;ENCODING=BASE64;FMTTYPE=text/plain:SGVsbG8gV29ybGQ=\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VJOURNAL with attachments should parse without errors");
        } catch (Exception e) {
            fail("VJOURNAL with attachments should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.10: VJOURNAL with alarms")
    void testVJournalWithAlarms() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal-alarm@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Journal with alarm\n" +
            "DESCRIPTION:Journal entry reminder\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT1H\n" +
            "DESCRIPTION:Journal entry due\n" +
            "END:VALARM\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VJOURNAL with alarms should parse without errors");
        } catch (Exception e) {
            fail("VJOURNAL with alarms should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.10: Multiple VJOURNAL components")
    void testMultipleVJournalComponents() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal1@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:First journal entry\n" +
            "END:VJOURNAL\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal2@example.com\n" +
            "DTSTAMP:20230102T100000Z\n" +
            "DTSTART:20230102T100000Z\n" +
            "SUMMARY:Second journal entry\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple VJOURNAL components should parse without errors");
        } catch (Exception e) {
            fail("Multiple VJOURNAL components should parse: " + e.getMessage());
        }
    }
}
