package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Uid.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class UidTest implements Constants {

  @Test
  public void testOne() {
    String str = "UID:SFSDFSDFSDF-SDFDFSDFSD@xxx.com";
    try {
      Uid x = new Uid(str, PARSE_STRICT);
      assertNotNull(x);
      assertTrue(x.value.equals("SFSDFSDFSDF-SDFDFSDFSD@xxx.com"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

}
