package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Constants;
import us.k5n.ical.Duration;

/**
 * RFC 5545 Section 3.3.6: DURATION Value Tests
 *
 * Tests for the DURATION value type as defined in RFC 5545.
 * The DURATION value type is used to identify properties that contain
 * a duration of time.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.3.6: DURATION Value Type")
public class DurationPropertyTest implements Constants {

  @Nested
  @DisplayName("Time Duration Parsing Tests")
  class TimeDurationParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.6: should parse one hour duration")
    void should_parseOneHour_when_pt1hProvided() {
      String str = "DURATION:+PT1H";
      try {
        Duration x = new Duration(str, PARSE_STRICT);
        assertNotNull(x, "Duration should not be null");
        assertEquals(3600, Duration.parseDuration(x.getValue()), "Duration should be 3600 seconds (1 hour)");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.3.6: should parse 90 minutes duration")
    void should_parseNinetyMinutes_when_pt1h30mProvided() {
      String str = "DURATION:+PT1H30M";
      try {
        Duration x = new Duration(str, PARSE_STRICT);
        assertNotNull(x, "Duration should not be null");
        assertEquals(5400, Duration.parseDuration(x.getValue()), "Duration should be 5400 seconds (1 hour 30 minutes)");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.3.6: should parse hours, minutes, and seconds")
    void should_parseHoursMinutesSeconds_when_fullTimeProvided() {
      String str = "DURATION:+PT1H30M15S";
      try {
        Duration x = new Duration(str, PARSE_STRICT);
        assertNotNull(x, "Duration should not be null");
        assertEquals(5415, Duration.parseDuration(x.getValue()),
            "Duration should be 5415 seconds (1 hour 30 minutes 15 seconds)");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Day Duration Parsing Tests")
  class DayDurationParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.6: should parse one day duration")
    void should_parseOneDay_when_p1dProvided() {
      String str = "DURATION:+P1D";
      try {
        Duration x = new Duration(str, PARSE_STRICT);
        assertNotNull(x, "Duration should not be null");
        assertEquals(86400, Duration.parseDuration(x.getValue()), "Duration should be 86400 seconds (1 day)");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.3.6: should parse days and hours duration")
    void should_parseDaysAndHours_when_p1dt2hProvided() {
      String str = "DURATION:+P1DT2H";
      try {
        Duration x = new Duration(str, PARSE_STRICT);
        assertNotNull(x, "Duration should not be null");
        assertEquals(93600, Duration.parseDuration(x.getValue()), "Duration should be 93600 seconds (1 day 2 hours)");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Week Duration Parsing Tests")
  class WeekDurationParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.6: should parse weeks and days duration")
    void should_parseWeeksAndDays_when_p1w2dProvided() {
      String str = "DURATION:+P1W2D";
      try {
        Duration x = new Duration(str, PARSE_STRICT);
        assertNotNull(x, "Duration should not be null");
        assertEquals(777600, Duration.parseDuration(x.getValue()), "Duration should be 777600 seconds (1 week 2 days)");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Complex Duration Parsing Tests")
  class ComplexDurationParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.6: should parse complex duration")
    void should_parseComplexDuration_when_multipleParts() {
      String str = "DURATION:P15DT5H0M20S";
      try {
        Duration x = new Duration(str, PARSE_STRICT);
        assertNotNull(x, "Duration should not be null");
        assertEquals(1314020, Duration.parseDuration(x.getValue()),
            "Duration should be 1314020 seconds (15 days 5 hours 20 seconds)");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Edge Case Duration Tests")
  class EdgeCaseDurationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.3.6: should parse negative duration")
    void should_parseNegativeDuration_when_minusSignProvided() {
      String str = "DURATION:-PT1H";
      try {
        Duration x = new Duration(str, PARSE_STRICT);
        assertNotNull(x, "Duration should not be null");
        assertEquals(-3600, Duration.parseDuration(x.getValue()), "Duration should be -3600 seconds (negative 1 hour)");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.3.6: should parse zero duration")
    void should_parseZeroDuration_when_pt0sProvided() {
      String str = "DURATION:PT0S";
      try {
        Duration x = new Duration(str, PARSE_STRICT);
        assertNotNull(x, "Duration should not be null");
        assertEquals(0, Duration.parseDuration(x.getValue()), "Duration should be 0 seconds (zero duration)");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Test failed due to exception: " + e.toString());
      }
    }
  }
}
