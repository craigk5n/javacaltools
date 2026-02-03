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
 * VAVAILABILITY Component Tests
 */
@DisplayName("VAVAILABILITY Component")
public class VAvailabilityTest {
  private ICalendarParser parser;

  @BeforeEach
  void setUp() {
    parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
  }

  private VAvailability createFromICalendar(String icalStr) throws Exception {
    List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
    return new VAvailability(parser, 1, lines);
  }

  private Event createEventFromICalendar(String icalStr) throws Exception {
    List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
    return new Event(parser, 1, lines);
  }

  private Todo createTodoFromICalendar(String icalStr) throws Exception {
    List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
    return new Todo(parser, 1, lines);
  }

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("should parse basic VAVAILABILITY properties")
    void should_parseBasic_when_basicPropertiesProvided() throws Exception {
      VAvailability va = createFromICalendar("BEGIN:VAVAILABILITY\nUID:avail-123@example.com\n"
          + "DTSTAMP:20230101T120000Z\nBUSYTYPE:BUSY\nEND:VAVAILABILITY");
      assertTrue(va.isValid());
      assertEquals("avail-123@example.com", va.getUid().getValue());
      assertEquals("BUSY", va.getBusyType());
    }

    @Test
    @DisplayName("should parse complete VAVAILABILITY")
    void should_parseComplete_when_allPropertiesProvided() throws Exception {
      VAvailability va = createFromICalendar("BEGIN:VAVAILABILITY\nUID:avail-456@example.com\n"
          + "DTSTAMP:20230101T120000Z\nDTSTART:20230101T090000Z\nDTEND:20230101T170000Z\n"
          + "BUSYTYPE:BUSY-UNAVAILABLE\nSUMMARY:Weekly Office Hours\n"
          + "DESCRIPTION:Regular working hours\nCATEGORIES:WORK,OFFICE\nEND:VAVAILABILITY");
      assertTrue(va.isValid());
      assertEquals("BUSY-UNAVAILABLE", va.getBusyType());
      assertEquals("Weekly Office Hours", va.getSummary().getValue());
    }
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    @DisplayName("should be invalid when UID is missing")
    void should_beInvalid_when_uidMissing() throws Exception {
      VAvailability va = createFromICalendar("BEGIN:VAVAILABILITY\nDTSTAMP:20230101T120000Z\n"
          + "BUSYTYPE:BUSY\nEND:VAVAILABILITY");
      assertFalse(va.isValid());
    }

    @Test
    @DisplayName("should be invalid when DTSTAMP is missing")
    void should_beInvalid_when_dtstampMissing() throws Exception {
      VAvailability va = createFromICalendar("BEGIN:VAVAILABILITY\nUID:avail-123@example.com\n"
          + "BUSYTYPE:BUSY\nEND:VAVAILABILITY");
      assertFalse(va.isValid());
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("should generate valid iCalendar output")
    void should_generateValidOutput_when_toICalendarCalled() throws Exception {
      VAvailability va = createFromICalendar("BEGIN:VAVAILABILITY\nUID:avail-test@example.com\n"
          + "DTSTAMP:20230101T120000Z\nBUSYTYPE:FREE\nSUMMARY:Available Time\nEND:VAVAILABILITY");
      String output = va.toICalendar();
      String unfolded = output.replace("\r\n ", "").replace("\r\n", "\n");
      assertTrue(unfolded.contains("BEGIN:VAVAILABILITY"));
      assertTrue(unfolded.contains("BUSYTYPE:FREE"));
      assertTrue(unfolded.contains("END:VAVAILABILITY"));
    }
  }

  @Nested
  @DisplayName("Availability References Tests")
  class AvailabilityReferencesTests {

    @Test
    @DisplayName("should parse event availability references")
    void should_parseEventAvailability_when_referencesProvided() throws Exception {
      Event event = createEventFromICalendar("BEGIN:VEVENT\nUID:event-123@example.com\n"
          + "SUMMARY:Test Event\nDTSTART:20230101T100000Z\nDTEND:20230101T110000Z\n"
          + "AVAILABILITY:office-hours-001@example.com,meeting-room-avail-001@example.com\nEND:VEVENT");
      assertNotNull(event.getAvailabilityIds());
      assertEquals(2, event.getAvailabilityIds().size());
    }

    @Test
    @DisplayName("should parse todo availability references")
    void should_parseTodoAvailability_when_referencesProvided() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:todo-123@example.com\n"
          + "SUMMARY:Test Todo\nAVAILABILITY:personal-avail-001@example.com\nEND:VTODO");
      assertNotNull(todo.getAvailabilityIds());
      assertEquals(1, todo.getAvailabilityIds().size());
    }
  }
}
