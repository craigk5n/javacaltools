package us.k5n.ical.rfc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;

/**
 * RFC 5545 Section 3.2: Formatting Conventions
 *
 * Tests for line folding, unfolding, and other formatting rules.
 */
public class Section_3_2_FormattingConventionsTest {

    @Test
    @DisplayName("RFC5545-3.2: Line folding (75 octet rule)")
    void testLineFolding() throws Exception {
        // Create a calendar with a long line that should be folded
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "SUMMARY:This is a very long summary that should exceed the 75 octet folding rule and be split across multiple lines properly\n" +
            "DTSTART:20230101T100000Z\n" +
            "DTEND:20230101T110000Z\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Should parse folded lines without errors");

        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertNotNull(ds.getAllEvents().get(0), "Event should be parsed correctly");
    }

    @Test
    @DisplayName("RFC5545-3.2: Line unfolding")
    void testLineUnfolding() throws Exception {
        // Test that CRLF followed by space is properly unfolded
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "SUMMARY:This is a folded line that\n" +
            " should be unfolded properly\n" +
            "DTSTART:20230101T100000Z\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Should handle folded lines correctly");
    }

    @Test
    @DisplayName("RFC5545-3.2: Case insensitive enumerated values")
    void testCaseInsensitiveEnumeratedValues() throws Exception {
        // Enumerated values like STATUS and CLASS should be case insensitive
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTART:20230101T100000Z\n" +
            "DTEND:20230101T110000Z\n" +
            "STATUS:confirmed\n" +
            "CLASS:public\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Should handle case insensitive enumerated values");
    }

    @Test
    @DisplayName("RFC5545-3.2: Line termination with CRLF")
    void testCRLFLineTermination() throws Exception {
        // All lines should be terminated with CRLF
        String icalData = "BEGIN:VCALENDAR\r\n" +
            "VERSION:2.0\r\n" +
            "PRODID:-//Test//Calendar//EN\r\n" +
            "BEGIN:VEVENT\r\n" +
            "UID:test@example.com\r\n" +
            "DTSTART:20230101T100000Z\r\n" +
            "END:VEVENT\r\n" +
            "END:VCALENDAR\r\n";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Should handle CRLF line termination correctly");
    }

    @Test
    @DisplayName("RFC5545-3.2: Long property values folding")
    void testLongPropertyValues() throws Exception {
        // Property values longer than 75 octets should be folded
        String longDescription = "This is a very long description that exceeds the 75 octet folding rule. " +
            "It should be properly folded across multiple lines with CRLF followed by a space character. " +
            "This ensures that the iCalendar data remains compliant with the RFC specifications.";

        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTART:20230101T100000Z\n" +
            "DESCRIPTION:" + longDescription + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Should handle long property values correctly");
    }
}