package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 2445 & RFC 2446 Compatibility Tests
 *
 * Tests for backward compatibility with RFC 2445 (original iCalendar) and
 * full RFC 2446 (iTIP) protocol support.
 */
public class RFC2445_2446_CompatibilityTest {

    @Test
    @DisplayName("RFC 2445: Parse calendar with EXRULE")
    void testRFC2445ExruleParsing() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:exrule-test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T090000Z\n" +
            "SUMMARY:Meeting with EXRULE\n" +
            "RRULE:FREQ=WEEKLY;BYDAY=MO;COUNT=10\n" +
            "EXRULE:FREQ=WEEKLY;BYDAY=FR;COUNT=2\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(Constants.PARSE_RFC2445);
        parser.parse(new StringReader(icalData));

        assertTrue(parser.getAllErrors().isEmpty(), "Should parse RFC 2445 data without errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        // Should have both RRULE and EXRULE parsed
        assertNotNull(event.getRrule(), "Should parse RRULE");
        assertNotNull(event.getExrule(), "Should parse EXRULE (RFC 2445)");
    }

    @Test
    @DisplayName("RFC 2445: EXRULE serialization in compatibility mode")
    void testRFC2445ExruleSerialization() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:exrule-serialize-test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240101T090000Z\n" +
            "SUMMARY:Meeting with EXRULE\n" +
            "RRULE:FREQ=WEEKLY;BYDAY=MO\n" +
            "EXRULE:FREQ=WEEKLY;BYDAY=FR\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        ICalendarParser parser = new ICalendarParser(Constants.PARSE_RFC2445);
        parser.parse(new StringReader(icalData));

        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        Event event = ds.getAllEvents().get(0);

