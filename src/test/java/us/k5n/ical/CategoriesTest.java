package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Categories.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class CategoriesTest implements Constants {

  public void setUp() {}

  @Test
  public void testOne() {
    String str = "CATEGORIES:Home,Personal";
    try {
      Categories c = new Categories(str, PARSE_STRICT);
      System.out.println("ICAL: " + c.toICalendar());
      assertNotNull(c);
      // TODO: add code for getting categories once it
      // is added to Categories.java.
    } catch (ParseException e) {
      fail("ParseException: " + e.getMessage());
    }
  }


}
