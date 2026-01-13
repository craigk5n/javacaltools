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
 * Test class for enhanced VTODO status handling and validation
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class TodoStatusTest {
	private ICalendarParser parser;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
	}

	@Test
	@DisplayName("Test VTODO status validation - valid status")
	void testTodoStatusValidationValid() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:NEEDS-ACTION\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		assertTrue(todo.isValid());
		assertEquals(Constants.STATUS_NEEDS_ACTION, todo.getStatus());
	}

	@Test
	@DisplayName("Test VTODO status validation - completed with completion date")
	void testTodoStatusValidationCompletedWithDate() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:COMPLETED\n" +
			"COMPLETED:20230101T100000Z\n" +
			"PERCENT-COMPLETE:100\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		assertTrue(todo.isValid());
	}

	@Test
	@DisplayName("Test VTODO status validation - completed without completion date")
	void testTodoStatusValidationCompletedWithoutDate() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:COMPLETED\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		todo.isValid(errors); // Collect validation errors
		assertFalse(errors.isEmpty());
		assertTrue(errors.stream().anyMatch(e -> e.contains("PERCENT-COMPLETE")));
	}

	@Test
	@DisplayName("Test VTODO status validation - 100% complete but not completed status")
	void testTodoStatusValidation100PercentNotCompleted() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:IN-PROCESS\n" +
			"PERCENT-COMPLETE:100\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(todo.isValid(errors));
		assertTrue(errors.contains("Todo with 100% PERCENT-COMPLETE must have STATUS:COMPLETED"));
	}

	@Test
	@DisplayName("Test VTODO status validation - cancelled with completion date")
	void testTodoStatusValidationCancelledWithCompletion() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:CANCELLED\n" +
			"COMPLETED:20230101T100000Z\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(todo.isValid(errors));
		assertTrue(errors.contains("Todo with STATUS:CANCELLED should not have COMPLETED date"));
	}

	@Test
	@DisplayName("Test VTODO automatic status setting - setCompleted")
	void testTodoAutomaticStatusSettingCompleted() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:IN-PROCESS\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		assertEquals(Constants.STATUS_IN_PROCESS, todo.getStatus());

		// Setting completed should auto-set status to COMPLETED
		Date completionDate = new Date("COMPLETED:20230101T100000Z");
		todo.setCompleted(completionDate);

		assertEquals(Constants.STATUS_COMPLETED, todo.getStatus());
		assertEquals(100, todo.getPercentComplete().intValue());
		assertEquals(completionDate, todo.getCompleted());
	}

	@Test
	@DisplayName("Test VTODO automatic status setting - setPercentComplete 100%")
	void testTodoAutomaticStatusSetting100Percent() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:IN-PROCESS\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		assertEquals(Constants.STATUS_IN_PROCESS, todo.getStatus());

		// Setting percent complete to 100% should auto-set status to COMPLETED
		todo.setPercentComplete(100);

		assertEquals(Constants.STATUS_COMPLETED, todo.getStatus());
		assertNotNull(todo.getCompleted());
	}

	@Test
	@DisplayName("Test VTODO automatic status setting - setPercentComplete 50%")
	void testTodoAutomaticStatusSetting50Percent() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:NEEDS-ACTION\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		assertEquals(Constants.STATUS_NEEDS_ACTION, todo.getStatus());

		// Setting percent complete to 50% should set status to IN-PROCESS
		todo.setPercentComplete(50);

		assertEquals(Constants.STATUS_IN_PROCESS, todo.getStatus());
	}

	@Test
	@DisplayName("Test VTODO status setting with automatic property updates")
	void testTodoStatusSettingWithAutoUpdates() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);

		// Setting status to COMPLETED should auto-set completion date and percent
		todo.setStatus(Constants.STATUS_COMPLETED);

		assertEquals(Constants.STATUS_COMPLETED, todo.getStatus());
		assertNotNull(todo.getCompleted());
		assertEquals(100, todo.getPercentComplete().intValue());
	}

	@Test
	@DisplayName("Test VTODO status setting to CANCELLED")
	void testTodoStatusSettingCancelled() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"COMPLETED:20230101T100000Z\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);

		// Setting status to CANCELLED should clear completion date
		todo.setStatus(Constants.STATUS_CANCELLED);

		assertEquals(Constants.STATUS_CANCELLED, todo.getStatus());
		assertNull(todo.getCompleted());
	}

	@Test
	@DisplayName("Test VTODO status validation with multiple errors")
	void testTodoStatusValidationMultipleErrors() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:COMPLETED\n" +
			"COMPLETED:20230101T100000Z\n" +
			"PERCENT-COMPLETE:50\n" +  // Inconsistent with COMPLETED status
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		List<String> errors = new ArrayList<>();
		assertFalse(todo.isValid(errors));
		assertTrue(errors.size() >= 1); // Should have error about inconsistent percent complete
	}

	@Test
	@DisplayName("Test VTODO status parsing - all valid statuses")
	void testTodoStatusParsingAllValid() throws Exception {
		String[] statuses = {"NEEDS-ACTION", "IN-PROCESS", "COMPLETED", "CANCELLED"};
		int[] statusConstants = {Constants.STATUS_NEEDS_ACTION, Constants.STATUS_IN_PROCESS,
			Constants.STATUS_COMPLETED, Constants.STATUS_CANCELLED};

		for (int i = 0; i < statuses.length; i++) {
			String icalStr = "BEGIN:VTODO\n" +
				"UID:test-todo-" + i + "@example.com\n" +
				"SUMMARY:Test Todo " + i + "\n" +
				"STATUS:" + statuses[i] + "\n";
			if (statuses[i].equals("COMPLETED")) {
				icalStr += "COMPLETED:20230101T100000Z\n" +
					"PERCENT-COMPLETE:100\n";
			}
			icalStr += "END:VTODO";

			Todo todo = createTodoFromICalendar(icalStr);
			assertTrue(todo.isValid());
			assertEquals(statusConstants[i], todo.getStatus());
		}
	}

	@Test
	@DisplayName("Test VTODO status toICalendar output")
	void testTodoStatusToICalendarOutput() throws Exception {
		String icalStr = "BEGIN:VTODO\n" +
			"UID:test-todo@example.com\n" +
			"SUMMARY:Test Todo\n" +
			"STATUS:COMPLETED\n" +
			"END:VTODO";

		Todo todo = createTodoFromICalendar(icalStr);
		String output = todo.toICalendar();

		assertTrue(output.contains("STATUS:COMPLETED"));
		assertTrue(output.contains("BEGIN:VTODO"));
		assertTrue(output.contains("END:VTODO"));
	}

	private Todo createTodoFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new Todo(parser, 1, lines);
	}
}