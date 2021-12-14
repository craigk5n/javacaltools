package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import com.google.ical.iter.RecurrenceIterator;
import com.google.ical.iter.RecurrenceIteratorFactory;
import com.google.ical.values.DateTimeValueImpl;
import com.google.ical.values.DateValueImpl;
import com.google.ical.values.Frequency;

/**
 * Test cases for Rrule.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class RruleTest implements Constants {

  @Test
  public void testSimple() {
    String str = "RRULE:FREQ=MONTHLY;BYMONTH=10;BYDAY=2MO";
    try {
      Rrule x = new Rrule(str, PARSE_STRICT);
      assertNotNull(x);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  @Test
  public void testYearly1() {
    String[] results = {"19970310", "19990110", "19990210", "19990310", "20010110", "20010210",
        "20010310", "20030110", "20030210", "20030310"};
    String str = "RRULE:FREQ=YEARLY;INTERVAL=2;COUNT=10;BYMONTH=1,2,3";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970310T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size(); i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testYearly1)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Every 3rd year on the 1st, 100th and 200th day for 10 occurrences:
  // 100th day of non-leapyear is April 10, April 11 in leapyear
  // 200th day of non-leapyear is July 19, July 20 in leapyear
  @Test
  public void testYearly2() {
    String[] results = {"19970101", "19970410", "19970719", "20000101", "20000409", "20000718",
        "20030101", "20030410", "20030719", "20060101"};
    String str = "RRULE:FREQ=YEARLY;INTERVAL=3;COUNT=10;BYYEARDAY=1,100,200";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970101T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size(); i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testYearly2)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Every 20th Monday of the year, forever
  @Test
  public void testYearly3() {
    String[] results = {"19970519", "19980518", "19990517"};
    String str = "RRULE:FREQ=YEARLY;BYDAY=20MO";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970519T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testYearly3)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertTrue(ymd.equals(results[i]));
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Every Thursday in March, forever
  @Test
  public void testYearly4() {
    String[] results = {"19970313", "19970320", "19970327", "19980305", "19980312", "19980319",
        "19980326", "19990304", "19990311", "19990318", "19990325"};
    String str = "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=TH";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970313T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testYearly4)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertTrue(ymd.equals(results[i]));
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // The first Saturday that follows the first Sunday of the month, forever
  @Test
  public void testMonthly1() {
    String[] results = {"19970913", "19971011", "19971108", "19971213", "19980110", "19980207",
        "19980307", "19980411", "19980509", "19980613"};
    String str = "RRULE:FREQ=MONTHLY;BYDAY=SA;BYMONTHDAY=7,8,9,10,11,12,13";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART;TZID=" + tzid + ":19970913T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testMonthly1)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertTrue(ymd.equals(results[i]));
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Every four years, the first Tuesday after a Monday in November,
  // forever (U.S. Presidential Election day)
  @Test
  public void testMonthly2() {
    String[] results = {"19961105", "20001107", "20041102"};
    String str = "RRULE:FREQ=YEARLY;INTERVAL=4;BYMONTH=11;BYDAY=TU;BYMONTHDAY=2,3,4,5,6,7,8";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART:19961105T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testMonthly2)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertTrue(ymd.equals(results[i]));
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // The 2nd to last weekday of the month
  // TODO: Fix this broken test. Some kind of problem with RRULE.
  // @Test
  public void testMonthly3() {
    String[] results =
        {"19970929", "19971030", "19971127", "19971230", "19980129", "19980226", "19980330"};
    String str = "RRULE:FREQ=MONTHLY;BYDAY=MO,TU,WE,TH,FR;BYSETPOS=-2";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART:19970929T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testMonthly3)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Monthly on the second to last Monday of the month for 6 months
  @Test
  public void testMonthly4() {
    String[] results = {"19970922", "19971020", "19971117", "19971222", "19980119", "19980216"};
    String str = "RRULE:FREQ=MONTHLY;COUNT=6;BYDAY=-2MO";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART:19970922T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testMonthly4)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Weekly for 10 occurrences
  @Test
  public void testWeekly1() {
    String[] results = {"19970902", "19970909", "19970916", "19970923", "19970930", "19971007",
        "19971014", "19971021", "19971028", "19971104"};
    String str = "RRULE:FREQ=WEEKLY;COUNT=10";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART:19970902T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testWeekly1)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Every other week with no end
  @Test
  public void testWeekly2() {
    String[] results =
        { /* "20060915", */"20060929", "20061013", "20061027", "20061110", "20061124"};
    String str = "RRULE:FREQ=WEEKLY;INTERVAL=2;BYDAY=FR";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART;VALUE=DATE:20060915");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testWeekly2)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Daily for until May 5
  @Test
  public void testDaily1() {
    String[] results = {"20070501", "20070502", "20070503", "20070504", "20070505"};
    String str = "RRULE:FREQ=DAILY;UNTIL=20070506T000000Z";
    try {
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      Date dtStart = new Date("DTSTART:20070501T090000");
      Rrule rrule = new Rrule(str, PARSE_STRICT);
      assertNotNull(rrule);
      List<Date> dates = rrule.generateRecurrances(dtStart, tzid);
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testDaily1)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
      assertEquals(5, dates.size());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Daily from May 1 until May 10, with EXDATE of May 5
  // TODO: Fix this broken test. Some kind of internal problem with RRULE.
  // Maybe try updating?
  @Test
  public void testDailyWithExdate1() {
    String[] results = {"20070502", "20070503", "20070504", "20070506", "20070507", "20070508",
        "20070509", "20070510"};
    List<String> lines = new ArrayList<String>();
    lines.add("BEGIN:VEVENT");
    lines.add("UID: xxx@1234");
    lines.add("SUMMARY:Test1");
    lines.add("DTSTART:20070501");
    lines.add("RRULE:FREQ=DAILY;UNTIL=20070511T000000Z");
    lines.add("EXDATE:20070505");
    lines.add("END:VEVENT");

    try {
      ICalendarParser parser = new ICalendarParser(Constants.PARSE_STRICT);
      Event e = new Event(parser, 0, lines);
      assertNotNull(e);
      List<Date> dates = e.getRecurranceDates();
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testDailyWithExdate1)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
      assertTrue(dates.size() == results.length);

      // Generate the iCalendar and do it all over...
      String icalOut = e.toICalendar();
      String[] icalLines = icalOut.split("[\r\n]+");
      List<String> icalLinesV = new ArrayList<String>();
      for (int i = 0; i < icalLines.length; i++) {
        icalLinesV.add(icalLines[i]);
      }
      e = new Event(parser, 0, lines);
      assertNotNull(e);
      dates = e.getRecurranceDates();
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testDailyWithExdate1)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertTrue(ymd.equals(results[i]));
      }
      assertTrue(dates.size() == results.length);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Daily for until May 10, witch EXDATE of May 5, May 7
  // TODO: Fix this broken test. Some kind of problem with RRULE.
  //@Test
  public void testDailyWithExdate2() {
    String[] results =
        {"20070502", "20070503", "20070504", "20070506", "20070508", "20070509", "20070510"};
    List<String> lines = new ArrayList<String>();
    lines.add("BEGIN:VEVENT");
    lines.add("UID: xxx@1234");
    lines.add("SUMMARY:Test1");
    lines.add("DTSTART:20070501");
    lines.add("RRULE:FREQ=DAILY;UNTIL=20070511T000000Z");
    lines.add("EXDATE:20070505,20070507");
    lines.add("END:VEVENT");

    try {
      ICalendarParser parser = new ICalendarParser(Constants.PARSE_STRICT);
      Event e = new Event(parser, 0, lines);
      assertNotNull(e);
      List<Date> dates = e.getRecurranceDates();
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testDailyWithExdate2)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
      assertTrue(dates.size() == results.length);

      // Generate the iCalendar and do it all over...
      String icalOut = e.toICalendar();
      String[] icalLines = icalOut.split("[\r\n]+");
      List<String> icalLinesV = new ArrayList<String>();
      for (int i = 0; i < icalLines.length; i++) {
        icalLinesV.add(icalLines[i]);
      }
      e = new Event(parser, 0, lines);
      assertNotNull(e);
      dates = e.getRecurranceDates();
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testDailyWithExdate2)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertTrue(ymd.equals(results[i]));
      }
      assertTrue(dates.size() == results.length);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

  // Test the RDATE support. Create a weekly repeating event. Add two extra
  // dates in the middle of the series.
  // TODO: Fix this broken test. Some kind of problem with RRULE.
  //@Test
  public void testWeeklyWithRdate1() {
    String[] results = {"20080114", "20080121", "20080122", "20080124", "20080128", "20080204",
        "20080211", "20080218"};
    List<String> lines = new ArrayList<String>();
    lines.add("BEGIN:VEVENT");
    lines.add("UID: xxx@1234");
    lines.add("SUMMARY:Test1");
    lines.add("DTSTART:20080107"); // Mon, 1/7/2008
    lines.add("RRULE:FREQ=WEEKLY;UNTIL=20080225T000000Z");
    lines.add("RDATE:20080122,20080124");
    lines.add("END:VEVENT");

    try {
      ICalendarParser parser = new ICalendarParser(Constants.PARSE_STRICT);
      Event e = new Event(parser, 0, lines);
      assertNotNull(e);
      List<Date> dates = e.getRecurranceDates();
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testWeeklyWithRdate1)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
      assertTrue(dates.size() == results.length);

      // Generate the iCalendar and do it all over...
      String icalOut = e.toICalendar();
      String[] icalLines = icalOut.split("[\r\n]+");
      List<String> icalLinesV = new ArrayList<String>();
      for (int i = 0; i < icalLines.length; i++) {
        icalLinesV.add(icalLines[i]);
      }
      e = new Event(parser, 0, lines);
      assertNotNull(e);
      dates = e.getRecurranceDates();
      for (int i = 0; i < dates.size() && i < results.length; i++) {
        Date d = dates.get(i);
        String ymd = Utils.DateToYYYYMMDD(d);
        System.out.println("testWeeklyWithRdate1)Date#" + i + ": " + Utils.DateToYYYYMMDD(d));
        assertEquals(results[i], ymd);
      }
      assertTrue(dates.size() == results.length);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
      e.printStackTrace();
    }
  }

  // DTSTART:20070501T090000
  // RRULE:FREQ=DAILY;UNTIL=20070506T000000Z
  @Test
  public void testGoogleRrule() {
    String[] results = {"20070501", "20070502", "20070503", "20070504", "20070505"};
    // String str = "RRULE:FREQ=DAILY;UNTIL=20070506T000000Z";
    try {
      com.google.ical.values.RRule rrule = new com.google.ical.values.RRule();
      TimeZone tz = TimeZone.getDefault();
      String tzid = tz.getID();
      com.google.ical.values.DateValue dtStart = null;
      dtStart = new DateTimeValueImpl(2007, 5, 1, 9, 0, 0);
      rrule.setFreq(Frequency.DAILY);
      rrule.setName("RRULE");
      rrule.setInterval(1);
      rrule.setUntil(new DateValueImpl(2007, 5, 6));
      java.util.TimeZone timezone = null;
      if (tzid != null)
        timezone = java.util.TimeZone.getTimeZone(tzid);
      RecurrenceIterator iter =
          RecurrenceIteratorFactory.createRecurrenceIterator(rrule, dtStart, timezone);
      int i = 0;
      while (iter.hasNext() && i < 10000) {
        com.google.ical.values.DateValue d = iter.next();
        Date date = new Date("XXX", d.year(), d.month(), d.day());
        String ymd = Utils.DateToYYYYMMDD(date);
        System.out.println("testDaily1)Date#" + i + ": " + Utils.DateToYYYYMMDD(date));
        assertEquals(results[i], ymd);
        i++;
      }
      assertEquals(5, i);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed: " + e.toString());
    }
  }

}
