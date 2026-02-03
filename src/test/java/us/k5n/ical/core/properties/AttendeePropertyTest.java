package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Attendee;
import us.k5n.ical.Constants;
import us.k5n.ical.ParseException;

/**
 * RFC 5545 Section 3.8.4.1: ATTENDEE Property Tests
 *
 * Tests for the ATTENDEE property as defined in RFC 5545.
 * The ATTENDEE property defines an "Attendee" within a calendar component.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.4.1: ATTENDEE Property")
public class AttendeePropertyTest implements Constants {

  @Nested
  @DisplayName("Role Parsing Tests")
  class RoleParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should parse ROLE=CHAIR correctly")
    void should_parseRole_when_roleIsChair() {
      String attendeeStr = "ATTENDEE;ROLE=CHAIR;CN=Joe Smith:MAILTO:joe@xxx.com";
      try {
        Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
        assertEquals(3, a.role, "Attendee has wrong role"); // ROLE_CHAIR = 3
        assertNotNull(a.cn, "Null CN");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should parse all standard ROLE values")
    void should_parseAllRoles_when_validRolesProvided() {
      String[] roles = {"REQ-PARTICIPANT", "OPT-PARTICIPANT", "NON-PARTICIPANT", "CHAIR"};
      // ROLE_REQ_PARTICIPANT=0, ROLE_OPT_PARTICIPANT=1, ROLE_NON_PARTICIPANT=2, ROLE_CHAIR=3
      int[] expectedRoles = {0, 1, 2, 3};

      for (int i = 0; i < roles.length; i++) {
        String attendeeStr = "ATTENDEE;ROLE=" + roles[i] + ":MAILTO:test@example.com";
        try {
          Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
          assertEquals(expectedRoles[i], a.role,
              "Role " + roles[i] + " should be parsed correctly");
        } catch (Exception e) {
          fail("Role " + roles[i] + " parsing failed: " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should reject invalid ROLE value in strict mode")
    void should_throwException_when_roleIsInvalid() {
      String attendeeStr = "ATTENDEE;ROLE=INVALID:MAILTO:test@example.com";
      try {
        Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
        fail("Should throw exception for invalid ROLE");
      } catch (ParseException e) {
        // Expected
      } catch (Exception e) {
        fail("Wrong exception type: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Common Name (CN) Parsing Tests")
  class CommonNameParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should parse CN parameter correctly")
    void should_parseCN_when_cnIsProvided() {
      String attendeeStr = "ATTENDEE;CN=John Doe:MAILTO:john@example.com";
      try {
        Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
        assertEquals("John Doe", a.cn, "CN should be parsed correctly");
      } catch (Exception e) {
        fail("CN parsing failed: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Participation Status (PARTSTAT) Tests")
  class ParticipationStatusTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should parse all standard PARTSTAT values")
    void should_parseAllPartstatValues_when_validStatusProvided() {
      String[] statuses = {
        "NEEDS-ACTION", "ACCEPTED", "DECLINED", "TENTATIVE",
        "DELEGATED", "COMPLETED", "IN-PROCESS"
      };
      // STATUS_NEEDS_ACTION=0, STATUS_ACCEPTED=1, STATUS_DECLINED=2, STATUS_TENTATIVE=3,
      // STATUS_DELEGATED=4, STATUS_COMPLETED=5, STATUS_IN_PROCESS=6
      int[] expectedStatuses = {0, 1, 2, 3, 4, 5, 6};

      for (int i = 0; i < statuses.length; i++) {
        String attendeeStr = "ATTENDEE;PARTSTAT=" + statuses[i] + ":MAILTO:test@example.com";
        try {
          Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
          assertEquals(expectedStatuses[i], a.status,
              "Status " + statuses[i] + " should be parsed correctly");
        } catch (Exception e) {
          fail("Status " + statuses[i] + " parsing failed: " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should reject invalid PARTSTAT value in strict mode")
    void should_throwException_when_partstatIsInvalid() {
      String attendeeStr = "ATTENDEE;PARTSTAT=INVALID:MAILTO:test@example.com";
      try {
        Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
        fail("Should throw exception for invalid PARTSTAT");
      } catch (ParseException e) {
        // Expected
      } catch (Exception e) {
        fail("Wrong exception type: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("RSVP Parameter Tests")
  class RsvpTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should parse RSVP=TRUE correctly")
    void should_setRsvpTrue_when_rsvpIsTrue() {
      String attendeeStr = "ATTENDEE;RSVP=TRUE:MAILTO:test@example.com";
      try {
        Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
        assertTrue(a.rsvp, "RSVP=TRUE should set rsvp to true");
      } catch (Exception e) {
        fail("RSVP parsing failed: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should parse RSVP=FALSE correctly")
    void should_setRsvpFalse_when_rsvpIsFalse() {
      String attendeeStr = "ATTENDEE;RSVP=FALSE:MAILTO:test@example.com";
      try {
        Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
        assertFalse(a.rsvp, "RSVP=FALSE should set rsvp to false");
      } catch (Exception e) {
        fail("RSVP parsing failed: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should reject invalid RSVP value in strict mode")
    void should_throwException_when_rsvpIsInvalid() {
      String attendeeStr = "ATTENDEE;RSVP=INVALID:MAILTO:test@example.com";
      try {
        Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
        fail("Should throw exception for invalid RSVP");
      } catch (ParseException e) {
        // Expected
      } catch (Exception e) {
        fail("Wrong exception type: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Calendar User Type (CUTYPE) Tests")
  class CalendarUserTypeTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should parse all standard CUTYPE values")
    void should_parseAllCutypeValues_when_validTypeProvided() {
      String[] cutypes = {"INDIVIDUAL", "GROUP", "RESOURCE", "ROOM", "UNKNOWN"};
      // TYPE_INDIVIDUAL=1, TYPE_GROUP=2, TYPE_RESOURCE=3, TYPE_ROOM=4, TYPE_UNKNOWN=5
      int[] expectedTypes = {1, 2, 3, 4, 5};

      for (int i = 0; i < cutypes.length; i++) {
        String attendeeStr = "ATTENDEE;CUTYPE=" + cutypes[i] + ":MAILTO:test@example.com";
        try {
          Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
          assertEquals(expectedTypes[i], a.type,
              "CUTYPE " + cutypes[i] + " should be parsed correctly");
        } catch (Exception e) {
          fail("CUTYPE " + cutypes[i] + " parsing failed: " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should reject invalid CUTYPE value in strict mode")
    void should_throwException_when_cutypeIsInvalid() {
      String attendeeStr = "ATTENDEE;CUTYPE=INVALID:MAILTO:test@example.com";
      try {
        Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
        fail("Should throw exception for invalid CUTYPE");
      } catch (ParseException e) {
        // Expected
      } catch (Exception e) {
        fail("Wrong exception type: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("CAL-ADDRESS Validation Tests")
  class CalAddressValidationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.4.1: should reject malformed CAL-ADDRESS in strict mode")
    void should_throwException_when_calAddressIsMalformed() {
      // Test with truly invalid CAL-ADDRESS (malformed URI)
      String attendeeStr = "ATTENDEE;CN=Test User:http://invalid uri with spaces";
      try {
        Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
        fail("Should throw exception for invalid CAL-ADDRESS");
      } catch (ParseException e) {
        assertTrue(e.getMessage().contains("Invalid CAL-ADDRESS"),
            "Should mention CAL-ADDRESS validation");
      } catch (Exception e) {
        fail("Wrong exception type: " + e.getMessage());
      }
    }
  }
}
