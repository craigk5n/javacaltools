package us.k5n.ical.core.rfc5545;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.k5n.ical.ICalendarParser;

/**
 * RFC 5545 Section 3.13: VALARM Component
 *
 * Tests for the VALARM calendar component, including required properties,
 * optional properties, and alarm-specific validation rules.
 */
public class Section3_13_ValarmTest {

    @Test
    @DisplayName("RFC5545-3.13: VALARM with ACTION and TRIGGER")
    void testValarmWithActionAndTrigger() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm-basic@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with basic alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT15M\n" +
            "DESCRIPTION:Meeting reminder\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with ACTION and TRIGGER should parse without errors");
        } catch (Exception e) {
            fail("VALARM with ACTION and TRIGGER should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: VALARM with AUDIO action")
    void testValarmWithAudioAction() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm-audio@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with audio alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:AUDIO\n" +
            "TRIGGER:-PT30M\n" +
            "ATTACH:Bell.mp3\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with AUDIO action should parse without errors");
        } catch (Exception e) {
            fail("VALARM with AUDIO action should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: VALARM with EMAIL action")
    void testValarmWithEmailAction() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm-email@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with email alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:EMAIL\n" +
            "TRIGGER:-PT1H\n" +
            "SUMMARY:Meeting Reminder\n" +
            "DESCRIPTION:Don't forget the meeting!\n" +
            "ATTENDEE:mailto:user@example.com\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with EMAIL action should parse without errors");
        } catch (Exception e) {
            fail("VALARM with EMAIL action should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: VALARM with PROCEDURE action")
    void testValarmWithProcedureAction() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm-procedure@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with procedure alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:PROCEDURE\n" +
            "TRIGGER:-PT10M\n" +
            "ATTACH:http://example.com/script.sh\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with PROCEDURE action should parse without errors");
        } catch (Exception e) {
            fail("VALARM with PROCEDURE action should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: VALARM with REPEAT and DURATION")
    void testValarmWithRepeatAndDuration() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm-repeat@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with repeating alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT15M\n" +
            "DURATION:PT5M\n" +
            "REPEAT:3\n" +
            "DESCRIPTION:Meeting in 15 minutes\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with REPEAT and DURATION should parse without errors");
        } catch (Exception e) {
            fail("VALARM with REPEAT and DURATION should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: VALARM with absolute TRIGGER")
    void testValarmWithAbsoluteTrigger() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm-absolute@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with absolute alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER;VALUE=DATE-TIME:20230101T094500Z\n" +
            "DESCRIPTION:Meeting starts in 15 minutes\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with absolute TRIGGER should parse without errors");
        } catch (Exception e) {
            fail("VALARM with absolute TRIGGER should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: VALARM with relative TRIGGER")
    void testValarmWithRelativeTrigger() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm-relative@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with relative alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT15M\n" +
            "DESCRIPTION:Meeting in 15 minutes\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with relative TRIGGER should parse without errors");
        } catch (Exception e) {
            fail("VALARM with relative TRIGGER should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: VALARM with multiple ATTENDEE for EMAIL")
    void testValarmWithMultipleAttendeeForEmail() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm-multi-attendee@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with multi-attendee email alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:EMAIL\n" +
            "TRIGGER:-PT1H\n" +
            "SUMMARY:Meeting Reminder\n" +
            "DESCRIPTION:Don't forget the meeting!\n" +
            "ATTENDEE:mailto:user1@example.com\n" +
            "ATTENDEE:mailto:user2@example.com\n" +
            "ATTENDEE:mailto:user3@example.com\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with multiple ATTENDEE should parse without errors");
        } catch (Exception e) {
            fail("VALARM with multiple ATTENDEE should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: VALARM with X-PROPERTIES")
    void testValarmWithXProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-alarm-xprop@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with X-property alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT15M\n" +
            "DESCRIPTION:Custom alarm\n" +
            "X-CUSTOM-ALARM-TYPE:REMINDER\n" +
            "X-ALARM-PRIORITY:HIGH\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM with X-PROPERTIES should parse without errors");
        } catch (Exception e) {
            fail("VALARM with X-PROPERTIES should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: Multiple VALARM components")
    void testMultipleValarmComponents() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:event-multi-alarm@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Event with multiple alarms\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT15M\n" +
            "DESCRIPTION:15 minute reminder\n" +
            "END:VALARM\n" +
            "BEGIN:VALARM\n" +
            "ACTION:AUDIO\n" +
            "TRIGGER:-PT30M\n" +
            "ATTACH:Bell.mp3\n" +
            "END:VALARM\n" +
            "BEGIN:VALARM\n" +
            "ACTION:EMAIL\n" +
            "TRIGGER:-PT1H\n" +
            "SUMMARY:Meeting Reminder\n" +
            "DESCRIPTION:Meeting in 1 hour\n" +
            "ATTENDEE:mailto:user@example.com\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple VALARM components should parse without errors");
        } catch (Exception e) {
            fail("Multiple VALARM components should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.13: VALARM in VTODO component")
    void testValarmInVtodoComponent() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo-alarm@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DUE:20230101T120000Z\n" +
            "SUMMARY:Todo with alarm\n" +
            "BEGIN:VALARM\n" +
            "ACTION:DISPLAY\n" +
            "TRIGGER:-PT30M\n" +
            "DESCRIPTION:Todo deadline approaching\n" +
            "END:VALARM\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "VALARM in VTODO should parse without errors");
        } catch (Exception e) {
            fail("VALARM in VTODO should parse: " + e.getMessage());
        }
    }
}
