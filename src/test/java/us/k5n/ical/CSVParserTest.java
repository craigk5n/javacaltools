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
 * These tests validate the parsing of CSV files containing event data.
 * The tests focus on different date formats and ensure that the parsed events
 * are correctly processed and stored.
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class CSVParserTest implements Constants {
	private CSVParser parser;
	private DataStore ds;

	@BeforeEach
	public void setUp() {
		parser = new CSVParser(PARSE_STRICT);
		ds = parser.getDataStoreAt(0);
	}

	private void showErrors(List<ParseError> errors) {
		for (int i = 0; i < errors.size(); i++) {
			ParseError err = errors.get(i);
			System.err.println("Error #" + (i + 1) + " at line " + err.lineNo
					+ ": " + err.error + "\n  Data: " + err.inputData);
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

	/**
	 * Test parsing of a CSV file with a simple date format.
	 * The test ensures that the event is correctly parsed and stored.
	 */
	@Test
	public void testParsingSimpleDate1() {
		File file = new File(getClass().getClassLoader().getResource("SimpleDate1.csv").getFile());
		parseCsvFile(file);

		List<ParseError> errors = parser.getAllErrors();
		List<Event> events = ds.getAllEvents();

		if (errors.size() > 0) {
			showErrors(errors);
			fail("Found errors in valid CSV file");
		}

		assertEquals(1, events.size(), "Did not load valid event");

		Event e = events.get(0);
		assertNotNull(e.startDate, "Null event start date");
		assertEquals(2007, e.startDate.year, "Wrong event year: " + e.startDate.year);
		assertEquals(4, e.startDate.month, "Wrong event month: " + e.startDate.month);
		assertEquals(1, e.startDate.day, "Wrong event day: " + e.startDate.day);
		assertTrue(e.startDate.isDateOnly(), "Did not set date-only correctly");
	}

	/**
	 * Test parsing of a CSV file with an ISO 8601 standard date format.
	 * The test ensures that the event is correctly parsed and stored.
	 */
	@Test
	public void testParsingISO8601Date() {
		File file = new File(getClass().getClassLoader().getResource("SimpleDate2.csv").getFile());
		parseCsvFile(file);

		List<ParseError> errors = parser.getAllErrors();
		List<Event> events = ds.getAllEvents();

		if (errors.size() > 0) {
			showErrors(errors);
			fail("Found errors in valid CSV file");
		}

		assertEquals(1, events.size(), "Did not load valid event");

		Event e = events.get(0);
		assertNotNull(e.startDate, "Null event start date");
		assertEquals(2007, e.startDate.year, "Wrong event year: " + e.startDate.year);
		assertEquals(4, e.startDate.month, "Wrong event month: " + e.startDate.month);
		assertEquals(1, e.startDate.day, "Wrong event day: " + e.startDate.day);
		assertTrue(e.startDate.isDateOnly(), "Did not set date-only correctly");
	}
}