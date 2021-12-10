package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Summary.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class SummaryTest implements Constants {


  @Test
  public void testOne() {
    String str = "SUMMARY;LANGUAGE=EN:This is\\na summary.";
    try {
      Description x = new Description(str, PARSE_STRICT);
      assertNotNull(x);
      assertNotNull(x.language);
      assertNotNull(x.value);
      assertTrue(x.value.startsWith("This is"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

}
