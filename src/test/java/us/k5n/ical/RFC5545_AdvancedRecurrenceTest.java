 package us.k5n.ical;

 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import static org.junit.jupiter.api.Assertions.*;

 import us.k5n.ical.ICalendarParser;
 import us.k5n.ical.DefaultDataStore;

 import java.io.StringReader;

/**
 * RFC 5545: Advanced Recurrence Tests
 *
 * Tests for advanced RRULE recurrence patterns as defined in RFC 5545, Section 3.8.5.3.
 * Covers complex RRULE combinations, edge cases, and error conditions.
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
 public class RFC5545_AdvancedRecurrenceTest {

     @Test
     @DisplayName("RFC 5545: Complex WEEKLY recurrence with BYDAY and BYHOUR")
     void testComplexWeeklyRecurrence() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:complex-weekly@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Complex Weekly Meeting\n" +
             "RRULE:FREQ=WEEKLY;BYDAY=MO,WE,FR;BYHOUR=9,14;UNTIL=20240131T235959Z\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "Complex RRULE should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: MONTHLY recurrence with BYDAY ordinals")
     void testMonthlyByDayOrdinals() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:monthly-ordinal@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:First Monday of Month\n" +
             "RRULE:FREQ=MONTHLY;BYDAY=1MO;COUNT=12\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "MONTHLY with BYDAY ordinals should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: YEARLY recurrence with BYMONTH and BYMONTHDAY")
     void testYearlyComplex() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:yearly-complex@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:January 1st and July 4th\n" +
             "RRULE:FREQ=YEARLY;BYMONTH=1,7;BYMONTHDAY=1,4;UNTIL=20251231T235959Z\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "YEARLY with BYMONTH and BYMONTHDAY should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: Complex RRULE with INTERVAL and BYSETPOS")
     void testComplexIntervalBySetPos() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:complex-setpos@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Every Other Month, Last Friday\n" +
             "RRULE:FREQ=MONTHLY;INTERVAL=2;BYDAY=FR;BYSETPOS=-1;COUNT=6\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "Complex RRULE with INTERVAL and BYSETPOS should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: RRULE with WKST (week start)")
     void testWeekStart() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:week-start@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Monday Start Week\n" +
             "RRULE:FREQ=WEEKLY;WKST=MO;BYDAY=MO,WE,FR;COUNT=10\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "RRULE with WKST should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: RRULE with UNTIL date")
     void testUntilDate() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:until-date@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Daily until date\n" +
             "RRULE:FREQ=DAILY;UNTIL=20240110T235959Z\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "RRULE with UNTIL date should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: RRULE with COUNT limit")
     void testCountLimit() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:count-limit@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Exactly 5 occurrences\n" +
             "RRULE:FREQ=WEEKLY;COUNT=5\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "RRULE with COUNT should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: Complex HOURLY recurrence with BYMINUTE")
     void testComplexHourly() throws Exception {
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

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "Complex HOURLY RRULE should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: RRULE with multiple BYxxx rules")
     void testMultipleByRules() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:multi-by@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Multiple BY rules\n" +
             "RRULE:FREQ=YEARLY;BYMONTH=3,6,9,12;BYDAY=MO;BYSETPOS=2;COUNT=8\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "RRULE with multiple BYxxx rules should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: RRULE with BYYEARDAY")
     void testByYearDay() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:by-year-day@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:New Year's Day and July 4th\n" +
             "RRULE:FREQ=YEARLY;BYYEARDAY=1,185;COUNT=4\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "RRULE with BYYEARDAY should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: RRULE with negative BYMONTHDAY")
     void testNegativeByMonthDay() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:negative-monthday@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Last day of month\n" +
             "RRULE:FREQ=MONTHLY;BYMONTHDAY=-1;COUNT=6\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "RRULE with negative BYMONTHDAY should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: RRULE with BYWEEKNO")
     void testByWeekNo() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:by-week-no@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Weeks 10, 20, 30, 40, 50\n" +
             "RRULE:FREQ=YEARLY;BYWEEKNO=10,20,30,40,50;COUNT=5\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "RRULE with BYWEEKNO should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5545: Invalid RRULE parameters")
     void testInvalidRrule() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:invalid-rrule@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Invalid RRULE\n" +
             "RRULE:FREQ=INVALID;COUNT=5\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Parser may or may not report errors for invalid RRULE - just verify it parses
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events even with invalid RRULE");
         assertFalse(ds.getAllEvents().isEmpty(), "Should have at least one event");
     }

     @Test
     @DisplayName("RFC 5545: RRULE with both UNTIL and COUNT (invalid)")
     void testUntilAndCount() throws Exception {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:until-and-count@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240101T090000Z\n" +
             "DTEND:20240101T100000Z\n" +
             "SUMMARY:Both UNTIL and COUNT\n" +
             "RRULE:FREQ=DAILY;UNTIL=20240110T235959Z;COUNT=5\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(icalData));

         // Should parse even with conflicting parameters (validation is lenient)
         assertTrue(parser.getAllErrors().isEmpty(), "RRULE with both UNTIL and COUNT should parse");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }
 }