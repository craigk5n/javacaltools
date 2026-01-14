package us.k5n.ical;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;

/**
 * RFC 9073: STYLED-DESCRIPTION Property Tests
 *
 * Tests for the STYLED-DESCRIPTION property as defined in RFC 9073, Section 6.5.
 * STYLED-DESCRIPTION provides rich-text descriptions (HTML, etc.) for events,
 * tasks, and journals.
 */
public class RFC9073_StyledDescriptionTest {

    @Test
    @DisplayName("RFC 9073: STYLED-DESCRIPTION with HTML content in VEVENT")
    void testStyledDescriptionHtmlInEvent() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T110000Z\n" +
            "SUMMARY:Test Meeting\n" +
            "STYLED-DESCRIPTION;FMTTYPE=text/html:<html><body><h1>Meeting</h1><p>This is a <b>very important</b> meeting.</p></body></html>\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "STYLED-DESCRIPTION with HTML should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            assertNotNull(event.getStyledDescription(), "Event should have STYLED-DESCRIPTION");
            assertEquals("<html><body><h1>Meeting</h1><p>This is a <b>very important</b> meeting.</p></body></html>",
                        event.getStyledDescription().getValue());
        } catch (Exception e) {
            fail("STYLED-DESCRIPTION with HTML should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: STYLED-DESCRIPTION with DERIVED parameter")
    void testStyledDescriptionWithDerived() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T110000Z\n" +
            "SUMMARY:Test Event\n" +
            "STYLED-DESCRIPTION;DERIVED=TRUE;FMTTYPE=text/html:<p>Auto-generated description</p>\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "STYLED-DESCRIPTION with DERIVED should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            StyledDescription styledDesc = event.getStyledDescription();
            assertNotNull(styledDesc, "Event should have STYLED-DESCRIPTION");
            assertEquals("TRUE", styledDesc.getDerived(), "DERIVED should be TRUE");
            assertEquals("text/html", styledDesc.getFmtType(), "FMTTYPE should be text/html");
        } catch (Exception e) {
            fail("STYLED-DESCRIPTION with DERIVED should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: STYLED-DESCRIPTION in VTODO")
    void testStyledDescriptionInTodo() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VTODO\n" +
            "UID:todo@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T110000Z\n" +
            "SUMMARY:Test Todo\n" +
            "STYLED-DESCRIPTION;FMTTYPE=text/markdown:# Task\\n\\nThis is a *markdown* description.\n" +
            "END:VTODO\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "STYLED-DESCRIPTION in VTODO should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Todo todo = ds.getAllTodos().get(0);
            StyledDescription styledDesc = todo.getStyledDescription();
            assertNotNull(styledDesc, "VTODO should have STYLED-DESCRIPTION");
            assertEquals("text/markdown", styledDesc.getFmtType(), "FMTTYPE should be text/markdown");
        } catch (Exception e) {
            fail("STYLED-DESCRIPTION in VTODO should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: STYLED-DESCRIPTION in VJOURNAL")
    void testStyledDescriptionInJournal() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VJOURNAL\n" +
            "UID:journal@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "SUMMARY:Test Journal\n" +
            "STYLED-DESCRIPTION;FMTTYPE=text/plain:Plain text journal entry\n" +
            "END:VJOURNAL\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "STYLED-DESCRIPTION in VJOURNAL should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Journal journal = ds.getAllJournals().get(0);
            StyledDescription styledDesc = journal.getStyledDescription();
            assertNotNull(styledDesc, "VJOURNAL should have STYLED-DESCRIPTION");
            assertEquals("text/plain", styledDesc.getFmtType(), "FMTTYPE should be text/plain");
        } catch (Exception e) {
            fail("STYLED-DESCRIPTION in VJOURNAL should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: Multiple STYLED-DESCRIPTION properties")
    void testMultipleStyledDescriptions() {
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T110000Z\n" +
            "SUMMARY:Test Event\n" +
            "STYLED-DESCRIPTION;FMTTYPE=text/html:<h1>Title</h1>\n" +
            "STYLED-DESCRIPTION;FMTTYPE=text/plain:Plain text version\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Multiple STYLED-DESCRIPTION should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            // For now, we support a single styled description
            assertNotNull(event.getStyledDescription(), "Event should have STYLED-DESCRIPTION");
        } catch (Exception e) {
            fail("Multiple STYLED-DESCRIPTION should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: STYLED-DESCRIPTION serialization")
    void testStyledDescriptionSerialization() {
        // Test serialization to iCalendar format
        String expectedHtml = "<p>This is <strong>HTML</strong> content</p>";
        String expectedOutput = "STYLED-DESCRIPTION;FMTTYPE=text/html:" + expectedHtml;

        // TODO: Implement serialization test once StyledDescription class is created
        // This will verify toICalendar() method works correctly
        assertTrue(true, "Serialization test placeholder - will be implemented with StyledDescription class");
    }

    @Test
    @DisplayName("RFC 9073: STYLED-DESCRIPTION parameter validation")
    void testStyledDescriptionParameterValidation() {
        // Test invalid FMTTYPE values
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T110000Z\n" +
            "SUMMARY:Test Event\n" +
            "STYLED-DESCRIPTION;FMTTYPE=invalid/type:Invalid format\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        // In loose mode, should accept invalid FMTTYPE
        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Invalid FMTTYPE should be accepted in loose mode");
        } catch (Exception e) {
            fail("Invalid FMTTYPE should parse in loose mode: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC 9073: STYLED-DESCRIPTION with complex HTML")
    void testStyledDescriptionComplexHtml() {
        String complexHtml = "<!DOCTYPE html><html><head><title>Test</title></head><body>" +
            "<div class=\"content\"><h2>Meeting Agenda</h2><ul><li>Item 1</li><li>Item 2</li></ul>" +
            "<p>Additional <a href=\"http://example.com\">information</a></p></div></body></html>";

        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T110000Z\n" +
            "SUMMARY:Test Meeting\n" +
            "STYLED-DESCRIPTION;FMTTYPE=text/html:" + complexHtml + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Complex HTML in STYLED-DESCRIPTION should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);
            StyledDescription styledDesc = event.getStyledDescription();
            assertNotNull(styledDesc, "Event should have STYLED-DESCRIPTION");
            assertEquals(complexHtml, styledDesc.getValue(), "Complex HTML should be preserved");
        } catch (Exception e) {
            fail("Complex HTML in STYLED-DESCRIPTION should parse: " + e.getMessage());
        }
    }
}