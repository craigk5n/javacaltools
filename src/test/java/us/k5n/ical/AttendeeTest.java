package us.k5n.ical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Attendee.
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class AttendeeTest implements Constants {

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testOne() {
        String attendeeStr = "ATTENDEE;ROLE=CHAIR;CN=Joe Smith:MAILTO:joe@xxx.com";
        try {
            Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
            assertTrue(a.role == Attendee.ROLE_CHAIR, "Attendee has wrong role");
            assertNotNull(a.cn, "Null CN");
            // assertNotNull ( "Null name", a.name );
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed: " + e.toString());
        }
    }

    @Test
    public void testCommonName() {
        String attendeeStr = "ATTENDEE;CN=John Doe:MAILTO:john@example.com";
        try {
            Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
            assertEquals("John Doe", a.cn, "CN should be parsed correctly");
        } catch (Exception e) {
            fail("CN parsing failed: " + e.getMessage());
        }
    }

    @Test
    public void testRoles() {
        String[] roles = {"REQ-PARTICIPANT", "OPT-PARTICIPANT", "NON-PARTICIPANT", "CHAIR"};
        int[] expectedRoles = {Attendee.ROLE_REQ_PARTICIPANT, Attendee.ROLE_OPT_PARTICIPANT,
                              Attendee.ROLE_NON_PARTICIPANT, Attendee.ROLE_CHAIR};

        for (int i = 0; i < roles.length; i++) {
            String attendeeStr = "ATTENDEE;ROLE=" + roles[i] + ":MAILTO:test@example.com";
            try {
                Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
                assertEquals(expectedRoles[i], a.role, "Role " + roles[i] + " should be parsed correctly");
            } catch (Exception e) {
                fail("Role " + roles[i] + " parsing failed: " + e.getMessage());
            }
        }
    }

    @Test
    public void testParticipationStatus() {
        String[] statuses = {"NEEDS-ACTION", "ACCEPTED", "DECLINED", "TENTATIVE", "DELEGATED", "COMPLETED", "IN-PROCESS"};
        int[] expectedStatuses = {Attendee.STATUS_NEEDS_ACTION, Attendee.STATUS_ACCEPTED,
                                 Attendee.STATUS_DECLINED, Attendee.STATUS_TENTATIVE,
                                 Attendee.STATUS_DELEGATED, Attendee.STATUS_COMPLETED,
                                 Attendee.STATUS_IN_PROCESS};

        for (int i = 0; i < statuses.length; i++) {
            String attendeeStr = "ATTENDEE;PARTSTAT=" + statuses[i] + ":MAILTO:test@example.com";
            try {
                Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
                assertEquals(expectedStatuses[i], a.status, "Status " + statuses[i] + " should be parsed correctly");
            } catch (Exception e) {
                fail("Status " + statuses[i] + " parsing failed: " + e.getMessage());
            }
        }
    }

    @Test
    public void testRsvp() {
        String attendeeStrTrue = "ATTENDEE;RSVP=TRUE:MAILTO:test@example.com";
        String attendeeStrFalse = "ATTENDEE;RSVP=FALSE:MAILTO:test@example.com";

        try {
            Attendee a1 = new Attendee(attendeeStrTrue, PARSE_STRICT);
            assertTrue(a1.rsvp, "RSVP=TRUE should set rsvp to true");

            Attendee a2 = new Attendee(attendeeStrFalse, PARSE_STRICT);
            assertFalse(a2.rsvp, "RSVP=FALSE should set rsvp to false");
        } catch (Exception e) {
            fail("RSVP parsing failed: " + e.getMessage());
        }
    }

    @Test
    public void testCalendarUserType() {
        String[] cutypes = {"INDIVIDUAL", "GROUP", "RESOURCE", "ROOM", "UNKNOWN"};
        int[] expectedTypes = {Attendee.TYPE_INDIVIDUAL, Attendee.TYPE_GROUP,
                              Attendee.TYPE_RESOURCE, Attendee.TYPE_ROOM,
                              Attendee.TYPE_UNKNOWN};

        for (int i = 0; i < cutypes.length; i++) {
            String attendeeStr = "ATTENDEE;CUTYPE=" + cutypes[i] + ":MAILTO:test@example.com";
            try {
                Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
                assertEquals(expectedTypes[i], a.type, "CUTYPE " + cutypes[i] + " should be parsed correctly");
            } catch (Exception e) {
                fail("CUTYPE " + cutypes[i] + " parsing failed: " + e.getMessage());
            }
        }
    }

    @Test
    public void testInvalidRole() {
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

    @Test
    public void testInvalidParticipationStatus() {
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

    @Test
    public void testInvalidCalendarUserType() {
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

    @Test
    public void testInvalidRsvp() {
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



    @Test
    public void testInvalidCalAddress() {
        // Test with truly invalid CAL-ADDRESS (malformed URI)
        String attendeeStr = "ATTENDEE;CN=Test User:http://invalid uri with spaces";
        try {
            Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
            fail("Should throw exception for invalid CAL-ADDRESS");
        } catch (ParseException e) {
            assertTrue(e.getMessage().contains("Invalid CAL-ADDRESS"), "Should mention CAL-ADDRESS validation");
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getMessage());
        }
    }
}