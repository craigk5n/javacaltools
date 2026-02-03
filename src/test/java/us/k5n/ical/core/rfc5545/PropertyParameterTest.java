package us.k5n.ical.core.rfc5545;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import us.k5n.ical.*;

/**
 * RFC 5545: Property Parameter Validation Tests
 *
 * Tests for complete parameter validation rules as defined in RFC 5545.
 * Covers parameter syntax, allowed values, and error handling.
 */
@DisplayName("RFC 5545: Property Parameter Validation")
public class PropertyParameterTest {

  private String wrapInEvent(String propertyLine) {
    return "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Test//Calendar//EN\n" +
        "BEGIN:VEVENT\nUID:test@example.com\nDTSTAMP:20240101T100000Z\n" +
        "DTSTART:20240101T140000Z\nSUMMARY:Test\n" +
        propertyLine + "\nEND:VEVENT\nEND:VCALENDAR";
  }

  @Nested
  @DisplayName("VALUE Parameter Tests")
  class ValueParameterTests {

    @Test
    @DisplayName("RFC 5545: should accept all valid VALUE parameters")
    void should_acceptAllValues_when_validValueParametersProvided() {
      String[] validValues = {"BINARY", "BOOLEAN", "CAL-ADDRESS", "DATE", "DATE-TIME",
          "DURATION", "FLOAT", "INTEGER", "PERIOD", "RECUR", "TEXT", "TIME", "URI",
          "UTC-OFFSET"};

      for (String value : validValues) {
        String icalData = wrapInEvent("X-CUSTOM;VALUE=" + value + ":test value");

        try {
          ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
          parser.parse(new StringReader(icalData));
          assertTrue(parser.getAllErrors().isEmpty(),
              "VALUE=" + value + " should be accepted in loose mode");
        } catch (Exception e) {
          fail("VALUE=" + value + " should parse: " + e.getMessage());
        }
      }
    }
  }

  @Nested
  @DisplayName("LANGUAGE Parameter Tests")
  class LanguageParameterTests {

