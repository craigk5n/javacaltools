package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Duration.
 * 
 * @author Craig Knudsen
 */
public class DurationTest implements Constants {

    @BeforeEach
    public void setUp() {
        // Setup code, if any, can be placed here
    }

    @Test
    public void testOneHourDuration() {
        String str = "DURATION:+PT1H";
        try {
            Duration x = new Duration(str, PARSE_STRICT);
            assertNotNull(x, "Duration should not be null");
            assertEquals(3600, x.duration, "Duration should be 3600 seconds (1 hour)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testNinetyMinutesDuration() {
        String str = "DURATION:+PT1H30M";
        try {
            Duration x = new Duration(str, PARSE_STRICT);
            assertNotNull(x, "Duration should not be null");
            assertEquals(5400, x.duration, "Duration should be 5400 seconds (1 hour 30 minutes)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testOneHourThirtyMinutesFifteenSecondsDuration() {
        String str = "DURATION:+PT1H30M15S";
        try {
            Duration x = new Duration(str, PARSE_STRICT);
            assertNotNull(x, "Duration should not be null");
            assertEquals(5415, x.duration, "Duration should be 5415 seconds (1 hour 30 minutes 15 seconds)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testOneDayDuration() {
        String str = "DURATION:+P1D";
        try {
            Duration x = new Duration(str, PARSE_STRICT);
            assertNotNull(x, "Duration should not be null");
            assertEquals(86400, x.duration, "Duration should be 86400 seconds (1 day)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testOneDayTwoHoursDuration() {
        String str = "DURATION:+P1DT2H";
        try {
            Duration x = new Duration(str, PARSE_STRICT);
            assertNotNull(x, "Duration should not be null");
            assertEquals(93600, x.duration, "Duration should be 93600 seconds (1 day 2 hours)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testOneWeekTwoDaysDuration() {
        String str = "DURATION:+P1W2D";
        try {
            Duration x = new Duration(str, PARSE_STRICT);
            assertNotNull(x, "Duration should not be null");
            assertEquals(777600, x.duration, "Duration should be 777600 seconds (1 week 2 days)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testFifteenDaysFiveHoursTwentySecondsDuration() {
        String str = "DURATION:P15DT5H0M20S";
        try {
            Duration x = new Duration(str, PARSE_STRICT);
            assertNotNull(x, "Duration should not be null");
            assertEquals(1314020, x.duration, "Duration should be 1314020 seconds (15 days 5 hours 20 seconds)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testNegativeDuration() {
        String str = "DURATION:-PT1H";
        try {
            Duration x = new Duration(str, PARSE_STRICT);
            assertNotNull(x, "Duration should not be null");
            assertEquals(-3600, x.duration, "Duration should be -3600 seconds (negative 1 hour)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testZeroDuration() {
        String str = "DURATION:PT0S";
        try {
            Duration x = new Duration(str, PARSE_STRICT);
            assertNotNull(x, "Duration should not be null");
            assertEquals(0, x.duration, "Duration should be 0 seconds (zero duration)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }
}