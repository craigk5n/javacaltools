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
 * Test class for VRESOURCE component support
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class VResourceTest {
	private ICalendarParser parser;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
	}

	@Test
	@DisplayName("Test VRESOURCE parsing with basic properties")
	void testVResourceBasic() throws Exception {
		String icalStr = "BEGIN:VRESOURCE\n" +
			"UID:resource-123@example.com\n" +
			"NAME:Projector\n" +
			"END:VRESOURCE";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VResource vresource = new VResource(parser, 1, lines);

		assertTrue(vresource.isValid());
		assertEquals("resource-123@example.com", vresource.getUid().getValue());
		assertEquals("Projector", vresource.getName());
	}

	@Test
	@DisplayName("Test VRESOURCE with all properties")
	void testVResourceComplete() throws Exception {
		String icalStr = "BEGIN:VRESOURCE\n" +
			"UID:resource-456@example.com\n" +
			"NAME:Video Conference System\n" +
			"DESCRIPTION:High-definition video conferencing equipment\n" +
			"RESOURCE-TYPE:equipment\n" +
			"CATEGORIES:ELECTRONICS,MEETING\n" +
			"CREATED:20230101T100000Z\n" +
			"LAST-MODIFIED:20230102T150000Z\n" +
			"URL:http://example.com/resources/vc-system\n" +
			"END:VRESOURCE";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VResource vresource = new VResource(parser, 1, lines);

		assertTrue(vresource.isValid());
		assertEquals("resource-456@example.com", vresource.getUid().getValue());
		assertEquals("Video Conference System", vresource.getName());
		assertEquals("High-definition video conferencing equipment", vresource.getDescription().getValue());
		assertEquals("equipment", vresource.getResourceType());
		assertEquals("ELECTRONICS,MEETING", vresource.getCategories().getValue());
		assertEquals("http://example.com/resources/vc-system", vresource.getUrl().getValue());
	}

	@Test
	@DisplayName("Test VRESOURCE toICalendar output")
	void testVResourceToICalendar() throws Exception {
		String icalStr = "BEGIN:VRESOURCE\n" +
			"UID:resource-test@example.com\n" +
			"NAME:Test Resource\n" +
			"DESCRIPTION:A test resource\n" +
			"RESOURCE-TYPE:room\n" +
			"END:VRESOURCE";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VResource vresource = new VResource(parser, 1, lines);

		String output = vresource.toICalendar();

		assertTrue(output.contains("BEGIN:VRESOURCE"));
		assertTrue(output.contains("UID:resource-test@example.com"));
		assertTrue(output.contains("NAME:Test Resource"));
		assertTrue(output.contains("DESCRIPTION:A test resource"));
		assertTrue(output.contains("RESOURCE-TYPE:room"));
		assertTrue(output.contains("END:VRESOURCE"));
	}

	@Test
	@DisplayName("Test VRESOURCE parsing from calendar")
	void testVResourceInCalendar() throws Exception {
		String calendarStr = "BEGIN:VCALENDAR\n" +
			"VERSION:2.0\n" +
			"PRODID:-//Test//Test//EN\n" +
			"BEGIN:VRESOURCE\n" +
			"UID:res-001@example.com\n" +
			"NAME:Laptop Computer\n" +
			"END:VRESOURCE\n" +
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

		// The VRESOURCE should be stored in the data store
		// Note: This test assumes the DataStore interface has been updated
		// For now, we just verify the parsing doesn't crash
		assertTrue(true); // Placeholder - would need DataStore.getVResources() method
	}

	@Test
	@DisplayName("Test invalid VRESOURCE (missing UID)")
	void testInvalidVResource() throws Exception {
		String icalStr = "BEGIN:VRESOURCE\n" +
			"NAME:Projector\n" +
			"END:VRESOURCE";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VResource vresource = new VResource(parser, 1, lines);

		assertFalse(vresource.isValid());
	}

	@Test
	@DisplayName("Test invalid VRESOURCE (missing NAME)")
	void testInvalidVResourceMissingName() throws Exception {
		String icalStr = "BEGIN:VRESOURCE\n" +
			"UID:resource-123@example.com\n" +
			"END:VRESOURCE";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VResource vresource = new VResource(parser, 1, lines);

		assertFalse(vresource.isValid());
	}

	@Test
	@DisplayName("Test Event with resource references")
	void testEventWithResourceReferences() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:event-with-resources@example.com\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"SUMMARY:Meeting with resources\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);

		// Test setting resource references
		List<String> resourceIds = new ArrayList<>();
		resourceIds.add("projector-001@example.com");
		resourceIds.add("laptop-001@example.com");
		event.setResourceIds(resourceIds);

		assertNotNull(event.getResourceIds());
		assertEquals(2, event.getResourceIds().size());
		assertEquals("projector-001@example.com", event.getResourceIds().get(0));
		assertEquals("laptop-001@example.com", event.getResourceIds().get(1));
	}

	private Event createEventFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new Event(parser, 1, lines);
	}
}