package us.k5n.ical.core.components;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import us.k5n.ical.*;

/**
 * RFC 5545 Section 3.6.1: VEVENT Component Tests
 */
@DisplayName("RFC 5545 Section 3.6.1: VEVENT Component")
public class VEventTest {
  private ICalendarParser parser;

  @BeforeEach
  void setUp() {
    parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
  }

  private Event createEventFromICalendar(String icalStr) throws Exception {
    List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
    return new Event(parser, 1, lines);
  }

  @Nested
  @DisplayName("Basic Property Parsing Tests")
  class BasicPropertyParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.1: should parse ORGANIZER property")
    void should_parseOrganizer_when_organizerProvided() throws Exception {
      String icalStr = "BEGIN:VEVENT\nDTSTAMP:20230101T120000Z\nUID:org-test@example.com\n"
          + "DTSTART:20230101T090000Z\nDTEND:20230101T100000Z\nSUMMARY:Meeting with organizer\n"
          + "ORGANIZER:mailto:organizer@example.com\nEND:VEVENT";
      Event event = createEventFromICalendar(icalStr);
      assertTrue(event.isValid());
      assertNotNull(event.getOrganizer());
      assertEquals("mailto:organizer@example.com", event.getOrganizer().getOrganizer());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.1: should parse CONTACT property")
    void should_parseContact_when_contactProvided() throws Exception {
      String icalStr = "BEGIN:VEVENT\nDTSTAMP:20230101T120000Z\nUID:contact-test@example.com\n"
          + "DTSTART:20230101T090000Z\nDTEND:20230101T100000Z\nSUMMARY:Meeting with contact\n"
          + "CONTACT:John Doe <john@example.com>\nEND:VEVENT";
      Event event = createEventFromICalendar(icalStr);
      assertTrue(event.isValid());
      assertNotNull(event.getContact());
      assertEquals("John Doe <john@example.com>", event.getContact().getContact());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.1: should parse PRIORITY property")
    void should_parsePriority_when_priorityProvided() throws Exception {
      String icalStr = "BEGIN:VEVENT\nDTSTAMP:20230101T120000Z\nUID:priority-test@example.com\n"
          + "DTSTART:20230101T090000Z\nDTEND:20230101T100000Z\nSUMMARY:High priority meeting\n"
          + "PRIORITY:1\nEND:VEVENT";
      Event event = createEventFromICalendar(icalStr);
      assertTrue(event.isValid());
      assertNotNull(event.getPriority());
      assertEquals(Integer.valueOf(1), event.getPriority());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.1: should parse GEO property")
    void should_parseGeo_when_geoProvided() throws Exception {
      String icalStr = "BEGIN:VEVENT\nDTSTAMP:20230101T120000Z\nUID:geo-test@example.com\n"
          + "DTSTART:20230101T090000Z\nDTEND:20230101T100000Z\nSUMMARY:Meeting at location\n"
          + "GEO:37.7765;-122.4027\nEND:VEVENT";
      Event event = createEventFromICalendar(icalStr);
      assertTrue(event.isValid());
      assertNotNull(event.getGeo());
      assertEquals("37.7765;-122.4027", event.getGeo());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.1: should parse REQUEST-STATUS property")
    void should_parseRequestStatus_when_requestStatusProvided() throws Exception {
      String icalStr = "BEGIN:VEVENT\nDTSTAMP:20230101T120000Z\nUID:request-test@example.com\n"
          + "DTSTART:20230101T090000Z\nDTEND:20230101T100000Z\nSUMMARY:Request needed\n"
          + "REQUEST-STATUS:NEEDS-ACTION\nEND:VEVENT";
      Event event = createEventFromICalendar(icalStr);
      assertTrue(event.isValid());
      assertEquals(4, event.getRequestStatus());
    }
  }

  @Nested
  @DisplayName("VALARM Component Tests")
  class ValarmTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.1: should parse embedded VALARM")
    void should_parseValarm_when_valarmProvided() throws Exception {
      String icalStr = "BEGIN:VEVENT\nDTSTAMP:20230101T120000Z\nUID:valarm-test@example.com\n"
          + "DTSTART:20230101T090000Z\nDTEND:20230101T100000Z\nSUMMARY:Event with alarm\n"
          + "BEGIN:VALARM\nACTION:AUDIO\nTRIGGER:-PT15M\nEND:VALARM\nEND:VEVENT";
      Event event = createEventFromICalendar(icalStr);
      assertTrue(event.isValid());
      assertNotNull(event.getAlarms());
      assertEquals(1, event.getAlarms().size());
      Valarm alarm = event.getAlarms().get(0);
      assertEquals("AUDIO", alarm.getAction());
      assertEquals("-PT15M", alarm.getTrigger());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.1: should parse multiple VALARMs")
    void should_parseMultipleValarms_when_multipleProvided() throws Exception {
      String icalStr = "BEGIN:VEVENT\nDTSTAMP:20230101T120000Z\nUID:multi-valarm-test@example.com\n"
          + "DTSTART:20230101T090000Z\nDTEND:20230101T100000Z\nSUMMARY:Event with multiple alarms\n"
          + "BEGIN:VALARM\nACTION:AUDIO\nTRIGGER:-PT15M\nEND:VALARM\n"
          + "BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT1H\nDESCRIPTION:Meeting reminder\nEND:VALARM\n"
          + "END:VEVENT";
      Event event = createEventFromICalendar(icalStr);
      assertTrue(event.isValid());
      assertNotNull(event.getAlarms());
      assertEquals(2, event.getAlarms().size());
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.1: should include VALARM in output")
    void should_includeValarm_when_toICalendarCalled() throws Exception {
      String icalStr = "BEGIN:VEVENT\nDTSTAMP:20230101T120000Z\nUID:icalendar-valarm-test@example.com\n"
          + "DTSTART:20230101T090000Z\nDTEND:20230101T100000Z\nSUMMARY:Test event with alarm\n"
          + "BEGIN:VALARM\nACTION:AUDIO\nTRIGGER:-PT15M\nEND:VALARM\nEND:VEVENT";
      Event event = createEventFromICalendar(icalStr);
      String output = event.toICalendar();
      String unfolded = output.replace("\r\n ", "").replace("\r\n", "\n");
      assertTrue(unfolded.contains("BEGIN:VALARM"));
      assertTrue(unfolded.contains("ACTION:AUDIO"));
      assertTrue(unfolded.contains("TRIGGER:-PT15M"));
      assertTrue(unfolded.contains("END:VALARM"));
    }
  }
}
