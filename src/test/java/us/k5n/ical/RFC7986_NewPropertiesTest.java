package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 7986: New Properties for iCalendar
 *
 * Tests for new properties introduced in RFC 7986: COLOR, IMAGE, CONFERENCE, STRUCTURED-DATA
 */
public class RFC7986_NewPropertiesTest {

    @Test
    @DisplayName("RFC7986: IMAGE property in VEVENT")
    void testImageProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event with Image\n" +
            "IMAGE:http://example.com/image.jpg\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "IMAGE property should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            assertNotNull(event.getImageUri(), "Event should have IMAGE property");
            assertEquals("http://example.com/image.jpg", event.getImageUri());
        } catch (Exception e) {
            fail("IMAGE property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC7986: CONFERENCE property in VEVENT")
    void testConferenceProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Conference Event\n" +
            "CONFERENCE:irc://example.com/calender\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "CONFERENCE property should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            assertNotNull(event.getConferenceUri(), "Event should have CONFERENCE property");
            assertEquals("irc://example.com/calender", event.getConferenceUri());
        } catch (Exception e) {
            fail("CONFERENCE property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC7986: STRUCTURED-DATA property in VEVENT")
    void testStructuredDataProperty() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event with Structured Data\n" +
            "STRUCTURED-DATA;VALUE=BINARY;ENCODING=BASE64;FMTTYPE=application/xml:PGhlbGxvIHdvcmxkPg==\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "STRUCTURED-DATA property should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            assertNotNull(event.getStructuredData(), "Event should have STRUCTURED-DATA property");
            assertEquals("PGhlbGxvIHdvcmxkPg==", event.getStructuredData());
        } catch (Exception e) {
            fail("STRUCTURED-DATA property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC7986: IMAGE property with parameters")
    void testImagePropertyWithParameters() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "IMAGE;VALUE=URI;DISPLAY=BADGE;FMTTYPE=image/png:http://example.com/logo.png\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "IMAGE property with parameters should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            assertNotNull(event.getImageUri(), "Event should have IMAGE property");
            assertEquals("http://example.com/logo.png", event.getImageUri());
        } catch (Exception e) {
            fail("IMAGE property with parameters should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC7986: Multiple IMAGE properties")
    void testMultipleImageProperties() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test Event\n" +
            "IMAGE;DISPLAY=BADGE:http://example.com/badge.png\n" +
            "IMAGE;DISPLAY=FULLSIZE:http://example.com/full.png\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple IMAGE properties should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            assertNotNull(event.getImageUri(), "Event should have IMAGE property");
        } catch (Exception e) {
            fail("Multiple IMAGE properties should parse: " + e.getMessage());
        }
    }
}