        String serialized = event.toICalendar();
        assertTrue(serialized.contains("RRULE:FREQ=WEEKLY"), "Should preserve RRULE");
        assertTrue(serialized.contains("EXRULE:FREQ=WEEKLY"), "Should preserve EXRULE in RFC 2445 mode");
    }

    @Test
    @DisplayName("RFC 2446: iTIP Method Validation")
    void testRFC2446MethodValidation() {
        // Test all valid iTIP methods
        assertNotNull(ITIPManager.ITIPMethod.fromString("PUBLISH"));
        assertNotNull(ITIPManager.ITIPMethod.fromString("REQUEST"));
        assertNotNull(ITIPManager.ITIPMethod.fromString("REPLY"));
        assertNotNull(ITIPManager.ITIPMethod.fromString("ADD"));
        assertNotNull(ITIPManager.ITIPMethod.fromString("CANCEL"));
        assertNotNull(ITIPManager.ITIPMethod.fromString("REFRESH"));
        assertNotNull(ITIPManager.ITIPMethod.fromString("COUNTER"));
        assertNotNull(ITIPManager.ITIPMethod.fromString("DECLINECOUNTER"));

        // Test invalid method
        assertEquals(null, ITIPManager.ITIPMethod.fromString("INVALID"));
    }

    @Test
    @DisplayName("RFC 2446: Method-Component Compatibility")
    void testRFC2446MethodComponentCompatibility() {
        // VEVENT compatible methods
        assertTrue(ITIPManager.isValidMethodForComponent(ITIPManager.ITIPMethod.REQUEST, "VEVENT"));
        assertTrue(ITIPManager.isValidMethodForComponent(ITIPManager.ITIPMethod.REPLY, "VEVENT"));
        assertTrue(ITIPManager.isValidMethodForComponent(ITIPManager.ITIPMethod.CANCEL, "VEVENT"));
        assertTrue(ITIPManager.isValidMethodForComponent(ITIPManager.ITIPMethod.PUBLISH, "VEVENT"));

        // VTODO compatible methods
        assertTrue(ITIPManager.isValidMethodForComponent(ITIPManager.ITIPMethod.REQUEST, "VTODO"));
        assertTrue(ITIPManager.isValidMethodForComponent(ITIPManager.ITIPMethod.REPLY, "VTODO"));

        // Invalid combinations
        assertFalse(ITIPManager.isValidMethodForComponent(ITIPManager.ITIPMethod.REPLY, "VJOURNAL"));
        assertFalse(ITIPManager.isValidMethodForComponent(ITIPManager.ITIPMethod.REQUEST, "VFREEBUSY"));
    }

    @Test
    @DisplayName("RFC 2446: Workflow Transition Validation")
    void testRFC2446WorkflowTransitions() {
        // Valid transitions
        assertTrue(ITIPManager.isValidWorkflowTransition(ITIPManager.ITIPMethod.REQUEST, ITIPManager.ITIPMethod.REPLY));
        assertTrue(ITIPManager.isValidWorkflowTransition(ITIPManager.ITIPMethod.REQUEST, ITIPManager.ITIPMethod.COUNTER));
        assertTrue(ITIPManager.isValidWorkflowTransition(ITIPManager.ITIPMethod.COUNTER, ITIPManager.ITIPMethod.DECLINECOUNTER));
        assertTrue(ITIPManager.isValidWorkflowTransition(ITIPManager.ITIPMethod.COUNTER, ITIPManager.ITIPMethod.REPLY));

        // Invalid transitions
        assertFalse(ITIPManager.isValidWorkflowTransition(ITIPManager.ITIPMethod.REPLY, ITIPManager.ITIPMethod.REQUEST));
        assertFalse(ITIPManager.isValidWorkflowTransition(ITIPManager.ITIPMethod.PUBLISH, ITIPManager.ITIPMethod.REPLY));
    }

    @Test
    @DisplayName("RFC 2446: Attendee Action to Method Mapping")
    void testRFC2446AttendeeActionMapping() {
        assertEquals(ITIPManager.ITIPMethod.REPLY, ITIPManager.getResponseMethodForAttendeeAction("ACCEPT"));
        assertEquals(ITIPManager.ITIPMethod.REPLY, ITIPManager.getResponseMethodForAttendeeAction("DECLINE"));
        assertEquals(ITIPManager.ITIPMethod.REPLY, ITIPManager.getResponseMethodForAttendeeAction("TENTATIVE"));
        assertEquals(ITIPManager.ITIPMethod.COUNTER, ITIPManager.getResponseMethodForAttendeeAction("COUNTER"));
        assertEquals(ITIPManager.ITIPMethod.DECLINECOUNTER, ITIPManager.getResponseMethodForAttendeeAction("DECLINE_COUNTER"));
    }

    @Test
    @DisplayName("RFC 2446: REQUEST-REPLY Cycle Parsing")
    void testRFC2446RequestReplyCycle() throws Exception {
        // REQUEST message
        String requestMessage =
            "BEGIN:VCALENDAR\n" +
            "METHOD:REQUEST\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
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

        ICalendarParser parser = new ICalendarParser(Constants.PARSE_LOOSE);
        parser.parse(new StringReader(requestMessage));

        assertTrue(parser.getAllErrors().isEmpty(), "REQUEST message should parse without errors");
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);

        // Verify METHOD is parsed correctly
        assertNotNull(ds.getMethod(), "Should have METHOD property");
        assertEquals("REQUEST", ds.getMethod().getValue(), "Should parse METHOD property value");
    }

    @Test
    @DisplayName("RFC 2445: Compatibility Layer Validation")
    void testRFC2445CompatibilityLayer() {
        // Test RFC 2445 property recognition
        assertTrue(RFC2445Compatibility.isRFC2445Property("EXRULE"));
        assertTrue(RFC2445Compatibility.isRFC2445Property("X-CUSTOM"));
        assertFalse(RFC2445Compatibility.isRFC2445Property("INVALID"));

        // Test value normalization
        assertEquals("test", RFC2445Compatibility.normalizeRFC2445Value("EXRULE", "test"));
        assertEquals("37.386013;-122.082932", RFC2445Compatibility.normalizeRFC2445Value("GEO", "37.386013;-122.082932"));

        // Test validation
        assertTrue(RFC2445Compatibility.validateRFC2445Property("EXRULE", "FREQ=WEEKLY"));
        assertFalse(RFC2445Compatibility.validateRFC2445Property("EXRULE", "invalid"));
    }

    @Test
    @DisplayName("RFC 2445 + RFC 5545: Mixed Compatibility")
    void testRFC2445And5545MixedCompatibility() throws Exception {
        String icalData =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:mixed-compat-test@example.com\n" +
            "DTSTAMP:20240101T100000Z\n" +
            "DTSTART:20240115T100000Z\n" +
            "SUMMARY:Mixed Compatibility Event\n" +
            "RRULE:FREQ=WEEKLY;BYDAY=MO\n" +
            "EXRULE:FREQ=WEEKLY;BYDAY=FR\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        // Test with RFC 2445 mode
        ICalendarParser parser2445 = new ICalendarParser(Constants.PARSE_RFC2445);
        parser2445.parse(new StringReader(icalData));
        assertTrue(parser2445.getAllErrors().isEmpty(), "RFC 2445 mode should accept EXRULE");

        // Test with RFC 5545 mode (loose parsing)
        ICalendarParser parser5545 = new ICalendarParser(Constants.PARSE_LOOSE);
        parser5545.parse(new StringReader(icalData));
        assertTrue(parser5545.getAllErrors().isEmpty(), "RFC 5545 mode should handle mixed data gracefully");
    }
}