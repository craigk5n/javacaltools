package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Comment;
import us.k5n.ical.Constants;
import us.k5n.ical.ParseException;

/**
 * RFC 5545 Section 3.8.1.4: COMMENT Property Tests
 *
 * Tests for the COMMENT property as defined in RFC 5545.
 * The COMMENT property specifies non-processing information intended to
 * provide a comment to the calendar user.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.1.4: COMMENT Property")
public class CommentPropertyTest implements Constants {

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.4: should parse comment without attributes")
    void should_parseComment_when_noAttributesProvided() {
      String str = "COMMENT:This is a comment.";
      try {
        Comment comment = new Comment(str, PARSE_STRICT);
        assertNotNull(comment, "Comment should not be null");
        assertNull(comment.getLanguage(), "Language should be null when not specified");
        assertNull(comment.getAltrep(), "ALTREP should be null when not specified");
        assertEquals("This is a comment.", comment.getValue(), "Value should be correct");
      } catch (Exception e) {
        fail("Comment parsing failed: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.4: should parse empty comment")
    void should_parseEmptyComment_when_emptyValueProvided() {
      String str = "COMMENT:";
      try {
        Comment comment = new Comment(str, PARSE_STRICT);
        assertNotNull(comment, "Comment should not be null");
        assertNull(comment.getLanguage(), "Language should be null");
        assertNull(comment.getAltrep(), "ALTREP should be null");
        assertEquals("", comment.getValue(), "Value should be empty");
      } catch (Exception e) {
        fail("Comment parsing failed: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("LANGUAGE Parameter Tests")
  class LanguageParameterTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.4: should parse comment with LANGUAGE parameter")
    void should_parseLanguage_when_languageParameterProvided() {
      String str = "COMMENT;LANGUAGE=fr:Un commentaire";
      try {
        Comment comment = new Comment(str, PARSE_STRICT);
        assertNotNull(comment, "Comment should not be null");
        assertEquals("fr", comment.getLanguage(), "Language should be 'fr'");
        assertEquals("Un commentaire", comment.getValue(), "Value should be correct");
        assertNull(comment.getAltrep(), "ALTREP should be null when not specified");
      } catch (Exception e) {
        fail("Comment parsing failed: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("ALTREP Parameter Tests")
  class AltRepParameterTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.4: should parse comment with ALTREP parameter")
    void should_parseAltrep_when_altrepParameterProvided() {
      String str = "COMMENT;ALTREP=\"http://example.com/comment\":A comment";
      try {
        Comment comment = new Comment(str, PARSE_STRICT);
        assertNotNull(comment, "Comment should not be null");
        assertEquals("http://example.com/comment", comment.getAltrep(), "ALTREP should be parsed");
        assertEquals("A comment", comment.getValue(), "Value should be correct");
        assertNull(comment.getLanguage(), "Language should be null when not specified");
      } catch (Exception e) {
        fail("Comment parsing failed: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Multiple Parameters Tests")
  class MultipleParametersTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.4: should parse comment with both LANGUAGE and ALTREP")
    void should_parseBothParameters_when_languageAndAltrepProvided() {
      String str = "COMMENT;LANGUAGE=en;ALTREP=\"http://example.com/comment\":An English comment";
      try {
        Comment comment = new Comment(str, PARSE_STRICT);
        assertNotNull(comment, "Comment should not be null");
        assertEquals("en", comment.getLanguage(), "Language should be 'en'");
        assertEquals("http://example.com/comment", comment.getAltrep(), "ALTREP should be parsed");
        assertEquals("An English comment", comment.getValue(), "Value should be correct");
      } catch (Exception e) {
        fail("Comment parsing failed: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Error Handling Tests")
  class ErrorHandlingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.4: should reject invalid attribute in STRICT mode")
    void should_rejectInvalidAttribute_when_strictModeEnabled() {
      String str = "COMMENT;INVALID=value:A comment";
      try {
        Comment comment = new Comment(str, PARSE_STRICT);
        fail("Should throw exception for invalid attribute");
      } catch (ParseException e) {
        // Expected for strict parsing
      } catch (Exception e) {
        fail("Wrong exception type: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.4: should accept invalid attribute in LOOSE mode")
    void should_acceptInvalidAttribute_when_looseModeEnabled() {
      String str = "COMMENT;INVALID=value:A comment";
      try {
        Comment comment = new Comment(str, PARSE_LOOSE);
        assertNotNull(comment, "Comment should not be null in loose mode");
        assertEquals("A comment", comment.getValue(), "Value should be parsed correctly");
      } catch (Exception e) {
        fail("Loose parsing should not fail: " + e.getMessage());
      }
    }
  }
}
