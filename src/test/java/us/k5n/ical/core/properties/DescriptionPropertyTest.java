package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Constants;
import us.k5n.ical.Description;

/**
 * RFC 5545 Section 3.8.1.5: DESCRIPTION Property Tests
 *
 * Tests for the DESCRIPTION property as defined in RFC 5545.
 * The DESCRIPTION property provides a more complete description
 * of the calendar component than that provided by the SUMMARY property.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.1.5: DESCRIPTION Property")
public class DescriptionPropertyTest implements Constants {

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.5: should parse basic description with language")
    void should_parseDescription_when_languageProvided() {
      String str = "DESCRIPTION;LANGUAGE=EN:This is\\na test.";
      try {
        Description x = new Description(str, PARSE_STRICT);
        assertNotNull(x, "Description should not be null");
        assertNotNull(x.getLanguage(), "Language should not be null");
        assertNotNull(x.getValue(), "Text should not be null");
        assertTrue(x.getValue().startsWith("This is"), "Text should start with 'This is'");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.5: should parse empty description")
    void should_parseEmptyDescription_when_emptyValueProvided() {
      String str = "DESCRIPTION:";
      try {
        Description x = new Description(str, PARSE_STRICT);
        assertNotNull(x, "Description should not be null");
        assertEquals("", x.getValue(), "Text should be empty");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.5: should parse description without language")
    void should_parseDescription_when_noLanguageProvided() {
      String str = "DESCRIPTION:No language specified for this description.";
      try {
        Description x = new Description(str, PARSE_STRICT);
        assertNotNull(x, "Description should not be null");
        assertNotNull(x.getValue(), "Text should not be null");
        assertEquals("No language specified for this description.", x.getValue(),
            "Text should match the expected description");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Multi-line and Escaped Characters Tests")
  class EscapedCharactersTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.5: should parse multi-line description")
    void should_parseMultiline_when_escapedNewlines() {
      String str = "DESCRIPTION:This is a test\\nwith multiple lines\\nand special characters\\, like commas.";
      try {
        Description x = new Description(str, PARSE_STRICT);
        assertNotNull(x, "Description should not be null");
        assertNotNull(x.getValue(), "Text should not be null");
        assertTrue(x.getValue().contains("multiple lines"), "Text should contain 'multiple lines'");
        assertTrue(x.getValue().contains("special characters"), "Text should contain 'special characters'");
        assertTrue(x.getValue().contains(","), "Text should contain a comma");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.5: should parse special escaped characters")
    void should_parseEscapedCharacters_when_specialCharactersPresent() {
      String str = "DESCRIPTION:This description contains special characters like \\n, \\t, and \\\\.";
      try {
        Description x = new Description(str, PARSE_STRICT);
        assertNotNull(x, "Description should not be null");
        assertNotNull(x.getValue(), "Text should not be null");
        assertTrue(x.getValue().contains("\n"), "Text should contain a newline character");
        assertTrue(x.getValue().contains("\t"), "Text should contain a tab character");
        assertTrue(x.getValue().contains("\\"), "Text should contain a backslash character");
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
    @DisplayName("RFC 5545 Section 3.8.1.5: should parse description with specific language")
    void should_parseLanguage_when_languageParameterProvided() {
      String str = "DESCRIPTION;LANGUAGE=FR:Voici une description.";
      try {
        Description x = new Description(str, PARSE_STRICT);
        assertNotNull(x, "Description should not be null");
        assertNotNull(x.getLanguage(), "Language should not be null");
        assertEquals("FR", x.getLanguage(), "Language should be 'FR'");
        assertNotNull(x.getValue(), "Text should not be null");
        assertTrue(x.getValue().startsWith("Voici une"), "Text should start with 'Voici une'");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.5: should accept non-standard language codes")
    void should_acceptNonStandardLanguage_when_invalidLanguageCode() {
      String str = "DESCRIPTION;LANGUAGE=INVALID:This is an invalid language code.";
      try {
        Description x = new Description(str, PARSE_STRICT);
        assertNotNull(x, "Description should not be null");
        assertEquals("INVALID", x.getLanguage(), "Language should be 'INVALID'");
        assertNotNull(x.getValue(), "Text should not be null");
      } catch (Exception e) {
        fail("Exception should not be thrown for invalid language: " + e.toString());
      }
    }
  }
}
