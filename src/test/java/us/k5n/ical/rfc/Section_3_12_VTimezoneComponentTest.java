package us.k5n.ical.rfc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;

/**
 * RFC 5545 Section 3.12: VTIMEZONE Component
 *
 * Tests for the VTIMEZONE calendar component, including required properties,
 * optional properties, and timezone-specific validation rules.
 */
public class Section_3_12_VTimezoneComponentTest {

    @Test
    @DisplayName("RFC5545-3.12: VTIMEZONE with STANDARD subcomponent")
    void testVTimezoneWithStandard() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20221106T020000\n" +
            "TZOFFSETFROM:-0400\n" +
            "TZOFFSETTO:-0500\n" +
            "TZNAME:EST\n" +
            "END:STANDARD\n" +
            "END:VTIMEZONE\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-timezone@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART;TZID=America/New_York:20230101T100000\n" +
            "SUMMARY:Event with timezone\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTIMEZONE with STANDARD should parse without errors");
        } catch (Exception e) {
            fail("VTIMEZONE with STANDARD should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.12: VTIMEZONE with DAYLIGHT subcomponent")
    void testVTimezoneWithDaylight() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20221106T020000\n" +
            "TZOFFSETFROM:-0400\n" +
            "TZOFFSETTO:-0500\n" +
            "TZNAME:EST\n" +
            "END:STANDARD\n" +
            "BEGIN:DAYLIGHT\n" +
            "DTSTART:20230312T020000\n" +
            "TZOFFSETFROM:-0500\n" +
            "TZOFFSETTO:-0400\n" +
            "TZNAME:EDT\n" +
            "END:DAYLIGHT\n" +
            "END:VTIMEZONE\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-daylight@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART;TZID=America/New_York:20230601T100000\n" +
            "SUMMARY:Event during daylight saving\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTIMEZONE with DAYLIGHT should parse without errors");
        } catch (Exception e) {
            fail("VTIMEZONE with DAYLIGHT should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.12: VTIMEZONE with both STANDARD and DAYLIGHT")
    void testVTimezoneWithBothStandardAndDaylight() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "TZURL:http://tzurl.org/zoneinfo/America/New_York\n" +
            "X-LIC-LOCATION:America/New_York\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20221106T020000\n" +
            "TZOFFSETFROM:-0400\n" +
            "TZOFFSETTO:-0500\n" +
            "TZNAME:EST\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=11;BYDAY=1SU\n" +
            "END:STANDARD\n" +
            "BEGIN:DAYLIGHT\n" +
            "DTSTART:20230312T020000\n" +
            "TZOFFSETFROM:-0500\n" +
            "TZOFFSETTO:-0400\n" +
            "TZNAME:EDT\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=2SU\n" +
            "END:DAYLIGHT\n" +
            "END:VTIMEZONE\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTIMEZONE with both STANDARD and DAYLIGHT should parse without errors");
        } catch (Exception e) {
            fail("VTIMEZONE with both STANDARD and DAYLIGHT should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.12: VTIMEZONE with TZURL and X-LIC-LOCATION")
    void testVTimezoneWithUrlAndLicLocation() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:Europe/London\n" +
            "TZURL:http://tzurl.org/zoneinfo/Europe/London\n" +
            "X-LIC-LOCATION:Europe/London\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20221030T010000\n" +
            "TZOFFSETFROM:+0100\n" +
            "TZOFFSETTO:+0000\n" +
            "TZNAME:GMT\n" +
            "END:STANDARD\n" +
            "BEGIN:DAYLIGHT\n" +
            "DTSTART:20230326T010000\n" +
            "TZOFFSETFROM:+0000\n" +
            "TZOFFSETTO:+0100\n" +
            "TZNAME:BST\n" +
            "END:DAYLIGHT\n" +
            "END:VTIMEZONE\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTIMEZONE with TZURL and X-LIC-LOCATION should parse without errors");
        } catch (Exception e) {
            fail("VTIMEZONE with TZURL and X-LIC-LOCATION should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.12: VTIMEZONE STANDARD with RRULE")
    void testVTimezoneStandardWithRrule() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20071104T020000\n" +
            "TZOFFSETFROM:-0400\n" +
            "TZOFFSETTO:-0500\n" +
            "TZNAME:EST\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=11;BYDAY=1SU\n" +
            "END:STANDARD\n" +
            "END:VTIMEZONE\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTIMEZONE STANDARD with RRULE should parse without errors");
        } catch (Exception e) {
            fail("VTIMEZONE STANDARD with RRULE should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.12: VTIMEZONE DAYLIGHT with RRULE")
    void testVTimezoneDaylightWithRrule() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "BEGIN:DAYLIGHT\n" +
            "DTSTART:20070311T020000\n" +
            "TZOFFSETFROM:-0500\n" +
            "TZOFFSETTO:-0400\n" +
            "TZNAME:EDT\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=2SU\n" +
            "END:DAYLIGHT\n" +
            "END:VTIMEZONE\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTIMEZONE DAYLIGHT with RRULE should parse without errors");
        } catch (Exception e) {
            fail("VTIMEZONE DAYLIGHT with RRULE should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.12: VTIMEZONE with COMMENT")
    void testVTimezoneWithComment() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "COMMENT:Timezone definition for Eastern Time\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20221106T020000\n" +
            "TZOFFSETFROM:-0400\n" +
            "TZOFFSETTO:-0500\n" +
            "TZNAME:EST\n" +
            "END:STANDARD\n" +
            "END:VTIMEZONE\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTIMEZONE with COMMENT should parse without errors");
        } catch (Exception e) {
            fail("VTIMEZONE with COMMENT should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.12: Multiple VTIMEZONE definitions")
    void testMultipleVTimezoneDefinitions() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20221106T020000\n" +
            "TZOFFSETFROM:-0400\n" +
            "TZOFFSETTO:-0500\n" +
            "TZNAME:EST\n" +
            "END:STANDARD\n" +
            "END:VTIMEZONE\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:Europe/London\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20221030T010000\n" +
            "TZOFFSETFROM:+0100\n" +
            "TZOFFSETTO:+0000\n" +
            "TZNAME:GMT\n" +
            "END:STANDARD\n" +
            "END:VTIMEZONE\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple VTIMEZONE definitions should parse without errors");
        } catch (Exception e) {
            fail("Multiple VTIMEZONE definitions should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.12: VTIMEZONE referenced by event")
    void testVTimezoneReferencedByEvent() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20221106T020000\n" +
            "TZOFFSETFROM:-0400\n" +
            "TZOFFSETTO:-0500\n" +
            "TZNAME:EST\n" +
            "END:STANDARD\n" +
            "BEGIN:DAYLIGHT\n" +
            "DTSTART:20230312T020000\n" +
            "TZOFFSETFROM:-0500\n" +
            "TZOFFSETTO:-0400\n" +
            "TZNAME:EDT\n" +
            "END:DAYLIGHT\n" +
            "END:VTIMEZONE\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-timezone-ref@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART;TZID=America/New_York:20230101T100000\n" +
            "DTEND;TZID=America/New_York:20230101T110000\n" +
            "SUMMARY:Event referencing timezone\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VTIMEZONE referenced by event should parse without errors");
        } catch (Exception e) {
            fail("VTIMEZONE referenced by event should parse: " + e.getMessage());
        }
    }
}