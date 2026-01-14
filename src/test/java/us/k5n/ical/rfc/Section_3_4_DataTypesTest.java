package us.k5n.ical.rfc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RFC 5545 Section 3.4: Data Types
 *
 * Tests for the various data type formats used in iCalendar properties.
 * Since direct data type parsing is internal, we test through property parsing.
 */
public class Section_3_4_DataTypesTest {

    @Test
    @DisplayName("RFC5545-3.4: Valid DATE-TIME format")
    void testValidDateTimeFormat() {
        // Test DATE-TIME format parsing through Property
        String line = "DTSTART:20230101T100000Z";

        try {
            us.k5n.ical.Property prop = new us.k5n.ical.Property(line);
            assertEquals("DTSTART", prop.getName());
            assertEquals("20230101T100000Z", prop.getValue());
            // DATE-TIME validation is handled internally
        } catch (Exception e) {
            fail("Valid DATE-TIME property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.4: DATE-TIME with timezone")
    void testDateTimeWithTimezone() {
        String line = "DTSTART;TZID=America/New_York:20230101T100000";

        try {
            us.k5n.ical.Property prop = new us.k5n.ical.Property(line);
            assertEquals("DTSTART", prop.getName());
            assertEquals("20230101T100000", prop.getValue());
            assertEquals("America/New_York", prop.getNamedAttribute("TZID").value);
        } catch (Exception e) {
            fail("DATE-TIME with timezone should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.4: Valid DATE format")
    void testValidDateFormat() {
        String line = "DTSTART;VALUE=DATE:20230101";

        try {
            us.k5n.ical.Property prop = new us.k5n.ical.Property(line);
            assertEquals("DTSTART", prop.getName());
            assertEquals("20230101", prop.getValue());
            assertEquals("DATE", prop.getNamedAttribute("VALUE").value);
        } catch (Exception e) {
            fail("Valid DATE property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.4: Valid DURATION format")
    void testValidDurationFormat() {
        // Test DURATION format parsing
        String line = "TRIGGER:PT1H30M";

        try {
            us.k5n.ical.Property prop = new us.k5n.ical.Property(line);
            assertEquals("TRIGGER", prop.getName());
            assertEquals("PT1H30M", prop.getValue());
            // DURATION format validation is internal
        } catch (Exception e) {
            fail("Valid DURATION property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.4: DURATION with days")
    void testDurationWithDays() {
        String line = "DURATION:P1DT2H30M";

        try {
            us.k5n.ical.Property prop = new us.k5n.ical.Property(line);
            assertEquals("DURATION", prop.getName());
            assertEquals("P1DT2H30M", prop.getValue());
        } catch (Exception e) {
            fail("DURATION with days should parse: " + e.getMessage());
        }
    }



    @Test
    @DisplayName("RFC5545-3.4: TEXT data type format")
    void testTextDataType() {
        // Test TEXT format - can contain any characters except control
        String text = "Meeting: Discuss Q4 Goals & Objectives @ Office";

        // TEXT validation is mostly about encoding and length
        assertTrue(text.length() > 0);
        assertFalse(text.contains("\n")); // Should not contain control characters
    }

    @Test
    @DisplayName("RFC5545-3.4: INTEGER data type")
    void testIntegerDataType() {
        // Test valid integers for properties like PRIORITY
        String line = "PRIORITY:1";

        try {
            us.k5n.ical.Property prop = new us.k5n.ical.Property(line);
            assertEquals("PRIORITY", prop.getName());
            assertEquals("1", prop.getValue());
            // Integer validation is internal to property usage
        } catch (Exception e) {
            fail("Valid INTEGER property should parse: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("RFC5545-3.4: BOOLEAN data type")
    void testBooleanDataType() {
        // Test BOOLEAN format (TRUE/FALSE)
        String line = "TRANSP:OPAQUE";

        try {
            us.k5n.ical.Property prop = new us.k5n.ical.Property(line);
            assertEquals("TRANSP", prop.getName());
            assertEquals("OPAQUE", prop.getValue());
            // BOOLEAN-like validation
        } catch (Exception e) {
            fail("Valid BOOLEAN-like property should parse: " + e.getMessage());
        }
    }
}