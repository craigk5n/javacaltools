package us.k5n.ical;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;

/**
 * RFC 5545: Property Parameter Validation Tests
 *
 * Tests for complete parameter validation rules as defined in RFC 5545.
 * Covers parameter syntax, allowed values, and error handling.
 */
public class RFC5545_PropertyParameterTest {

    @Test
    @DisplayName("RFC 5545: VALUE parameter validation")
    void testValueParameterValidation() {
        // Test valid VALUE parameters
        String[] validValues = {"BINARY", "BOOLEAN", "CAL-ADDRESS", "DATE", "DATE-TIME", "DURATION", "FLOAT", "INTEGER", "PERIOD", "RECUR", "TEXT", "TIME", "URI", "UTC-OFFSET"};

        for (String value : validValues) {
            String icalData = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VEVENT\nUID:test@example.com\nDTSTAMP:20240101T100000Z\nDTSTART:20240101T140000Z\nSUMMARY:Test\n" +
                "X-CUSTOM;VALUE=" + value + ":test value\nEND:VEVENT\nEND:VCALENDAR";

            try {
                ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
                parser.parse(new StringReader(icalData));
                assertTrue(parser.getAllErrors().isEmpty(), "VALUE=" + value + " should be accepted in loose mode");
            } catch (Exception e) {
                fail("VALUE=" + value + " should parse: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("RFC 5545: LANGUAGE parameter")
    void testLanguageParameter() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY;LANGUAGE=en:Meeting\n" +
            "SUMMARY;LANGUAGE=fr:Réunion\n" +
            "DESCRIPTION;LANGUAGE=en:English description\n" +
            "DESCRIPTION;LANGUAGE=es:Descripción en español\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "LANGUAGE parameters should parse without errors");

        } catch (Exception e) {
            fail("LANGUAGE parameters should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 5545: TZID parameter validation")
    void testTzidParameter() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20231105T020000\n" +
            "TZOFFSETFROM:-0400\n" +
            "TZOFFSETTO:-0500\n" +
            "END:STANDARD\n" +
            "END:VTIMEZONE\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART;TZID=America/New_York:20240101T140000\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "TZID parameter should parse without errors");

        } catch (Exception e) {
            fail("TZID parameter should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 5545: CUTYPE parameter values")
    void testCutypeParameter() {
        String[] cutypes = {"INDIVIDUAL", "GROUP", "RESOURCE", "ROOM", "UNKNOWN"};

        for (String cutype : cutypes) {
            String icalData = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VEVENT\nUID:test@example.com\nDTSTAMP:20240101T100000Z\nDTSTART:20240101T140000Z\nSUMMARY:Test\n" +
                "ATTENDEE;CUTYPE=" + cutype + ":mailto:test@example.com\nEND:VEVENT\nEND:VCALENDAR";

            try {
                ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
                parser.parse(new StringReader(icalData));
                assertTrue(parser.getAllErrors().isEmpty(), "CUTYPE=" + cutype + " should be valid");
            } catch (Exception e) {
                fail("CUTYPE=" + cutype + " should parse: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("RFC 5545: ROLE parameter values")
    void testRoleParameter() {
        String[] roles = {"CHAIR", "REQ-PARTICIPANT", "OPT-PARTICIPANT", "NON-PARTICIPANT"};

        for (String role : roles) {
            String icalData = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VEVENT\nUID:test@example.com\nDTSTAMP:20240101T100000Z\nDTSTART:20240101T140000Z\nSUMMARY:Test\n" +
                "ATTENDEE;ROLE=" + role + ":mailto:test@example.com\nEND:VEVENT\nEND:VCALENDAR";

            try {
                ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
                parser.parse(new StringReader(icalData));
                assertTrue(parser.getAllErrors().isEmpty(), "ROLE=" + role + " should be valid");
            } catch (Exception e) {
                fail("ROLE=" + role + " should parse: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("RFC 5545: PARTSTAT parameter values")
    void testPartstatParameter() {
        String[] partstats = {"NEEDS-ACTION", "ACCEPTED", "DECLINED", "TENTATIVE", "DELEGATED", "COMPLETED", "IN-PROCESS"};

        for (String partstat : partstats) {
            String icalData = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VEVENT\nUID:test@example.com\nDTSTAMP:20240101T100000Z\nDTSTART:20240101T140000Z\nSUMMARY:Test\n" +
                "ATTENDEE;PARTSTAT=" + partstat + ":mailto:test@example.com\nEND:VEVENT\nEND:VCALENDAR";

            try {
                ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
                parser.parse(new StringReader(icalData));
                assertTrue(parser.getAllErrors().isEmpty(), "PARTSTAT=" + partstat + " should be valid");
            } catch (Exception e) {
                fail("PARTSTAT=" + partstat + " should parse: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("RFC 5545: RSVP parameter")
    void testRsvpParameter() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test Event\n" +
            "ATTENDEE;RSVP=TRUE:mailto:attendee@example.com\n" +
            "ATTENDEE;RSVP=FALSE:mailto:chair@example.com\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "RSVP parameters should parse without errors");

        } catch (Exception e) {
            fail("RSVP parameters should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 5545: Multiple parameters on single property")
    void testMultipleParameters() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test Event\n" +
            "ATTENDEE;CUTYPE=INDIVIDUAL;ROLE=REQ-PARTICIPANT;PARTSTAT=ACCEPTED;RSVP=FALSE;CN=John Doe:mailto:john@example.com\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple parameters should parse without errors");

        } catch (Exception e) {
            fail("Multiple parameters should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 5545: Parameter quoting and escaping")
    void testParameterQuoting() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test Event\n" +
            "ATTENDEE;CN=\"Dr. John \\\"Johnny\\\" Doe\":mailto:john@example.com\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Quoted parameter values should parse without errors");

        } catch (Exception e) {
            fail("Quoted parameter values should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 5545: Invalid parameter handling")
    void testInvalidParameterHandling() {
        // Test invalid parameter name
        String icalData = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\nUID:test@example.com\nDTSTAMP:20240101T100000Z\nDTSTART:20240101T140000Z\nSUMMARY:Test\n" +
            "ATTENDEE;INVALID-PARAM=test:mailto:test@example.com\nEND:VEVENT\nEND:VCALENDAR";

        // In loose mode, should accept unknown parameters
        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Unknown parameters should be accepted in loose mode");
        } catch (Exception e) {
            fail("Unknown parameters should parse in loose mode: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 5545: Parameter case sensitivity")
    void testParameterCaseSensitivity() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T140000Z\n" +
            "SUMMARY:Test Event\n" +
            "ATTENDEE;cutype=individual;ROLE=req-participant:mailto:test@example.com\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Case-insensitive parameters should parse without errors");

        } catch (Exception e) {
            fail("Case-insensitive parameters should parse: " + e.getMessage());
        }
    }
}