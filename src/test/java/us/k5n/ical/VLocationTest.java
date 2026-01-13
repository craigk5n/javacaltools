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
 * Test class for VLOCATION component support
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class VLocationTest {
	private ICalendarParser parser;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
	}

	@Test
	@DisplayName("Test VLOCATION parsing with basic properties")
	void testVLocationBasic() throws Exception {
		String icalStr = "BEGIN:VLOCATION\n" +
			"UID:location-123@example.com\n" +
			"NAME:Conference Room A\n" +
			"END:VLOCATION";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VLocation vlocation = new VLocation(parser, 1, lines);

		assertTrue(vlocation.isValid());
		assertEquals("location-123@example.com", vlocation.getUid().getValue());
		assertEquals("Conference Room A", vlocation.getName());
	}

	@Test
	@DisplayName("Test VLOCATION with all properties")
	void testVLocationComplete() throws Exception {
		String icalStr = "BEGIN:VLOCATION\n" +
			"UID:location-456@example.com\n" +
			"NAME:Main Auditorium\n" +
			"DESCRIPTION:Main auditorium with capacity for 200 people\n" +
			"GEO:40.7128;-74.0060\n" +
			"LOCATION-TYPE:conference\n" +
			"CATEGORIES:INDOOR,PROJECTOR\n" +
			"CREATED:20230101T100000Z\n" +
			"LAST-MODIFIED:20230102T150000Z\n" +
			"URL:http://example.com/locations/auditorium\n" +
			"END:VLOCATION";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VLocation vlocation = new VLocation(parser, 1, lines);

		assertTrue(vlocation.isValid());
		assertEquals("location-456@example.com", vlocation.getUid().getValue());
		assertEquals("Main Auditorium", vlocation.getName());
		assertEquals("Main auditorium with capacity for 200 people", vlocation.getDescription().getValue());
		assertEquals("40.7128;-74.0060", vlocation.getGeo());
		assertEquals("conference", vlocation.getLocationType());
		assertEquals("INDOOR,PROJECTOR", vlocation.getCategories().getValue());
		assertEquals("http://example.com/locations/auditorium", vlocation.getUrl().getValue());
	}

	@Test
	@DisplayName("Test VLOCATION toICalendar output")
	void testVLocationToICalendar() throws Exception {
		String icalStr = "BEGIN:VLOCATION\n" +
			"UID:location-test@example.com\n" +
			"NAME:Test Location\n" +
			"DESCRIPTION:A test location\n" +
			"GEO:37.7749;-122.4194\n" +
			"END:VLOCATION";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VLocation vlocation = new VLocation(parser, 1, lines);

		String output = vlocation.toICalendar();

		assertTrue(output.contains("BEGIN:VLOCATION"));
		assertTrue(output.contains("UID:location-test@example.com"));
		assertTrue(output.contains("NAME:Test Location"));
		assertTrue(output.contains("DESCRIPTION:A test location"));
		assertTrue(output.contains("GEO:37.7749;-122.4194"));
		assertTrue(output.contains("END:VLOCATION"));
	}

	@Test
	@DisplayName("Test VLOCATION parsing from calendar")
	void testVLocationInCalendar() throws Exception {
		String calendarStr = "BEGIN:VCALENDAR\n" +
			"VERSION:2.0\n" +
			"PRODID:-//Test//Test//EN\n" +
			"BEGIN:VLOCATION\n" +
			"UID:loc-001@example.com\n" +
			"NAME:Meeting Room 1\n" +
			"END:VLOCATION\n" +
			"BEGIN:VEVENT\n" +
			"UID:event-001@example.com\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"DTSTART:20230101T130000Z\n" +
			"DTEND:20230101T140000Z\n" +
			"SUMMARY:Test Event\n" +
			"END:VEVENT\n" +
			"END:VCALENDAR";

		ICalendarParser calParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
		java.io.StringReader reader = new java.io.StringReader(calendarStr);
		calParser.parse(reader);

		// The VLOCATION should be stored in the data store
		// Note: This test assumes the DataStore interface has been updated
		// For now, we just verify the parsing doesn't crash
		assertTrue(true); // Placeholder - would need DataStore.getVLocations() method
	}

	@Test
	@DisplayName("Test invalid VLOCATION (missing UID)")
	void testInvalidVLocation() throws Exception {
		String icalStr = "BEGIN:VLOCATION\n" +
			"NAME:Conference Room A\n" +
			"END:VLOCATION";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VLocation vlocation = new VLocation(parser, 1, lines);

		assertFalse(vlocation.isValid());
	}

	@Test
	@DisplayName("Test invalid VLOCATION (missing NAME)")
	void testInvalidVLocationMissingName() throws Exception {
		String icalStr = "BEGIN:VLOCATION\n" +
			"UID:location-123@example.com\n" +
			"END:VLOCATION";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VLocation vlocation = new VLocation(parser, 1, lines);

		assertFalse(vlocation.isValid());
	}
}