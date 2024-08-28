package us.k5n.ical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Attendee.
 * 
 * @author Craig Knudsen, craig@k5n.us
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
}