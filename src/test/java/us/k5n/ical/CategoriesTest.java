package us.k5n.ical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Categories.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class CategoriesTest implements Constants {

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testOne() {
        String str = "CATEGORIES:Home,Personal";
        try {
            Categories c = new Categories(str, PARSE_STRICT);
            System.out.println("ICAL: " + c.toICalendar());
            assertNotNull(c, "Null categories");
            assertEquals("Home,Personal", c.getValue(), "Value should be parsed correctly");
            assertNull(c.language, "Language should be null when not specified");
        } catch (ParseException e) {
            fail("ParseException: " + e.getMessage());
        }
    }

    @Test
    public void testCategoriesWithLanguage() {
        String str = "CATEGORIES;LANGUAGE=en:Work,Meeting,Project";
        try {
            Categories c = new Categories(str, PARSE_STRICT);
            assertNotNull(c, "Categories should not be null");
            assertEquals("Work,Meeting,Project", c.getValue(), "Value should be parsed correctly");
            assertEquals("en", c.language, "Language should be 'en'");
        } catch (ParseException e) {
            fail("ParseException: " + e.getMessage());
        }
    }

    @Test
    public void testCategoriesWithoutLanguage() {
        String str = "CATEGORIES:Personal,Business,Social";
        try {
            Categories c = new Categories(str, PARSE_STRICT);
            assertNotNull(c, "Categories should not be null");
            assertEquals("Personal,Business,Social", c.getValue(), "Value should be parsed correctly");
            assertNull(c.language, "Language should be null when not specified");
        } catch (ParseException e) {
            fail("ParseException: " + e.getMessage());
        }
    }

    @Test
    public void testEmptyCategories() {
        String str = "CATEGORIES:";
        try {
            Categories c = new Categories(str, PARSE_STRICT);
            assertNotNull(c, "Categories should not be null");
            assertEquals("", c.getValue(), "Value should be empty");
            assertNull(c.language, "Language should be null");
        } catch (ParseException e) {
            fail("ParseException: " + e.getMessage());
        }
    }

    @Test
    public void testCategoriesWithSpecialCharacters() {
        String str = "CATEGORIES:Work\\,Home,Emergency\\,Urgent";
        try {
            Categories c = new Categories(str, PARSE_STRICT);
            assertNotNull(c, "Categories should not be null");
            assertEquals("Work,Home,Emergency,Urgent", c.getValue(), "Escaped commas should be handled");
        } catch (ParseException e) {
            fail("ParseException: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidAttribute() {
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
    public void testLanguageOverride() {
        // iCalendar allows attribute override, so duplicate attributes replace previous ones
        String str = "CATEGORIES;LANGUAGE=en;LANGUAGE=fr:Work";
        try {
            Categories c = new Categories(str, PARSE_STRICT);
            // Should keep the last value (fr)
            assertEquals("fr", c.language, "Should keep the last LANGUAGE value");
            assertEquals("Work", c.getValue(), "Value should be parsed correctly");
        } catch (Exception e) {
            fail("Should parse successfully: " + e.getMessage());
        }
    }

    @Test
    public void testLooseParsingWithInvalidAttribute() {
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