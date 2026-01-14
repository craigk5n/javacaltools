 package us.k5n.ical;

 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import static org.junit.jupiter.api.Assertions.*;

 import us.k5n.ical.ICalendarParser;
 import us.k5n.ical.DefaultDataStore;

 import java.io.StringReader;

 /**
  * RFC 7986: REFRESH-INTERVAL Property Tests
  *
  * Tests for the REFRESH-INTERVAL property as defined in RFC 7986, Section 5.7.
  * REFRESH-INTERVAL specifies a suggested minimum interval for polling for changes.
  */
 public class RFC7986_RefreshIntervalTest {

     @Test
     @DisplayName("RFC 7986: REFRESH-INTERVAL basic parsing")
     void testRefreshIntervalBasic() {
         String icalStr = "REFRESH-INTERVAL;VALUE=DURATION:P1W";

         try {
             RefreshInterval ri = new RefreshInterval(icalStr);
             assertEquals("P1W", ri.getValue());
             assertTrue(ri.isValidInterval());
             assertNotNull(ri.getDuration());
         } catch (Exception e) {
             fail("REFRESH-INTERVAL should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: REFRESH-INTERVAL with different durations")
     void testRefreshIntervalDifferentDurations() {
         String[] durations = {"P1D", "P1W", "PT1H", "PT30M", "P15DT5H0M20S"};

         for (String duration : durations) {
             String icalStr = "REFRESH-INTERVAL;VALUE=DURATION:" + duration;
             try {
                 RefreshInterval ri = new RefreshInterval(icalStr);
                 assertEquals(duration, ri.getValue(), "Should parse duration: " + duration);
                 assertTrue(ri.isValidInterval(), "Should be valid interval: " + duration);
                 assertNotNull(ri.getDuration(), "Should create Duration object: " + duration);
             } catch (Exception e) {
                 fail("REFRESH-INTERVAL with duration '" + duration + "' should parse: " + e.getMessage());
             }
         }
     }

     @Test
     @DisplayName("RFC 7986: REFRESH-INTERVAL serialization")
     void testRefreshIntervalSerialization() {
         RefreshInterval ri = new RefreshInterval();
         ri.setValue("P1W");

         String icalOutput = ri.toICalendar();
         assertTrue(icalOutput.contains("REFRESH-INTERVAL;VALUE=DURATION:P1W"),
                   "Serialized output should contain the refresh interval");
     }

     @Test
     @DisplayName("RFC 7986: REFRESH-INTERVAL in VCALENDAR")
     void testRefreshIntervalInCalendar() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "REFRESH-INTERVAL;VALUE=DURATION:P1D\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "REFRESH-INTERVAL in VCALENDAR should parse without errors");
         } catch (Exception e) {
             fail("REFRESH-INTERVAL in VCALENDAR should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: REFRESH-INTERVAL with parameters")
     void testRefreshIntervalWithParameters() {
         String icalStr = "REFRESH-INTERVAL;VALUE=DURATION;X-CUSTOM=value:P1W";

         try {
             RefreshInterval ri = new RefreshInterval(icalStr, ICalendarParser.PARSE_LOOSE);
             assertEquals("P1W", ri.getValue());
             assertTrue(ri.isValidInterval());
         } catch (Exception e) {
             fail("REFRESH-INTERVAL with parameters should parse in loose mode: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: REFRESH-INTERVAL invalid duration")
     void testRefreshIntervalInvalidDuration() {
         String icalStr = "REFRESH-INTERVAL;VALUE=DURATION:INVALID";

         try {
             RefreshInterval ri = new RefreshInterval(icalStr);
             assertEquals("INVALID", ri.getValue());
             // Should not be valid since it's not a proper duration
             assertFalse(ri.isValidInterval());
             assertNull(ri.getDuration());
         } catch (Exception e) {
             fail("REFRESH-INTERVAL with invalid duration should still parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: REFRESH-INTERVAL negative duration")
     void testRefreshIntervalNegativeDuration() {
         String icalStr = "REFRESH-INTERVAL;VALUE=DURATION:-P1D";

         try {
             RefreshInterval ri = new RefreshInterval(icalStr);
             assertEquals("-P1D", ri.getValue());
             // Should not be valid since negative durations are not allowed
             assertFalse(ri.isValidInterval());
         } catch (Exception e) {
             fail("REFRESH-INTERVAL with negative duration should parse but be invalid: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: REFRESH-INTERVAL empty value")
     void testRefreshIntervalEmptyValue() {
         RefreshInterval ri = new RefreshInterval();
         ri.setValue("");

         String icalOutput = ri.toICalendar();
         assertTrue(icalOutput.contains("REFRESH-INTERVAL;VALUE=DURATION:"),
                   "Empty refresh interval should serialize");
         assertFalse(ri.isValidInterval(), "Empty value should not be valid");
     }
 }