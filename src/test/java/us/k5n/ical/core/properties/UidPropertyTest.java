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
import us.k5n.ical.Uid;

/**
 * RFC 5545 Section 3.8.4.7: UID Property Tests
 *
 * Tests for the UID property as defined in RFC 5545.
 * The UID property defines the persistent, globally unique identifier
 * for the calendar component.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.4.7: UID Property")
public class UidPropertyTest implements Constants {

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.7: should parse UID with domain")
    void should_parseUid_when_validUidWithDomain() {
      String str = "UID:SFSDFSDFSDF-SDFDFSDFSD@xxx.com";
      try {
        Uid uid = new Uid(str, PARSE_STRICT);
        assertNotNull(uid, "Uid should not be null");
        assertEquals("SFSDFSDFSDF-SDFDFSDFSD@xxx.com", uid.getValue(), "Uid value is incorrect");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.7: should parse UID with special characters")
    void should_parseUid_when_uidContainsSpecialCharacters() {
      String str = "UID:1234-5678_90@domain.com";
      try {
        Uid uid = new Uid(str, PARSE_STRICT);
        assertNotNull(uid, "Uid should not be null");
        assertEquals("1234-5678_90@domain.com", uid.getValue(), "Uid value is incorrect");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.7: should parse UID without @ symbol")
    void should_parseUid_when_uidHasNoAtSymbol() {
      String str = "UID:1234567890domain.com";
      try {
        Uid uid = new Uid(str, PARSE_STRICT);
        assertNotNull(uid, "Uid should not be null");
        assertEquals("1234567890domain.com", uid.getValue(), "Uid value is incorrect");
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
    @DisplayName("RFC 5545 Section 3.8.4.7: should reject empty UID")
    void should_rejectEmpty_when_emptyUidProvided() {
      String str = "UID:";
      Exception exception = assertThrows(Exception.class, () -> {
        new Uid(str, PARSE_STRICT);
      });
      assertTrue(exception.getMessage().contains("Missing UID"), "Expected exception due to empty UID");
    }
  }
}
