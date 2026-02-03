package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Categories;
import us.k5n.ical.Constants;
import us.k5n.ical.ParseException;

/**
 * RFC 5545 Section 3.8.1.2: CATEGORIES Property Tests
 *
 * Tests for the CATEGORIES property as defined in RFC 5545.
 * The CATEGORIES property defines the categories for a calendar component.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.1.2: CATEGORIES Property")
public class CategoriesPropertyTest implements Constants {

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.2: should parse comma-separated categories")
    void should_parseCategories_when_commaSeparated() {
      String str = "CATEGORIES:Home,Personal";
      try {
        Categories c = new Categories(str, PARSE_STRICT);
        assertNotNull(c, "Null categories");
        assertEquals("Home,Personal", c.getValue(), "Value should be parsed correctly");
        assertNull(c.getLanguage(), "Language should be null when not specified");
      } catch (ParseException e) {
        fail("ParseException: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.2: should parse multiple categories")
    void should_parseMultipleCategories_when_threeCategories() {
      String str = "CATEGORIES:Personal,Business,Social";
      try {
        Categories c = new Categories(str, PARSE_STRICT);
        assertNotNull(c, "Categories should not be null");
        assertEquals("Personal,Business,Social", c.getValue(), "Value should be parsed correctly");
        assertNull(c.getLanguage(), "Language should be null when not specified");
      } catch (ParseException e) {
        fail("ParseException: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.2: should parse empty categories")
    void should_parseEmptyCategories_when_emptyValueProvided() {
      String str = "CATEGORIES:";
      try {
        Categories c = new Categories(str, PARSE_STRICT);
        assertNotNull(c, "Categories should not be null");
        assertEquals("", c.getValue(), "Value should be empty");
        assertNull(c.getLanguage(), "Language should be null");
      } catch (ParseException e) {
        fail("ParseException: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("LANGUAGE Parameter Tests")
  class LanguageParameterTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.2: should parse categories with LANGUAGE parameter")
    void should_parseLanguage_when_languageParameterProvided() {
      String str = "CATEGORIES;LANGUAGE=en:Work,Meeting,Project";
      try {
        Categories c = new Categories(str, PARSE_STRICT);
        assertNotNull(c, "Categories should not be null");
        assertEquals("Work,Meeting,Project", c.getValue(), "Value should be parsed correctly");
        assertEquals("en", c.getLanguage(), "Language should be 'en'");
      } catch (ParseException e) {
        fail("ParseException: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.2: should use last LANGUAGE value when duplicate")
    void should_useLastLanguage_when_duplicateLanguageProvided() {
      String str = "CATEGORIES;LANGUAGE=en;LANGUAGE=fr:Work";
      try {
        Categories c = new Categories(str, PARSE_STRICT);
        assertEquals("fr", c.getLanguage(), "Should keep the last LANGUAGE value");
        assertEquals("Work", c.getValue(), "Value should be parsed correctly");
      } catch (Exception e) {
        fail("Should parse successfully: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Escaped Characters Tests")
  class EscapedCharactersTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.2: should handle escaped commas in categories")
    void should_handleEscapedCommas_when_categoryContainsComma() {
      String str = "CATEGORIES:Work\\,Home,Emergency\\,Urgent";
      try {
        Categories c = new Categories(str, PARSE_STRICT);
        assertNotNull(c, "Categories should not be null");
        assertEquals("Work,Home,Emergency,Urgent", c.getValue(), "Escaped commas should be handled");
      } catch (ParseException e) {
        fail("ParseException: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Error Handling Tests")
  class ErrorHandlingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.2: should reject invalid attribute in STRICT mode")
    void should_rejectInvalidAttribute_when_strictModeEnabled() {
      String str = "CATEGORIES;INVALID=VALUE:Work,Home";
      try {
        Categories c = new Categories(str, PARSE_STRICT);
        fail("Should throw exception for invalid attribute");
      } catch (ParseException e) {
        // Expected for strict parsing
      } catch (Exception e) {
        fail("Wrong exception type: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.2: should accept invalid attribute in LOOSE mode")
    void should_acceptInvalidAttribute_when_looseModeEnabled() {
      String str = "CATEGORIES;INVALID=VALUE:Work,Home";
      try {
        Categories c = new Categories(str, PARSE_LOOSE);
        assertNotNull(c, "Categories should not be null in loose mode");
        assertEquals("Work,Home", c.getValue(), "Value should be parsed correctly");
      } catch (Exception e) {
        fail("Loose parsing should not fail: " + e.getMessage());
      }
    }
  }
}
