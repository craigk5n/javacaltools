package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Test cases for ParseError.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class ParseErrorTest {

    @Test
    public void testParseErrorConstructorWithInputData() {
        ParseError error = new ParseError(5, "Invalid property", "BEGIN:INVALID");

        assertEquals(5, error.lineNo, "Line number should be 5");
        assertEquals("Invalid property", error.error, "Error message should match");
        assertEquals("BEGIN:INVALID", error.inputData, "Input data should match");
    }

    @Test
    public void testParseErrorConstructorWithoutInputData() {
        ParseError error = new ParseError(10, "Missing required property");

        assertEquals(10, error.lineNo, "Line number should be 10");
        assertEquals("Missing required property", error.error, "Error message should match");
        assertEquals("[unknown]", error.inputData, "Input data should be default");
    }

    @Test
    public void testToString() {
        ParseError error = new ParseError(3, "Syntax error", "INVALID:LINE");

        String result = error.toString();

        assertTrue(result.contains("Error  : Syntax error"), "Should contain error message");
        assertTrue(result.contains("Line No: 3"), "Should contain line number");
        assertTrue(result.contains("Input  : INVALID:LINE"), "Should contain input data");
    }

    @Test
    public void testToStringWithIndent() {
        ParseError error = new ParseError(1, "Parse error", "BAD:DATA");

        String result = error.toString(4);

        assertTrue(result.contains("    Error  : Parse error"), "Should be indented");
        assertTrue(result.contains("    Line No: 1"), "Should be indented");
        assertTrue(result.contains("    Input  : BAD:DATA"), "Should be indented");
    }

    @Test
    public void testToStringIndentZero() {
        ParseError error = new ParseError(0, "Test error", "TEST");

        String result = error.toString(0);

        assertTrue(result.startsWith("Error  :"), "Should not be indented");
        assertFalse(result.contains("    Error"), "Should not contain indentation");
    }
}