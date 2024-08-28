package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Sequence.
 * 
 * @author Craig Knudsen
 */
public class SequenceTest implements Constants {

	@BeforeEach
	public void setUp() {
		// Setup code, if necessary
	}

	@Test
	public void testSequenceInitializationAndIncrement() {
		String str = "SEQUENCE:1";
		try {
			Sequence sequence = new Sequence(str, PARSE_STRICT);
			assertNotNull(sequence, "Sequence should not be null");
			assertEquals(1, sequence.num, "Incorrect initial sequence number");

			sequence.increment();
			assertEquals(2, sequence.num, "Incorrect sequence number after increment");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testSequenceWithLargeNumber() {
		String str = "SEQUENCE:999999999";
		try {
			Sequence sequence = new Sequence(str, PARSE_STRICT);
			assertNotNull(sequence, "Sequence should not be null");
			assertEquals(999999999, sequence.num, "Incorrect initial sequence number");

			sequence.increment();
			assertEquals(1000000000, sequence.num, "Incorrect sequence number after increment");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testSequenceWithZero() {
		String str = "SEQUENCE:0";
		try {
			Sequence sequence = new Sequence(str, PARSE_STRICT);
			assertNotNull(sequence, "Sequence should not be null");
			assertEquals(0, sequence.num, "Incorrect initial sequence number");

			sequence.increment();
			assertEquals(1, sequence.num, "Incorrect sequence number after increment");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testInvalidSequenceNumber() {
		String str = "SEQUENCE:ABC";
		Exception exception = assertThrows(Exception.class, () -> {
			new Sequence(str, PARSE_STRICT);
		});
		assertTrue(exception.getMessage().contains("Invalid SEQUENCE"),
				"Expected invalid sequence number exception");
	}

	@Test
	public void testNegativeSequenceNumber() {
		String str = "SEQUENCE:-1";
		try {
			Sequence sequence = new Sequence(str, PARSE_STRICT);
			assertNotNull(sequence, "Sequence should not be null");
			assertEquals(-1, sequence.num, "Incorrect initial sequence number");

			sequence.increment();
			assertEquals(0, sequence.num, "Incorrect sequence number after increment from negative");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}
}