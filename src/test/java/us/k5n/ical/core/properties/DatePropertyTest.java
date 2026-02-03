package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import us.k5n.ical.BogusDataException;
import us.k5n.ical.Constants;
import us.k5n.ical.Date;
import us.k5n.ical.Utils;

/**
 * RFC 5545 Section 3.3.4/3.3.5: DATE and DATE-TIME Property Tests
 *
 * Tests for date and date-time values as defined in RFC 5545.
 * Validates parsing, handling of date-only and date-time values,
 * timezone support, and iCalendar output generation.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.3.4/3.3.5: DATE and DATE-TIME Properties")
public class DatePropertyTest implements Constants {

  @Nested
  @DisplayName("Date-Only Parsing Tests")
  class DateOnlyParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.4: should parse date-only value correctly")
    void should_parseDateOnly_when_noTimeComponent() {
      String dateStr = "DATE:20070131";
      try {
        Date d = new Date(dateStr, PARSE_STRICT);
        assertTrue(d.isDateOnly(), "Date has time");
        assertEquals(2007, d.getYear(), "Incorrect year");
        assertEquals(1, d.getMonth(), "Incorrect month");
        assertEquals(31, d.getDay(), "Incorrect day");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Date-Time Parsing Tests")
  class DateTimeParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.5: should parse date-time value correctly")
    void should_parseDateTime_when_timeComponentPresent() {
      String dateStr = "DTSTART:20070131T132047";
      try {
        Date d = new Date(dateStr, PARSE_STRICT);
        assertFalse(d.isDateOnly(), "Date has no time");
        assertEquals(2007, d.getYear(), "Incorrect year");
        assertEquals(1, d.getMonth(), "Incorrect month");
        assertEquals(31, d.getDay(), "Incorrect day");
        assertEquals(13, d.getHour(), "Incorrect hour");
        assertEquals(20, d.getMinute(), "Incorrect minute");
        assertEquals(47, d.getSecond(), "Incorrect second");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.3.5: should parse floating date-time without timezone")
    void should_parseFloatingDateTime_when_noTimezoneSpecified() {
      String dateStr = "DTSTART;VALUE=\"DATE-TIME\":20070901T120000";
      try {
        Date d = new Date(dateStr, PARSE_STRICT);
        assertEquals(12, d.getHour(), "Wrong hour in floating time");
        assertFalse(d.isDateOnly(), "Date should have time");
        String icalStr = d.toICalendar();
        assertFalse(icalStr.contains("TZID"), "Date-time should not have timezone");
      } catch (Exception e) {
        fail("Caught unexpected exception: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.3.5: should parse UTC date-time with Z suffix")
    void should_parseUtcDateTime_when_zSuffixPresent() {
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
  }

  @Nested
  @DisplayName("Invalid Date Detection Tests")
  class InvalidDateDetectionTests {

    @ParameterizedTest
    @ValueSource(strings = {
        "20011301", "20010132", "19990012", "19991200",
        "19990132", "19990229", "20010230", "20010332",
        "20010431", "20010532", "20010631", "20010732",
        "20010832", "20010931", "20021032", "20021131",
        "20021232"
    })
    @DisplayName("RFC 5545 Section 3.3.4: should reject invalid date values")
    void should_rejectInvalidDate_when_dateIsOutOfRange(String dStr) {
      try {
        Date d = new Date("DATE:" + dStr);
        fail("Did not catch invalid date '" + dStr + "'");
      } catch (BogusDataException e) {
        // Expected exception
      } catch (Exception e2) {
        fail("Caught unexpected exception: " + e2.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Valid Date Handling Tests")
  class ValidDateHandlingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.4: should accept all valid dates in leap year")
    void should_acceptValidDates_when_leapYear() {
      Calendar c = Calendar.getInstance();
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
    }

    @Test
    @DisplayName("RFC 5545 Section 3.3.4: should accept all valid dates in non-leap year")
    void should_acceptValidDates_when_nonLeapYear() {
      Calendar c = Calendar.getInstance();
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
  }

  @Nested
  @DisplayName("iCalendar Conversion Tests")
  class ICalendarConversionTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.4: should round-trip date-only value")
    void should_roundTripDateOnly_when_convertedToICalendar() {
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

    @Test
    @DisplayName("RFC 5545 Section 3.3.5: should round-trip date-time value")
    void should_roundTripDateTime_when_convertedToICalendar() {
      String dateStr = "DATE:20070131T115959";
      try {
        Date d = new Date(dateStr, PARSE_STRICT);
        Date newDate = new Date(d.toICalendar(), PARSE_STRICT);
        assertEquals(d, newDate,
            "Dates not equal: " + d.toICalendar() + " vs " + newDate.toICalendar());
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.3.5: should handle VALUE=DATE with time component")
    void should_handleValueDate_when_timeComponentPresent() {
      String dateStr = "DTSTART;VALUE=\"DATE\":20070413T091945";
      try {
        Date d = new Date(dateStr, PARSE_STRICT);
        Date newDate = new Date(d.toICalendar(), PARSE_STRICT);
        assertEquals(d, newDate,
            "Dates not equal: " + d.toICalendar() + " vs " + newDate.toICalendar());
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Date Creation Tests")
  class DateCreationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.4: should create date-only using constructor")
    void should_createDateOnly_when_noTimeProvided() {
      try {
        Date d = new Date("DTSTART", 1999, 12, 31);
        assertTrue(d.isDateOnly(), "Date has time");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.3.5: should create date-time using constructor")
    void should_createDateTime_when_timeProvided() {
      try {
        Date d = new Date("DTSTART", 1999, 12, 31, 23, 59, 59);
        assertFalse(d.isDateOnly(), "Date has no time");
        assertEquals(23, d.getHour(), "Wrong hour");
        assertEquals(59, d.getMinute(), "Wrong minute");
        assertEquals(59, d.getSecond(), "Wrong second");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Week of Year Calculation Tests")
  class WeekOfYearTests {

    @ParameterizedTest
    @CsvSource({
        "1/1/1999, 53", "1/2/1999, 53", "1/3/1999, 53", "1/1/2000, 52",
        "1/3/2000, 1", "1/1/2001, 1", "1/2/2001, 1",
        "1/3/2001, 1", "1/1/2002, 1", "1/2/2002, 1", "1/3/2002, 1",
        "1/4/2002, 1", "1/5/2002, 1", "1/6/2002, 2",
        "1/1/2005, 53", "1/2/2005, 53", "1/3/2005, 1",
        "1/2/2020, 1", "1/4/2020, 1", "1/5/2020, 2"
    })
    @DisplayName("RFC 5545: should calculate correct ISO week of year")
    void should_calculateWeekOfYear_when_dateProvided(String dateString, int expectedWeek) {
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

    @ParameterizedTest
    @CsvSource({
        "2019-12-31, 1",
        "2020-01-01, 1",
        "2021-12-31, 52",
        "2022-01-01, 52"
    })
    @DisplayName("RFC 5545: should handle New Year's week transition correctly")
    void should_handleNewYearsTransition_when_crossingYearBoundary(String dateStr, int expectedWeek) {
      try {
        String[] dateParts = dateStr.split("-");
        Date d = new Date("XXX", Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
            Integer.parseInt(dateParts[2]));
        assertEquals(expectedWeek, d.getWeekOfYear(), "Incorrect week number for " + dateStr);
      } catch (BogusDataException e) {
        fail("Caught BogusDataException: " + e.getMessage());
      }
    }

    @ParameterizedTest
    @CsvSource({
        "2020-01-01, 1",
        "2020-02-28, 9",
        "2020-02-29, 9",
        "2020-03-01, 10"
    })
    @DisplayName("RFC 5545: should handle leap year week calculations correctly")
    void should_handleLeapYear_when_calculatingWeekOfYear(String dateStr, int expectedWeek) {
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

  @Nested
  @DisplayName("Timezone Handling Tests")
  class TimezoneHandlingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.5: should include timezone in output when specified")
    void should_includeTimezone_when_tzidSpecified() {
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

    @Test
    @DisplayName("RFC 5545 Section 3.3.5: should parse and preserve TZID parameter")
    void should_preserveTzid_when_parsingDateTime() {
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

    @Test
    @DisplayName("RFC 5545 Section 3.3.5: should handle UTC date-time with TZID")
    void should_handleUtcWithTzid_when_zSuffixUsed() {
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
  }
}
