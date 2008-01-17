package us.k5n.ical;

import java.util.TimeZone;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.ical.iter.RecurrenceIterator;
import com.google.ical.iter.RecurrenceIteratorFactory;
import com.google.ical.values.DateTimeValueImpl;
import com.google.ical.values.DateValueImpl;
import com.google.ical.values.Frequency;

/**
 * Test cases for Rrule.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class RruleTest extends TestCase implements Constants {

	public void setUp () {
	}

	public void testSimple () {
		String str = "RRULE:FREQ=MONTHLY;BYMONTH=10;BYDAY=2MO";
		try {
			Rrule x = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", x );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public void testYearly1 () {
		String[] results = { "19970310", "19990110", "19990210", "19990310",
		    "20010110", "20010210", "20010310", "20030110", "20030210", "20030310" };
		String str = "RRULE:FREQ=YEARLY;INTERVAL=2;COUNT=10;BYMONTH=1,2,3";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART;TZID=" + tzid + ":19970310T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size (); i++ ) {
				Date d = (Date) dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testYearly1)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date, got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Every 3rd year on the 1st, 100th and 200th day for 10 occurrences:
	public void testYearly2 () {
		String[] results = { "19970101", "19970410", "19970719", "20000101",
		    "20000410", "20000719", "20030101", "20030410", "20030719", "20060101" };
		String str = "RRULE:FREQ=YEARLY;INTERVAL=3;COUNT=10;BYYEARDAY=1,100,200";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART;TZID=" + tzid + ":19970101T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size (); i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testYearly2)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Every 20th Monday of the year, forever
	public void testYearly3 () {
		String[] results = { "19970519", "19980518", "19990517" };
		String str = "RRULE:FREQ=YEARLY;BYDAY=20MO";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART;TZID=" + tzid + ":19970519T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testYearly3)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Every Thursday in March, forever
	public void testYearly4 () {
		String[] results = { "19970313", "19970320", "19970327", "19980305",
		    "19980312", "19980319", "19980326", "19990304", "19990311", "19990318",
		    "19990325" };
		String str = "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=TH";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART;TZID=" + tzid + ":19970313T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testYearly4)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// The first Saturday that follows the first Sunday of the month, forever
	public void testMonthly1 () {
		String[] results = { "19970913", "19971011", "19971108", "19971213",
		    "19980110", "19980207", "19980307", "19980411", "19980509", "19980613" };
		String str = "RRULE:FREQ=MONTHLY;BYDAY=SA;BYMONTHDAY=7,8,9,10,11,12,13";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART;TZID=" + tzid + ":19970913T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testMonthly1)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Every four years, the first Tuesday after a Monday in November,
	// forever (U.S. Presidential Election day)
	public void testMonthly2 () {
		String[] results = { "19961105", "20001107", "20041102" };
		String str = "RRULE:FREQ=YEARLY;INTERVAL=4;BYMONTH=11;BYDAY=TU;BYMONTHDAY=2,3,4,5,6,7,8";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART:19961105T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testMonthly2)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// The 2nd to last weekday of the month
	public void testMonthly3 () {
		String[] results = { "19970929", "19971030", "19971127", "19971230",
		    "19980129", "19980226", "19980330" };
		String str = "RRULE:FREQ=MONTHLY;BYDAY=MO,TU,WE,TH,FR;BYSETPOS=-2";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART:19970929T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testMonthly3)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Monthly on the second to last Monday of the month for 6 months
	public void testMonthly4 () {
		String[] results = { "19970922", "19971020", "19971117", "19971222",
		    "19980119", "19980216" };
		String str = "RRULE:FREQ=MONTHLY;COUNT=6;BYDAY=-2MO";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART:19970922T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testMonthly4)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Weekly for 10 occurrences
	public void testWeekly1 () {
		String[] results = { "19970902", "19970909", "19970916", "19970923",
		    "19970930", "19971007", "19971014", "19971021", "19971028", "19971104" };
		String str = "RRULE:FREQ=WEEKLY;COUNT=10";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART:19970902T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testWeekly1)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Every other week with no end
	public void testWeekly2 () {
		String[] results = { /* "20060915", */"20060929", "20061013", "20061027",
		    "20061110", "20061124" };
		String str = "RRULE:FREQ=WEEKLY;INTERVAL=2;BYDAY=FR";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART;VALUE=DATE:20060915" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testWeekly2)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Daily for until May 5
	public void testDaily1 () {
		String[] results = { "20070501", "20070502", "20070503", "20070504",
		    "20070505" };
		String str = "RRULE:FREQ=DAILY;UNTIL=20070506T000000Z";
		try {
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			Date dtStart = new Date ( "DTSTART:20070501T090000" );
			Rrule rrule = new Rrule ( str, PARSE_STRICT );
			assertNotNull ( "Null RRULE", rrule );
			Vector<Date> dates = rrule.generateRecurrances ( dtStart, tzid );
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testDaily1)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
			assertTrue ( "More than 5 events returned: " + dates.size (), dates
			    .size () == 5 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Daily for until May 10, witch EXDATE of May 5
	public void testDailyWithExdate1 () {
		String[] results = { "20070502", "20070503", "20070504", "20070506",
		    "20070507", "20070508", "20070509", "20070510" };
		Vector<String> lines = new Vector<String> ();
		lines.addElement ( "BEGIN:VEVENT" );
		lines.addElement ( "UID: xxx@1234" );
		lines.addElement ( "SUMMARY:Test1" );
		lines.addElement ( "DTSTART:20070501" );
		lines.addElement ( "RRULE:FREQ=DAILY;UNTIL=20070511T000000Z" );
		lines.addElement ( "EXDATE:20070505" );
		lines.addElement ( "END:VEVENT" );

		try {
			ICalendarParser parser = new ICalendarParser ( Constants.PARSE_STRICT );
			Event e = new Event ( parser, 0, lines );
			assertNotNull ( "Null Event", e );
			Vector<Date> dates = e.getRecurranceDates ();
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testDailyWithExdate1)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
			assertTrue ( "More than " + results.length + " events returned: "
			    + dates.size (), dates.size () == results.length );

			// Generate the iCalendar and do it all over...
			String icalOut = e.toICalendar ();
			String[] icalLines = icalOut.split ( "[\r\n]+" );
			Vector<String> icalLinesV = new Vector<String> ();
			for ( int i = 0; i < icalLines.length; i++ ) {
				icalLinesV.addElement ( icalLines[i] );
			}
			e = new Event ( parser, 0, lines );
			assertNotNull ( "Null Event", e );
			dates = e.getRecurranceDates ();
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testDailyWithExdate1)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
			assertTrue ( "More than " + results.length + " events returned: "
			    + dates.size (), dates.size () == results.length );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Daily for until May 10, witch EXDATE of May 5, May 7
	public void testDailyWithExdate2 () {
		String[] results = { "20070502", "20070503", "20070504", "20070506",
		    "20070508", "20070509", "20070510" };
		Vector<String> lines = new Vector<String> ();
		lines.addElement ( "BEGIN:VEVENT" );
		lines.addElement ( "UID: xxx@1234" );
		lines.addElement ( "SUMMARY:Test1" );
		lines.addElement ( "DTSTART:20070501" );
		lines.addElement ( "RRULE:FREQ=DAILY;UNTIL=20070511T000000Z" );
		lines.addElement ( "EXDATE:20070505,20070507" );
		lines.addElement ( "END:VEVENT" );

		try {
			ICalendarParser parser = new ICalendarParser ( Constants.PARSE_STRICT );
			Event e = new Event ( parser, 0, lines );
			assertNotNull ( "Null Event", e );
			Vector<Date> dates = e.getRecurranceDates ();
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testDailyWithExdate2)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
			assertTrue ( "More than " + results.length + " events returned: "
			    + dates.size (), dates.size () == results.length );

			// Generate the iCalendar and do it all over...
			String icalOut = e.toICalendar ();
			String[] icalLines = icalOut.split ( "[\r\n]+" );
			Vector<String> icalLinesV = new Vector<String> ();
			for ( int i = 0; i < icalLines.length; i++ ) {
				icalLinesV.addElement ( icalLines[i] );
			}
			e = new Event ( parser, 0, lines );
			assertNotNull ( "Null Event", e );
			dates = e.getRecurranceDates ();
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testDailyWithExdate2)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
			assertTrue ( "More than " + results.length + " events returned: "
			    + dates.size (), dates.size () == results.length );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	// Test the RDATE support. Create a weekly repeating event. Add two extra
	// dates in the middle of the series.
	public void testWeeklyWithRdate1 () {
		String[] results = { "20080114", "20080121", "20080122", "20080124",
		    "20080128", "20080204", "20080211", "20080218" };
		Vector<String> lines = new Vector<String> ();
		lines.addElement ( "BEGIN:VEVENT" );
		lines.addElement ( "UID: xxx@1234" );
		lines.addElement ( "SUMMARY:Test1" );
		lines.addElement ( "DTSTART:20080107" ); // Mon, 1/7/2008
		lines.addElement ( "RRULE:FREQ=WEEKLY;UNTIL=20080225T000000Z" );
		lines.addElement ( "RDATE:20080122,20080124" );
		lines.addElement ( "END:VEVENT" );

		try {
			ICalendarParser parser = new ICalendarParser ( Constants.PARSE_STRICT );
			Event e = new Event ( parser, 0, lines );
			assertNotNull ( "Null Event", e );
			Vector<Date> dates = e.getRecurranceDates ();
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testWeeklyWithRdate1)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
			assertTrue ( "More than " + results.length + " events returned: "
			    + dates.size (), dates.size () == results.length );

			// Generate the iCalendar and do it all over...
			String icalOut = e.toICalendar ();
			String[] icalLines = icalOut.split ( "[\r\n]+" );
			Vector<String> icalLinesV = new Vector<String> ();
			for ( int i = 0; i < icalLines.length; i++ ) {
				icalLinesV.addElement ( icalLines[i] );
			}
			e = new Event ( parser, 0, lines );
			assertNotNull ( "Null Event", e );
			dates = e.getRecurranceDates ();
			for ( int i = 0; i < dates.size () && i < results.length; i++ ) {
				Date d = dates.elementAt ( i );
				String ymd = Utils.DateToYYYYMMDD ( d );
				System.out.println ( "testWeeklyWithRdate1)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( d ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
			}
			assertTrue ( "More than " + results.length + " events returned: "
			    + dates.size (), dates.size () == results.length );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
			e.printStackTrace ();
		}
	}

	// DTSTART:20070501T090000
	// RRULE:FREQ=DAILY;UNTIL=20070506T000000Z
	public void testGoogleRrule () {
		String[] results = { "20070501", "20070502", "20070503", "20070504",
		    "20070505" };
		// String str = "RRULE:FREQ=DAILY;UNTIL=20070506T000000Z";
		try {
			com.google.ical.values.RRule rrule = new com.google.ical.values.RRule ();
			TimeZone tz = TimeZone.getDefault ();
			String tzid = tz.getID ();
			com.google.ical.values.DateValue dtStart = null;
			dtStart = new DateTimeValueImpl ( 2007, 5, 1, 9, 0, 0 );
			rrule.setFreq ( Frequency.DAILY );
			rrule.setName ( "RRULE" );
			rrule.setInterval ( 1 );
			rrule.setUntil ( new DateValueImpl ( 2007, 5, 6 ) );
			java.util.TimeZone timezone = null;
			if ( tzid != null )
				timezone = java.util.TimeZone.getTimeZone ( tzid );
			RecurrenceIterator iter = RecurrenceIteratorFactory
			    .createRecurrenceIterator ( rrule, dtStart, timezone );
			int i = 0;
			while ( iter.hasNext () && i < 10000 ) {
				com.google.ical.values.DateValue d = iter.next ();
				Date date = new Date ( "XXX", d.year (), d.month (), d.day () );
				String ymd = Utils.DateToYYYYMMDD ( date );
				System.out.println ( "testDaily1)Date#" + i + ": "
				    + Utils.DateToYYYYMMDD ( date ) );
				assertTrue ( "Unexpected date#" + i + ", got " + ymd + " instead of "
				    + results[i], ymd.equals ( results[i] ) );
				i++;
			}
			assertTrue ( "More than 5 events returned: " + i, i == 5 );
		} catch ( Exception e ) {
			e.printStackTrace ();
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( RruleTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( RruleTest.class );
	}

}
