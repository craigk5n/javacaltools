/*
 * Copyright (C) 2005-2006 Craig Knudsen and other authors
 * (see AUTHORS for a complete list)
 *
 * JavaCalTools is free software; you can redistribute it and/or modify
 * it under terms of GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * A copy of the GNU Lesser General Public License is included in the Wine
 * distribution in the file COPYING.LIB. If you did not receive this copy,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 */

package us.k5n.ical;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for VFREEBUSY component parsing and functionality
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class FreebusyTest {
	private ICalendarParser parser;
	private List<String> textLines;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
		textLines = new ArrayList<String>();
	}

	@Test
	@DisplayName("Test basic VFREEBUSY with DTSTART/DTEND")
	void testBasicFreebusy() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTSTART:20230101T090000Z");
		textLines.add("DTEND:20230101T100000Z");
		textLines.add("FREEBUSY:20230101T090000Z/20230101T093000Z");
		textLines.add("FREEBUSY:20230101T094500Z/20230101T100000Z");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		
		assertTrue(fb.isValid());
		assertNotNull(fb.getStartDate());
		assertNotNull(fb.getEndDate());
		assertEquals(2, fb.getBusyPeriods().size());
	}

	@Test
	@DisplayName("Test VFREEBUSY with DTSTART/DURATION")
	void testFreebusyWithDuration() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTSTART:20230101T090000Z");
		textLines.add("DURATION:PT1H");
		textLines.add("FREEBUSY:20230101T090000Z/PT30M");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		
		assertTrue(fb.isValid());
		assertNotNull(fb.getStartDate());
		assertNotNull(fb.getDuration());
		assertNull(fb.getEndDate());
	}

	@Test
	@DisplayName("Test VFREEBUSY with optional properties")
	void testFreebusyWithOptionalProperties() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTSTAMP:20230101T120000Z");
		textLines.add("UID:12345@freebusy.example.com");
		textLines.add("DTSTART:20230101T090000Z");
		textLines.add("DTEND:20230101T100000Z");
		textLines.add("SUMMARY:Busy time");
		textLines.add("CONTACT:John Doe <john@example.com>");
		textLines.add("URL:http://example.com/freebusy");
		textLines.add("COMMENT:Meeting time");
		textLines.add("FREEBUSY:20230101T090000Z/20230101T100000Z");
		textLines.add("ORGANIZER:mailto:organizer@example.com");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		
		assertTrue(fb.isValid());
		assertEquals("Busy time", fb.getSummary().value);
		assertEquals("John Doe <john@example.com>", fb.getContact().getContact());
		assertEquals(1, fb.getBusyPeriods().size());
		assertNotNull(fb.getURL());
		assertNotNull(fb.getComment());
		assertNotNull(fb.getOrganizer());
	}

	@Test
	@DisplayName("Test VFREEBUSY toICalendar output")
	void testFreebusyToICalendar() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTSTAMP:20230101T120000Z");
		textLines.add("UID:output-test@freebusy.example.com");
		textLines.add("DTSTART:20230101T090000Z");
		textLines.add("DTEND:20230101T100000Z");
		textLines.add("SUMMARY:Busy time");
		textLines.add("FREEBUSY:20230101T090000Z/20230101T093000Z");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		String output = fb.toICalendar();
		
		assertTrue(output.contains("BEGIN:VFREEBUSY"));
		assertTrue(output.contains("SUMMARY:Busy time"));
		assertTrue(output.contains("FREEBUSY:"));
		assertTrue(output.contains("END:VFREEBUSY"));
		assertTrue(output.contains(Constants.CRLF));
	}

	@Test
	@DisplayName("Test FreebusyPeriod parsing with start/end")
	void testFreebusyPeriodStartEnd() throws Exception {
		FreebusyPeriod period = new FreebusyPeriod("FREEBUSY:20230101T090000Z/20230101T100000Z");
		
		assertTrue(period.isValid());
		assertNotNull(period.getStartDate());
		assertNotNull(period.getEndDate());
		assertNull(period.getDuration());
	}

	@Test
	@DisplayName("Test FreebusyPeriod parsing with start/duration")
	void testFreebusyPeriodStartDuration() throws Exception {
		FreebusyPeriod period = new FreebusyPeriod("FREEBUSY:20230101T090000Z/PT1H30M");
		
		assertTrue(period.isValid());
		assertNotNull(period.getStartDate());
		assertNotNull(period.getDuration());
		assertNull(period.getEndDate());
	}

	@Test
	@DisplayName("Test FreebusyPeriod toICalendar")
	void testFreebusyPeriodToICalendar() throws Exception {
		FreebusyPeriod period = new FreebusyPeriod("FREEBUSY:20230101T090000Z/PT1H30M");
		String output = period.toICalendar();
		
		assertTrue(output.contains("FREEBUSY:"));
		assertTrue(output.contains("/PT1H30M"));
	}

	@Test
	@DisplayName("Test VFREEBUSY invalid - missing DTSTART")
	void testInvalidFreebusyMissingDtstart() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTEND:20230101T100000Z");
		textLines.add("FREEBUSY:20230101T090000Z/20230101T100000Z");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		
		assertFalse(fb.isValid());
	}

	@Test
	@DisplayName("Test VFREEBUSY invalid - missing both DTEND and DURATION")
	void testInvalidFreebusyMissingDtendAndDuration() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTSTART:20230101T090000Z");
		textLines.add("FREEBUSY:20230101T090000Z/20230101T100000Z");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		
		assertFalse(fb.isValid());
	}

	@Test
	@DisplayName("Test VFREEBUSY user data")
	void testFreebusyUserData() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTSTAMP:20230101T120000Z");
		textLines.add("UID:userdata-test@freebusy.example.com");
		textLines.add("DTSTART:20230101T090000Z");
		textLines.add("DTEND:20230101T100000Z");
		textLines.add("SUMMARY:Test user data");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		
		Object customData = "Custom user object";
		fb.setUserData(customData);
		
		assertEquals(customData, fb.getUserData());
	}

	@Test
	@DisplayName("Test VFREEBUSY toString")
	void testFreebusyToString() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTSTAMP:20230101T120000Z");
		textLines.add("UID:tostring-test@freebusy.example.com");
		textLines.add("DTSTART:20230101T090000Z");
		textLines.add("DTEND:20230101T100000Z");
		textLines.add("SUMMARY:Test toString");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		String stringRep = fb.toString();
		
		assertTrue(stringRep.contains("tostring-test@freebusy.example.com"));
	}

	@Test
	@DisplayName("Test VFREEBUSY with multiple busy periods")
	void testFreebusyMultiplePeriods() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTSTART:20230101T090000Z");
		textLines.add("DTEND:20230101T170000Z");
		textLines.add("FREEBUSY:20230101T090000Z/20230101T100000Z,20230101T110000Z/20230101T120000Z,20230101T130000Z/20230101T140000Z");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		
		assertTrue(fb.isValid());
		assertEquals(3, fb.getBusyPeriods().size());
	}

	@Test
	@DisplayName("Test VFREEBUSY with SEQUENCE")
	void testFreebusyWithSequence() throws Exception {
		textLines.add("BEGIN:VFREEBUSY");
		textLines.add("DTSTAMP:20230101T120000Z");
		textLines.add("UID:seq-test@freebusy.example.com");
		textLines.add("SEQUENCE:2");
		textLines.add("DTSTART:20230101T090000Z");
		textLines.add("DTEND:20230101T100000Z");
		textLines.add("END:VFREEBUSY");
		
		Freebusy fb = new Freebusy(parser, 1, textLines);
		
		assertTrue(fb.isValid());
		assertEquals(2, fb.getSequence().getNum());
	}

	@Test
	@DisplayName("Test Contact class")
	void testContact() throws Exception {
		Contact contact = new Contact("CONTACT:John Doe <john@example.com>");
		
		assertEquals("John Doe <john@example.com>", contact.getContact());
	}

	@Test
	@DisplayName("Test FreebusyPeriod invalid format")
	void testFreebusyPeriodInvalidFormat() throws Exception {
		assertThrows(ParseException.class, () -> {
			new FreebusyPeriod("INVALID:20230101T090000Z");
		});
	}
}
