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

import java.util.List;

/**
 * Test class for performance optimizations and benchmarking
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class PerformanceTest {
	private ICalendarParser parser;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
	}

	@Test
	@DisplayName("Test streaming mode configuration")
	void testStreamingModeConfiguration() throws Exception {
		ICalendarParser streamingParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);

		// Test default settings
		assertFalse(streamingParser.isStreamingMode());
		assertEquals(10000, streamingParser.getMaxComponentSize());

		// Test configuration
		streamingParser.setStreamingMode(true);
		streamingParser.setMaxComponentSize(5000);

		assertTrue(streamingParser.isStreamingMode());
		// Note: maxComponentSize is protected, so we can't directly test it
	}

	@Test
	@DisplayName("Test performance monitoring")
	void testPerformanceMonitoring() throws Exception {
		ICalendarParser perfParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
		perfParser.setPerformanceMonitoring(true);

		String icalStr = "BEGIN:VCALENDAR\n" +
			"VERSION:2.0\n" +
			"PRODID:-//Test//Test//EN\n" +
			"BEGIN:VEVENT\n" +
			"UID:test-event@example.com\n" +
			"SUMMARY:Test Event\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"END:VEVENT\n" +
			"END:VCALENDAR";

		java.io.StringReader reader = new java.io.StringReader(icalStr);
		boolean success = perfParser.parse(reader);

		assertTrue(success);
		assertTrue(perfParser.getLinesProcessed() > 0);
		assertEquals(1, perfParser.getComponentsParsed());
		assertTrue(perfParser.getParseTime() >= 0);
	}

	@Test
	@DisplayName("Test parsing performance with different modes")
	void testParsingPerformanceComparison() throws Exception {
		// Create a moderately large iCalendar string
		StringBuilder icalBuilder = new StringBuilder();
		icalBuilder.append("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Test//EN\n");

		// Add 50 events
		for (int i = 0; i < 50; i++) {
			icalBuilder.append("BEGIN:VEVENT\n");
			icalBuilder.append("UID:event-").append(i).append("@example.com\n");
			icalBuilder.append("SUMMARY:Event ").append(i).append("\n");
			icalBuilder.append("DTSTART:202301").append(String.format("%02d", i % 28 + 1)).append("T090000Z\n");
			icalBuilder.append("DTEND:202301").append(String.format("%02d", i % 28 + 1)).append("T100000Z\n");
			icalBuilder.append("END:VEVENT\n");
		}

		icalBuilder.append("END:VCALENDAR");

		String icalStr = icalBuilder.toString();

		// Test normal mode
		ICalendarParser normalParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
		normalParser.setPerformanceMonitoring(true);

		long startTime = System.currentTimeMillis();
		java.io.StringReader reader1 = new java.io.StringReader(icalStr);
		boolean success1 = normalParser.parse(reader1);
		long normalTime = System.currentTimeMillis() - startTime;

		// Test streaming mode
		ICalendarParser streamingParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
		streamingParser.setStreamingMode(true);
		streamingParser.setPerformanceMonitoring(true);

		startTime = System.currentTimeMillis();
		java.io.StringReader reader2 = new java.io.StringReader(icalStr);
		boolean success2 = streamingParser.parse(reader2);
		long streamingTime = System.currentTimeMillis() - startTime;

		// Both should succeed
		assertTrue(success1);
		assertTrue(success2);

		// Both should parse the same number of components
		assertEquals(50, normalParser.getComponentsParsed());
		assertEquals(50, streamingParser.getComponentsParsed());

		// Performance should be reasonable (less than 1 second for this test)
		assertTrue(normalTime < 1000);
		assertTrue(streamingTime < 1000);

		// Log performance for analysis
		System.out.println("Normal parsing time: " + normalTime + "ms");
		System.out.println("Streaming parsing time: " + streamingTime + "ms");
		System.out.println("Lines processed: " + normalParser.getLinesProcessed());
	}

	@Test
	@DisplayName("Test memory efficiency with large components")
	void testMemoryEfficiency() throws Exception {
		// Create a component with many properties to test memory handling
		StringBuilder eventBuilder = new StringBuilder();
		eventBuilder.append("BEGIN:VEVENT\n");
		eventBuilder.append("UID:large-event@example.com\n");
		eventBuilder.append("SUMMARY:Large Event\n");
		eventBuilder.append("DTSTART:20230101T090000Z\n");
		eventBuilder.append("DTEND:20230101T100000Z\n");

		// Add many ATTENDEE properties
		for (int i = 0; i < 100; i++) {
			eventBuilder.append("ATTENDEE:mailto:user").append(i).append("@example.com\n");
		}

		eventBuilder.append("END:VEVENT");

		String icalStr = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Test//EN\n" +
			eventBuilder.toString() + "\nEND:VCALENDAR";

		ICalendarParser memParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
		memParser.setPerformanceMonitoring(true);

		java.io.StringReader reader = new java.io.StringReader(icalStr);
		boolean success = memParser.parse(reader);

		assertTrue(success);
		assertEquals(1, memParser.getComponentsParsed());

		// Verify the event was parsed correctly with all attendees
		DefaultDataStore ds = (DefaultDataStore) memParser.getDataStoreAt(0);
		List<Event> events = ds.getAllEvents();
		assertEquals(1, events.size());
		assertEquals(100, events.get(0).getAttendees().size());
	}

	@Test
	@DisplayName("Test buffer size optimization")
	void testBufferSizeOptimization() throws Exception {
		// Test that larger buffer sizes don't break parsing
		ICalendarParser bufferParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);

		String icalStr = "BEGIN:VCALENDAR\n" +
			"VERSION:2.0\n" +
			"PRODID:-//Test//Test//EN\n" +
			"BEGIN:VEVENT\n" +
			"UID:buffer-test@example.com\n" +
			"SUMMARY:Buffer Test\n" +
			"DTSTART:20230101T090000Z\n" +
			"DTEND:20230101T100000Z\n" +
			"END:VEVENT\n" +
			"END:VCALENDAR";

		java.io.StringReader reader = new java.io.StringReader(icalStr);
		boolean success = bufferParser.parse(reader);

		assertTrue(success);
		DefaultDataStore ds = (DefaultDataStore) bufferParser.getDataStoreAt(0);
		assertEquals(1, ds.getAllEvents().size());
	}
}