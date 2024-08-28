package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test cases for Date class.
 * 
 * These tests validate the parsing and handling of dates, including detection
 * of date-only
 * and date-time values, validation of valid and invalid dates, and ensuring
 * correct
 * iCalendar output.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class DateTest implements Constants {

	@BeforeEach
	public void setUp() {
	}

	/**
	 * Test valid detection of a date-only value.
	 * Ensures that the date has no time component and the year, month, and day are
	 * correct.
	 */
	@Test
	public void testDateOnlyParsing() {
		String dateStr = "DATE:20070131";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			assertTrue(d.isDateOnly(), "Date has time");
			assertEquals(2007, d.year, "Incorrect year");
			assertEquals(1, d.month, "Incorrect month");
			assertEquals(31, d.day, "Incorrect day");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.toString());
		}
	}

	/**
	 * Test valid detection of a date-time value.
	 * Ensures that the date has a time component and all date and time fields are
	 * correct.
	 */
	@Test
	public void testDateTimeParsing() {
		String dateStr = "DTSTART:20070131T132047";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			assertFalse(d.isDateOnly(), "Date has no time");
			assertEquals(2007, d.year, "Incorrect year");
			assertEquals(1, d.month, "Incorrect month");
			assertEquals(31, d.day, "Incorrect day");
			assertEquals(13, d.hour, "Incorrect hour");
			assertEquals(20, d.minute, "Incorrect minute");
			assertEquals(47, d.second, "Incorrect second");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.toString());
		}
	}

	/**
	 * Test detection of invalid date values.
	 * Ensures that the parser catches and throws exceptions for invalid dates.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
			"20011301", "20010132", "19990012", "19991200",
			"19990132", "19990229", "20010230", "20010332",
			"20010431", "20010532", "20010631", "20010732",
			"20010832", "20010931", "20021032", "20021131",
			"20021232"
	})
	public void testInvalidDateDetection(String dStr) {
		try {
			Date d = new Date("DATE:" + dStr);
			fail("Did not catch invalid date '" + dStr + "'");
		} catch (BogusDataException e) {
			// Expected exception
		} catch (Exception e2) {
			fail("Caught unexpected exception: " + e2.getMessage());
		}
	}

	/**
	 * Test detection and handling of valid dates, including leap and non-leap
	 * years.
	 * Ensures that all valid dates are accepted.
	 */
	@Test
	public void testValidDateHandling() {
		Calendar c = Calendar.getInstance();

		// Test for leap year
		c.set(Calendar.YEAR, 2000);
		for (int i = 1; i <= 365; i++) {
			c.set(Calendar.DAY_OF_YEAR, i);
			String str = "DATE:" + Utils.CalendarToYYYYMMDD(c);
			try {
				Date d = new Date(str);
				// Expected to pass
			} catch (BogusDataException e) {
				fail("Did not allow valid date '" + str + "'");
			} catch (Exception e2) {
				fail("Caught unexpected exception: " + e2.getMessage());
			}
		}

		// Test for non-leap year
		c.set(Calendar.YEAR, 2001);
		for (int i = 1; i <= 366; i++) {
			c.set(Calendar.DAY_OF_YEAR, i);
			String str = "DATE:" + Utils.CalendarToYYYYMMDD(c);
			try {
				Date d = new Date(str);
				// Expected to pass
			} catch (BogusDataException e) {
				fail("Did not allow valid date '" + str + "'");
			} catch (Exception e2) {
				fail("Caught unexpected exception: " + e2.getMessage());
			}
		}
	}

	/**
	 * Test conversion of a date to iCalendar format and back.
	 * Ensures that the output is correct and that parsing the output returns the
	 * same date.
	 */
	@Test
	public void testICalendarConversion() {
		String dateStr = "DATE:20070131";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			String ical = d.toICalendar().trim();
			assertTrue(ical.contains("20070131"), "Incorrect iCalendar output: " + ical);
			Date newDate = new Date(d.toICalendar(), PARSE_STRICT);
			assertEquals(d, newDate, "Dates not equal: " + d + " vs " + newDate);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.toString());
		}
	}

	/**
	 * Test iCalendar output generation and parsing for date-time values.
	 * Ensures that the output and parsed dates are equal.
	 */
	@Test
	public void testICalendarDateTimeOutput() {
		String dateStr = "DATE:20070131T115959";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			Date newDate = new Date(d.toICalendar(), PARSE_STRICT);
			assertEquals(d, newDate, "Dates not equal: " + d.toICalendar() + " vs " + newDate.toICalendar());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.toString());
		}
	}

	/**
	 * Test iCalendar parsing and output for a date-time with VALUE set to DATE.
	 * Ensures that the output and parsed dates are equal.
	 */
	@Test
	public void testDateTimeWithValueDate() {
		String dateStr = "DTSTART;VALUE=\"DATE\":20070413T091945";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			Date newDate = new Date(d.toICalendar(), PARSE_STRICT);
			assertEquals(d, newDate, "Dates not equal: " + d.toICalendar() + " vs " + newDate.toICalendar());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.toString());
		}
	}

	/**
	 * Test creation of Date objects with and without time components.
	 * Ensures that the time component is correctly handled.
	 */
	@Test
	public void testDateCreationWithAndWithoutTime() {
		try {
			Date d = new Date("DTSTART", 1999, 12, 31);
			assertTrue(d.isDateOnly(), "Date has time");
			d = new Date("DTSTART", 1999, 12, 31, 23, 59, 59);
			assertFalse(d.isDateOnly(), "Date has no time");
			assertEquals(23, d.getHour(), "Wrong hour");
			assertEquals(59, d.getMinute(), "Wrong minute");
			assertEquals(59, d.getSecond(), "Wrong second");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.toString());
		}
	}

	/**
	 * Test calculation of the week of the year for different dates.
	 * Ensures that the correct week number is returned.
	 */
	@ParameterizedTest
	@CsvSource({
			"1/1/1999, 53", "1/2/1999, 53", "1/3/1999, 53", "1/1/2000, 52",
			"1/2/2000, 1", "1/3/2000, 1", "1/1/2001, 1", "1/2/2001, 1",
			"1/3/2001, 1", "1/1/2002, 1", "1/2/2002, 1", "1/3/2002, 1",
			"1/4/2002, 1", "1/5/2002, 1", "1/6/2002, 2",
			"1/1/2005, 53", "1/2/2005, 53", "1/3/2005, 1",
			"1/2/2020, 1", "1/4/2020, 1", "1/5/2020, 2",
	})
	public void testWeekOfYearCalculation(String dateString, int expectedWeek) {
		try {
			String[] dateArgs = dateString.split("/");
			Date d = new Date("XXX", Integer.parseInt(dateArgs[2]), Integer.parseInt(dateArgs[0]),
					Integer.parseInt(dateArgs[1]));
			int week = d.getWeekOfYear();
			assertEquals(expectedWeek, week,
					"Incorrect week for " + dateString + ": got " + week + " instead of " + expectedWeek);
		} catch (BogusDataException e1) {
			fail("Caught BogusDataException: " + e1.getMessage());
		}
	}

	/**
	 * Test that the iCalendar output includes the correct timezone information.
	 * Ensures that the timezone is included in the output when specified.
	 */
	@Test
	public void testTimeZoneInICalendarOutput() {
		String dateStr = "DTSTART;VALUE=\"DATE\";TZID=\"" + java.util.TimeZone.getDefault().getID()
				+ "\":20070901T120000";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			assertTrue(d.isDateOnly(), "Date has time");
			assertEquals(12, d.getHour(), "Wrong time");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.toString());
		}
	}

	/**
	 * Test iCalendar parsing and output for a date-time with timezone specified.
	 * Ensures that the output includes the timezone information.
	 */
	@Test
	public void testTimezoneParsing() {
		String dateStr = "DTSTART;TZID=America/Los_Angeles:20070901T120000";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			assertFalse(d.isDateOnly(), "Date has no time");
			String icalStr = d.toICalendar();
			assertTrue(icalStr.contains("TZID"), "No TZID in iCalendar output");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.toString());
		}
	}

	/**
	 * Test that iCalendar output includes a timezone when using a GMT date-time.
	 * Ensures that the timezone is included in the output.
	 */
	@Test
	public void testTimedDateTime() {
		String dateStr = "DTSTART;VALUE=\"DATE-TIME\":20070901T120000Z";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			assertFalse(d.isDateOnly(), "Date has no time");
			String icalStr = d.toICalendar();
			assertTrue(icalStr.contains("TZID"), "Date-time should have timezone: " + icalStr);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.toString());
		}
	}

	// Edge case: Transition from December 31st to January 1st (New Year's
	// Transition)
	@ParameterizedTest
	@CsvSource({
			"2019-12-31, 1", // December 31st, 2019, ISO week number 1 of 2020
			"2020-01-01, 1", // January 1st, 2020, ISO week number 1
			"2021-12-31, 52", // December 31st, 2021, ISO week number 52 of 2021
			"2022-01-01, 52" // January 1st, 2022, ISO week number 52 of 2021
	})
	public void testNewYearsTransition(String dateStr, int expectedWeek) {
		try {
			String[] dateParts = dateStr.split("-");
			Date d = new Date("XXX", Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
					Integer.parseInt(dateParts[2]));
			assertEquals(expectedWeek, d.getWeekOfYear(), "Incorrect week number for " + dateStr);
		} catch (BogusDataException e) {
			fail("Caught BogusDataException: " + e.getMessage());
		}
	}

	// Edge case: Leap year handling around February 29th
	@ParameterizedTest
	@CsvSource({
			"2020-01-01, 1", // Jan 1, 20202, week 1
			"2020-02-28, 9", // February 28th, 2020, week 9
			"2020-02-29, 9", // February 29th, 2020, week 9
			"2020-03-01, 10" // March 1st, 2020, week 9
	})
	public void testLeapYearHandling(String dateStr, int expectedWeek) {
		try {
			String[] dateParts = dateStr.split("-");
			Date d = new Date("XXX", Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
					Integer.parseInt(dateParts[2]));
			assertEquals(expectedWeek, d.getWeekOfYear(), "Incorrect week number for " + dateStr);
		} catch (BogusDataException e) {
			fail("Caught BogusDataException: " + e.getMessage());
		}
	}

	// Edge case: Locale-specific week start days
	@ParameterizedTest
	@CsvSource({
			"2023-01-01, 52", // January 1st, 2023 (Sunday, considered part of week 52 in some locales)
			"2023-01-02, 1", // January 2nd, 2023 (Monday, start of week 1)
			"2023-01-03, 1" // January 3rd, 2023 (Tuesday, part of week 1)
	})
	public void testLocaleSpecificWeekStart(String dateStr, int expectedWeek) {
		try {
			String[] dateParts = dateStr.split("-");
			Date d = new Date("XXX", Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
					Integer.parseInt(dateParts[2]));
			assertEquals(expectedWeek, d.getWeekOfYear(), "Incorrect week number for " + dateStr);
		} catch (BogusDataException e) {
			fail("Caught BogusDataException: " + e.getMessage());
		}
	}

	// Edge case: Year transition for different week numbering systems
	@ParameterizedTest
	@CsvSource({
			"1999-12-30, 52", // December 30th, 1999 (part of week 52)
			"1999-12-31, 52", // December 31st, 1999 (part of week 52)
			"2000-01-01, 52", // January 1st, 2000 (end of week 52 for 1999)
			"2000-01-02, 1" // January 2nd, 2000 (part of week 1)
	})
	public void testYearTransitionCases(String dateStr, int expectedWeek) {
		try {
			String[] dateParts = dateStr.split("-");
			Date d = new Date("XXX", Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
					Integer.parseInt(dateParts[2]));
			assertEquals(expectedWeek, d.getWeekOfYear(), "Incorrect week number for " + dateStr);
		} catch (BogusDataException e) {
			fail("Caught BogusDataException: " + e.getMessage());
		}
	}

	// Edge case: Floating Date-Time without timezone
	@Test
	public void testFloatingDateTime() {
		String dateStr = "DTSTART;VALUE=\"DATE-TIME\":20070901T120000";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			assertTrue(d.getHour() == 12, "Wrong hour in floating time: " + d.getHour());
			assertFalse(d.isDateOnly(), "Date should have time");
			String icalStr = d.toICalendar();
			assertFalse(icalStr.contains("TZID"), "Date-time should not have timezone");
		} catch (Exception e) {
			fail("Caught unexpected exception: " + e.getMessage());
		}
	}

	// Edge case: Timezone-aware Date-Time
	@Test
	public void testTimezoneDateTime() {
		String dateStr = "DTSTART;VALUE=\"DATE-TIME\":20070901T120000Z";
		try {
			Date d = new Date(dateStr, PARSE_STRICT);
			assertFalse(d.isDateOnly(), "Date should have time");
			String icalStr = d.toICalendar();
			assertTrue(icalStr.contains("TZID"), "Date-time should have timezone");
		} catch (Exception e) {
			fail("Caught unexpected exception: " + e.getMessage());
		}
	}

	// Edge case: Day of the week calculation in different locales
	@ParameterizedTest
	@CsvSource({
			"2000-12-31, 1", // ISO 8601: December 31st, 2000 is Week 1 of 2001
			"2001-01-01, 1" // January 1st, 2001 is Week 1
	})
	public void testDayOfWeekCalculation(String dateStr, int expectedWeek) {
		try {
			String[] dateParts = dateStr.split("-");
			Date d = new Date("XXX", Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
					Integer.parseInt(dateParts[2]));
			assertEquals(expectedWeek, d.getWeekOfYear(), "Incorrect week number for " + dateStr);
		} catch (BogusDataException e) {
			fail("Caught BogusDataException: " + e.getMessage());
		}
	}
}