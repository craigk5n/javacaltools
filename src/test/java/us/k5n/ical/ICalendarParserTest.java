package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for ICalendarParser.
 * 
 * @author Craig Knudsen
 */
public class ICalendarParserTest implements Constants {
	private ICalendarParser parser;
	private DataStore ds;

	@BeforeEach
	public void setUp() {
		parser = new ICalendarParser(PARSE_STRICT);
		ds = parser.getDataStoreAt(0);
	}

	private void showErrors(List<ParseError> errors) {
		for (int i = 0; i < errors.size(); i++) {
			ParseError err = errors.get(i);
			System.err.println("Error #" + (i + 1) + " at line " + err.lineNo
					+ ": " + err.error + "\n  Data: " + err.inputData);
		}
	}

	private void parseIcsFile(File file) {
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				parser.parse(reader);
			} catch (IOException e) {
				System.err.println("Error opening " + file + ": " + e);
				fail("Error opening " + file + ": " + e);
			}
		} else {
			System.err.println("Could not find test file: " + file);
			fail("Could not find test file: " + file);
		}
	}

	@Test
	public void testBadDateParsing() {
		File file = new File(getClass().getClassLoader().getResource("BadDate1.ics").getFile());
		parseIcsFile(file);
		List<ParseError> errors = parser.getAllErrors();
		assertTrue(errors.size() > 0, "Expected to find bad date errors");
		List<Event> events = ds.getAllEvents();
		assertTrue(events.isEmpty(), "No events should be loaded with a bad date");
	}

	@Test
	public void testValidSimpleDateParsing() {
		File file = new File(getClass().getClassLoader().getResource("SimpleDate1.ics").getFile());
		parseIcsFile(file);
		List<ParseError> errors = parser.getAllErrors();
		List<Event> events = ds.getAllEvents();
		if (!errors.isEmpty()) {
			showErrors(errors);
			fail("Found errors in a valid ICS file");
		}
		assertTrue(events.size() == 1, "Expected to load one valid event");
		Event event = events.get(0);
		assertNotNull(event.startDate, "Event start date should not be null");
		assertTrue(event.startDate.year == 2007, "Event year should be 2007");
		assertTrue(event.startDate.month == 4, "Event month should be April");
		assertTrue(event.startDate.day == 1, "Event day should be 1");
		assertTrue(event.startDate.isDateOnly(), "Date-only flag should be set");
	}

	@Test
	public void testEmptyFileHandling() {
		File emptyFile = new File(getClass().getClassLoader().getResource("EmptyFile.ics").getFile());
		parseIcsFile(emptyFile);
		List<Event> events = ds.getAllEvents();
		assertTrue(events.isEmpty(), "No events should be loaded from an empty file");
	}

	@Test
	public void testMethodPropertyParsing() {
		String icalStr = "BEGIN:VCALENDAR\n" +
			"METHOD:REQUEST\n" +
			"VERSION:2.0\n" +
			"BEGIN:VEVENT\n" +
			"UID:test@example.com\n" +
			"SUMMARY:Test Event\n" +
			"DTSTART:20230101T100000Z\n" +
			"END:VEVENT\n" +
			"END:VCALENDAR";

		try {
			parser.parse(new StringReader(icalStr));
			Property method = ((DefaultDataStore) ds).getMethod();
			assertNotNull(method, "METHOD property should be parsed");
			assertEquals("REQUEST", method.value, "METHOD value should be REQUEST");
		} catch (Exception e) {
			fail("Parsing should not fail: " + e.getMessage());
		}
	}
}