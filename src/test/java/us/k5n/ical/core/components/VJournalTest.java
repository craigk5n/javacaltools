package us.k5n.ical.core.components;

import static org.junit.jupiter.api.Assertions.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import us.k5n.ical.*;

/**
 * RFC 5545 Section 3.6.3: VJOURNAL Component Tests
 */
@DisplayName("RFC 5545 Section 3.6.3: VJOURNAL Component")
public class VJournalTest implements Constants {
  private ICalendarParser parser;
  private DataStore ds;
  private String header = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//FOO//BAR//EN\nMETHOD:PUBLISH\nBEGIN:VJOURNAL\n";
  private String trailer = "END:VJOURNAL\nEND:VCALENDAR\n";

  @BeforeEach
  void setUp() {
    parser = new ICalendarParser(PARSE_STRICT);
    ds = parser.getDataStoreAt(0);
  }

  private Journal createJournalFromICalendar(String icalStr) throws Exception {
    List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
    ICalendarParser looseParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
    return new Journal(looseParser, 1, lines);
  }

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse valid journal entry")
    void should_parseValidJournal_when_validContentProvided() {
      String content = header
          + "SUMMARY:Test Journal Entry\nDESCRIPTION:This is the description\n"
          + "DTSTAMP:20060501\nUID:journaltest1@k5n.us\n" + trailer;
      StringReader reader = new StringReader(content);
      try {
        boolean parsedSuccessfully = parser.parse(reader);
        assertTrue(parsedSuccessfully);
        List<Journal> journals = ds.getAllJournals();
        assertTrue(journals.size() > 0);
        Journal journal = journals.get(0);
        assertTrue(journal.isValid());
        assertNotNull(journal.getSummary());
        assertEquals("Test Journal Entry", journal.getSummary().getValue());
        assertNotNull(journal.getDescription());
        assertEquals("This is the description", journal.getDescription().getValue());
      } catch (Exception e) {
        fail("Test failed: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse journal without description")
    void should_parseJournal_when_noDescriptionProvided() {
      String content = header
          + "SUMMARY:Test Journal Entry\nDTSTAMP:20060501\nUID:journaltest2@k5n.us\n" + trailer;
      StringReader reader = new StringReader(content);
      try {
        parser.parse(reader);
        List<Journal> journals = ds.getAllJournals();
        Journal journal = journals.get(0);
        assertTrue(journal.isValid());
        assertNotNull(journal.getSummary());
        assertNull(journal.getDescription());
      } catch (Exception e) {
        fail("Test failed: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse journal without summary")
    void should_parseJournal_when_noSummaryProvided() {
      String content = header
          + "DESCRIPTION:This is the description\nDTSTAMP:20060501\nUID:journaltest3@k5n.us\n" + trailer;
      StringReader reader = new StringReader(content);
      try {
        parser.parse(reader);
        List<Journal> journals = ds.getAllJournals();
        Journal journal = journals.get(0);
        assertTrue(journal.isValid());
        assertNull(journal.getSummary());
        assertNotNull(journal.getDescription());
      } catch (Exception e) {
        fail("Test failed: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("Enhanced Properties Tests")
  class EnhancedPropertiesTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse ORGANIZER property")
    void should_parseOrganizer_when_organizerProvided() throws Exception {
      String icalStr = "BEGIN:VJOURNAL\nDTSTAMP:20230101T120000Z\nUID:org-journal-test@example.com\n"
          + "DTSTART;VALUE=\"DATE\":20230101\nSUMMARY:Journal with organizer\n"
          + "ORGANIZER:mailto:organizer@example.com\nEND:VJOURNAL";
      Journal journal = createJournalFromICalendar(icalStr);
      assertTrue(journal.isValid());
      assertNotNull(journal.getOrganizer());
      assertEquals("mailto:organizer@example.com", journal.getOrganizer().getOrganizer());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse COMMENT property")
    void should_parseComment_when_commentProvided() throws Exception {
      String icalStr = "BEGIN:VJOURNAL\nDTSTAMP:20230101T120000Z\nUID:comment-journal-test@example.com\n"
          + "DTSTART;VALUE=\"DATE\":20230101\nSUMMARY:Journal with comment\n"
          + "COMMENT:This is a test comment\nEND:VJOURNAL";
      Journal journal = createJournalFromICalendar(icalStr);
      assertTrue(journal.isValid());
      assertNotNull(journal.getComment());
      assertEquals("This is a test comment", journal.getComment().getValue());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse CONTACT property")
    void should_parseContact_when_contactProvided() throws Exception {
      String icalStr = "BEGIN:VJOURNAL\nDTSTAMP:20230101T120000Z\nUID:contact-journal-test@example.com\n"
          + "DTSTART;VALUE=\"DATE\":20230101\nSUMMARY:Journal with contact\n"
          + "CONTACT:John Doe <john@example.com>\nEND:VJOURNAL";
      Journal journal = createJournalFromICalendar(icalStr);
      assertTrue(journal.isValid());
      assertNotNull(journal.getContact());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse ATTENDEE property")
    void should_parseAttendee_when_attendeeProvided() throws Exception {
      String icalStr = "BEGIN:VJOURNAL\nDTSTAMP:20230101T120000Z\nUID:attendee-journal-test@example.com\n"
          + "DTSTART;VALUE=\"DATE\":20230101\nSUMMARY:Journal with attendee\n"
          + "ATTENDEE:mailto:attendee@example.com\nEND:VJOURNAL";
      Journal journal = createJournalFromICalendar(icalStr);
      assertTrue(journal.isValid());
      assertNotNull(journal.getAttendees());
      assertEquals(1, journal.getAttendees().size());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse EXDATE property")
    void should_parseExdate_when_exdateProvided() throws Exception {
      String icalStr = "BEGIN:VJOURNAL\nDTSTAMP:20230101T120000Z\nUID:exdate-journal-test@example.com\n"
          + "DTSTART;VALUE=\"DATE\":20230101\nRRULE:FREQ=WEEKLY\nSUMMARY:Journal with exception\n"
          + "EXDATE;VALUE=\"DATE\":20230108,20230115\nEND:VJOURNAL";
      Journal journal = createJournalFromICalendar(icalStr);
      assertTrue(journal.isValid());
      assertNotNull(journal.getExceptions());
      assertEquals(2, journal.getExceptions().size());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse RDATE property")
    void should_parseRdate_when_rdateProvided() throws Exception {
      String icalStr = "BEGIN:VJOURNAL\nDTSTAMP:20230101T120000Z\nUID:rdate-journal-test@example.com\n"
          + "DTSTART;VALUE=\"DATE\":20230101\nSUMMARY:Journal with recurrence dates\n"
          + "RDATE;VALUE=\"DATE\":20230105,20230112\nEND:VJOURNAL";
      Journal journal = createJournalFromICalendar(icalStr);
      assertTrue(journal.isValid());
      assertNotNull(journal.getRdates());
      assertEquals(2, journal.getRdates().size());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should parse RELATED-TO property")
    void should_parseRelatedTo_when_relatedToProvided() throws Exception {
      String icalStr = "BEGIN:VJOURNAL\nDTSTAMP:20230101T120000Z\nUID:related-journal-test@example.com\n"
          + "DTSTART;VALUE=\"DATE\":20230101\nSUMMARY:Journal with related component\n"
          + "RELATED-TO:related-uid@example.com\nEND:VJOURNAL";
      Journal journal = createJournalFromICalendar(icalStr);
      assertTrue(journal.isValid());
      assertNotNull(journal.getRelatedTo());
      assertEquals("related-uid@example.com", journal.getRelatedTo().getRelatedTo());
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.3: should include new properties in output")
    void should_includeNewProperties_when_toICalendarCalled() throws Exception {
      String icalStr = "BEGIN:VJOURNAL\nDTSTAMP:20230101T120000Z\nUID:icalendar-test@example.com\n"
          + "DTSTART;VALUE=\"DATE\":20230101\nSUMMARY:Test journal\n"
          + "ORGANIZER:mailto:test@example.com\nCOMMENT:Test comment\n"
          + "CONTACT:Test Contact\nRELATED-TO:test-uid\nEND:VJOURNAL";
      Journal journal = createJournalFromICalendar(icalStr);
      String output = journal.toICalendar();
      assertTrue(output.contains("ORGANIZER:mailto:test@example.com"));
      assertTrue(output.contains("COMMENT:Test comment"));
      assertTrue(output.contains("CONTACT:Test Contact"));
      assertTrue(output.contains("RELATED-TO:test-uid"));
      assertTrue(output.contains("BEGIN:VJOURNAL"));
      assertTrue(output.contains("END:VJOURNAL"));
    }
  }
}
