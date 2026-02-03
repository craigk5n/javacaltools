package us.k5n.ical.infrastructure;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.StringReader;
import java.util.List;

import us.k5n.ical.*;

/**
 * Test cases for CalendarParser base class methods.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class CalendarParserTest implements Constants {

    private TestCalendarParser parser;

    @BeforeEach
    public void setUp() {
        parser = new TestCalendarParser(PARSE_LOOSE);
    }

    @Test
    public void testConstructor() {
        assertEquals(PARSE_LOOSE, parser.getParseMethod(), "Parse method should be set correctly");
        assertEquals(1, parser.numDataStores(), "Should have one default datastore");
        assertTrue(parser.isParseLoose(), "Should be loose parsing");
        assertFalse(parser.isParseStrict(), "Should not be strict parsing");
    }

    @Test
    public void testSetParseMethod() {
        parser.setParseMethod(PARSE_STRICT);
        assertEquals(PARSE_STRICT, parser.getParseMethod(), "Parse method should be updated");
        assertTrue(parser.isParseStrict(), "Should be strict parsing");
        assertFalse(parser.isParseLoose(), "Should not be loose parsing");
    }

    @Test
    public void testDataStoreManagement() {
        // Should start with 1 datastore
        assertEquals(1, parser.numDataStores());

        // Add another datastore
        DataStore ds = new DefaultDataStore();
        parser.addDataStore(ds);
        assertEquals(2, parser.numDataStores());

        // Get datastore
        assertNotNull(parser.getDataStoreAt(0));
        assertNotNull(parser.getDataStoreAt(1));
        assertEquals(ds, parser.getDataStoreAt(1));

        // Remove datastore
        assertTrue(parser.removeDataStoreAt(1));
        assertEquals(1, parser.numDataStores());

        // Try to remove non-existent datastore
        assertFalse(parser.removeDataStoreAt(5));
        assertEquals(1, parser.numDataStores());
    }

    @Test
    public void testErrorHandling() {
        // Initially no errors
        assertEquals(0, parser.getAllErrors().size());

        // Add a parse error
        ParseError error = new ParseError(1, "Test error", "BAD:DATA");
        parser.reportParseError(error);

        List<ParseError> errors = parser.getAllErrors();
        assertEquals(1, errors.size());
        assertEquals("Test error", errors.get(0).error);
    }

    @Test
    public void testParseErrorListener() {
        TestParseErrorListener listener = new TestParseErrorListener();
        parser.addParseErrorListener(listener);

        // Report an error
        ParseError error = new ParseError(2, "Listener test", "TEST:DATA");
        parser.reportParseError(error);

        // Check that listener was called
        assertEquals(1, listener.errorsReceived.size());
        assertEquals("Listener test", listener.errorsReceived.get(0).error);
    }

    @Test
    public void testToICalendar() {
        // Create a basic iCalendar output
        String ical = parser.toICalendar();

        assertTrue(ical.contains("BEGIN:VCALENDAR"), "Should contain VCALENDAR begin");
        assertTrue(ical.contains("VERSION:2.0"), "Should contain version");
        assertTrue(ical.contains("PRODID:"), "Should contain PRODID");
        assertTrue(ical.contains("END:VCALENDAR"), "Should contain VCALENDAR end");
    }

    @Test
    public void testToICalendarBasic() {
        // Test basic iCalendar structure generation
        String ical = parser.toICalendar();

        assertTrue(ical.startsWith("BEGIN:VCALENDAR"), "Should start with VCALENDAR begin");
        assertTrue(ical.contains("VERSION:2.0"), "Should contain version");
        assertTrue(ical.contains("PRODID:"), "Should contain PRODID");
        assertTrue(ical.contains("END:VCALENDAR"), "Should contain VCALENDAR end");
    }

    // Test implementation of CalendarParser for testing
    private static class TestCalendarParser extends CalendarParser {
        public TestCalendarParser(int parseMethod) {
            super(parseMethod);
        }

        @Override
        public boolean parse(java.io.Reader reader) throws java.io.IOException {
            // Dummy implementation for testing
            return true;
        }
    }

    // Test implementation of ParseErrorListener
    private static class TestParseErrorListener implements ParseErrorListener {
        public java.util.List<ParseError> errorsReceived = new java.util.ArrayList<>();

        @Override
        public void reportParseError(ParseError error) {
            errorsReceived.add(error);
        }
    }
}
