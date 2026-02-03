package us.k5n.ical.performance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import us.k5n.ical.*;

/**
 * Test class for performance optimizations and benchmarking
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("Performance Benchmarks")
public class PerformanceTest {
  private ICalendarParser parser;

  @BeforeEach
  void setUp() {
    parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
  }

  @Nested
  @DisplayName("Streaming Mode Tests")
  class StreamingModeTests {

    @Test
    @DisplayName("should configure streaming mode settings")
    void should_configureSettings_when_streamingModeSet() throws Exception {
      ICalendarParser streamingParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);

      assertFalse(streamingParser.isStreamingMode());
      assertEquals(10000, streamingParser.getMaxComponentSize());

      streamingParser.setStreamingMode(true);
      streamingParser.setMaxComponentSize(5000);

      assertTrue(streamingParser.isStreamingMode());
    }
  }

  @Nested
  @DisplayName("Performance Monitoring Tests")
  class MonitoringTests {

    @Test
    @DisplayName("should track performance metrics")
    void should_trackMetrics_when_performanceMonitoringEnabled() throws Exception {
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
  }

  @Nested
  @DisplayName("Parsing Performance Tests")
  class ParsingPerformanceTests {

    @Test
    @DisplayName("should parse 50 events within reasonable time")
    void should_parseWithinTime_when_50EventsProvided() throws Exception {
      StringBuilder icalBuilder = new StringBuilder();
      icalBuilder.append("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Test//EN\n");

      for (int i = 0; i < 50; i++) {
        icalBuilder.append("BEGIN:VEVENT\n");
        icalBuilder.append("UID:event-").append(i).append("@example.com\n");
        icalBuilder.append("SUMMARY:Event ").append(i).append("\n");
        icalBuilder.append("DTSTART:202301")
            .append(String.format("%02d", i % 28 + 1)).append("T090000Z\n");
        icalBuilder.append("DTEND:202301")
            .append(String.format("%02d", i % 28 + 1)).append("T100000Z\n");
        icalBuilder.append("END:VEVENT\n");
      }

      icalBuilder.append("END:VCALENDAR");
      String icalStr = icalBuilder.toString();

      ICalendarParser normalParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
      normalParser.setPerformanceMonitoring(true);

      long startTime = System.currentTimeMillis();
      java.io.StringReader reader1 = new java.io.StringReader(icalStr);
      boolean success1 = normalParser.parse(reader1);
      long normalTime = System.currentTimeMillis() - startTime;

      ICalendarParser streamingParser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
      streamingParser.setStreamingMode(true);
      streamingParser.setPerformanceMonitoring(true);

      startTime = System.currentTimeMillis();
      java.io.StringReader reader2 = new java.io.StringReader(icalStr);
      boolean success2 = streamingParser.parse(reader2);
      long streamingTime = System.currentTimeMillis() - startTime;

      assertTrue(success1);
      assertTrue(success2);
      assertEquals(50, normalParser.getComponentsParsed());
      assertEquals(50, streamingParser.getComponentsParsed());
      assertTrue(normalTime < 1000);
      assertTrue(streamingTime < 1000);
    }
  }

  @Nested
  @DisplayName("Memory Efficiency Tests")
  class MemoryTests {

    @Test
    @DisplayName("should handle large components with many attendees")
    void should_handleLargeComponent_when_manyAttendeesProvided() throws Exception {
      StringBuilder eventBuilder = new StringBuilder();
      eventBuilder.append("BEGIN:VEVENT\n");
      eventBuilder.append("UID:large-event@example.com\n");
      eventBuilder.append("SUMMARY:Large Event\n");
      eventBuilder.append("DTSTART:20230101T090000Z\n");
      eventBuilder.append("DTEND:20230101T100000Z\n");

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

      DefaultDataStore ds = (DefaultDataStore) memParser.getDataStoreAt(0);
      List<Event> events = ds.getAllEvents();
      assertEquals(1, events.size());
      assertEquals(100, events.get(0).getAttendees().size());
    }

    @Test
    @DisplayName("should handle different buffer sizes")
    void should_handleBufferSize_when_defaultBufferUsed() throws Exception {
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
}
