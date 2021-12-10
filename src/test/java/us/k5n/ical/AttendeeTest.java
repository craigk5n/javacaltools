package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Attendee.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class AttendeeTest implements Constants {

  public void setUp() {}

  @Test
  public void testOne() {
    String attendeeStr = "ATTENDEE;ROLE=CHAIR;CN=Joe Smith:MAILTO:joe@xxx.com";
    try {
      Attendee a = new Attendee(attendeeStr, PARSE_STRICT);
      assertTrue(a.role == Attendee.ROLE_CHAIR);
      assertNotNull("Null CN", a.cn);
      // assertNotNull ( "Null name", a.name );
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }



}
