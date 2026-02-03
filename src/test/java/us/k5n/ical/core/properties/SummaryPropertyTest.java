package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Constants;
import us.k5n.ical.ParseException;
import us.k5n.ical.Summary;

/**
 * RFC 5545 Section 3.8.1.12: SUMMARY Property Tests
 *
 * Tests for the SUMMARY property as defined in RFC 5545.
 * The SUMMARY property defines a short summary or subject for the
 * calendar component.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.1.12: SUMMARY Property")
public class SummaryPropertyTest implements Constants {

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.12: should parse summary without attributes")
    void should_parseSummary_when_noAttributesProvided() {
      String str = "SUMMARY:This is a summary.";
      try {
        Summary summary = new Summary(str, PARSE_STRICT);
        assertNotNull(summary, "Summary should not be null");
        assertNull(summary.getLanguage(), "Language should be null when not specified");
        assertNull(summary.getAltrep(), "ALTREP should be null when not specified");
        assertEquals("This is a summary.", summary.getValue(), "Value should be correct");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.12: should parse empty summary")
    void should_parseEmptySummary_when_emptyValueProvided() {
      String str = "SUMMARY:";
      try {
        Summary summary = new Summary(str, PARSE_STRICT);
        assertNotNull(summary, "Summary should not be null");
        assertNull(summary.getLanguage(), "Language should be null when not specified");
        assertNull(summary.getAltrep(), "ALTREP should be null when not specified");
        assertEquals("", summary.getValue(), "Value should be empty");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("LANGUAGE Parameter Tests")
  class LanguageParameterTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.12: should parse summary with LANGUAGE parameter")
    void should_parseLanguage_when_languageParameterProvided() {
      String str = "SUMMARY;LANGUAGE=en:Meeting Summary";
      try {
        Summary summary = new Summary(str, PARSE_STRICT);
        assertNotNull(summary, "Summary should not be null");
        assertEquals("en", summary.getLanguage(), "Language should be 'en'");
        assertEquals("Meeting Summary", summary.getValue(), "Value should be correct");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.12: should use last LANGUAGE value when duplicate")
    void should_useLastLanguage_when_duplicateLanguageProvided() {
      String str = "SUMMARY;LANGUAGE=en;LANGUAGE=fr:Test Summary";
      try {
        Summary summary = new Summary(str, PARSE_STRICT);
        assertEquals("fr", summary.getLanguage(), "Should keep the last LANGUAGE value");
        assertEquals("Test Summary", summary.getValue(), "Value should be parsed correctly");
      } catch (Exception e) {
        fail("Should parse successfully: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("ALTREP Parameter Tests")
  class AltRepParameterTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.12: should parse summary with ALTREP parameter")
    void should_parseAltrep_when_altrepParameterProvided() {
      String str = "SUMMARY;ALTREP=\"http://example.com/summary\":Meeting Summary";
      try {
        Summary summary = new Summary(str, PARSE_STRICT);
        assertNotNull(summary, "Summary should not be null");
        assertEquals("http://example.com/summary", summary.getAltrep(), "ALTREP should be parsed");
        assertEquals("Meeting Summary", summary.getValue(), "Value should be correct");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.12: should use last ALTREP value when duplicate")
    void should_useLastAltrep_when_duplicateAltrepProvided() {
      String str = "SUMMARY;ALTREP=\"http://a.com\";ALTREP=\"http://b.com\":Test Summary";
      try {
        Summary summary = new Summary(str, PARSE_STRICT);
        assertEquals("http://b.com", summary.getAltrep(), "Should keep the last ALTREP value");
        assertEquals("Test Summary", summary.getValue(), "Value should be parsed correctly");
      } catch (Exception e) {
        fail("Should parse successfully: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Multiple Parameters Tests")
  class MultipleParametersTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.12: should parse summary with both LANGUAGE and ALTREP")
    void should_parseBothParameters_when_languageAndAltrepProvided() {
      String str = "SUMMARY;LANGUAGE=fr;ALTREP=\"http://example.com/summary\":Résumé de réunion";
      try {
        Summary summary = new Summary(str, PARSE_STRICT);
        assertNotNull(summary, "Summary should not be null");
        assertEquals("fr", summary.getLanguage(), "Language should be 'fr'");
        assertEquals("http://example.com/summary", summary.getAltrep(), "ALTREP should be parsed");
        assertEquals("Résumé de réunion", summary.getValue(), "Value should be correct");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Error Handling Tests")
  class ErrorHandlingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.12: should reject invalid attribute in STRICT mode")
    void should_rejectInvalidAttribute_when_strictModeEnabled() {
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
    @DisplayName("RFC 5545 Section 3.8.1.12: should accept invalid attribute in LOOSE mode")
    void should_acceptInvalidAttribute_when_looseModeEnabled() {
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
}
