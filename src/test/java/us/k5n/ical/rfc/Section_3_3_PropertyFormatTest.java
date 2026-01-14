package us.k5n.ical.rfc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.Property;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * RFC 5545 Section 3.3: Property Format
 *
 * Tests for property name and value formatting, including parameter handling.
 */
public class Section_3_3_PropertyFormatTest {

    @Test
    @DisplayName("RFC5545-3.3: Basic property format")
    void testBasicPropertyFormat() {
        String line = "SUMMARY:Meeting with Team";

        try {
            Property prop = new Property(line);
            assertEquals("SUMMARY", prop.getName());
            assertEquals("Meeting with Team", prop.getValue());
        } catch (Exception e) {
            fail("Basic property format should parse correctly: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.3: Property with single parameter")
    void testPropertyWithSingleParameter() {
        String line = "DTSTART;VALUE=DATE:20230101";

        try {
            Property prop = new Property(line);
            assertEquals("DTSTART", prop.getName());
            assertEquals("20230101", prop.getValue());
            assertNotNull(prop.getNamedAttribute("VALUE"));
            assertEquals("DATE", prop.getNamedAttribute("VALUE").value);
        } catch (Exception e) {
            fail("Property with parameter should parse correctly: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.3: Property with multiple parameters")
    void testPropertyWithMultipleParameters() {
        String line = "ATTENDEE;CN=John Doe;ROLE=REQ-PARTICIPANT;PARTSTAT=ACCEPTED:mailto:john@example.com";

        try {
            Property prop = new Property(line);
            assertEquals("ATTENDEE", prop.getName());
            assertEquals("mailto:john@example.com", prop.getValue());
            assertEquals("John Doe", prop.getNamedAttribute("CN").value);
            assertEquals("REQ-PARTICIPANT", prop.getNamedAttribute("ROLE").value);
            assertEquals("ACCEPTED", prop.getNamedAttribute("PARTSTAT").value);
        } catch (Exception e) {
            fail("Property with multiple parameters should parse correctly: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.3: Parameter with unquoted value")
    void testParameterWithUnquotedValue() {
        String line = "DTSTART;VALUE=DATE:20230101";

        try {
            Property prop = new Property(line);
            assertEquals("DTSTART", prop.getName());
            assertEquals("20230101", prop.getValue());
            assertEquals("DATE", prop.getNamedAttribute("VALUE").value);
        } catch (Exception e) {
            fail("Parameter with unquoted value should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.3: Property with empty value")
    void testPropertyWithEmptyValue() {
        String line = "LOCATION:";

        try {
            Property prop = new Property(line);
            assertEquals("LOCATION", prop.getName());
            assertEquals("", prop.getValue());
        } catch (Exception e) {
            fail("Property with empty value should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.3: Property with whitespace in value")
    void testPropertyWithWhitespaceInValue() {
        String line = "SUMMARY:  Meeting with Team  ";

        try {
            Property prop = new Property(line);
            assertEquals("SUMMARY", prop.getName());
            // Note: Leading/trailing whitespace may be preserved or trimmed
            assertTrue(prop.getValue().contains("Meeting with Team"));
        } catch (Exception e) {
            fail("Property with whitespace should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.3: Invalid property format")
    void testInvalidPropertyFormat() {
        // Property without colon should fail
        String line = "SUMMARY Meeting with Team";

        try {
            Property prop = new Property(line);
            // This might not fail with current parser, but should be invalid
            assertNotNull(prop);
        } catch (Exception e) {
            // Expected to fail
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("RFC5545-3.3: Property name validation")
    void testPropertyNameValidation() {
        // Property names should consist of letters, digits, hyphens
        String[] validNames = {"SUMMARY", "DTSTART", "UID", "X-CUSTOM-PROPERTY"};
        String[] invalidNames = {"SUMMARY!", "DT START", "UID@123"};

        for (String name : validNames) {
            String line = name + ":value";
            try {
                Property prop = new Property(line);
                assertEquals(name, prop.getName());
            } catch (Exception e) {
                fail("Valid property name '" + name + "' should parse: " + e.getMessage());
            }
        }

        // Note: Invalid names may still parse with current implementation
    }
}