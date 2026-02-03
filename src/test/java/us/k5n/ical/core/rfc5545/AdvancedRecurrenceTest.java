package us.k5n.ical.core.rfc5545;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import us.k5n.ical.*;

/**
 * RFC 5545: Advanced Recurrence Tests
 *
 * Tests for advanced RRULE recurrence patterns as defined in RFC 5545, Section 3.8.5.3.
 * Covers complex RRULE combinations, edge cases, and error conditions.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.5.3: Advanced Recurrence")
public class AdvancedRecurrenceTest {

  private void parseAndVerify(String icalData, String description) throws Exception {
    ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
    parser.parse(new StringReader(icalData));
    assertTrue(parser.getAllErrors().isEmpty(), description + " should parse without errors");
    DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
    assertNotNull(ds.getAllEvents(), "Should have parsed events");
  }

  private String wrapEvent(String uid, String summary, String rrule) {
    return "BEGIN:VCALENDAR\n" +
        "VERSION:2.0\n" +
        "PRODID:-//Test//Calendar//EN\n" +
        "BEGIN:VEVENT\n" +
        "UID:" + uid + "@example.com\n" +
        "DTSTAMP:20240101T100000Z\n" +
        "DTSTART:20240101T090000Z\n" +
        "DTEND:20240101T100000Z\n" +
        "SUMMARY:" + summary + "\n" +
        "RRULE:" + rrule + "\n" +
        "END:VEVENT\n" +
        "END:VCALENDAR";
  }

  @Nested
  @DisplayName("Complex Frequency Tests")
  class ComplexFrequencyTests {

    @Test
    @DisplayName("RFC 5545: should parse WEEKLY with BYDAY and BYHOUR")
    void should_parseComplex_when_weeklyByDayByHour() throws Exception {
      parseAndVerify(
          wrapEvent("complex-weekly", "Complex Weekly Meeting",
              "FREQ=WEEKLY;BYDAY=MO,WE,FR;BYHOUR=9,14;UNTIL=20240131T235959Z"),
          "Complex RRULE");
    }

    @Test
    @DisplayName("RFC 5545: should parse MONTHLY with BYDAY ordinals")
    void should_parseOrdinals_when_monthlyByDayOrdinals() throws Exception {
      parseAndVerify(
          wrapEvent("monthly-ordinal", "First Monday of Month",
              "FREQ=MONTHLY;BYDAY=1MO;COUNT=12"),
          "MONTHLY with BYDAY ordinals");
    }

    @Test
    @DisplayName("RFC 5545: should parse YEARLY with BYMONTH and BYMONTHDAY")
    void should_parseComplex_when_yearlyByMonthByMonthDay() throws Exception {
      parseAndVerify(
          wrapEvent("yearly-complex", "January 1st and July 4th",
              "FREQ=YEARLY;BYMONTH=1,7;BYMONTHDAY=1,4;UNTIL=20251231T235959Z"),
          "YEARLY with BYMONTH and BYMONTHDAY");
    }

    @Test
    @DisplayName("RFC 5545: should parse HOURLY with BYMINUTE")
    void should_parseComplex_when_hourlyByMinute() throws Exception {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:complex-hourly@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T090000Z\n" +
          "DTEND:20240101T091500Z\n" +
          "SUMMARY:Every 2 hours at 15 and 45 minutes\n" +
          "RRULE:FREQ=HOURLY;INTERVAL=2;BYMINUTE=15,45;UNTIL=20240101T180000Z\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";
      parseAndVerify(icalData, "Complex HOURLY RRULE");
    }
  }

  @Nested
  @DisplayName("INTERVAL and BYSETPOS Tests")
  class IntervalBySetPosTests {

    @Test
    @DisplayName("RFC 5545: should parse INTERVAL with BYSETPOS")
    void should_parseIntervalBySetPos_when_complexRuleProvided() throws Exception {
      parseAndVerify(
          wrapEvent("complex-setpos", "Every Other Month, Last Friday",
              "FREQ=MONTHLY;INTERVAL=2;BYDAY=FR;BYSETPOS=-1;COUNT=6"),
          "Complex RRULE with INTERVAL and BYSETPOS");
    }

    @Test
    @DisplayName("RFC 5545: should parse WKST (week start)")
    void should_parseWkst_when_weekStartProvided() throws Exception {
      parseAndVerify(
          wrapEvent("week-start", "Monday Start Week",
              "FREQ=WEEKLY;WKST=MO;BYDAY=MO,WE,FR;COUNT=10"),
          "RRULE with WKST");
    }
  }

  @Nested
  @DisplayName("Termination Rule Tests")
  class TerminationTests {

    @Test
    @DisplayName("RFC 5545: should parse UNTIL date")
    void should_parseUntil_when_untilDateProvided() throws Exception {
      parseAndVerify(
          wrapEvent("until-date", "Daily until date",
              "FREQ=DAILY;UNTIL=20240110T235959Z"),
          "RRULE with UNTIL date");
    }

    @Test
    @DisplayName("RFC 5545: should parse COUNT limit")
    void should_parseCount_when_countLimitProvided() throws Exception {
      parseAndVerify(
          wrapEvent("count-limit", "Exactly 5 occurrences",
              "FREQ=WEEKLY;COUNT=5"),
          "RRULE with COUNT");
    }
  }

  @Nested
  @DisplayName("Multiple BYxxx Rules Tests")
  class MultipleBYRulesTests {

    @Test
    @DisplayName("RFC 5545: should parse multiple BYxxx rules")
    void should_parseMultipleBY_when_multipleBYRulesProvided() throws Exception {
      parseAndVerify(
          wrapEvent("multi-by", "Multiple BY rules",
              "FREQ=YEARLY;BYMONTH=3,6,9,12;BYDAY=MO;BYSETPOS=2;COUNT=8"),
          "RRULE with multiple BYxxx rules");
    }

    @Test
    @DisplayName("RFC 5545: should parse BYYEARDAY")
    void should_parseByYearDay_when_byYearDayProvided() throws Exception {
      parseAndVerify(
          wrapEvent("by-year-day", "New Year's Day and July 4th",
              "FREQ=YEARLY;BYYEARDAY=1,185;COUNT=4"),
          "RRULE with BYYEARDAY");
    }

    @Test
    @DisplayName("RFC 5545: should parse negative BYMONTHDAY")
    void should_parseNegativeByMonthDay_when_negativeValueProvided() throws Exception {
      parseAndVerify(
          wrapEvent("negative-monthday", "Last day of month",
              "FREQ=MONTHLY;BYMONTHDAY=-1;COUNT=6"),
          "RRULE with negative BYMONTHDAY");
    }

    @Test
    @DisplayName("RFC 5545: should parse BYWEEKNO")
    void should_parseByWeekNo_when_byWeekNoProvided() throws Exception {
      parseAndVerify(
          wrapEvent("by-week-no", "Weeks 10, 20, 30, 40, 50",
              "FREQ=YEARLY;BYWEEKNO=10,20,30,40,50;COUNT=5"),
          "RRULE with BYWEEKNO");
    }
  }

  @Nested
  @DisplayName("Error Handling Tests")
  class ErrorHandlingTests {

    @Test
    @DisplayName("RFC 5545: should handle invalid RRULE parameters")
    void should_handleInvalid_when_invalidFrequencyProvided() throws Exception {
      String icalData = wrapEvent("invalid-rrule", "Invalid RRULE", "FREQ=INVALID;COUNT=5");

      ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
      parser.parse(new StringReader(icalData));

      DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
      assertNotNull(ds.getAllEvents(), "Should have parsed events even with invalid RRULE");
      assertFalse(ds.getAllEvents().isEmpty(), "Should have at least one event");
    }

    @Test
    @DisplayName("RFC 5545: should handle both UNTIL and COUNT (invalid)")
    void should_handleConflict_when_bothUntilAndCountProvided() throws Exception {
      String icalData = wrapEvent("until-and-count", "Both UNTIL and COUNT",
          "FREQ=DAILY;UNTIL=20240110T235959Z;COUNT=5");

      ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
      parser.parse(new StringReader(icalData));

      assertTrue(parser.getAllErrors().isEmpty(),
          "RRULE with both UNTIL and COUNT should parse");
      DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
      assertNotNull(ds.getAllEvents(), "Should have parsed events");
    }
  }
}
