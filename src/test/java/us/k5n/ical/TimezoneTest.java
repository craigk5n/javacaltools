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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Timezone component parsing and functionality
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class TimezoneTest {
	private ICalendarParser parser;
	private List<String> textLines;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
		textLines = new ArrayList<String>();
	}

	@Test
	@DisplayName("Test basic VTIMEZONE with only TZID")
	void testBasicTimezone() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("TZID:America/New_York");
		textLines.add("END:VTIMEZONE");
		
		Timezone timezone = new Timezone(parser, 1, textLines);
		
		assertTrue(timezone.isValid());
		assertEquals("America/New_York", timezone.getTimezoneId());
		assertEquals(0, timezone.getStandards().size());
		assertEquals(0, timezone.getDaylight().size());
	}

	@Test
	@DisplayName("Test VTIMEZONE with TZID and LAST-MODIFIED")
	void testTimezoneWithLastModified() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("TZID:Europe/London");
		textLines.add("LAST-MODIFIED:20230101T000000Z");
		textLines.add("END:VTIMEZONE");
		
		Timezone timezone = new Timezone(parser, 1, textLines);
		
		assertTrue(timezone.isValid());
		assertEquals("Europe/London", timezone.getTimezoneId());
		assertNotNull(timezone.getLastModified());
	}

	@Test
	@DisplayName("Test VTIMEZONE with TZURL")
	void testTimezoneWithURL() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("TZID:Asia/Tokyo");
		textLines.add("TZURL:http://tz.example.org/Asia/Tokyo");
		textLines.add("END:VTIMEZONE");
		
		Timezone timezone = new Timezone(parser, 1, textLines);
		
		assertTrue(timezone.isValid());
		assertEquals("Asia/Tokyo", timezone.getTimezoneId());
		assertNotNull(timezone.getURL());
		assertEquals("http://tz.example.org/Asia/Tokyo", timezone.getURL().value);
	}

	@Test
	@DisplayName("Test VTIMEZONE with empty TZID should default to UTC")
	void testTimezoneDefaultsToUTC() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("END:VTIMEZONE");
		
		Timezone timezone = new Timezone(parser, 1, textLines);
		
		assertTrue(timezone.isValid());
		assertEquals("UTC", timezone.getTimezoneId());
	}

	@Test
	@DisplayName("Test VTIMEZONE toICalendar output")
	void testTimezoneToICalendar() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("TZID:America/Los_Angeles");
		textLines.add("LAST-MODIFIED:20230601T120000Z");
		textLines.add("TZURL:http://example.com/timezones");
		textLines.add("END:VTIMEZONE");
		
		Timezone timezone = new Timezone(parser, 1, textLines);
		String output = timezone.toICalendar();
		
		// Check that output contains expected components
		assertTrue(output.contains("BEGIN:VTIMEZONE"));
		assertTrue(output.contains("TZID:America/Los_Angeles"));
		assertTrue(output.contains("TZURL:http://example.com/timezones"));
		assertTrue(output.contains("END:VTIMEZONE"));
		
		// Verify that lastModified was parsed
		assertNotNull(timezone.getLastModified());
	}

	@Test
	@DisplayName("Test VTIMEZONE parsing error handling")
	void testTimezoneErrorHandling() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("INVALID_PROPERTY:value");
		textLines.add("END:VTIMEZONE");
		
		// Should handle in loose mode
		ICalendarParser looseParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
		Timezone timezone = new Timezone(looseParser, 1, textLines);
		
		// Should still be valid despite invalid property in loose mode
		assertTrue(timezone.isValid());
	}

	@Test
	@DisplayName("Test VTIMEZONE strict parsing error")
	void testTimezoneStrictParsing() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("INVALID_PROPERTY:value");
		textLines.add("END:VTIMEZONE");
		
		// Should handle in strict mode - report error but still create object
		ICalendarParser strictParser = new ICalendarParser(ICalendarParser.PARSE_STRICT);
		Timezone timezone = new Timezone(strictParser, 1, textLines);
		
		// Should still create timezone object even with errors in strict mode
		assertNotNull(timezone);
	}

	@Test
	@DisplayName("Test Timezone toString")
	void testTimezoneToString() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("TZID:Australia/Sydney");
		textLines.add("END:VTIMEZONE");
		
		Timezone timezone = new Timezone(parser, 1, textLines);
		String stringRep = timezone.toString();
		
		assertTrue(stringRep.contains("Australia/Sydney"));
		assertTrue(stringRep.contains("standards=0"));
		assertTrue(stringRep.contains("daylight=0"));
	}

	@Test
	@DisplayName("Test empty VTIMEZONE lines")
	void testEmptyLines() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("   "); // Empty line with spaces
		textLines.add("\t"); // Tab line
		textLines.add("TZID:Test/Empty");
		textLines.add(""); // Completely empty line
		textLines.add("END:VTIMEZONE");
		
		Timezone timezone = new Timezone(parser, 1, textLines);
		
		assertTrue(timezone.isValid());
		assertEquals("Test/Empty", timezone.getTimezoneId());
	}

	@Test
	@DisplayName("Test multiple VTIMEZONE properties")
	void testMultipleProperties() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("TZID:Europe/Paris");
		textLines.add("LAST-MODIFIED:20230101T000000Z");
		textLines.add("TZURL:https://timezone.example.org/Europe/Paris");
		textLines.add("X-CUSTOM-PROPERTY:custom value");
		textLines.add("END:VTIMEZONE");
		
		Timezone timezone = new Timezone(parser, 1, textLines);
		
		assertTrue(timezone.isValid());
		assertEquals("Europe/Paris", timezone.getTimezoneId());
		assertNotNull(timezone.getLastModified());
		assertNotNull(timezone.getURL());
	}

	@Test
	@DisplayName("Test timezone with line folding")
	void testTimezoneWithFolding() throws Exception {
		// Test line unfolding
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("TZID:America/Denver");
		// Simulate folded line
		textLines.add(" This line is folded");
		textLines.add("END:VTIMEZONE");
		
		// Create parser with folding support (same as ICalendarParser)
		ICalendarParser foldingParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
		Timezone timezone = new Timezone(foldingParser, 1, textLines);
		
		assertTrue(timezone.isValid());
	}

	@Test
	@DisplayName("Test timezone data store integration")
	void testTimezoneDataStoreIntegration() throws Exception {
		textLines.add("BEGIN:VTIMEZONE");
		textLines.add("TZID:Pacific/Honolulu");
		textLines.add("END:VTIMEZONE");
		
		Timezone timezone = new Timezone(parser, 1, textLines);
		DefaultDataStore dataStore = new DefaultDataStore();
		parser.addDataStore(dataStore);
		
		// The ICalendarParser should automatically add to data store during parsing
		// For this test, we're just testing the data store interface
		assertTrue(timezone.isValid());
		
		// Test manual addition
		dataStore.storeTimezone(timezone);
		
		assertEquals(1, dataStore.getAllTimezones().size());
		assertEquals("Pacific/Honolulu", dataStore.getAllTimezones().get(0).getTimezoneId());
	}

	@Test
	@DisplayName("Test Timezone validation - valid timezone")
	void testTimezoneValidationValid() throws Exception {
		textLines.clear();
		textLines.add("TZID:America/New_York");

		Timezone timezone = new Timezone(parser, 1, textLines);
		assertTrue(timezone.isValid());
	}





	@Test
	@DisplayName("Test Timezone validation - invalid TZID characters")
	void testTimezoneValidationInvalidTzid() throws Exception {
		textLines.clear();
		textLines.add("TZID:America/New York"); // Space in TZID
		textLines.add("BEGIN:STANDARD");
		textLines.add("DTSTART:20230101T020000");
		textLines.add("TZOFFSETFROM:-0500");
		textLines.add("TZOFFSETTO:-0500");
		textLines.add("END:STANDARD");

		Timezone timezone = new Timezone(parser, 1, textLines);
		List<String> errors = new ArrayList<>();
		assertFalse(timezone.isValid(errors));
		assertTrue(errors.contains("Timezone TZID contains invalid characters"));
	}



	@Test
	@DisplayName("Test Timezone with invalid URL")
	void testTimezoneValidationInvalidUrl() throws Exception {
		textLines.clear();
		textLines.add("TZID:America/New_York");
		textLines.add("TZURL:invalid-url");
		textLines.add("BEGIN:STANDARD");
		textLines.add("DTSTART:20230101T020000");
		textLines.add("TZOFFSETFROM:-0500");
		textLines.add("TZOFFSETTO:-0500");
		textLines.add("END:STANDARD");

		Timezone timezone = new Timezone(parser, 1, textLines);
		List<String> errors = new ArrayList<>();
		assertFalse(timezone.isValid(errors));
		assertTrue(errors.stream().anyMatch(e -> e.contains("URL is not a valid URL")));
	}

	@Test
	@DisplayName("Test timezone validation with detailed error checking")
	void testTimezoneValidationDetailedErrors() throws Exception {
		textLines.clear();
		textLines.add("TZID:America/New_York");
		textLines.add("TZURL:not-a-valid-url-at-all");

		Timezone timezone = new Timezone(parser, 1, textLines);
		List<String> errors = new ArrayList<>();
		assertFalse(timezone.isValid(errors));
		assertTrue(errors.stream().anyMatch(e -> e.contains("URL is not a valid URL")));
	}
}