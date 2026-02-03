package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Constants;
import us.k5n.ical.Sequence;

/**
 * RFC 5545 Section 3.8.7.4: SEQUENCE Property Tests
 *
 * Tests for the SEQUENCE property as defined in RFC 5545.
 * The SEQUENCE property defines the revision sequence number of the
 * calendar component within a sequence of revisions.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.7.4: SEQUENCE Property")
public class SequencePropertyTest implements Constants {

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.7.4: should parse sequence number and support increment")
    void should_parseAndIncrement_when_validSequenceProvided() {
      String str = "SEQUENCE:1";
      try {
        Sequence sequence = new Sequence(str, PARSE_STRICT);
        assertNotNull(sequence, "Sequence should not be null");
        assertEquals(1, sequence.getNum(), "Incorrect initial sequence number");

        sequence.increment();
        assertEquals(2, sequence.getNum(), "Incorrect sequence number after increment");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Exception thrown during test: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.7.4: should parse zero sequence")
    void should_parseZero_when_zeroSequenceProvided() {
      String str = "SEQUENCE:0";
      try {
        Sequence sequence = new Sequence(str, PARSE_STRICT);
        assertNotNull(sequence, "Sequence should not be null");
        assertEquals(0, sequence.getNum(), "Incorrect initial sequence number");

        sequence.increment();
        assertEquals(1, sequence.getNum(), "Incorrect sequence number after increment");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Exception thrown during test: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Large Number Tests")
  class LargeNumberTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.7.4: should handle large sequence numbers")
    void should_handleLargeNumber_when_largeSequenceProvided() {
      String str = "SEQUENCE:999999999";
      try {
        Sequence sequence = new Sequence(str, PARSE_STRICT);
        assertNotNull(sequence, "Sequence should not be null");
        assertEquals(999999999, sequence.getNum(), "Incorrect initial sequence number");

        sequence.increment();
        assertEquals(1000000000, sequence.getNum(), "Incorrect sequence number after increment");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Exception thrown during test: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.7.4: should handle negative sequence numbers")
    void should_handleNegative_when_negativeSequenceProvided() {
      String str = "SEQUENCE:-1";
      try {
        Sequence sequence = new Sequence(str, PARSE_STRICT);
        assertNotNull(sequence, "Sequence should not be null");
        assertEquals(-1, sequence.getNum(), "Incorrect initial sequence number");

        sequence.increment();
        assertEquals(0, sequence.getNum(), "Incorrect sequence number after increment from negative");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Exception thrown during test: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Error Handling Tests")
  class ErrorHandlingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.7.4: should reject invalid sequence number")
    void should_rejectInvalid_when_nonNumericSequenceProvided() {
      String str = "SEQUENCE:ABC";
      Exception exception = assertThrows(Exception.class, () -> {
        new Sequence(str, PARSE_STRICT);
      });
      assertTrue(exception.getMessage().contains("Invalid SEQUENCE"),
          "Expected invalid sequence number exception");
    }
  }
}
