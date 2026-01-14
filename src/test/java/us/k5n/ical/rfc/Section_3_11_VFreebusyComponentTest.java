package us.k5n.ical.rfc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;

/**
 * RFC 5545 Section 3.11: VFREEBUSY Component
 *
 * Tests for the VFREEBUSY calendar component, including required properties,
 * optional properties, and component-specific validation rules.
 */
public class Section_3_11_VFreebusyComponentTest {

    @Test
    @DisplayName("RFC5545-3.11: VFREEBUSY with required properties")
    void testVFreebusyRequiredProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VFREEBUSY\n" +
            "UID:freebusy-required@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T000000Z\n" +
            "DTEND:20230102T000000Z\n" +
            "FREEBUSY:FREE,20230101T100000Z/20230101T110000Z\n" +
            "END:VFREEBUSY\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VFREEBUSY with required properties should parse without errors");
        } catch (Exception e) {
            fail("VFREEBUSY with required properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.11: VFREEBUSY with ORGANIZER")
    void testVFreebusyWithOrganizer() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VFREEBUSY\n" +
            "UID:freebusy-organizer@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T000000Z\n" +
            "DTEND:20230102T000000Z\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "FREEBUSY:BUSY,20230101T100000Z/20230101T110000Z\n" +
            "END:VFREEBUSY\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VFREEBUSY with ORGANIZER should parse without errors");
        } catch (Exception e) {
            fail("VFREEBUSY with ORGANIZER should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.11: VFREEBUSY with multiple FREEBUSY periods")
    void testVFreebusyWithMultipleFreebusyPeriods() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VFREEBUSY\n" +
            "UID:freebusy-multi@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T000000Z\n" +
            "DTEND:20230108T000000Z\n" +
            "FREEBUSY:BUSY,20230101T090000Z/20230101T110000Z\n" +
            "FREEBUSY:BUSY,20230102T140000Z/20230102T160000Z\n" +
            "FREEBUSY:TENTATIVE,20230103T100000Z/20230103T120000Z\n" +
            "END:VFREEBUSY\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VFREEBUSY with multiple FREEBUSY periods should parse without errors");
        } catch (Exception e) {
            fail("VFREEBUSY with multiple FREEBUSY periods should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.11: VFREEBUSY with attendees")
    void testVFreebusyWithAttendees() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VFREEBUSY\n" +
            "UID:freebusy-attendees@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T000000Z\n" +
            "DTEND:20230102T000000Z\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee1@example.com\n" +
            "ATTENDEE:mailto:attendee2@example.com\n" +
            "FREEBUSY:BUSY,20230101T100000Z/20230101T110000Z\n" +
            "END:VFREEBUSY\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VFREEBUSY with attendees should parse without errors");
        } catch (Exception e) {
            fail("VFREEBUSY with attendees should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.11: VFREEBUSY with COMMENT and CONTACT")
    void testVFreebusyWithCommentAndContact() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VFREEBUSY\n" +
            "UID:freebusy-comment@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T000000Z\n" +
            "DTEND:20230102T000000Z\n" +
            "COMMENT:This is a freebusy response\n" +
            "CONTACT:Phone: 555-1234\n" +
            "FREEBUSY:FREE,20230101T000000Z/20230102T000000Z\n" +
            "END:VFREEBUSY\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VFREEBUSY with COMMENT and CONTACT should parse without errors");
        } catch (Exception e) {
            fail("VFREEBUSY with COMMENT and CONTACT should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.11: VFREEBUSY with URL")
    void testVFreebusyWithUrl() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VFREEBUSY\n" +
            "UID:freebusy-url@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T000000Z\n" +
            "DTEND:20230102T000000Z\n" +
            "URL:http://example.com/freebusy\n" +
            "FREEBUSY:BUSY-UNAVAILABLE,20230101T100000Z/20230101T110000Z\n" +
            "END:VFREEBUSY\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VFREEBUSY with URL should parse without errors");
        } catch (Exception e) {
            fail("VFREEBUSY with URL should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.11: VFREEBUSY freebusy type validation")
    void testVFreebusyTypeValidation() {
        String[] validTypes = {"FREE", "BUSY", "BUSY-UNAVAILABLE", "BUSY-TENTATIVE"};

        for (String type : validTypes) {
            String icalData = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VFREEBUSY\n" +
                "UID:freebusy-type-" + type.toLowerCase() + "@example.com\n" +
                "DTSTAMP:20230101T100000Z\n" +
                "DTSTART:20230101T000000Z\n" +
                "DTEND:20230102T000000Z\n" +
                "FREEBUSY:" + type + ",20230101T100000Z/20230101T110000Z\n" +
                "END:VFREEBUSY\n" +
                "END:VCALENDAR";

            try {
                ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
                parser.parse(new StringReader(icalData));
                assertTrue(parser.getAllErrors().isEmpty(), "VFREEBUSY with type " + type + " should parse without errors");
            } catch (Exception e) {
                fail("VFREEBUSY with type " + type + " should parse: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("RFC5545-3.11: VFREEBUSY with all optional properties")
    void testVFreebusyWithAllOptionalProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VFREEBUSY\n" +
            "UID:freebusy-complete@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T000000Z\n" +
            "DTEND:20230102T000000Z\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee@example.com\n" +
            "COMMENT:Freebusy information\n" +
            "CONTACT:Phone: 555-1234\n" +
            "URL:http://example.com/freebusy\n" +
            "FREEBUSY:BUSY,20230101T090000Z/20230101T110000Z\n" +
            "FREEBUSY:FREE,20230101T110000Z/20230101T140000Z\n" +
            "END:VFREEBUSY\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VFREEBUSY with all optional properties should parse without errors");
        } catch (Exception e) {
            fail("VFREEBUSY with all optional properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.11: VFREEBUSY period format validation")
    void testVFreebusyPeriodFormat() {
        // Test different period formats: START/END and START/DURATION
        String[] periodFormats = {
            "20230101T100000Z/20230101T110000Z",  // START/END
            "20230101T100000Z/PT1H"             // START/DURATION
        };

        for (String period : periodFormats) {
            String icalData = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VFREEBUSY\n" +
                "UID:freebusy-period@example.com\n" +
                "DTSTAMP:20230101T100000Z\n" +
                "DTSTART:20230101T000000Z\n" +
                "DTEND:20230102T000000Z\n" +
                "FREEBUSY:BUSY," + period + "\n" +
                "END:VFREEBUSY\n" +
                "END:VCALENDAR";

            try {
                ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
                parser.parse(new StringReader(icalData));
                assertTrue(parser.getAllErrors().isEmpty(), "VFREEBUSY with period format " + period + " should parse without errors");
            } catch (Exception e) {
                fail("VFREEBUSY with period format " + period + " should parse: " + e.getMessage());
            }
        }
    }
}