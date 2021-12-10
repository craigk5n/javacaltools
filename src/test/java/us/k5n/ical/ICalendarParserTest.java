package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test cases for ICalendarParser.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class ICalendarParserTest implements Constants {
  static ICalendarParser parser;
  static DataStore ds;

  @BeforeAll
  static public void setUp() {
    parser = new ICalendarParser(PARSE_STRICT);
    ds = parser.getDataStoreAt(0);
  }

  private void showErrors(List<ParseError> errors) {
    for (int i = 0; i < errors.size(); i++) {
      ParseError err = errors.get(i);
      System.err.println("Error #" + (i + 1) + " at line " + err.lineNo + ": " + err.error
          + "\n  Data: " + err.inputData);
    }
  }

  private void parseIcsFile(File f) {
    BufferedReader reader = null;
    if (f.exists()) {
      try {
        FileReader fr = new FileReader(f);
        reader = new BufferedReader(fr);
        parser.parse(reader);
        reader.close();
      } catch (IOException e) {
        System.err.println("Error opening " + f + ": " + e);
        fail("Error opening " + f + ": " + e);
      }
    } else {
      System.err.println("Could not find test file: " + f);
      fail("Could not find test file: " + f);
    }
  }

  @Test
  public void testBadDate1() {
    int numEventsBefore = ds.getAllEvents().size();
    parseIcsFile(new File("src/test/resources/BadDate1.ics"));
    List<ParseError> errors = parser.getAllErrors();
    assertTrue(errors.size() > 0);
    assertEquals(numEventsBefore, ds.getAllEvents().size());
  }

  @Test
  public void testSimpleDate1() {
    int numEventsBefore = ds.getAllEvents().size();
    parseIcsFile(new File("src/test/resources/SimpleDate1.ics"));
    List<ParseError> errors = parser.getAllErrors();
    List<Event> events = ds.getAllEvents();
    if (errors.size() > 0) {
      showErrors(errors);
      fail("Found errors in valid ICS file");
    }
    assertEquals(numEventsBefore + 1, events.size());
    Event e = (Event) events.get(0);
    assertNotNull(e.startDate);
    assertEquals(2007, e.startDate.year);
    assertEquals(4, e.startDate.month);
    assertEquals(1, e.startDate.day);
    assertTrue(e.startDate.isDateOnly());
  }


}
