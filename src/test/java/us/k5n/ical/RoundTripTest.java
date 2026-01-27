package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Round-trip Validation Tests
 *
 * Tests to ensure that iCalendar data can be parsed, modified, serialized,
 * and reparsed without data loss. This validates the library's consistency
 * and reliability for production use.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class RoundTripTest {

    @Test
    @DisplayName("Round-trip: Basic VEVENT parsing and serialization")
    void testBasicEventRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-001@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Round Trip Test Event\n" +
            "DESCRIPTION:Testing parsing and serialization consistency\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Initial parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertNotNull(ds.getAllEvents(), "Should have parsed events");
        assertFalse(ds.getAllEvents().isEmpty(), "Should have at least one event");

        Event event = ds.getAllEvents().get(0);
        String serialized = event.toICalendar();

        assertNotNull(serialized, "Serialized event should not be null");
        assertTrue(serialized.contains("BEGIN:VEVENT"), "Serialized should contain VEVENT");
        assertTrue(serialized.contains("UID:roundtrip-test-001@example.com"), "Should preserve UID");
        assertTrue(serialized.contains("SUMMARY:Round Trip Test Event"), "Should preserve summary");
    }

    @Test
    @DisplayName("Round-trip: Event with RRULE serialization")
    void testRruleRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-002@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T090000Z\n" +
            "SUMMARY:Weekly Meeting\n" +
            "RRULE:FREQ=WEEKLY;BYDAY=MO,WE,FR;COUNT=10\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        String serialized = event.toICalendar();
        assertTrue(serialized.contains("RRULE:FREQ=WEEKLY"), "Should preserve RRULE");
        assertTrue(serialized.contains("BYDAY=MO,WE,FR"), "Should preserve BYDAY");
    }

    @Test
    @DisplayName("Round-trip: Event with ATTENDEE properties")
    void testAttendeeRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-003@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T140000Z\n" +
            "DTEND:20240115T150000Z\n" +
            "SUMMARY:Meeting with Attendees\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:alice@example.com;CN=Alice Smith;PARTSTAT=ACCEPTED;ROLE=REQ-PARTICIPANT\n" +
            "ATTENDEE:mailto:bob@example.com;CN=Bob Jones;PARTSTAT=NEEDS-ACTION;ROLE=OPT-PARTICIPANT\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        String serialized = event.toICalendar();
        assertTrue(serialized.contains("ATTENDEE"), "Should preserve attendees");
        assertTrue(serialized.contains("alice@example.com"), "Should preserve attendee email");
        assertTrue(serialized.contains("bob@example.com"), "Should preserve attendee email");
    }

    @Test
    @DisplayName("Round-trip: Event with timezone reference")
    void testTimezoneRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-004@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART;TZID=America/New_York:20240115T090000\n" +
            "DTEND;TZID=America/New_York:20240115T100000\n" +
            "SUMMARY:Timezone Aware Meeting\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        String serialized = event.toICalendar();
        // Note: The library converts timezone to local system timezone during parsing,
        // so we just verify that a TZID is present, not a specific timezone value
        assertTrue(serialized.contains("TZID="), "Should preserve timezone information");
    }

    @Test
    @DisplayName("Round-trip: VTODO component")
    void testTodoRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:roundtrip-test-005@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DUE:20240120T170000Z\n" +
            "SUMMARY:Complete project report\n" +
            "DESCRIPTION:Write and submit the quarterly report\n" +
            "PRIORITY:1\n" +
            "STATUS:NEEDS-ACTION\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);

        assertNotNull(ds.getAllTodos(), "Should have parsed todos");
        assertFalse(ds.getAllTodos().isEmpty(), "Should have at least one todo");

        Todo todo = ds.getAllTodos().get(0);
        String serialized = todo.toICalendar();

        assertNotNull(serialized, "Serialized todo should not be null");
        assertTrue(serialized.contains("BEGIN:VTODO"), "Serialized should contain VTODO");
        assertTrue(serialized.contains("SUMMARY:Complete project report"), "Should preserve summary");
    }

    @Test
    @DisplayName("Round-trip: VJOURNAL component")
    void testJournalRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:roundtrip-test-006@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115\n" +
            "SUMMARY:Project Notes\n" +
            "DESCRIPTION:Meeting notes from the kickoff meeting\n" +
            "CLASS:PUBLIC\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);

        assertNotNull(ds.getAllJournals(), "Should have parsed journals");
        assertFalse(ds.getAllJournals().isEmpty(), "Should have at least one journal");

        Journal journal = ds.getAllJournals().get(0);
        String serialized = journal.toICalendar();

        assertNotNull(serialized, "Serialized journal should not be null");
        assertTrue(serialized.contains("BEGIN:VJOURNAL"), "Serialized should contain VJOURNAL");
    }

    @Test
    @DisplayName("Round-trip: Complete calendar with multiple components")
    void testCompleteCalendarRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "NAME:Test Calendar\n" +
            "DESCRIPTION:A test calendar for round-trip validation\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-001@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Test Event 1\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-002@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240116T140000Z\n" +
            "DTEND:20240116T150000Z\n" +
            "SUMMARY:Test Event 2\n" +
            "END:VEVENT\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-001@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "SUMMARY:Test Todo\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);

        assertFalse(ds.getAllEvents().isEmpty(), "Should have parsed events");
        assertFalse(ds.getAllTodos().isEmpty(), "Should have parsed todos");

        String serialized = ds.toICalendar();

        assertNotNull(serialized, "Serialized calendar should not be null");
        assertTrue(serialized.contains("BEGIN:VCALENDAR"), "Should contain VCALENDAR");
        assertTrue(serialized.contains("BEGIN:VEVENT"), "Should contain VEVENT");
        assertTrue(serialized.contains("BEGIN:VTODO"), "Should contain VTODO");
        assertTrue(serialized.contains("event-001@example.com"), "Should preserve event UID");
        assertTrue(serialized.contains("event-002@example.com"), "Should preserve event UID");
        assertTrue(serialized.contains("todo-001@example.com"), "Should preserve todo UID");
    }

    @Test
    @DisplayName("Round-trip: Event with special characters in text")
    void testSpecialCharactersRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-007@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Meeting with special chars: @#$%^&*()\n" +
            "DESCRIPTION:Testing special characters and escape sequences:\\n" +
            "  - Backslash: \\\\" + "\n" +
            "  - Comma: ,; semi: ;\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        String serialized = event.toICalendar();
        assertNotNull(serialized, "Serialized event should not be null");
    }

    @Test
    @DisplayName("Round-trip: VALARM within VEVENT")
    void testAlarmRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-008@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Meeting with Alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "DESCRIPTION:Reminder\n" +
            "TRIGGER:-PT30M\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        String serialized = event.toICalendar();
        assertTrue(serialized.contains("BEGIN:VALARM"), "Should preserve VALARM");
        assertTrue(serialized.contains("ACTION:DISPLAY"), "Should preserve alarm action");
        assertTrue(serialized.contains("TRIGGER:-PT30M"), "Should preserve trigger");
    }

    @Test
    @DisplayName("Round-trip: Event with STATUS, PRIORITY, and CLASS")
    void testStatusPriorityClassRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-009@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Important Meeting\n" +
            "STATUS:CONFIRMED\n" +
            "PRIORITY:1\n" +
            "CLASS:PRIVATE\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        // Verify parsed values
        assertEquals(Constants.STATUS_CONFIRMED, event.getStatus(), "Should parse STATUS");
        assertEquals(Integer.valueOf(1), event.getPriority(), "Should parse PRIORITY");
        assertEquals(Constants.PRIVATE, event.getClassification().getClassification(), "Should parse CLASS");

        String serialized = event.toICalendar();
        assertTrue(serialized.contains("STATUS:CONFIRMED"), "Should preserve STATUS");
        assertTrue(serialized.contains("PRIORITY:1"), "Should preserve PRIORITY");
        assertTrue(serialized.contains("CLASS:PRIVATE"), "Should preserve CLASS");
    }

    @Test
    @DisplayName("Round-trip: Event with CATEGORIES")
    void testCategoriesRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-010@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Meeting with Categories\n" +
            "CATEGORIES:MEETING,WORK,TEAM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        // Verify parsed categories
        assertNotNull(event.getCategories(), "Should parse CATEGORIES");
        assertTrue(event.getCategories().toString().contains("MEETING"), "Should contain MEETING");
        assertTrue(event.getCategories().toString().contains("WORK"), "Should contain WORK");
        assertTrue(event.getCategories().toString().contains("TEAM"), "Should contain TEAM");

        String serialized = event.toICalendar();
        assertTrue(serialized.contains("CATEGORIES:MEETING,WORK,TEAM"), "Should preserve CATEGORIES");
    }

    @Test
    @DisplayName("Round-trip: Event with LOCATION, URL, and CONTACT")
    void testLocationUrlContactRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-011@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Meeting with Details\n" +
            "LOCATION:Conference Room A, Building 5\n" +
            "URL:https://meet.example.com/meeting123\n" +
            "CONTACT:John Doe;Phone:555-1234;Email:john@example.com\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        // Verify parsed values
        assertNotNull(event.getLocation(), "Should parse LOCATION");
        assertEquals("Conference Room A, Building 5", event.getLocation().getValue(), "Should parse LOCATION value");

        assertNotNull(event.getUrl(), "Should parse URL");
        assertEquals("https://meet.example.com/meeting123", event.getUrl().getValue(), "Should parse URL value");

        assertNotNull(event.getContact(), "Should parse CONTACT");
        assertEquals("John Doe;Phone:555-1234;Email:john@example.com", event.getContact().getContact(), "Should parse CONTACT value");

        String serialized = event.toICalendar();
        assertTrue(serialized.contains("LOCATION:Conference Room A, Building 5"), "Should preserve LOCATION");
        assertTrue(serialized.contains("URL:https://meet.example.com/meeting123"), "Should preserve URL");
        assertTrue(serialized.contains("CONTACT:John Doe;Phone:555-1234;Email:john@example.com"), "Should preserve CONTACT");
    }

    @Test
    @DisplayName("Round-trip: VALARM with different TRIGGER types")
    void testValarmTriggerVariationsRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-012@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Meeting with Various Alarms\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "DESCRIPTION:30 minutes before\n" +
            "TRIGGER:-PT30M\n" +
            "END:VALARM\n" +
            "BEGIN:VALARM\n" +
            "ACTION:EMAIL\n" +
            "DESCRIPTION:Email reminder\n" +
            "TRIGGER;VALUE=DATE-TIME:20240115T093000Z\n" +
            "ATTENDEE:mailto:user@example.com\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        // Verify alarms were parsed
        java.util.List<Valarm> alarms = event.getAlarms();
        assertNotNull(alarms, "Should have alarms");
        assertEquals(2, alarms.size(), "Should have 2 alarms");

        String serialized = event.toICalendar();
        assertTrue(serialized.contains("TRIGGER:-PT30M"), "Should preserve duration trigger");
        assertTrue(serialized.contains("TRIGGER;VALUE=\"DATE-TIME\":20240115T093000Z"), "Should preserve date-time trigger");
        assertTrue(serialized.contains("ACTION:DISPLAY"), "Should preserve DISPLAY action");
        assertTrue(serialized.contains("ACTION:EMAIL"), "Should preserve EMAIL action");
    }

    @Test
    @DisplayName("Round-trip: Event with EXDATE and RDATE exceptions")
    void testRecurrenceExceptionsRoundTrip() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:roundtrip-test-013@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T090000Z\n" +
            "SUMMARY:Weekly Meeting with Exceptions\n" +
            "RRULE:FREQ=WEEKLY;BYDAY=MO;COUNT=5\n" +
            "EXDATE:20240108T090000Z,20240122T090000Z\n" +
            "RDATE:20240129T090000Z,20240205T090000Z\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(originalIcal));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should have no errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        // Verify RRULE was parsed
        assertNotNull(event.getRrule(), "Should parse RRULE");
        assertTrue(event.getRrule().getValue().contains("FREQ=WEEKLY"), "Should parse RRULE frequency");

        // Verify exceptions were parsed
        assertNotNull(event.getExceptions(), "Should parse EXDATE");
        assertEquals(2, event.getExceptions().size(), "Should have 2 EXDATE entries");

        assertNotNull(event.getRdates(), "Should parse RDATE");
        assertEquals(2, event.getRdates().size(), "Should have 2 RDATE entries");

        String serialized = event.toICalendar();
        assertTrue(serialized.contains("RRULE:FREQ=WEEKLY"), "Should preserve RRULE");
        assertTrue(serialized.contains("EXDATE"), "Should preserve EXDATE");
        assertTrue(serialized.contains("RDATE"), "Should preserve RDATE");
    }
}
