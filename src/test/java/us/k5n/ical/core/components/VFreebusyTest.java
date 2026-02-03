package us.k5n.ical.core.components;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import us.k5n.ical.*;

/**
 * RFC 5545 Section 3.6.4: VFREEBUSY Component Tests
 */
@DisplayName("RFC 5545 Section 3.6.4: VFREEBUSY Component")
public class VFreebusyTest {
  private ICalendarParser parser;
  private List<String> textLines;

  @BeforeEach
  void setUp() {
    parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
    textLines = new ArrayList<String>();
  }

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.4: should parse basic VFREEBUSY with DTSTART/DTEND")
    void should_parseBasicFreebusy_when_dtstartDtendProvided() throws Exception {
      textLines.add("BEGIN:VFREEBUSY");
      textLines.add("DTSTART:20230101T090000Z");
      textLines.add("DTEND:20230101T100000Z");
      textLines.add("FREEBUSY:20230101T090000Z/20230101T093000Z");
      textLines.add("FREEBUSY:20230101T094500Z/20230101T100000Z");
      textLines.add("END:VFREEBUSY");
      Freebusy fb = new Freebusy(parser, 1, textLines);
      assertTrue(fb.isValid());
      assertNotNull(fb.getStartDate());
      assertNotNull(fb.getEndDate());
      assertEquals(2, fb.getBusyPeriods().size());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.4: should parse VFREEBUSY with DURATION")
    void should_parseFreebusy_when_durationProvided() throws Exception {
      textLines.add("BEGIN:VFREEBUSY");
      textLines.add("DTSTART:20230101T090000Z");
      textLines.add("DURATION:PT1H");
      textLines.add("FREEBUSY:20230101T090000Z/PT30M");
      textLines.add("END:VFREEBUSY");
      Freebusy fb = new Freebusy(parser, 1, textLines);
      assertTrue(fb.isValid());
      assertNotNull(fb.getDuration());
      assertNull(fb.getEndDate());
    }
  }

  @Nested
  @DisplayName("Optional Properties Tests")
  class OptionalPropertiesTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.4: should parse optional properties")
    void should_parseOptionalProperties_when_provided() throws Exception {
      textLines.add("BEGIN:VFREEBUSY");
      textLines.add("DTSTAMP:20230101T120000Z");
      textLines.add("UID:12345@freebusy.example.com");
      textLines.add("DTSTART:20230101T090000Z");
      textLines.add("DTEND:20230101T100000Z");
      textLines.add("SUMMARY:Busy time");
      textLines.add("CONTACT:John Doe <john@example.com>");
      textLines.add("URL:http://example.com/freebusy");
      textLines.add("COMMENT:Meeting time");
      textLines.add("FREEBUSY:20230101T090000Z/20230101T100000Z");
      textLines.add("ORGANIZER:mailto:organizer@example.com");
      textLines.add("END:VFREEBUSY");
      Freebusy fb = new Freebusy(parser, 1, textLines);
      assertTrue(fb.isValid());
      assertEquals("Busy time", fb.getSummary().getValue());
      assertNotNull(fb.getContact());
      assertNotNull(fb.getURL());
      assertNotNull(fb.getOrganizer());
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.4: should generate valid iCalendar output")
    void should_generateValidOutput_when_toICalendarCalled() throws Exception {
      textLines.add("BEGIN:VFREEBUSY");
      textLines.add("DTSTAMP:20230101T120000Z");
      textLines.add("UID:output-test@freebusy.example.com");
      textLines.add("DTSTART:20230101T090000Z");
      textLines.add("DTEND:20230101T100000Z");
      textLines.add("SUMMARY:Busy time");
      textLines.add("FREEBUSY:20230101T090000Z/20230101T093000Z");
      textLines.add("END:VFREEBUSY");
      Freebusy fb = new Freebusy(parser, 1, textLines);
      String output = fb.toICalendar();
      assertTrue(output.contains("BEGIN:VFREEBUSY"));
      assertTrue(output.contains("SUMMARY:Busy time"));
      assertTrue(output.contains("END:VFREEBUSY"));
    }
  }

  @Nested
  @DisplayName("FreebusyPeriod Tests")
  class FreebusyPeriodTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.4: should parse period with start/end")
    void should_parsePeriod_when_startEndProvided() throws Exception {
      FreebusyPeriod period = new FreebusyPeriod("FREEBUSY:20230101T090000Z/20230101T100000Z");
      assertTrue(period.isValid());
      assertNotNull(period.getStartDate());
      assertNotNull(period.getEndDate());
      assertNull(period.getDuration());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.4: should parse period with start/duration")
    void should_parsePeriod_when_startDurationProvided() throws Exception {
      FreebusyPeriod period = new FreebusyPeriod("FREEBUSY:20230101T090000Z/PT1H30M");
      assertTrue(period.isValid());
      assertNotNull(period.getStartDate());
      assertNotNull(period.getDuration());
      assertNull(period.getEndDate());
    }
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.4: should be invalid when DTSTART missing")
    void should_beInvalid_when_dtstartMissing() throws Exception {
      textLines.add("BEGIN:VFREEBUSY");
      textLines.add("DTEND:20230101T100000Z");
      textLines.add("FREEBUSY:20230101T090000Z/20230101T100000Z");
      textLines.add("END:VFREEBUSY");
      Freebusy fb = new Freebusy(parser, 1, textLines);
      assertFalse(fb.isValid());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.4: should be invalid when both DTEND and DURATION missing")
    void should_beInvalid_when_dtendAndDurationMissing() throws Exception {
      textLines.add("BEGIN:VFREEBUSY");
      textLines.add("DTSTART:20230101T090000Z");
      textLines.add("FREEBUSY:20230101T090000Z/20230101T100000Z");
      textLines.add("END:VFREEBUSY");
      Freebusy fb = new Freebusy(parser, 1, textLines);
      assertFalse(fb.isValid());
    }
  }

  @Nested
  @DisplayName("Multiple Periods Tests")
  class MultiplePeriodsTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.4: should parse multiple busy periods")
    void should_parseMultiplePeriods_when_commaDelimited() throws Exception {
      textLines.add("BEGIN:VFREEBUSY");
      textLines.add("DTSTART:20230101T090000Z");
      textLines.add("DTEND:20230101T170000Z");
      textLines.add("FREEBUSY:20230101T090000Z/20230101T100000Z,20230101T110000Z/20230101T120000Z,20230101T130000Z/20230101T140000Z");
      textLines.add("END:VFREEBUSY");
      Freebusy fb = new Freebusy(parser, 1, textLines);
      assertTrue(fb.isValid());
      assertEquals(3, fb.getBusyPeriods().size());
    }
  }
}
