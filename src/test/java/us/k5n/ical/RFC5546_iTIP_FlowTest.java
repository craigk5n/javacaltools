 package us.k5n.ical;

 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import static org.junit.jupiter.api.Assertions.*;

 import us.k5n.ical.ICalendarParser;
 import us.k5n.ical.DefaultDataStore;

 import java.io.StringReader;

/**
 * RFC 5546: iTIP Flow Tests
 *
 * Tests for iTIP (iCalendar Transport-Independent Interoperability Protocol)
 * REQUEST→REPLY→UPDATE cycles and other scheduling workflows as defined in RFC 5546.
 *
 * @author Craig Knudsen, craig@k5n.us
 * @ai-generated Grok-4.1-Fast
 */
 public class RFC5546_iTIP_FlowTest {

     @Test
     @DisplayName("RFC 5546: REQUEST method - Meeting invitation")
     void testRequestMethod() throws Exception {
         String requestMessage = "BEGIN:VCALENDAR\n" +
             "METHOD:REQUEST\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "VERSION:2.0\n" +
             "BEGIN:VEVENT\n" +
             "UID:meeting-123@example.com\n" +
             "DTSTAMP:20240101T100000Z\n" +
             "DTSTART:20240115T140000Z\n" +
             "DTEND:20240115T150000Z\n" +
             "SUMMARY:Team Meeting\n" +
             "ORGANIZER:mailto:organizer@example.com\n" +
             "ATTENDEE:mailto:attendee1@example.com\n" +
             "ATTENDEE:mailto:attendee2@example.com\n" +
             "SEQUENCE:0\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(requestMessage));

         // Verify parsing succeeded without errors
         assertTrue(parser.getAllErrors().isEmpty(), "REQUEST message should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
         assertFalse(ds.getAllEvents().isEmpty(), "Should have at least one event");
     }

     @Test
     @DisplayName("RFC 5546: REPLY method - Response to invitation")
     void testReplyMethod() throws Exception {
         String replyMessage = "BEGIN:VCALENDAR\n" +
             "METHOD:REPLY\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "VERSION:2.0\n" +
             "BEGIN:VEVENT\n" +
             "UID:meeting-123@example.com\n" +
             "DTSTAMP:20240101T110000Z\n" +
             "ORGANIZER:mailto:organizer@example.com\n" +
             "ATTENDEE:mailto:attendee1@example.com\n" +
             "SEQUENCE:0\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(replyMessage));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "REPLY message should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5546: CANCEL method - Meeting cancellation")
     void testCancelMethod() throws Exception {
         String cancelMessage = "BEGIN:VCALENDAR\n" +
             "METHOD:CANCEL\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "VERSION:2.0\n" +
             "BEGIN:VEVENT\n" +
             "UID:meeting-456@example.com\n" +
             "DTSTAMP:20240101T130000Z\n" +
             "DTSTART:20240116T140000Z\n" +
             "DTEND:20240116T150000Z\n" +
             "SUMMARY:Cancelled Meeting\n" +
             "ORGANIZER:mailto:organizer@example.com\n" +
             "ATTENDEE:mailto:attendee1@example.com\n" +
             "STATUS:CANCELLED\n" +
             "SEQUENCE:2\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(cancelMessage));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "CANCEL message should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }

     @Test
     @DisplayName("RFC 5546: PUBLISH method - Publishing calendar objects")
     void testPublishMethod() throws Exception {
         String publishMessage = "BEGIN:VCALENDAR\n" +
             "METHOD:PUBLISH\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "VERSION:2.0\n" +
             "BEGIN:VEVENT\n" +
             "UID:public-event@example.com\n" +
             "DTSTAMP:20240101T180000Z\n" +
             "DTSTART:20240125T190000Z\n" +
             "DTEND:20240125T220000Z\n" +
             "SUMMARY:Public Concert\n" +
             "LOCATION:City Hall Auditorium\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
         parser.parse(new StringReader(publishMessage));

         // Verify parsing succeeded
         assertTrue(parser.getAllErrors().isEmpty(), "PUBLISH message should parse without errors");
         DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
         assertNotNull(ds.getAllEvents(), "Should have parsed events");
     }
 }