package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 5546: iTIP Flow Tests
 *
 * Tests for iTIP (iCalendar Transport-Independent Interoperability Protocol)
 * REQUEST→REPLY→UPDATE cycles and other scheduling workflows as defined in RFC 5546.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class RFC5546_iTIP_FlowTest {

    @Test
    @DisplayName("RFC 5546: REQUEST method - Meeting invitation")
    void testRequestMethod() throws Exception {
        String requestMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:REQUEST\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:meeting-123@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T140000Z\n" +
            "DTEND:20240115T150000Z\n" +
            "SUMMARY:Team Meeting\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee1@example.com\n" +
            "ATTENDEE:mailto:attendee2@example.com\n" +
            "SEQUENCE:0\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(requestMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "REQUEST message should parse without errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertNotNull(ds.getAllEvents(), "Should have parsed events");
        assertFalse(ds.getAllEvents().isEmpty(), "Should have at least one event");
    }

    @Test
    @DisplayName("RFC 5546: REPLY method - Response to invitation")
    void testReplyMethod() throws Exception {
        String replyMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:REPLY\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:meeting-123@example.com\n" +
            "DTSTAMP:20240101T110000Z\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee1@example.com\n" +
            "SEQUENCE:0\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(replyMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "REPLY message should parse without errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertNotNull(ds.getAllEvents(), "Should have parsed events");
    }

    @Test
    @DisplayName("RFC 5546: CANCEL method - Meeting cancellation")
    void testCancelMethod() throws Exception {
        String cancelMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:CANCEL\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:meeting-456@example.com\n" +
            "DTSTAMP:20240101T130000Z\n" +
            "DTSTART:20240116T140000Z\n" +
            "DTEND:20240116T150000Z\n" +
            "SUMMARY:Cancelled Meeting\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee1@example.com\n" +
            "STATUS:CANCELLED\n" +
            "SEQUENCE:2\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(cancelMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "CANCEL message should parse without errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertNotNull(ds.getAllEvents(), "Should have parsed events");
    }

    @Test
    @DisplayName("RFC 5546: PUBLISH method - Publishing calendar objects")
    void testPublishMethod() throws Exception {
        String publishMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:PUBLISH\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:public-event@example.com\n" +
            "DTSTAMP:20240101T180000Z\n" +
            "DTSTART:20240125T190000Z\n" +
            "DTEND:20240125T220000Z\n" +
            "SUMMARY:Public Concert\n" +
            "LOCATION:City Hall Auditorium\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(publishMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "PUBLISH message should parse without errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertNotNull(ds.getAllEvents(), "Should have parsed events");
    }

    @Test
    @DisplayName("RFC 5546: ADD method - Adding instances to recurring event")
    void testAddMethod() throws Exception {
        String addMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:ADD\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:recurring-meeting@example.com\n" +
            "DTSTAMP:20240101T200000Z\n" +
            "DTSTART:20240201T140000Z\n" +
            "DTEND:20240201T150000Z\n" +
            "SUMMARY:Additional Meeting Instance\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee1@example.com\n" +
            "RECURRENCE-ID:20240201T140000Z\n" +
            "SEQUENCE:1\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(addMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "ADD message should parse without errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertNotNull(ds.getAllEvents(), "Should have parsed events");
    }

    @Test
    @DisplayName("RFC 5546: REFRESH method - Requesting event update")
    void testRefreshMethod() throws Exception {
        String refreshMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:REFRESH\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:existing-meeting@example.com\n" +
            "DTSTAMP:20240101T210000Z\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee1@example.com\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(refreshMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "REFRESH message should parse without errors");
    }

    @Test
    @DisplayName("RFC 5546: COUNTER method - Counter-proposing new time")
    void testCounterMethod() throws Exception {
        String counterMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:COUNTER\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:meeting-789@example.com\n" +
            "DTSTAMP:20240101T220000Z\n" +
            "DTSTART:20240120T150000Z\n" +
            "DTEND:20240120T160000Z\n" +
            "SUMMARY:Counter Proposal Meeting\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee1@example.com\n" +
            "SEQUENCE:0\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(counterMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "COUNTER message should parse without errors");
    }

    @Test
    @DisplayName("RFC 5546: DECLINECOUNTER method - Declining counter proposal")
    void testDeclineCounterMethod() throws Exception {
        String declineMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:DECLINECOUNTER\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:meeting-789@example.com\n" +
            "DTSTAMP:20240101T230000Z\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee1@example.com\n" +
            "SEQUENCE:0\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(declineMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "DECLINECOUNTER message should parse without errors");
    }

    @Test
    @DisplayName("RFC 5546: Strict mode validation - Invalid METHOD")
    void testStrictModeInvalidMethod() throws Exception {
        String invalidMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:INVALID\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
        parser.parse(new StringReader(invalidMessage));

        assertFalse(parser.getAllErrors().isEmpty(), "Invalid METHOD should produce parse errors in strict mode");
    }

    @Test
    @DisplayName("RFC 5546: All valid METHOD values parse successfully")
    void testAllValidMethods() {
        String[] validMethods = {"PUBLISH", "REQUEST", "REPLY", "ADD", "CANCEL", "REFRESH", "COUNTER", "DECLINECOUNTER"};

        for (String method : validMethods) {
            String message = "BEGIN:VCALENDAR\n" +
                "METHOD:" + method + "\n" +
                "PRODID:-//Test//Calendar//EN\n" +
                "VERSION:2.0\n" +
                "BEGIN:VEVENT\n" +
                "UID:test-" + method + "@example.com\n" +
                "DTSTAMP:20240101T100000Z\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";

            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            try {
                parser.parse(new StringReader(message));
                assertTrue(parser.getAllErrors().isEmpty(), "METHOD:" + method + " should parse without errors");
            } catch (Exception e) {
                assertTrue(false, "METHOD:" + method + " should not throw exception: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("RFC 5546: REQUEST with ATTENDEE PARTSTAT values")
    void testRequestWithAttendeePartstat() throws Exception {
        String requestMessage = "BEGIN:VCALENDAR\n" +
            "METHOD:REQUEST\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "VERSION:2.0\n" +
            "BEGIN:VEVENT\n" +
            "UID:meeting-stats@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T140000Z\n" +
            "DTEND:20240115T150000Z\n" +
            "SUMMARY:Meeting with PARTSTAT\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:attendee1@example.com;PARTSTAT=NEEDS-ACTION\n" +
            "ATTENDEE:mailto:attendee2@example.com;PARTSTAT=ACCEPTED\n" +
            "ATTENDEE:mailto:attendee3@example.com;PARTSTAT=DECLINED\n" +
            "SEQUENCE:0\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(requestMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "REQUEST with PARTSTAT should parse without errors");
    }
}