package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Organizer class.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class OrganizerTest implements Constants {

    @Nested
    @DisplayName("Basic Parsing Tests")
    class BasicParsingTests {

        @Test
        @DisplayName("Parse simple organizer with mailto")
        void testSimpleOrganizer() throws Exception {
            String orgStr = "ORGANIZER:mailto:organizer@example.com";
            Organizer org = new Organizer(orgStr, PARSE_STRICT);

            assertNotNull(org, "Organizer should not be null");
            assertEquals("mailto:organizer@example.com", org.getOrganizer());
            assertEquals("mailto:organizer@example.com", org.getValue());
        }

        @Test
        @DisplayName("Parse organizer with CN parameter")
        void testOrganizerWithCN() throws Exception {
            String orgStr = "ORGANIZER;CN=John Smith:mailto:john@example.com";
            Organizer org = new Organizer(orgStr, PARSE_STRICT);

            assertEquals("mailto:john@example.com", org.getOrganizer());

            // Check CN attribute
            Attribute cnAttr = org.getNamedAttribute("CN");
            assertNotNull(cnAttr, "CN attribute should exist");
            assertEquals("John Smith", cnAttr.value);
        }

        @Test
        @DisplayName("Parse organizer with quoted CN parameter")
        void testOrganizerWithQuotedCN() throws Exception {
            String orgStr = "ORGANIZER;CN=\"Smith, John\":mailto:john@example.com";
            Organizer org = new Organizer(orgStr, PARSE_LOOSE);

            assertEquals("mailto:john@example.com", org.getOrganizer());

            Attribute cnAttr = org.getNamedAttribute("CN");
            assertNotNull(cnAttr, "CN attribute should exist");
            assertEquals("Smith, John", cnAttr.value);
        }

        @Test
        @DisplayName("Parse organizer with DIR parameter")
        void testOrganizerWithDir() throws Exception {
            String orgStr = "ORGANIZER;DIR=\"ldap://example.com/uid=john\":mailto:john@example.com";
            Organizer org = new Organizer(orgStr, PARSE_LOOSE);

            assertEquals("mailto:john@example.com", org.getOrganizer());

            Attribute dirAttr = org.getNamedAttribute("DIR");
            assertNotNull(dirAttr, "DIR attribute should exist");
            assertEquals("ldap://example.com/uid=john", dirAttr.value);
        }

        @Test
        @DisplayName("Parse organizer with SENT-BY parameter")
        void testOrganizerWithSentBy() throws Exception {
            String orgStr = "ORGANIZER;SENT-BY=\"mailto:assistant@example.com\":mailto:boss@example.com";
            Organizer org = new Organizer(orgStr, PARSE_LOOSE);

            assertEquals("mailto:boss@example.com", org.getOrganizer());

            Attribute sentByAttr = org.getNamedAttribute("SENT-BY");
            assertNotNull(sentByAttr, "SENT-BY attribute should exist");
            assertEquals("mailto:assistant@example.com", sentByAttr.value);
        }
    }

    @Nested
    @DisplayName("RFC 9073 PARTICIPANT-ID Tests")
    class ParticipantIdTests {

        @Test
        @DisplayName("Parse organizer with PARTICIPANT-ID parameter")
        void testOrganizerWithParticipantId() throws Exception {
            String orgStr = "ORGANIZER;PARTICIPANT-ID=participant-123:mailto:organizer@example.com";
            Organizer org = new Organizer(orgStr, PARSE_LOOSE);

            assertEquals("mailto:organizer@example.com", org.getOrganizer());
            assertEquals("participant-123", org.getParticipantId());
        }

        @Test
        @DisplayName("Organizer without PARTICIPANT-ID should return null")
        void testOrganizerWithoutParticipantId() throws Exception {
            String orgStr = "ORGANIZER:mailto:organizer@example.com";
            Organizer org = new Organizer(orgStr, PARSE_LOOSE);

            assertNull(org.getParticipantId());
        }

        @Test
        @DisplayName("Parse organizer with multiple parameters including PARTICIPANT-ID")
        void testOrganizerWithMultipleParams() throws Exception {
            String orgStr = "ORGANIZER;CN=John Smith;PARTICIPANT-ID=p-456:mailto:john@example.com";
            Organizer org = new Organizer(orgStr, PARSE_LOOSE);

            assertEquals("mailto:john@example.com", org.getOrganizer());
            assertEquals("p-456", org.getParticipantId());

            Attribute cnAttr = org.getNamedAttribute("CN");
            assertNotNull(cnAttr);
            assertEquals("John Smith", cnAttr.value);
        }
    }

    @Nested
    @DisplayName("Serialization Tests")
    class SerializationTests {

        @Test
        @DisplayName("Round-trip simple organizer")
        void testRoundTripSimple() throws Exception {
            String orgStr = "ORGANIZER:mailto:test@example.com";
            Organizer org = new Organizer(orgStr, PARSE_LOOSE);

            String output = org.toICalendar();
            assertNotNull(output);
            // Should contain the original value
            assertTrue(output.contains("mailto:test@example.com") ||
                       output.contains("MAILTO:test@example.com"));
        }

        @Test
        @DisplayName("Round-trip organizer with attributes")
        void testRoundTripWithAttributes() throws Exception {
            String orgStr = "ORGANIZER;CN=Test User:mailto:test@example.com";
            Organizer org = new Organizer(orgStr, PARSE_LOOSE);

            String output = org.toICalendar();
            assertNotNull(output);
            assertTrue(output.contains("ORGANIZER"));
        }

        private boolean assertTrue(boolean condition) {
            if (!condition) {
                fail("Assertion failed");
            }
            return condition;
        }
    }

    @Nested
    @DisplayName("Event Integration Tests")
    class EventIntegrationTests {

        @Test
        @DisplayName("Parse event with organizer")
        void testEventWithOrganizer() throws Exception {
            String icalData = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Test//EN\n" +
                "BEGIN:VEVENT\n" +
                "UID:test-123@example.com\n" +
                "DTSTAMP:20240101T100000Z\n" +
                "DTSTART:20240115T140000Z\n" +
                "SUMMARY:Test Event\n" +
                "ORGANIZER;CN=Meeting Organizer:mailto:organizer@example.com\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";

            ICalendarParser parser = new ICalendarParser(PARSE_LOOSE);
            parser.parse(new StringReader(icalData));

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertNotNull(ds.getAllEvents());
            assertEquals(1, ds.getAllEvents().size());

            Event event = ds.getAllEvents().get(0);
            assertNotNull(event.getOrganizer());
            assertEquals("mailto:organizer@example.com", event.getOrganizer().getOrganizer());
        }

        @Test
        @DisplayName("Parse iTIP REQUEST with organizer")
        void testItipRequestWithOrganizer() throws Exception {
            String icalData = "BEGIN:VCALENDAR\n" +
                "METHOD:REQUEST\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test//Test//EN\n" +
                "BEGIN:VEVENT\n" +
                "UID:meeting-456@example.com\n" +
                "DTSTAMP:20240101T100000Z\n" +
                "DTSTART:20240115T140000Z\n" +
                "SUMMARY:Meeting Request\n" +
                "ORGANIZER;CN=Boss;SENT-BY=\"mailto:assistant@example.com\":mailto:boss@example.com\n" +
                "ATTENDEE;PARTSTAT=NEEDS-ACTION:mailto:attendee@example.com\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";

            ICalendarParser parser = new ICalendarParser(PARSE_LOOSE);
            parser.parse(new StringReader(icalData));

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            Event event = ds.getAllEvents().get(0);

            assertNotNull(event.getOrganizer());
            assertEquals("mailto:boss@example.com", event.getOrganizer().getOrganizer());
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Parse mode LOOSE should accept various formats")
        void testLooseParseMode() {
            // Various formats that might not be strictly valid
            String[] testCases = {
                "ORGANIZER:mailto:test@example.com",
                "ORGANIZER:MAILTO:TEST@EXAMPLE.COM",
                "ORGANIZER;CN=Test:mailto:test@example.com"
            };

            for (String testCase : testCases) {
                try {
                    Organizer org = new Organizer(testCase, PARSE_LOOSE);
                    assertNotNull(org);
                } catch (Exception e) {
                    fail("LOOSE mode should accept: " + testCase + " but got: " + e.getMessage());
                }
            }
        }

        @Test
        @DisplayName("Empty organizer value should be handled")
        void testEmptyValue() {
            try {
                // This may or may not throw depending on implementation
                Organizer org = new Organizer("ORGANIZER:", PARSE_LOOSE);
                // If it doesn't throw, the value should be empty
                assertEquals("", org.getValue());
            } catch (ParseException e) {
                // This is also acceptable behavior
            }
        }
    }
}
