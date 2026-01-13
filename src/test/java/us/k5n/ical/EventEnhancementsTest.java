/*
 * Copyright (C) 2005-2006 Craig Knudsen and other authors
 * (see AUTHORS for a complete list)
 *
 * JavaCalTools is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for VEVENT enhancements - new properties added in ICAL-05
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class EventEnhancementsTest {
	private ICalendarParser parser;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
	}

	private Event createEventFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new Event(parser, 1, lines);
	}

	@Test
	@DisplayName("Test EVENT with ORGANIZER property")
	void testEventWithOrganizer() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:org-test@example.com\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"SUMMARY:Meeting with organizer\n" +
			"ORGANIZER:mailto:organizer@example.com\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		assertTrue(event.isValid());
		assertNotNull(event.getOrganizer());
		assertEquals("mailto:organizer@example.com", event.getOrganizer().getOrganizer());
	}

	@Test
	@DisplayName("Test EVENT with CONTACT property")
	void testEventWithContact() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:contact-test@example.com\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"SUMMARY:Meeting with contact\n" +
			"CONTACT:John Doe <john@example.com>\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		assertTrue(event.isValid());
		assertNotNull(event.getContact());
		assertEquals("John Doe <john@example.com>", event.getContact().getContact());
	}

	@Test
	@DisplayName("Test EVENT with PRIORITY property")
	void testEventWithPriority() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:priority-test@example.com\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"SUMMARY:High priority meeting\n" +
			"PRIORITY:1\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		assertTrue(event.isValid());
		assertNotNull(event.getPriority());
		assertEquals(Integer.valueOf(1), event.getPriority());
	}

	@Test
	@DisplayName("Test EVENT with GEO property")
	void testEventWithGeo() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:geo-test@example.com\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"SUMMARY:Meeting at location\n" +
			"GEO:37.7765;-122.4027\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		assertTrue(event.isValid());
		assertNotNull(event.getGeo());
		assertEquals("37.7765;-122.4027", event.getGeo());
	}

	@Test
	@DisplayName("Test EVENT with REQUEST-STATUS property")
	void testEventWithRequestStatus() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:request-test@example.com\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"SUMMARY:Request needed\n" +
			"REQUEST-STATUS:NEEDS-ACTION\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		assertTrue(event.isValid());
		assertEquals(4, event.getRequestStatus()); // STATUS_NEEDS_ACTION
	}

	@Test
	@DisplayName("Test EVENT with multiple new properties")
	void testEventWithMultipleNewProperties() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:multi-test@example.com\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"SUMMARY:Complete meeting\n" +
			"ORGANIZER:mailto:boss@example.com\n" +
			"CONTACT:Secretary <secretary@example.com>\n" +
			"PRIORITY:2\n" +
			"GEO:40.7128;-74.0060\n" +
			"REQUEST-STATUS:ACCEPTED\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		assertTrue(event.isValid());
		
		// Test all new properties
		assertNotNull(event.getOrganizer());
		assertEquals("mailto:boss@example.com", event.getOrganizer().getOrganizer());
		
		assertNotNull(event.getContact());
		assertEquals("Secretary <secretary@example.com>", event.getContact().getContact());
		
		assertEquals(Integer.valueOf(2), event.getPriority());
		assertEquals("40.7128;-74.0060", event.getGeo());
		assertEquals(3, event.getRequestStatus()); // STATUS_IN_PROCESS for ACCEPTED
	}
}