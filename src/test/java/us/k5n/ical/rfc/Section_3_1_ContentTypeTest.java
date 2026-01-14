package us.k5n.ical.rfc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.k5n.ical.DefaultDataStore;
import us.k5n.ical.ICalendarParser;
import us.k5n.ical.Property;

/**
 * RFC 5545 Section 3.1: Content Lines
 *
 * Tests for the format and parsing of content lines in iCalendar objects.
 * Content lines have the format: name *(";" param) ":" value
 */
public class Section_3_1_ContentTypeTest {

    @Test
    @DisplayName("RFC5545-3.1: Valid content line format")
    void testValidContentLineFormat() throws Exception {
        String icalData = Files.readString(Path.of("src/test/resources/rfc5545/content-type/valid-basic.ics"));

        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
        parser.parse(new StringReader(icalData));

        // Should parse without errors
        assertTrue(parser.getAllErrors().isEmpty(), "Basic calendar should parse without errors");

        // Verify basic structure
        DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
        assertNotNull(ds, "DataStore should be created");
        assertFalse(ds.getAllEvents().isEmpty(), "Should contain at least one event");
    }

    @Test
    @DisplayName("RFC5545-3.1: Content line with parameters")
    void testContentLineWithParameters() {
        // Test property parsing with parameters
        String line = "DTSTART;VALUE=DATE:20230101";

        try {
            Property prop = new Property(line);
            assertEquals("DTSTART", prop.getName(), "Property name should be parsed correctly");
            assertEquals("20230101", prop.getValue(), "Property value should be parsed correctly");
            assertEquals("DATE", prop.getNamedAttribute("VALUE").value, "VALUE parameter should be DATE");
        } catch (Exception e) {
            fail("Property parsing should not fail: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.1: Multiple parameter content line")
    void testMultipleParameterContentLine() {
        String line = "ATTENDEE;CN=John Doe;ROLE=REQ-PARTICIPANT:mailto:john@example.com";

        try {
            Property prop = new Property(line);
            assertEquals("ATTENDEE", prop.getName());
            assertEquals("mailto:john@example.com", prop.getValue());
            assertEquals("John Doe", prop.getNamedAttribute("CN").value);
            assertEquals("REQ-PARTICIPANT", prop.getNamedAttribute("ROLE").value);
        } catch (Exception e) {
            fail("Multiple parameter parsing should not fail: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.1: Parameter values")
    void testParameterValues() {
        String line = "SUMMARY;LANGUAGE=en:Meeting Summary";

        try {
            Property prop = new Property(line);
            assertEquals("SUMMARY", prop.getName());
            assertEquals("Meeting Summary", prop.getValue());
            assertEquals("en", prop.getNamedAttribute("LANGUAGE").value);
        } catch (Exception e) {
            fail("Parameter parsing should not fail: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.1: Case insensitive property names")
    void testCaseInsensitivePropertyNames() {
        String line1 = "dtstart:20230101T100000Z";
        String line2 = "DTSTART:20230101T100000Z";

        try {
            Property prop1 = new Property(line1);
            Property prop2 = new Property(line2);

            assertEquals("DTSTART", prop1.getName());
            assertEquals("DTSTART", prop2.getName());
        } catch (Exception e) {
            fail("Case insensitive parsing should not fail: " + e.getMessage());
        }
    }
}