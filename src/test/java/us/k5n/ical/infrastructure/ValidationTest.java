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

package us.k5n.ical.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import us.k5n.ical.*;

/**
 * Test class for enhanced validation and error handling
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class ValidationTest {
	private ICalendarParser parser;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
	}

	@Test
	@DisplayName("Test Event validation - valid event")
	void testEventValidationValid() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"UID:test-event@example.com\n" +
			"SUMMARY:Test Event\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		assertTrue(event.isValid());
	}

	@Test
	@DisplayName("Test Event validation - missing UID")
	void testEventValidationMissingUid() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"SUMMARY:Test Event\n" +
			"DTSTART:20230101T090000Z\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(event.isValid(errors));
		assertTrue(errors.contains("Event must have a UID"));
	}

	@Test
	@DisplayName("Test Event validation - missing summary")
	void testEventValidationMissingSummary() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"UID:test-event@example.com\n" +
			"DTSTART:20230101T090000Z\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(event.isValid(errors));
		assertTrue(errors.contains("Event must have a SUMMARY"));
	}

	@Test
	@DisplayName("Test Event validation - end date before start date")
	void testEventValidationEndBeforeStart() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"UID:test-event@example.com\n" +
			"SUMMARY:Test Event\n" +
			"DTSTART:20230101T100000Z\n" +
			"DTEND:20230101T090000Z\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(event.isValid(errors));
		assertTrue(errors.contains("Event DTEND must be after DTSTART"));
	}

	@Test
	@DisplayName("Test Event validation - both duration and end date")
	void testEventValidationDurationAndEndDate() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"UID:test-event@example.com\n" +
			"SUMMARY:Test Event\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"DURATION:PT1H\n" +
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(event.isValid(errors));
		assertTrue(errors.contains("Event cannot have both DURATION and DTEND"));
	}

	@Test
	@DisplayName("Test Todo validation - valid todo")
	void testTodoValidationValid() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		assertTrue(todo.isValid());
	}

	@Test
	@DisplayName("Test Todo validation - missing UID")
	void testTodoValidationMissingUid() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"SUMMARY:Test Todo\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(todo.isValid(errors));
		assertTrue(errors.contains("Todo must have a UID"));
	}

	@Test
	@DisplayName("Test Todo validation - invalid percent complete")
	void testTodoValidationInvalidPercentComplete() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"PERCENT-COMPLETE:150\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(todo.isValid(errors));
		assertTrue(errors.contains("Todo PERCENT-COMPLETE must be between 0 and 100"));
	}

	@Test
	@DisplayName("Test Todo validation - due date before start date")
	void testTodoValidationDueBeforeStart() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"DTSTART:20230101T100000Z\n" +
			"DUE:20230101T090000Z\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(todo.isValid(errors));
		assertTrue(errors.contains("Todo DUE date must be after DTSTART"));
	}

	@Test
	@DisplayName("Test Journal validation - valid journal")
	void testJournalValidationValid() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"UID:test-journal@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"SUMMARY:Test Journal\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());
	}

	@Test
	@DisplayName("Test Journal validation - valid with minimal content")
	void testJournalValidationValidMinimal() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"UID:test-journal@example.com\n" +
			"SUMMARY:Test Journal\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());
	}

	@Test
	@DisplayName("Test Journal validation - missing content")
	void testJournalValidationMissingContent() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"UID:test-journal@example.com\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(journal.isValid(errors));
		assertTrue(errors.contains("Journal must have either SUMMARY or DESCRIPTION"));
	}

	@Test
	@DisplayName("Test validation catches multiple errors")
	void testValidationMultipleErrors() throws Exception {
		String icalStr = "BEGIN:VEVENT\n" +
			"DTSTART:20230101T100000Z\n" +
			"DTEND:20230101T090000Z\n" +  // End before start
			"END:VEVENT";

		Event event = createEventFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(event.isValid(errors));
		assertTrue(errors.size() >= 2); // Should have errors for missing UID, SUMMARY, and date order
		assertTrue(errors.stream().anyMatch(e -> e.contains("DTEND must be after DTSTART")));
	}

	private Event createEventFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new Event(parser, 1, lines);
	}

	private Todo createTodoFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new Todo(parser, 1, lines);
	}

	private Journal createJournalFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new Journal(parser, 1, lines);
	}
}
