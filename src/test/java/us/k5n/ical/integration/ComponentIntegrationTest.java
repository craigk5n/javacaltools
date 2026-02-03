package us.k5n.ical.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.k5n.ical.*;

/**
 * Integration Tests for Component Interaction
 *
 * Tests that verify proper interaction and data flow between
 * different iCalendar components and the parser.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class ComponentIntegrationTest {

    @Test
    @DisplayName("Integration: VEVENT with nested VALARM parsing")
    void testEventWithNestedAlarm() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-with-alarm@example.com\n" +
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
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should succeed");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        List<Event> events = ds.getAllEvents();
        assertEquals(1, events.size(), "Should have one event");

        Event event = events.get(0);
        List<Valarm> alarms = event.getAlarms();
        assertNotNull(alarms, "Alarms list should not be null");
        assertEquals(1, alarms.size(), "Event should have one alarm");

        Valarm alarm = alarms.get(0);
        assertEquals("DISPLAY", alarm.getAction(), "Alarm action should be DISPLAY");
        assertEquals("-PT30M", alarm.getTrigger(), "Alarm trigger should be -PT30M");
    }

    @Test
    @DisplayName("Integration: Multiple VEVENT components in single calendar")
    void testMultipleEventsInCalendar() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-1@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T090000Z\n" +
            "SUMMARY:Morning Meeting\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-2@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T120000Z\n" +
            "SUMMARY:Lunch Meeting\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-3@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T150000Z\n" +
            "SUMMARY:Afternoon Meeting\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should succeed");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        List<Event> events = ds.getAllEvents();
        assertEquals(3, events.size(), "Should have three events");

        assertEquals("Morning Meeting", events.get(0).getSummary().getValue());
        assertEquals("Lunch Meeting", events.get(1).getSummary().getValue());
        assertEquals("Afternoon Meeting", events.get(2).getSummary().getValue());
    }

    @Test
    @DisplayName("Integration: VTODO with recurrence")
    void testTodoWithRecurrence() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:recurring-todo@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T090000Z\n" +
            "SUMMARY:Weekly Task\n" +
            "RRULE:FREQ=WEEKLY;BYDAY=MO\n" +
            "DURATION:PT1H\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should succeed");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        List<Todo> todos = ds.getAllTodos();
        assertEquals(1, todos.size(), "Should have one todo");

        Todo todo = todos.get(0);
        assertNotNull(todo.getRrule(), "Todo should have RRULE");
        assertEquals("FREQ=WEEKLY;BYDAY=MO", todo.getRrule().getValue());
    }

    @Test
    @DisplayName("Integration: Calendar with VTIMEZONE reference")
    void testCalendarWithTimezone() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:America/New_York\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20231105T020000\n" +
            "TZOFFSETFROM:-0400\n" +
            "TZOFFSETTO:-0500\n" +
            "TZNAME:EST\n" +
            "END:STANDARD\n" +
            "BEGIN:DAYLIGHT\n" +
            "DTSTART:20240310T020000\n" +
            "TZOFFSETFROM:-0500\n" +
            "TZOFFSETTO:-0400\n" +
            "TZNAME:EDT\n" +
            "END:DAYLIGHT\n" +
            "END:VTIMEZONE\n" +
            "BEGIN:VEVENT\n" +
            "UID:tz-event@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART;TZID=America/New_York:20240115T090000\n" +
            "SUMMARY:Timezone Aware Event\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should succeed");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        List<Timezone> timezones = ds.getAllTimezones();
        assertEquals(1, timezones.size(), "Should have one timezone");
        assertEquals("America/New_York", timezones.get(0).getTimezoneId());
    }

    @Test
    @DisplayName("Integration: Event with multiple attendees and organizer")
    void testEventWithMultipleAttendees() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:meeting@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T140000Z\n" +
            "DTEND:20240115T150000Z\n" +
            "SUMMARY:Team Meeting\n" +
            "ORGANIZER:mailto:organizer@example.com\n" +
            "ATTENDEE:mailto:alice@example.com;CN=Alice;PARTSTAT=ACCEPTED\n" +
            "ATTENDEE:mailto:bob@example.com;CN=Bob;PARTSTAT=NEEDS-ACTION\n" +
            "ATTENDEE:mailto:charlie@example.com;CN=Charlie;PARTSTAT=DECLINED\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should succeed");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        assertNotNull(event.getOrganizer(), "Event should have organizer");
        assertEquals("mailto:organizer@example.com", event.getOrganizer().getValue());

        List<Attendee> attendees = event.getAttendees();
        assertEquals(3, attendees.size(), "Event should have 3 attendees");
    }

    @Test
    @DisplayName("Integration: Event with categories and classification")
    void testEventWithCategoriesAndClassification() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:categorized-event@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Work Meeting\n" +
            "CLASS:CONFIDENTIAL\n" +
            "CATEGORIES:MEETING,WORK,TEAM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should succeed");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        assertNotNull(event.getClassification(), "Event should have classification");
        assertEquals(2, event.getClassification().getClassification());

        assertNotNull(event.getCategories(), "Event should have categories");
        assertTrue(event.getCategories().toString().contains("MEETING"));
    }

    @Test
    @DisplayName("Integration: Calendar with all component types")
    void testCalendarWithAllComponentTypes() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "END:VEVENT\n" +
            "BEGIN:VTODO\n" +
            "UID:todo@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "SUMMARY:Test Todo\n" +
            "END:VTODO\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "SUMMARY:Test Journal\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should succeed");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);

        assertEquals(1, ds.getAllEvents().size(), "Should have 1 event");
        assertEquals(1, ds.getAllTodos().size(), "Should have 1 todo");
        assertEquals(1, ds.getAllJournals().size(), "Should have 1 journal");
    }

    @Test
    @DisplayName("Integration: Event with EXDATE exception dates")
    void testEventWithExdate() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-with-exdate@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T090000Z\n" +
            "RRULE:FREQ=DAILY;COUNT=7\n" +
            "SUMMARY:Daily Meeting\n" +
            "EXDATE:20240103T090000Z,20240105T090000Z\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should succeed");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        List<Date> exdates = event.getExceptions();
        assertNotNull(exdates, "Should have exceptions list");
        assertEquals(2, exdates.size(), "Should have 2 exception dates");
    }

    @Test
    @DisplayName("Integration: Event with RDATE inclusion dates")
    void testEventWithRdate() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-with-rdate@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T090000Z\n" +
            "SUMMARY:Event with Extra Dates\n" +
            "RDATE:20240110T090000Z,20240115T090000Z\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Parse should succeed");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        List<Date> rdates = event.getRdates();
        assertNotNull(rdates, "Should have RDATEs list");
        assertEquals(2, rdates.size(), "Should have 2 RDATE entries");
    }

    @Test
    @DisplayName("Integration: Parse error handling in loose mode")
    void testLooseModeErrorHandling() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Event with optional fields\n" +
            "UNKNOWN-PROPERTY:value\n" +
            "X-CUSTOM-PROPERTY:custom\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Loose mode should ignore unknown properties");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertEquals(1, ds.getAllEvents().size(), "Event should be parsed despite unknown properties");
    }

    @Test
    @DisplayName("Integration: Serialization and reparsing of event with all properties")
    void testFullRoundTripWithAllProperties() throws Exception {
        String originalIcal =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:full-event@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "DTEND:20240115T110000Z\n" +
            "SUMMARY:Full Feature Event\n" +
            "DESCRIPTION:An event with many properties\n" +
            "CLASS:PUBLIC\n" +
            "CATEGORIES:MEETING,WORK\n" +
            "PRIORITY:1\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser1 = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser1.parse(new StringReader(originalIcal));

        assertTrue(parser1.getAllErrors().isEmpty(), "Initial parse should succeed");
        DefaultDataStore ds1 = (DefaultDataStore) parser1.getDataStoreAt(0);
        Event event1 = ds1.getAllEvents().get(0);

        String serialized = event1.toICalendar();

        // Wrap the serialized event in a VCALENDAR for re-parsing
        String fullCalendar = "BEGIN:VCALENDAR\r\n" +
            "VERSION:2.0\r\n" +
            "PRODID:-//Test//Calendar//EN\r\n" +
            serialized +
            "END:VCALENDAR\r\n";

        ICalendarParser parser2 = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser2.parse(new StringReader(fullCalendar));

        assertTrue(parser2.getAllErrors().isEmpty(), "Reparse should succeed");
        DefaultDataStore ds2 = (DefaultDataStore) parser2.getDataStoreAt(0);
        Event event2 = ds2.getAllEvents().get(0);

        assertEquals(event1.getSummary().getValue(), event2.getSummary().getValue(), "Summary should match");
    }
}
