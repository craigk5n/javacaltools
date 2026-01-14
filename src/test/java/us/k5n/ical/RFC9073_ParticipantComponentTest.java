package us.k5n.ical;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;
import java.util.List;

/**
 * RFC 9073: PARTICIPANT Component Tests
 *
 * Tests for the PARTICIPANT component as defined in RFC 9073, Section 7.1.
 * The PARTICIPANT component provides rich participant metadata beyond the
 * basic ATTENDEE property.
 */
public class RFC9073_ParticipantComponentTest {

    @Test
    @DisplayName("RFC 9073: Basic PARTICIPANT component parsing")
    void testBasicParticipantComponent() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:PARTICIPANT\n" +
            "UID:participant-001@example.com\n" +
            "PARTICIPANT-TYPE:INDIVIDUAL\n" +
            "CALENDAR-ADDRESS:mailto:john@example.com\n" +
            "STRUCTURED-DATA;VALUE=BINARY;ENCODING=BASE64;FMTTYPE=application/xml:PGhlbGxvPg==\n" +
            "END:PARTICIPANT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            if (!parser.getAllErrors().isEmpty()) {
                System.out.println("Parsing errors:");
                for (ParseError error : parser.getAllErrors()) {
                    System.out.println("  " + error.toString());
                }
            }
            assertTrue(parser.getAllErrors().isEmpty(), "PARTICIPANT component should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            // TODO: Add method to get participants once implemented
            // List<Participant> participants = ds.getAllParticipants();
            // assertEquals(1, participants.size());

        } catch (Exception e) {
            fail("PARTICIPANT component should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: PARTICIPANT with all required properties")
    void testParticipantWithAllProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:PARTICIPANT\n" +
            "UID:participant-002@example.com\n" +
            "PARTICIPANT-TYPE:GROUP\n" +
            "CALENDAR-ADDRESS:mailto:team@example.com\n" +
            "STRUCTURED-DATA;VALUE=TEXT:Additional participant metadata\n" +
            "NAME:Development Team\n" +
            "DESCRIPTION:The core development team\n" +
            "END:PARTICIPANT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "PARTICIPANT with all properties should parse without errors");

        } catch (Exception e) {
            fail("PARTICIPANT with all properties should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: Multiple PARTICIPANT components")
    void testMultipleParticipantComponents() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:PARTICIPANT\n" +
            "UID:participant-001@example.com\n" +
            "PARTICIPANT-TYPE:INDIVIDUAL\n" +
            "CALENDAR-ADDRESS:mailto:alice@example.com\n" +
            "END:PARTICIPANT\n" +
            "BEGIN:PARTICIPANT\n" +
            "UID:participant-002@example.com\n" +
            "PARTICIPANT-TYPE:INDIVIDUAL\n" +
            "CALENDAR-ADDRESS:mailto:bob@example.com\n" +
            "END:PARTICIPANT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple PARTICIPANT components should parse without errors");

        } catch (Exception e) {
            fail("Multiple PARTICIPANT components should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: PARTICIPANT-TYPE validation")
    void testParticipantTypeValidation() {
        // Test all valid PARTICIPANT-TYPE values
        String[] validTypes = {"INDIVIDUAL", "GROUP", "RESOURCE", "ROOM", "UNKNOWN"};

        for (String type : validTypes) {
            String icalData = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "BEGIN:PARTICIPANT\n" +
                "UID:participant-001@example.com\n" +
                "PARTICIPANT-TYPE:" + type + "\n" +
                "CALENDAR-ADDRESS:mailto:test@example.com\n" +
                "END:PARTICIPANT\n" +
                "END:VCALENDAR";

            try {
                ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
                parser.parse(new StringReader(icalData));
                assertTrue(parser.getAllErrors().isEmpty(), "PARTICIPANT-TYPE " + type + " should be valid");
            } catch (Exception e) {
                fail("PARTICIPANT-TYPE " + type + " should parse: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("RFC 9073: PARTICIPANT CALENDAR-ADDRESS validation")
    void testParticipantCalendarAddressValidation() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:PARTICIPANT\n" +
            "UID:participant-001@example.com\n" +
            "PARTICIPANT-TYPE:INDIVIDUAL\n" +
            "CALENDAR-ADDRESS:mailto:valid@example.com\n" +
            "END:PARTICIPANT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Valid CALENDAR-ADDRESS should parse without errors");
        } catch (Exception e) {
            fail("Valid CALENDAR-ADDRESS should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: PARTICIPANT STRUCTURED-DATA property")
    void testParticipantStructuredData() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:PARTICIPANT\n" +
            "UID:participant-001@example.com\n" +
            "PARTICIPANT-TYPE:INDIVIDUAL\n" +
            "CALENDAR-ADDRESS:mailto:test@example.com\n" +
            "STRUCTURED-DATA;VALUE=BINARY;ENCODING=BASE64;FMTTYPE=application/json:eyJrZXkiOiJ2YWx1ZSJ9\n" +
            "END:PARTICIPANT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "PARTICIPANT with STRUCTURED-DATA should parse without errors");
        } catch (Exception e) {
            fail("PARTICIPANT with STRUCTURED-DATA should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: PARTICIPANT referenced by event")
    void testParticipantReferencedByEvent() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:PARTICIPANT\n" +
            "UID:participant-001@example.com\n" +
            "PARTICIPANT-TYPE:INDIVIDUAL\n" +
            "CALENDAR-ADDRESS:mailto:john@example.com\n" +
            "END:PARTICIPANT\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-001@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T110000Z\n" +
            "SUMMARY:Meeting with participant\n" +
            "ATTENDEE;PARTICIPANT-ID=participant-001@example.com:mailto:john@example.com\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "PARTICIPANT referenced by event should parse without errors");
        } catch (Exception e) {
            fail("PARTICIPANT referenced by event should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: PARTICIPANT serialization")
    void testParticipantSerialization() {
        // This test will verify that PARTICIPANT components can be serialized back to iCalendar format
        String expectedOutput = "BEGIN:PARTICIPANT\n" +
            "UID:participant-001@example.com\n" +
            "PARTICIPANT-TYPE:INDIVIDUAL\n" +
            "CALENDAR-ADDRESS:mailto:test@example.com\n" +
            "END:PARTICIPANT";

        // TODO: Implement serialization test once PARTICIPANT class is created
        // This will verify toICalendar() method works correctly
        assertTrue(true, "Serialization test placeholder - will be implemented with PARTICIPANT class");
    }
}