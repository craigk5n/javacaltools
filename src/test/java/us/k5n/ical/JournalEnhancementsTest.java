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
 * Test class for VJOURNAL enhancements - new properties added in ICAL-06
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class JournalEnhancementsTest {
	private ICalendarParser parser;

	@BeforeEach
	void setUp() {
		parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
	}

	private Journal createJournalFromICalendar(String icalStr) throws Exception {
		List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
		return new Journal(parser, 1, lines);
	}

	@Test
	@DisplayName("Test VJOURNAL with ORGANIZER property")
	void testJournalWithOrganizer() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:org-journal-test@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"SUMMARY:Journal with organizer\n" +
			"ORGANIZER:mailto:organizer@example.com\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());
		assertNotNull(journal.getOrganizer());
		assertEquals("mailto:organizer@example.com", journal.getOrganizer().getOrganizer());
	}

	@Test
	@DisplayName("Test VJOURNAL with COMMENT property")
	void testJournalWithComment() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:comment-journal-test@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"SUMMARY:Journal with comment\n" +
			"COMMENT:This is a test comment\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());
		assertNotNull(journal.getComment());
		assertEquals("This is a test comment", journal.getComment().getValue());
	}

	@Test
	@DisplayName("Test VJOURNAL with CONTACT property")
	void testJournalWithContact() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:contact-journal-test@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"SUMMARY:Journal with contact\n" +
			"CONTACT:John Doe <john@example.com>\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());
		assertNotNull(journal.getContact());
		assertEquals("John Doe <john@example.com>", journal.getContact().getContact());
	}

	@Test
	@DisplayName("Test VJOURNAL with ATTENDEE property")
	void testJournalWithAttendee() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:attendee-journal-test@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"SUMMARY:Journal with attendee\n" +
			"ATTENDEE:mailto:attendee@example.com\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());
		assertNotNull(journal.getAttendees());
		assertEquals(1, journal.getAttendees().size());
		assertEquals("mailto:attendee@example.com", journal.getAttendees().get(0).getValue());
	}

	@Test
	@DisplayName("Test VJOURNAL with EXDATE property")
	void testJournalWithExdate() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:exdate-journal-test@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"RRULE:FREQ=WEEKLY\n" +
			"SUMMARY:Journal with exception\n" +
			"EXDATE;VALUE=\"DATE\":20230108,20230115\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());
		assertNotNull(journal.getExceptions());
		assertEquals(2, journal.getExceptions().size());
	}

	@Test
	@DisplayName("Test VJOURNAL with RDATE property")
	void testJournalWithRdate() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:rdate-journal-test@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"SUMMARY:Journal with recurrence dates\n" +
			"RDATE;VALUE=\"DATE\":20230105,20230112\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());
		assertNotNull(journal.getRdates());
		assertEquals(2, journal.getRdates().size());
	}

	@Test
	@DisplayName("Test VJOURNAL with RELATED-TO property")
	void testJournalWithRelatedTo() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:related-journal-test@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"SUMMARY:Journal with related component\n" +
			"RELATED-TO:related-uid@example.com\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());
		assertNotNull(journal.getRelatedTo());
		assertEquals("related-uid@example.com", journal.getRelatedTo().getRelatedTo());
	}

	@Test
	@DisplayName("Test VJOURNAL with multiple new properties")
	void testJournalWithMultipleNewProperties() throws Exception {
		String icalStr = "BEGIN:VJOURNAL\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:multi-journal-test@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"SUMMARY:Complete journal entry\n" +
			"DESCRIPTION:This is a detailed description\n" +
			"ORGANIZER:mailto:boss@example.com\n" +
			"CONTACT:Secretary <secretary@example.com>\n" +
			"COMMENT:Important meeting notes\n" +
			"ATTENDEE:mailto:attendee1@example.com\n" +
			"ATTENDEE:mailto:attendee2@example.com\n" +
			"RELATED-TO:previous-meeting-uid@example.com\n" +
			"EXDATE;VALUE=\"DATE\":20230108\n" +
			"RDATE;VALUE=\"DATE\":20230105\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		assertTrue(journal.isValid());

		// Test all new properties
		assertNotNull(journal.getOrganizer());
		assertEquals("mailto:boss@example.com", journal.getOrganizer().getOrganizer());

		assertNotNull(journal.getContact());
		assertEquals("Secretary <secretary@example.com>", journal.getContact().getContact());

		assertNotNull(journal.getComment());
		assertEquals("Important meeting notes", journal.getComment().getValue());

		assertNotNull(journal.getAttendees());
		assertEquals(2, journal.getAttendees().size());

		assertNotNull(journal.getRelatedTo());
		assertEquals("previous-meeting-uid@example.com", journal.getRelatedTo().getRelatedTo());

		assertNotNull(journal.getExceptions());
		assertEquals(1, journal.getExceptions().size());

		assertNotNull(journal.getRdates());
		assertEquals(1, journal.getRdates().size());
	}

	@Test
	@DisplayName("Test VJOURNAL toICalendar output includes new properties")
	void testJournalToICalendarIncludesNewProperties() throws Exception {
		// Create a journal with all new properties
		String icalStr = "BEGIN:VJOURNAL\n" +
			"DTSTAMP:20230101T120000Z\n" +
			"UID:icalendar-test@example.com\n" +
			"DTSTART;VALUE=\"DATE\":20230101\n" +
			"SUMMARY:Test journal\n" +
			"ORGANIZER:mailto:test@example.com\n" +
			"COMMENT:Test comment\n" +
			"CONTACT:Test Contact\n" +
			"RELATED-TO:test-uid\n" +
			"END:VJOURNAL";

		Journal journal = createJournalFromICalendar(icalStr);
		String output = journal.toICalendar();

		// Verify the output contains all the new properties
		assertTrue(output.contains("ORGANIZER:mailto:test@example.com"));
		assertTrue(output.contains("COMMENT:Test comment"));
		assertTrue(output.contains("CONTACT:Test Contact"));
		assertTrue(output.contains("RELATED-TO:test-uid"));
		assertTrue(output.contains("BEGIN:VJOURNAL"));
		assertTrue(output.contains("END:VJOURNAL"));
	}
}