    @Test
    @DisplayName("RFC 5545: should parse LANGUAGE parameter")
    void should_parseLanguage_when_languageParameterProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:test@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY;LANGUAGE=en:Meeting\n" +
          "SUMMARY;LANGUAGE=fr:Réunion\n" +
          "DESCRIPTION;LANGUAGE=en:English description\n" +
          "DESCRIPTION;LANGUAGE=es:Descripción en español\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "LANGUAGE parameters should parse without errors");
      } catch (Exception e) {
        fail("LANGUAGE parameters should parse: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("TZID Parameter Tests")
  class TzidParameterTests {

    @Test
    @DisplayName("RFC 5545: should parse TZID parameter")
    void should_parseTzid_when_tzidParameterProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VTIMEZONE\n" +
          "TZID:America/New_York\n" +
          "BEGIN:STANDARD\n" +
          "DTSTART:20231105T020000\n" +
          "TZOFFSETFROM:-0400\n" +
          "TZOFFSETTO:-0500\n" +
          "END:STANDARD\n" +
          "END:VTIMEZONE\n" +
          "BEGIN:VEVENT\n" +
          "UID:test@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART;TZID=America/New_York:20240101T140000\n" +
          "SUMMARY:Test Event\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "TZID parameter should parse without errors");
      } catch (Exception e) {
        fail("TZID parameter should parse: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("ATTENDEE Parameter Tests")
  class AttendeeParameterTests {

    @Test
    @DisplayName("RFC 5545: should accept all CUTYPE values")
    void should_acceptAllCutypes_when_validCutypeProvided() {
      String[] cutypes = {"INDIVIDUAL", "GROUP", "RESOURCE", "ROOM", "UNKNOWN"};

      for (String cutype : cutypes) {
        String icalData = wrapInEvent(
            "ATTENDEE;CUTYPE=" + cutype + ":mailto:test@example.com");

        try {
          ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
          parser.parse(new StringReader(icalData));
          assertTrue(parser.getAllErrors().isEmpty(),
              "CUTYPE=" + cutype + " should be valid");
        } catch (Exception e) {
          fail("CUTYPE=" + cutype + " should parse: " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("RFC 5545: should accept all ROLE values")
    void should_acceptAllRoles_when_validRoleProvided() {
      String[] roles = {"CHAIR", "REQ-PARTICIPANT", "OPT-PARTICIPANT", "NON-PARTICIPANT"};

      for (String role : roles) {
        String icalData = wrapInEvent(
            "ATTENDEE;ROLE=" + role + ":mailto:test@example.com");

        try {
          ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
          parser.parse(new StringReader(icalData));
          assertTrue(parser.getAllErrors().isEmpty(),
              "ROLE=" + role + " should be valid");
        } catch (Exception e) {
          fail("ROLE=" + role + " should parse: " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("RFC 5545: should accept all PARTSTAT values")
    void should_acceptAllPartstats_when_validPartstatProvided() {
      String[] partstats = {"NEEDS-ACTION", "ACCEPTED", "DECLINED", "TENTATIVE",
          "DELEGATED", "COMPLETED", "IN-PROCESS"};

      for (String partstat : partstats) {
        String icalData = wrapInEvent(
            "ATTENDEE;PARTSTAT=" + partstat + ":mailto:test@example.com");

        try {
          ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
          parser.parse(new StringReader(icalData));
          assertTrue(parser.getAllErrors().isEmpty(),
              "PARTSTAT=" + partstat + " should be valid");
        } catch (Exception e) {
          fail("PARTSTAT=" + partstat + " should parse: " + e.getMessage());
        }
      }
    }

    @Test
    @DisplayName("RFC 5545: should parse RSVP parameter")
    void should_parseRsvp_when_rsvpParameterProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:test@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY:Test Event\n" +
          "ATTENDEE;RSVP=TRUE:mailto:attendee@example.com\n" +
          "ATTENDEE;RSVP=FALSE:mailto:chair@example.com\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "RSVP parameters should parse without errors");
      } catch (Exception e) {
        fail("RSVP parameters should parse: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545: should parse multiple parameters on single property")
    void should_parseMultiple_when_multipleParametersProvided() {
      String icalData = wrapInEvent(
          "ATTENDEE;CUTYPE=INDIVIDUAL;ROLE=REQ-PARTICIPANT;PARTSTAT=ACCEPTED;" +
          "RSVP=FALSE;CN=John Doe:mailto:john@example.com");

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "Multiple parameters should parse without errors");
      } catch (Exception e) {
        fail("Multiple parameters should parse: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Parameter Quoting Tests")
  class QuotingTests {

    @Test
    @DisplayName("RFC 5545: should parse quoted parameter values")
    void should_parseQuoted_when_quotedValuesProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:test@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY:Test Event\n" +
          "ATTENDEE;CN=\"Dr. John \\\"Johnny\\\" Doe\":mailto:john@example.com\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "Quoted parameter values should parse without errors");
      } catch (Exception e) {
        fail("Quoted parameter values should parse: " + e.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("Error Handling Tests")
  class ErrorHandlingTests {

    @Test
    @DisplayName("RFC 5545: should accept unknown parameters in loose mode")
    void should_acceptUnknown_when_looseModeUsed() {
      String icalData = wrapInEvent(
          "ATTENDEE;INVALID-PARAM=test:mailto:test@example.com");

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "Unknown parameters should be accepted in loose mode");
      } catch (Exception e) {
        fail("Unknown parameters should parse in loose mode: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("RFC 5545: should handle case-insensitive parameters")
    void should_handleCaseInsensitive_when_lowercaseParamsProvided() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:test@example.com\n" +
          "DTSTAMP:20240101T100000Z\n" +
          "DTSTART:20240101T140000Z\n" +
          "SUMMARY:Test Event\n" +
          "ATTENDEE;cutype=individual;ROLE=req-participant:mailto:test@example.com\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "Case-insensitive parameters should parse without errors");
      } catch (Exception e) {
        fail("Case-insensitive parameters should parse: " + e.getMessage());
      }
    }
  }
}
