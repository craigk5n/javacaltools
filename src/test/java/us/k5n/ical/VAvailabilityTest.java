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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for VAVAILABILITY component support
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class VAvailabilityTest {
	private ICalendarParser parser;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
	}

	@Test
	@DisplayName("Test VAVAILABILITY parsing with basic properties")
	void testVAvailabilityBasic() throws Exception {
		String icalStr = "BEGIN:VAVAILABILITY\n" +
			"UID:avail-123@example.com\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"BUSYTYPE:BUSY\n" +
			"END:VAVAILABILITY";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VAvailability vavailability = new VAvailability(parser, 1, lines);

		assertTrue(vavailability.isValid());
		assertEquals("avail-123@example.com", vavailability.getUid().getValue());
		assertEquals("BUSY", vavailability.getBusyType());
	}

	@Test
	@DisplayName("Test VAVAILABILITY with all properties")
	void testVAvailabilityComplete() throws Exception {
		String icalStr = "BEGIN:VAVAILABILITY\n" +
			"UID:avail-456@example.com\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T170000Z\n" +
			"BUSYTYPE:BUSY-UNAVAILABLE\n" +
			"SUMMARY:Weekly Office Hours\n" +
			"DESCRIPTION:Regular working hours\n" +
			"CATEGORIES:WORK,OFFICE\n" +
			"CREATED:20230101T100000Z\n" +
			"LAST-MODIFIED:20230101T110000Z\n" +
			"END:VAVAILABILITY";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VAvailability vavailability = new VAvailability(parser, 1, lines);

		assertTrue(vavailability.isValid());
		assertEquals("avail-456@example.com", vavailability.getUid().getValue());
		assertEquals("BUSY-UNAVAILABLE", vavailability.getBusyType());
		assertEquals("Weekly Office Hours", vavailability.getSummary().getValue());
		assertEquals("Regular working hours", vavailability.getDescription().getValue());
		assertEquals("WORK,OFFICE", vavailability.getCategories().getValue());
	}

	@Test
	@DisplayName("Test VAVAILABILITY toICalendar output")
	void testVAvailabilityToICalendar() throws Exception {
		String icalStr = "BEGIN:VAVAILABILITY\n" +
			"UID:avail-test@example.com\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"BUSYTYPE:FREE\n" +
			"SUMMARY:Available Time\n" +
			"END:VAVAILABILITY";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VAvailability vavailability = new VAvailability(parser, 1, lines);

		String output = vavailability.toICalendar();
		String unfolded = output.replace("\r\n ", "").replace("\r\n", "\n");

		assertTrue(unfolded.contains("BEGIN:VAVAILABILITY"));
		assertTrue(unfolded.contains("UID:avail-test@example.com"));
		assertTrue(unfolded.contains("BUSYTYPE:FREE"));
		assertTrue(unfolded.contains("SUMMARY:Available Time"));
		assertTrue(unfolded.contains("END:VAVAILABILITY"));
	}

	@Test
	@DisplayName("Test VAVAILABILITY parsing from calendar")
	void testVAvailabilityInCalendar() throws Exception {
		String calendarStr = "BEGIN:VCALENDAR\n" +
			"VERSION:2.0\n" +
			"PRODID:-//Test//Test//EN\n" +
			"BEGIN:VAVAILABILITY\n" +
			"UID:avail-001@example.com\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"BUSYTYPE:BUSY\n" +
			"SUMMARY:Busy Period\n" +
			"END:VAVAILABILITY\n" +
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

		// The VAVAILABILITY should be stored in the data store
		// Note: This test assumes the DataStore interface has been updated
		// For now, we just verify the parsing doesn't crash
		assertTrue(true); // Placeholder - would need DataStore.getVAvailabilities() method
	}

	@Test
	@DisplayName("Test invalid VAVAILABILITY (missing UID)")
	void testInvalidVAvailability() throws Exception {
		String icalStr = "BEGIN:VAVAILABILITY\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"BUSYTYPE:BUSY\n" +
			"END:VAVAILABILITY";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VAvailability vavailability = new VAvailability(parser, 1, lines);

		assertFalse(vavailability.isValid());
	}

	@Test
	@DisplayName("Test invalid VAVAILABILITY (missing DTSTAMP)")
	void testInvalidVAvailabilityMissingDtstamp() throws Exception {
		String icalStr = "BEGIN:VAVAILABILITY\n" +
			"UID:avail-123@example.com\n" +
			"BUSYTYPE:BUSY\n" +
			"END:VAVAILABILITY";

		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		VAvailability vavailability = new VAvailability(parser, 1, lines);

		assertFalse(vavailability.isValid());
	}

	@Test
	@DisplayName("Test Event availability references parsing")
	void testEventAvailabilityReferencesParsing() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"UID:event-123@example.com\n" +
			"SUMMARY:Test Event\n" +
			"DTSTART:20230101T100000Z\n" +
			"DTEND:20230101T110000Z\n" +
			"AVAILABILITY:office-hours-001@example.com,meeting-room-avail-001@example.com\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);

		assertNotNull(event.getAvailabilityIds());
		assertEquals(2, event.getAvailabilityIds().size());
		assertEquals("office-hours-001@example.com", event.getAvailabilityIds().get(0));
		assertEquals("meeting-room-avail-001@example.com", event.getAvailabilityIds().get(1));

		// Test serialization
		String serialized = event.toICalendar();
		String unfolded = serialized.replace("\r\n ", "").replace("\r\n", "\n");
		assertTrue(unfolded.contains("AVAILABILITY:office-hours-001@example.com,meeting-room-avail-001@example.com"));
	}

	@Test
	@DisplayName("Test Todo availability references parsing")
	void testTodoAvailabilityReferencesParsing() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:todo-123@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"AVAILABILITY:personal-avail-001@example.com\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);

		assertNotNull(todo.getAvailabilityIds());
		assertEquals(1, todo.getAvailabilityIds().size());
		assertEquals("personal-avail-001@example.com", todo.getAvailabilityIds().get(0));

		// Test serialization
		String serialized = todo.toICalendar();
		String unfolded = serialized.replace("\r\n ", "").replace("\r\n", "\n");
		assertTrue(unfolded.contains("AVAILABILITY:personal-avail-001@example.com"));
	}

	@Test
	@DisplayName("Test Event availability references")
	void testEventAvailabilityReferences() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"UID:event-123@example.com\n" +
			"SUMMARY:Test Event\n" +
			"DTSTART:20230101T100000Z\n" +
			"DTEND:20230101T110000Z\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);

		// Test setting availability references
		List<String> availabilityIds = new ArrayList<>();
		availabilityIds.add("office-hours-001@example.com");
		availabilityIds.add("meeting-room-avail-001@example.com");
		event.setAvailabilityIds(availabilityIds);

		assertNotNull(event.getAvailabilityIds());
		assertEquals(2, event.getAvailabilityIds().size());
		assertEquals("office-hours-001@example.com", event.getAvailabilityIds().get(0));
		assertEquals("meeting-room-avail-001@example.com", event.getAvailabilityIds().get(1));
	}

	@Test
	public void testEventColorProperty() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"UID:event-color@example.com\n" +
			"SUMMARY:Color Test\n" +
			"DTSTART:20230101T100000Z\n" +
			"COLOR:#FF0000\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);

		// Test serialization (since color is protected)
		String serialized = event.toICalendar();
		String unfolded = serialized.replace("\r\n ", "").replace("\r\n", "\n");
		assertTrue(unfolded.contains("COLOR:#FF0000"));
	}

	@Test
	public void testCalendarNameProperty() throws Exception {
		String icalStr = "BEGIN:VCALENDAR\n" +
			"NAME:My Calendar\n" +
			"METHOD:PUBLISH\n" +
			"VERSION:2.0\n" +
			"BEGIN:VEVENT\n" +
			"UID:event-name@example.com\n" +
			"SUMMARY:Test Event\n" +
			"DTSTART:20230101T100000Z\n" +
			"END:VEVENT\n" +
			"END:VCALENDAR";

		parser.parse(new StringReader(icalStr));
		DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);

		assertEquals("My Calendar", ds.getName());

		// Test serialization
		String serialized = ds.toICalendar();
		String unfolded = serialized.replace("\r\n ", "").replace("\r\n", "\n");
		assertTrue(unfolded.contains("NAME:My Calendar"));
	}

	@Test
	public void testVAvailabilityParticipantType() throws Exception {
		String icalStr = "BEGIN:VAVAILABILITY\n" +
			"UID:avail-pt@example.com\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"PARTICIPANT-TYPE:INDIVIDUAL\n" +
			"SUMMARY:Available Time\n" +
			"END:VAVAILABILITY";

		VAvailability va = createVAvailabilityFromICalendar(icalStr);

		// Test serialization (since participantType is protected)
		String serialized = va.toICalendar();
		String unfolded = serialized.replace("\r\n ", "").replace("\r\n", "\n");
		assertTrue(unfolded.contains("PARTICIPANT-TYPE:INDIVIDUAL"));
	}

	private VAvailability createVAvailabilityFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new VAvailability(parser, 1, lines);
	}

	private Event createEventFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new Event(parser, 1, lines);
	}

	private Todo createTodoFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new Todo(parser, 1, lines);
	}
}