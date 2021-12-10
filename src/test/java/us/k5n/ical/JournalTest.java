package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Journal.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class JournalTest implements Constants {
  ICalendarParser parser;
  DataStore ds;
  String header =
      "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//FOO//BAR//EN\n" + "METHOD:PUBLISH\nBEGIN:VJOURNAL\n";
  String trailer = "END:VJOURNAL\nEND:VCALENDAR\n";

  @BeforeEach
  public void setUp() {
    parser = new ICalendarParser(PARSE_STRICT);
    ds = parser.getDataStoreAt(0);
  }

  @Test
  public void testOne() {
    String x = header + "SUMMARY:Test Journal Entry\nDESCRIPTION:This is the description\n"
        + "DTSTAMP:20060501\nUID:journaltest1@k5n.us\n" + trailer;
    StringReader reader = new StringReader(x);
    try {
      boolean ret = parser.parse(reader);
      assertTrue(ret);
      List<Journal> journals = ds.getAllJournals();
      assertTrue(journals.size() > 0);
      Journal j = (Journal) journals.get(0);
      assertTrue(j.isValid());
      Date d = j.dtstamp;
      assertTrue(d.isDateOnly());
      assertEquals(2006, d.year);
      assertEquals(5, d.month);
      assertEquals(1, d.day);
      assertNotNull(j.summary);
      assertTrue(j.summary.value.equals("Test Journal Entry"));
      assertNotNull(j.description);
      assertTrue(j.description.value.equals("This is the description"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

}
