package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Classification.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class ClassificationTest implements Constants {

  @Test
  public void testOne() {
    try {
      Classification c = new Classification("CLASS: PUBLIC");
      assertTrue(c.getClassification() == PUBLIC);
    } catch (Exception e) {
      fail("Failed: " + e.toString());
    }
    try {
      Classification c = new Classification("CLASS: public");
      assertTrue(c.getClassification() == PUBLIC);
    } catch (Exception e) {
      fail("Failed: " + e.toString());
    }
    try {
      Classification c = new Classification(PUBLIC);
      assertTrue(c.getClassification() == PUBLIC);
    } catch (Exception e) {
      fail("Failed: " + e.toString());
    }
  }

  @Test
  public void testTwo() {
    try {
      Classification c = new Classification("CLASS: PRIVATE");
      assertTrue(c.getClassification() == PRIVATE);
    } catch (Exception e) {
      fail("Failed: " + e.toString());
    }
    try {
      Classification c = new Classification("CLASS: private");
      assertTrue(c.getClassification() == PRIVATE);
    } catch (Exception e) {
      fail("Failed: " + e.toString());
    }
    try {
      Classification c = new Classification(PRIVATE);
      assertTrue(c.getClassification() == PRIVATE);
    } catch (Exception e) {
      fail("Failed: " + e.toString());
    }
  }

  @Test
  public void testThree() {
    try {
      Classification c = new Classification("CLASS: CONFIDENTIAL");
      assertTrue(c.getClassification() == CONFIDENTIAL);
    } catch (Exception e) {
      fail("Failed: " + e.toString());
    }
    try {
      Classification c = new Classification("CLASS: confidential");
      assertTrue(c.getClassification() == CONFIDENTIAL);
    } catch (Exception e) {
      fail("Failed: " + e.toString());
    }
    try {
      Classification c = new Classification(CONFIDENTIAL);
      assertTrue(c.getClassification() == CONFIDENTIAL);
    } catch (Exception e) {
      fail("Failed: " + e.toString());
    }
  }

}
