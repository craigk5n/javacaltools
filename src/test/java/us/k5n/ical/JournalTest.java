package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Journal.
 * 
 * @author Craig Knudsen
 */
public class JournalTest implements Constants {
    private ICalendarParser parser;
    private DataStore ds;
    private String header = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//FOO//BAR//EN\n"
            + "METHOD:PUBLISH\nBEGIN:VJOURNAL\n";
    private String trailer = "END:VJOURNAL\nEND:VCALENDAR\n";

    @BeforeEach
    public void setUp() {
        parser = new ICalendarParser(PARSE_STRICT);
        ds = parser.getDataStoreAt(0);
    }

    @Test
    public void testValidJournalEntry() {
        String content = header
                + "SUMMARY:Test Journal Entry\nDESCRIPTION:This is the description\n"
                + "DTSTAMP:20060501\nUID:journaltest1@k5n.us\n" + trailer;
        StringReader reader = new StringReader(content);
        try {
            boolean parsedSuccessfully = parser.parse(reader);
            assertTrue(parsedSuccessfully, "Failed to parse valid journal entry");

            List<Journal> journals = ds.getAllJournals();
            assertTrue(journals.size() > 0, "Journal not found in the datastore");

            Journal journal = journals.get(0);
            assertTrue(journal.isValid(), "Journal entry is not valid");

            Date dtstamp = journal.dtstamp;
            assertTrue(dtstamp.isDateOnly(), "Journal entry should be date-only");
            assertTrue(dtstamp.year == 2006, "Incorrect year");
            assertTrue(dtstamp.month == 5, "Incorrect month");
            assertTrue(dtstamp.day == 1, "Incorrect day");

            assertNotNull(journal.summary, "Summary should not be null");
            assertTrue(journal.summary.value.equals("Test Journal Entry"), "Incorrect summary");

            assertNotNull(journal.description, "Description should not be null");
            assertTrue(journal.description.value.equals("This is the description"), "Incorrect description");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testJournalEntryWithoutDescription() {
        String content = header
                + "SUMMARY:Test Journal Entry\n"
                + "DTSTAMP:20060501\nUID:journaltest2@k5n.us\n" + trailer;
        StringReader reader = new StringReader(content);
        try {
            boolean parsedSuccessfully = parser.parse(reader);
            assertTrue(parsedSuccessfully, "Failed to parse journal entry without description");

            List<Journal> journals = ds.getAllJournals();
            assertTrue(journals.size() > 0, "Journal not found in the datastore");

            Journal journal = journals.get(0);
            assertTrue(journal.isValid(), "Journal entry is not valid");

            assertNotNull(journal.summary, "Summary should not be null");
            assertTrue(journal.summary.value.equals("Test Journal Entry"), "Incorrect summary");

            assertTrue(journal.description == null, "Description should be null");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testJournalEntryWithoutSummary() {
        String content = header
                + "DESCRIPTION:This is the description\n"
                + "DTSTAMP:20060501\nUID:journaltest3@k5n.us\n" + trailer;
        StringReader reader = new StringReader(content);
        try {
            boolean parsedSuccessfully = parser.parse(reader);
            assertTrue(parsedSuccessfully, "Failed to parse journal entry without summary");

            List<Journal> journals = ds.getAllJournals();
            assertTrue(journals.size() > 0, "Journal not found in the datastore");

            Journal journal = journals.get(0);
            assertTrue(journal.isValid(), "Journal entry is not valid");

            assertTrue(journal.summary == null, "Summary should be null");

            assertNotNull(journal.description, "Description should not be null");
            assertTrue(journal.description.value.equals("This is the description"), "Incorrect description");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testJournalEntryWithInvalidDate() {
        String content = header
                + "SUMMARY:Test Journal Entry\nDESCRIPTION:This is the description\n"
                + "DTSTAMP:InvalidDate\nUID:journaltest4@k5n.us\n" + trailer;
        StringReader reader = new StringReader(content);
        try {
            boolean parsedSuccessfully = parser.parse(reader);
            assertTrue(parsedSuccessfully, "Parsing should succeed even with an invalid date");

            List<Journal> journals = ds.getAllJournals();
			assertEquals(1, journals.size());
            assertTrue(journals.get(0).getDtstamp() == null, "Empty date when invalid");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }
}