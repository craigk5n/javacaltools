package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Duration.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class DurationTest implements Constants {

  // 1 hour test
  @Test
  public void test001() {
    String str = "DURATION:+PT1H";
    try {
      Duration x = new Duration(str, PARSE_STRICT);
      assertNotNull(x);
      assertEquals(3600, x.duration);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // 90 minute test
  public void test002() {
    String str = "DURATION:+PT1H30M";
    try {
      Duration x = new Duration(str, PARSE_STRICT);
      assertNotNull(x);
      assertEquals(5400, x.duration);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // 1hr, 30 min, 15 sec minute test
  @Test
  public void test003() {
    String str = "DURATION:+PT1H30M15S";
    try {
      Duration x = new Duration(str, PARSE_STRICT);
      assertNotNull(x);
      assertEquals(5415, x.duration);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // 1 day
  @Test
  public void test004() {
    String str = "DURATION:+P1D";
    try {
      Duration x = new Duration(str, PARSE_STRICT);
      assertNotNull(x);
      assertEquals(86400, x.duration);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // 1 day, 2 hours
  @Test
  public void test005() {
    String str = "DURATION:+P1DT2H";
    try {
      Duration x = new Duration(str, PARSE_STRICT);
      assertNotNull(x);
      assertEquals(93600, x.duration);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // 1 week, 1 day
  @Test
  public void test006() {
    String str = "DURATION:+P1W2D";
    try {
      Duration x = new Duration(str, PARSE_STRICT);
      assertNotNull(x);
      assertEquals(777600, x.duration);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // 15 days, 5 hours and 20 seconds
  // (example from RFC 2445 spec)
  @Test
  public void test007() {
    String str = "DURATION:P15DT5H0M20S";
    try {
      Duration x = new Duration(str, PARSE_STRICT);
      assertNotNull(x);
      assertEquals(1314020, x.duration);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

}
