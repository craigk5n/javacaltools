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
	public void testSummaryWithLanguage() {
		String str = "SUMMARY;LANGUAGE=en:Meeting Summary";
		try {
			Summary summary = new Summary(str, PARSE_STRICT);
			assertNotNull(summary, "Summary should not be null");
			assertEquals("en", summary.language, "Language should be 'en'");
			assertEquals("Meeting Summary", summary.getValue(), "Value should be correct");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testSummaryWithAltRep() {
		String str = "SUMMARY;ALTREP=\"http://example.com/summary\":Meeting Summary";
		try {
			Summary summary = new Summary(str, PARSE_STRICT);
			assertNotNull(summary, "Summary should not be null");
			assertEquals("http://example.com/summary", summary.altrep, "ALTREP should be parsed");
			assertEquals("Meeting Summary", summary.getValue(), "Value should be correct");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testSummaryWithoutAttributes() {
		String str = "SUMMARY:This is a summary.";
		try {
			Summary summary = new Summary(str, PARSE_STRICT);
			assertNotNull(summary, "Summary should not be null");
			assertNull(summary.language, "Language should be null when not specified");
			assertNull(summary.altrep, "ALTREP should be null when not specified");
			assertEquals("This is a summary.", summary.getValue(), "Value should be correct");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testEmptySummary() {
		String str = "SUMMARY:";
		try {
			Summary summary = new Summary(str, PARSE_STRICT);
			assertNotNull(summary, "Summary should not be null");
			assertNull(summary.language, "Language should be null when not specified");
			assertNull(summary.altrep, "ALTREP should be null when not specified");
			assertEquals("", summary.getValue(), "Value should be empty");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testSummaryWithBothAttributes() {
		String str = "SUMMARY;LANGUAGE=fr;ALTREP=\"http://example.com/summary\":Résumé de réunion";
		try {
			Summary summary = new Summary(str, PARSE_STRICT);
			assertNotNull(summary, "Summary should not be null");
			assertEquals("fr", summary.language, "Language should be 'fr'");
			assertEquals("http://example.com/summary", summary.altrep, "ALTREP should be parsed");
			assertEquals("Résumé de réunion", summary.getValue(), "Value should be correct");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testInvalidAttribute() {
		String str = "SUMMARY;INVALID=value:Test Summary";
		try {
			Summary summary = new Summary(str, PARSE_STRICT);
			fail("Should throw exception for invalid attribute");
		} catch (ParseException e) {
			// Expected for strict parsing
		} catch (Exception e) {
			fail("Wrong exception type: " + e.getMessage());
		}
	}

    @Test
    public void testLanguageOverride() {
        // iCalendar allows attribute override, so duplicate attributes replace previous ones
        String str = "SUMMARY;LANGUAGE=en;LANGUAGE=fr:Test Summary";
        try {
            Summary summary = new Summary(str, PARSE_STRICT);
            // Should keep the last value (fr)
            assertEquals("fr", summary.language, "Should keep the last LANGUAGE value");
            assertEquals("Test Summary", summary.getValue(), "Value should be parsed correctly");
        } catch (Exception e) {
            fail("Should parse successfully: " + e.getMessage());
        }
    }

    @Test
    public void testAltRepOverride() {
        // iCalendar allows attribute override, so duplicate attributes replace previous ones
        String str = "SUMMARY;ALTREP=\"http://a.com\";ALTREP=\"http://b.com\":Test Summary";
        try {
            Summary summary = new Summary(str, PARSE_STRICT);
            // Should keep the last value
            assertEquals("http://b.com", summary.altrep, "Should keep the last ALTREP value");
            assertEquals("Test Summary", summary.getValue(), "Value should be parsed correctly");
        } catch (Exception e) {
            fail("Should parse successfully: " + e.getMessage());
        }
    }

	@Test
	public void testLooseParsingWithInvalidAttribute() {
		String str = "SUMMARY;INVALID=value:Test Summary";
		try {
			Summary summary = new Summary(str, PARSE_LOOSE);
			assertNotNull(summary, "Summary should not be null in loose mode");
			assertEquals("Test Summary", summary.getValue(), "Value should be parsed correctly");
		} catch (Exception e) {
			fail("Loose parsing should not fail: " + e.getMessage());
		}
	}
}