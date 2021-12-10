package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Sequence.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class SequenceTest implements Constants {


  @Test
  public void testOne() {
    String str = "SEQUENCE:1";
    try {
      Sequence x = new Sequence(str, PARSE_STRICT);
      assertNotNull(x);
      assertEquals(1, x.num);
      x.increment();
      assertEquals(2, x.num);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }


}
