package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.ical.iter.RecurrenceIterator;
import com.google.ical.iter.RecurrenceIteratorFactory;
import com.google.ical.values.DateTimeValueImpl;
import com.google.ical.values.DateValueImpl;
import com.google.ical.values.Frequency;

public class RruleTest implements Constants {

	@BeforeEach
	public void setUp() {
		// Setup code can be placed here if necessary
	}

	@Test
	public void testSimpleRruleParsing() {
		String str = "RRULE:FREQ=MONTHLY;BYMONTH=10;BYDAY=2MO";
		try {
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testYearlyRruleWithMonthlyOccurrences() {
		String[] expectedResults = { "19970310", "19990110", "19990210", "19990310", "20010110", "20010210", "20010310",
				"20030110", "20030210", "20030310" };
		String str = "RRULE:FREQ=YEARLY;INTERVAL=2;COUNT=10;BYMONTH=1,2,3";
		try {
			TimeZone tz = TimeZone.getTimeZone("UTC"); // Explicitly set to UTC
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970310T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			// This will generate the repeating event dates and does not include the
			// original event date.
			// So it should be 9 dates.
			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			assertEquals(9, dates.size());
			for (int i = 0; i < dates.size(); i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i + 1], ymd);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testYearlyRruleWithYearDayOccurrences() {
		String[] expectedResults = { "19970101", "19970410", "19970719", "20000101", "20000409", "20000718", "20030101",
				"20030410", "20030719", "20060101" };
		String str = "RRULE:FREQ=YEARLY;INTERVAL=3;COUNT=10;BYYEARDAY=1,100,200";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970101T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size(); i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testYearlyRruleWithNthDayOfWeekOccurrences() {
		String[] expectedResults = { "19970519", "19980518", "19990517" };
		String str = "RRULE:FREQ=YEARLY;BYDAY=20MO";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970519T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testYearlyRruleWithDayOfWeekOccurrences() {
		String[] expectedResults = { "19970313", "19970320", "19970327", "19980305", "19980312", "19980319", "19980326",
				"19990304", "19990311", "19990318", "19990325" };
		String str = "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=TH";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970313T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testMonthlyRruleWithSpecificDayOccurrences() {
		String[] expectedResults = { "19970913", "19971011", "19971108", "19971213", "19980110", "19980207", "19980307",
				"19980411", "19980509", "19980613" };
		String str = "RRULE:FREQ=MONTHLY;BYDAY=SA;BYMONTHDAY=7,8,9,10,11,12,13";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970913T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testYearlyRruleWithElectionDayPattern() {
		String[] expectedResults = { "19961105", "20001107", "20041102" };
		String str = "RRULE:FREQ=YEARLY;INTERVAL=4;BYMONTH=11;BYDAY=TU;BYMONTHDAY=2,3,4,5,6,7,8";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART:19961105T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	// @Test
	public void testMonthlyRruleWithSecondToLastWeekday() {
		String[] expectedResults = { "19970929", "19971030", "19971127", "19971230", "19980129", "19980226",
				"19980330" };
		String str = "RRULE:FREQ=MONTHLY;BYDAY=MO,TU,WE,TH,FR;BYSETPOS=-2";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART:19970929T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testMonthlyRruleWithSecondToLastMonday() {
		String[] expectedResults = { "19970922", "19971020", "19971117", "19971222", "19980119", "19980216" };
		String str = "RRULE:FREQ=MONTHLY;COUNT=6;BYDAY=-2MO";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART:19970922T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testWeeklyRruleWithTenOccurrences() {
		String[] expectedResults = { "19970902", "19970909", "19970916", "19970923", "19970930", "19971007", "19971014",
				"19971021", "19971028", "19971104" };
		String str = "RRULE:FREQ=WEEKLY;COUNT=10";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART:19970902T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testWeeklyRruleWithEveryOtherWeek() {
		String[] expectedResults = { "20060929", "20061013", "20061027", "20061110", "20061124" };
		String str = "RRULE:FREQ=WEEKLY;INTERVAL=2;BYDAY=FR";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART;VALUE=DATE:20060915");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testDailyRruleUntilSpecificDate() {
		String[] expectedResults = { "20070501", "20070502", "20070503", "20070504", "20070505" };
		String str = "RRULE:FREQ=DAILY;UNTIL=20070506T000000Z";
		try {
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			Date dtStart = new Date("DTSTART:20070501T090000");
			Rrule rrule = new Rrule(str, PARSE_STRICT);
			assertNotNull(rrule, "RRULE should not be null");

			List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
			assertTrue(dates.size() == 5, "Expected 5 events, but got: " + dates.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testDailyRruleWithExdate() {
		String[] expectedResults = { "20070502", "20070503", "20070504", "20070506", "20070507", "20070508", "20070509",
				"20070510" };
		List<String> lines = new ArrayList<>();
		lines.add("BEGIN:VEVENT");
		lines.add("UID: xxx@1234");
		lines.add("SUMMARY:Test1");
		lines.add("DTSTART:20070501");
		lines.add("RRULE:FREQ=DAILY;UNTIL=20070511T000000Z");
		lines.add("EXDATE:20070505");
		lines.add("END:VEVENT");

		try {
			ICalendarParser parser = new ICalendarParser(Constants.PARSE_STRICT);
			Event event = new Event(parser, 0, lines);
			assertNotNull(event, "Event should not be null");

			List<Date> dates = event.getRecurranceDates();
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
			assertEquals(expectedResults.length, dates.size(),
					"Expected " + expectedResults.length + " events, but got: " + dates.size());

			// Generate the iCalendar and reparse
			String icalOut = event.toICalendar();
			List<String> icalLines = Arrays.asList(icalOut.split("[\r\n]+"));
			event = new Event(parser, 0, icalLines);
			assertNotNull(event, "Event should not be null");

			dates = event.getRecurranceDates();
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
			assertEquals(expectedResults.length, dates.size(),
					"Expected " + expectedResults.length + " events, but got: " + dates.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testWeeklyRruleWithRdate() {
		String[] expectedResults = {
				"20080114", "20080121", "20080122", "20080124",
				"20080128", "20080204", "20080211", "20080218"
		};
		List<String> lines = new ArrayList<>();
		lines.add("BEGIN:VEVENT");
		lines.add("UID: xxx@1234");
		lines.add("SUMMARY:Test1");
		lines.add("DTSTART:20080107"); // Mon, 1/7/2008
		lines.add("RRULE:FREQ=WEEKLY;UNTIL=20080225T000000Z");
		lines.add("RDATE:20080122,20080124");
		lines.add("END:VEVENT");

		try {
			ICalendarParser parser = new ICalendarParser(Constants.PARSE_STRICT);
			Event event = new Event(parser, 0, lines);
			assertNotNull(event, "Event should not be null");

			List<Date> dates = event.getRecurranceDates();
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				System.out.println("Date #" + i + ": " + Utils.DateToYYYYMMDD(dates.get(i)));
			}
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
			assertEquals(expectedResults.length, dates.size(),
					"Expected " + expectedResults.length + " events, but got: " + dates.size());

			// Generate the iCalendar and reparse
			String icalOut = event.toICalendar();
			System.out.println("iCalendar:\n" + icalOut + "\n\n");
			List<String> icalLines = Arrays.asList(icalOut.split("[\r\n]+"));
			event = new Event(parser, 0, icalLines);
			assertNotNull(event, "Event should not be null");

			dates = event.getRecurranceDates();
			for (int i = 0; i < dates.size() && i < expectedResults.length; i++) {
				Date d = dates.get(i);
				String ymd = Utils.DateToYYYYMMDD(d);
				assertEquals(expectedResults[i], ymd,
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
			}
			assertEquals(expectedResults.length, dates.size(),
					"Expected " + expectedResults.length + " events, but got: " + dates.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}

	@Test
	public void testGoogleRruleWithDailyFrequency() {
		String[] expectedResults = { "20070501", "20070502", "20070503", "20070504", "20070505" };
		try {
			com.google.ical.values.RRule rrule = new com.google.ical.values.RRule();
			TimeZone tz = TimeZone.getDefault();
			String tzid = tz.getID();
			com.google.ical.values.DateValue dtStart = new DateTimeValueImpl(2007, 5, 1, 9, 0, 0);
			rrule.setFreq(Frequency.DAILY);
			rrule.setName("RRULE");
			rrule.setInterval(1);
			rrule.setUntil(new DateValueImpl(2007, 5, 6));
			RecurrenceIterator iter = RecurrenceIteratorFactory.createRecurrenceIterator(rrule, dtStart, tz);
			int i = 0;
			while (iter.hasNext() && i < 10000) {
				com.google.ical.values.DateValue d = iter.next();
				Date date = new Date("XXX", d.year(), d.month(), d.day());
				String ymd = Utils.DateToYYYYMMDD(date);
				assertTrue(ymd.equals(expectedResults[i]),
						"Unexpected date: got " + ymd + " instead of " + expectedResults[i]);
				i++;
			}
			assertTrue(i == 5, "Expected 5 events, but got: " + i);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown during test: " + e.toString());
		}
	}
}
