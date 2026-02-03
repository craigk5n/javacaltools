package us.k5n.ical.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.k5n.ical.*;

/**
 * RFC Extensions Comprehensive Tests
 *
 * Tests for RFC 7986, RFC 9073, and RFC 9074 extensions to ensure
 * full coverage of modern iCalendar features.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class RFCExtensionsIntegrationTest {

    @Test
    @DisplayName("RFC 7986: COLOR property in VCALENDAR")
    void testCalendarColor() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "NAME:My Calendar\n" +
            "COLOR:#FF5733\n" +
            "BEGIN:VEVENT\n" +
            "UID:color-test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Calendar with COLOR should parse without errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertNotNull(ds, "Should have data store");
    }

    @Test
    @DisplayName("RFC 7986: Multiple calendar-level properties")
    void testMultipleCalendarLevelProperties() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "NAME:Extended Calendar\n" +
            "DESCRIPTION:A calendar with all RFC 7986 extensions\n" +
            "UID:calendar-uid@example.com\n" +
            "URL:https://calendar.example.com\n" +
            "LAST-MODIFIED:20240101T120000Z\n" +
            "REFRESH-INTERVAL;VALUE=DURATION:PT4H\n" +
            "SOURCE:https://calendar.example.com/feed.ics\n" +
            "COLOR:blue\n" +
            "BEGIN:VEVENT\n" +
            "UID:event@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Calendar with all extensions should parse");
    }

    @Test
    @DisplayName("RFC 9073: PARTICIPANT component with all properties")
    void testParticipantComponent() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:PARTICIPANT\n" +
            "UID:participant-001@example.com\n" +
            "PARTICIPANT-TYPE:INDIVIDUAL\n" +
            "CALENDAR-ADDRESS:mailto:john.doe@example.com\n" +
            "NAME:John Doe\n" +
            "DESCRIPTION:Primary organizer\n" +
            "END:PARTICIPANT\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-with-participant@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Meeting with Participant\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "PARTICIPANT component should parse");
    }

    @Test
    @DisplayName("RFC 9073: VLOCATION component")
    void testVLocationComponent() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VLOCATION\n" +
            "UID:location-001@example.com\n" +
            "LOCATION:Conference Room A\n" +
            "LOCATION-TYPE:CONFERENCE-ROOM\n" +
            "DESCRIPTION:Main conference room on 3rd floor\n" +
            "END:VLOCATION\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-with-location@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Meeting in Conference Room A\n" +
            "LOCATION:location-001@example.com\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "VLOCATION component should parse");
    }

    @Test
    @DisplayName("RFC 9073: VRESOURCE component")
    void testVResourceComponent() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VRESOURCE\n" +
            "UID:resource-001@example.com\n" +
            "RESOURCE-TYPE:PROJECTOR\n" +
            "DESCRIPTION:1080p Projector\n" +
            "END:VRESOURCE\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-with-resource@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Presentation\n" +
            "RESOURCES:resource-001@example.com\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "VRESOURCE component should parse");
    }

    @Test
    @DisplayName("RFC 9073: STYLED-DESCRIPTION property")
    void testStyledDescription() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:styled-test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Event with Styled Description\n" +
            "DESCRIPTION;ALTREP=\"data:text/html,<h1>Event Details</h1>\":\n" +
            " This is the plain text description\n" +
            "STYLED-DESCRIPTION;VALUE=URI;FMTTYPE=text/html:\n" +
            " https://example.com/descriptions/event-001.html\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "STYLED-DESCRIPTION should parse");
    }

    @Test
    @DisplayName("RFC 9074: VALARM with PROXIMITY")
    void testAlarmWithProximity() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:proximity-alarm-test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Meeting with Location Alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "DESCRIPTION:You're approaching the meeting location\n" +
            "TRIGGER:-PT15M\n" +
            "PROXIMITY:ARRIVE\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "VALARM with PROXIMITY should parse");
    }

    @Test
    @DisplayName("RFC 9074: VALARM with STRUCTURED-DATA")
    void testAlarmWithStructuredData() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:structured-alarm-test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Meeting with Structured Alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "DESCRIPTION:Meeting reminder\n" +
            "TRIGGER:-PT30M\n" +
            "STRUCTURED-DATA;VALUE=BINARY;ENCODING=BASE64;FMTTYPE=application/json:\n" +
            " eyJtZXR0aW5nSWQiOiJ0ZXN0LW1lZXRpbmcifQ==\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "VALARM with STRUCTURED-DATA should parse");
    }

    @Test
    @DisplayName("RFC 9073: Multiple PARTICIPANT-TYPE values")
    void testParticipantTypeValues() throws Exception {
        String[] participantTypes = {"INDIVIDUAL", "GROUP", "RESOURCE", "ROOM", "UNKNOWN"};

        for (String type : participantTypes) {
            String icalData =
                "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "BEGIN:PARTICIPANT\n" +
                "UID:participant-" + type + "@example.com\n" +
                "PARTICIPANT-TYPE:" + type + "\n" +
                "CALENDAR-ADDRESS:mailto:test@example.com\n" +
                "END:PARTICIPANT\n" +
                "END:VCALENDAR";

            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));

            assertTrue(parser.getAllErrors().isEmpty(),
                "PARTICIPANT-TYPE:" + type + " should parse without errors");
        }
    }

    @Test
    @DisplayName("RFC 9073: LOCATION-TYPE values")
    void testLocationTypeValues() throws Exception {
        String[] locationTypes = {
            "PHYSICAL", "VIRTUAL", "PHYSICAL-VIRTUAL",
            "CONFERENCE-ROOM", "AUDITORIUM", "OFFICE"
        };

        for (String type : locationTypes) {
            String icalData =
                "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VLOCATION\n" +
                "UID:location-" + type.replace("-", "") + "@example.com\n" +
                "LOCATION:Test Location\n" +
                "LOCATION-TYPE:" + type + "\n" +
                "END:VLOCATION\n" +
                "END:VCALENDAR";

            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));

            assertTrue(parser.getAllErrors().isEmpty(),
                "LOCATION-TYPE:" + type + " should parse without errors");
        }
    }

    @Test
    @DisplayName("RFC 9073: RESOURCE-TYPE values")
    void testResourceTypeValues() throws Exception {
        String[] resourceTypes = {
            "PROJECTOR", "WHITEBOARD", "VIDEO_DISPLAY",
            "COMPUTER", "AUDIO_CONFERENCING", "PHONE_CONFERENCE"
        };

        for (String type : resourceTypes) {
            String icalData =
                "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VRESOURCE\n" +
                "UID:resource-" + type.replace("_", "") + "@example.com\n" +
                "RESOURCE-TYPE:" + type + "\n" +
                "END:VRESOURCE\n" +
                "END:VCALENDAR";

            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));

            assertTrue(parser.getAllErrors().isEmpty(),
                "RESOURCE-TYPE:" + type + " should parse without errors");
        }
    }

    @Test
    @DisplayName("RFC 9074: PROXIMITY values")
    void testProximityValues() throws Exception {
        String[] proximityValues = {"ARRIVE", "DEPART"};

        for (String value : proximityValues) {
            String icalData =
                "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "BEGIN:VEVENT\n" +
                "UID:proximity-" + value.toLowerCase() + "@example.com\n" +
                "DTSTAMP:20240101T100000Z\n" +
                "DTSTART:20240115T100000Z\n" +
                "SUMMARY:Test Event\n" +
                "BEGIN:VALARM\n" +
                "ACTION:DISPLAY\n" +
                "DESCRIPTION:Test\n" +
                "TRIGGER:-PT10M\n" +
                "PROXIMITY:" + value + "\n" +
                "END:VALARM\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";

            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));

            assertTrue(parser.getAllErrors().isEmpty(),
                "PROXIMITY:" + value + " should parse without errors");
        }
    }

    @Test
    @DisplayName("RFC 7986: CALENDAR-ADDRESS property")
    void testCalendarAddress() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "CALENDAR-ADDRESS:mailto:calendar@example.com\n" +
            "NAME:Shared Calendar\n" +
            "BEGIN:VEVENT\n" +
            "UID:calendar-address-test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "CALENDAR-ADDRESS should parse");
    }

    @Test
    @DisplayName("Combined: Full RFC extensions integration")
    void testFullExtensionsIntegration() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "NAME:Extended Calendar Example\n" +
            "DESCRIPTION:A calendar demonstrating all RFC extensions\n" +
            "UID:extended-calendar@example.com\n" +
            "URL:https://example.com/calendar\n" +
            "COLOR:green\n" +
            "BEGIN:PARTICIPANT\n" +
            "UID:organizer@example.com\n" +
            "PARTICIPANT-TYPE:INDIVIDUAL\n" +
            "CALENDAR-ADDRESS:mailto:organizer@example.com\n" +
            "NAME:Calendar Organizer\n" +
            "END:PARTICIPANT\n" +
            "BEGIN:VLOCATION\n" +
            "UID:main-office@example.com\n" +
            "LOCATION:Main Office\n" +
            "LOCATION-TYPE:PHYSICAL\n" +
            "END:VLOCATION\n" +
            "BEGIN:VRESOURCE\n" +
            "UID:projector@example.com\n" +
            "RESOURCE-TYPE:PROJECTOR\n" +
            "END:VRESOURCE\n" +
            "BEGIN:VEVENT\n" +
            "UID:full-test-event@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T090000Z\n" +
            "DTEND:20240115T100000Z\n" +
            "SUMMARY:Extended Features Demo\n" +
            "DESCRIPTION:This event demonstrates all RFC extensions\n" +
            "LOCATION:main-office@example.com\n" +
            "RESOURCES:projector@example.com\n" +
            "ATTENDEE;PARTICIPANT-ID=organizer@example.com:mailto:organizer@example.com\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "DESCRIPTION:Reminder\n" +
            "TRIGGER:-PT30M\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(),
            "Calendar with all RFC extensions should parse without errors");
    }
}
