package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Summary.
 * 
 * @author Craig Knudsen
 */
public class SummaryTest implements Constants {

	@BeforeEach
	public void setUp() {
		// Setup code, if necessary
	}

	@Test
	public void testSummaryWithLanguageAndNewline() {
		String str = "SUMMARY;LANGUAGE=EN:This is\\na summary.";
		try {
			Description description = new Description(str, PARSE_STRICT);
			assertNotNull(description, "Description should not be null");
			assertNotNull(description.language, "Language should not be null");
			assertNotNull(description.value, "Text should not be null");
			assertTrue(description.value.startsWith("This is"), "Text should start with 'This is'");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testSummaryWithoutLanguage() {
		String str = "SUMMARY:This is a summary.";
		try {
			Description description = new Description(str, PARSE_STRICT);
			assertNotNull(description, "Description should not be null");
			assertNull(description.language, "Language should be null when not specified");
			assertNotNull(description.value, "Text should not be null");
			assertEquals("This is a summary.", description.value, "Incorrect summary text");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testEmptySummary() {
		String str = "SUMMARY:";
		try {
			Description description = new Description(str, PARSE_STRICT);
			assertNotNull(description, "Description should not be null");
			assertNull(description.language, "Language should be null when not specified");
			assertEquals("", description.value, "Text should be empty for an empty summary");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testSummaryWithSpecialCharacters() {
		String str = "SUMMARY:This is a summary with special characters: \\n, \\t, \\ ;";
		try {
			Description description = new Description(str, PARSE_STRICT);
			assertNotNull(description, "Description should not be null");
			assertNull(description.language, "Language should be null when not specified");
			assertTrue(description.value.contains("\n, \t, \\ ;"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}
}