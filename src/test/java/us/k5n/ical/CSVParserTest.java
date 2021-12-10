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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for CSVParser.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class CSVParserTest implements Constants {
  CSVParser parser;
  DataStore ds;

  @BeforeEach
  public void setUp() {
    parser = new CSVParser(PARSE_STRICT);
    ds = parser.getDataStoreAt(0);
  }

  private void showErrors(List<ParseError> errors) {
    for (int i = 0; i < errors.size(); i++) {
      ParseError err = errors.get(i);
      System.err.println("Error #" + (i + 1) + " at line " + err.lineNo + ": " + err.error
          + "\n  Data: " + err.inputData);
    }
  }


  private void parseCsvFile(File f) {
    BufferedReader reader = null;
    if (f.exists()) {
      try {
        reader = new BufferedReader(new FileReader(f));
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
  public void testSimpleDate1() {
    parseCsvFile(new File("src/test/resources/SimpleDate1.csv"));
    List<ParseError> errors = parser.getAllErrors();
    List<Event> events = ds.getAllEvents();
    if (errors.size() > 0) {
      showErrors(errors);
      fail("Found errors in valid CSV file");
    }
    assertEquals(1, events.size());
    Event e = events.get(0);
    assertNotNull(e.startDate);
    assertEquals(2007, e.startDate.year);
    assertEquals(4, e.startDate.month);
    assertEquals(1, e.startDate.day);
    assertTrue(e.startDate.isDateOnly());
  }

  // Test parsing of ISO 8601 standard dates.
  @Test
  public void testSimpleDate2() {
    parseCsvFile(new File("src/test/resources/SimpleDate2.csv"));
    List<ParseError> errors = parser.getAllErrors();
    List<Event> events = ds.getAllEvents();
    if (errors.size() > 0) {
      showErrors(errors);
      fail("Found errors in valid CSV file");
    }
    assertEquals(1, events.size());
    Event e = events.get(0);
    assertNotNull(e.startDate);
    assertEquals(2007, e.startDate.year);
    assertEquals(4, e.startDate.month);
    assertEquals(1, e.startDate.day);
    assertTrue(e.startDate.isDateOnly());
  }


}
