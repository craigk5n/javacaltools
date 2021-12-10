package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Calendar;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Utils.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class UtilsTest implements Constants {

  @Test
  public void testDayOfWeek() {
    int[] wdays = {Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
    Calendar c = Calendar.getInstance();
    c.setLenient(true);
    // test date ranges of 1700 through 2200
    c.set(Calendar.YEAR, 1700);
    while (true) {
      int y = c.get(Calendar.YEAR);
      int m = c.get(Calendar.MONTH) + 1;
      int d = c.get(Calendar.DAY_OF_MONTH);
      int javaWeekday = c.get(Calendar.DAY_OF_WEEK);
      int wday = Utils.getDayOfWeek(y, m, d);
      assertTrue(javaWeekday == wdays[wday]);
      // increment date
      c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
      if (y >= 2201)
        break;
    }
  }

  @Test
  public void testFileExtensionToMime() {
    String mime = Utils.getMimeTypeForExtension("picture.jpg");
    assertTrue(mime.equals("image/jpeg"));
    mime = Utils.getMimeTypeForExtension("picture.jpeg");
    assertTrue(mime.equals("image/jpeg"));
    mime = Utils.getMimeTypeForExtension("file.doc");
    assertTrue(mime.equals("application/msword"));
    mime = Utils.getMimeTypeForExtension("file.xls");
    assertTrue(mime.equals("application/excel"));
    mime = Utils.getMimeTypeForExtension("file.html");
    assertTrue(mime.equals("text/html"));
  }

}
