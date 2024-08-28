package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Description.
 * 
 * @author Craig Knudsen
 */
public class DescriptionTest implements Constants {

	@BeforeEach
	public void setUp() {
	}

	@Test
	public void testBasicDescription() {
		String str = "DESCRIPTION;LANGUAGE=EN:This is\\na test.";
		try {
			Description x = new Description(str, PARSE_STRICT);
			assertNotNull(x, "Description should not be null");
			assertNotNull(x.language, "Language should not be null");
			assertNotNull(x.value, "Text should not be null");
			assertTrue(x.value.startsWith("This is"), "Text should start with 'This is'");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testEmptyDescription() {
		String str = "DESCRIPTION:";
		try {
			Description x = new Description(str, PARSE_STRICT);
			assertNotNull(x, "Description should not be null");
			assertEquals("", x.value, "Text should be empty");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testMultiLineDescription() {
		String str = "DESCRIPTION:This is a test\\nwith multiple lines\\nand special characters\\, like commas.";
		try {
			Description x = new Description(str, PARSE_STRICT);
			assertNotNull(x, "Description should not be null");
			assertNotNull(x.value, "Text should not be null");
			assertTrue(x.value.contains("multiple lines"), "Text should contain 'multiple lines'");
			assertTrue(x.value.contains("special characters"), "Text should contain 'special characters'");
			assertTrue(x.value.contains(","), "Text should contain a comma");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testDescriptionWithLanguage() {
		String str = "DESCRIPTION;LANGUAGE=FR:Voici une description.";
		try {
			Description x = new Description(str, PARSE_STRICT);
			assertNotNull(x, "Description should not be null");
			assertNotNull(x.language, "Language should not be null");
			assertEquals("FR", x.language, "Language should be 'FR'");
			assertNotNull(x.value, "Text should not be null");
			assertTrue(x.value.startsWith("Voici une"), "Text should start with 'Voici une'");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testDescriptionWithSpecialCharacters() {
		String str = "DESCRIPTION:This description contains special characters like \\n, \\t, and \\\\.";
		try {
			Description x = new Description(str, PARSE_STRICT);
			assertNotNull(x, "Description should not be null");
			assertNotNull(x.value, "Text should not be null");
			assertTrue(x.value.contains("\n"), "Text should contain a newline character");
			assertTrue(x.value.contains("\t"), "Text should contain a tab character");
			assertTrue(x.value.contains("\\"), "Text should contain a backslash character");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testDescriptionWithNoLanguage() {
		String str = "DESCRIPTION:No language specified for this description.";
		try {
			Description x = new Description(str, PARSE_STRICT);
			assertNotNull(x, "Description should not be null");
			assertNotNull(x.value, "Text should not be null");
			assertEquals("No language specified for this description.", x.value,
					"Text should match the expected description");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception: " + e.toString());
		}
	}

	@Test
	public void testInvalidDescription() {
		String str = "DESCRIPTION;LANGUAGE=INVALID:This is an invalid language code.";
		try {
			Description x = new Description(str, PARSE_STRICT);
			assertNotNull(x, "Description should not be null");
			assertEquals("INVALID", x.language, "Language should be 'INVALID'");
			assertNotNull(x.value, "Text should not be null");
		} catch (Exception e) {
			fail("Exception should not be thrown for invalid language: " + e.toString());
		}
	}
}