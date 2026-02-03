package us.k5n.ical.extensions.rfc9074;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import us.k5n.ical.*;

/**
 * RFC 9074: PROXIMITY Property Tests
 *
 * Tests for the PROXIMITY property as defined in RFC 9074, Section 8.1.
 * PROXIMITY indicates that a location-based trigger is applied to an alarm.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 9074 Section 8.1: PROXIMITY Property")
public class ProximityPropertyTest {

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 9074 Section 8.1: should parse ARRIVE value")
    void should_parseArrive_when_arriveValueProvided() throws Exception {
      Proximity proximity = new Proximity("PROXIMITY:ARRIVE");
      assertEquals("ARRIVE", proximity.getValue());
      assertTrue(proximity.isValidProximity());
      assertTrue(proximity.isArrive());
    }

    @Test
    @DisplayName("RFC 9074 Section 8.1: should parse all RFC-defined values")
    void should_parseAllValues_when_validValuesProvided() throws Exception {
      String[] validValues = {"ARRIVE", "DEPART", "CONNECT", "DISCONNECT"};

      for (String value : validValues) {
        Proximity proximity = new Proximity("PROXIMITY:" + value);
        assertEquals(value, proximity.getValue(), "Should parse value: " + value);
        assertTrue(proximity.isValidProximity(), "Should be valid proximity: " + value);

        switch (value) {
          case "ARRIVE":
            assertTrue(proximity.isArrive());
            break;
          case "DEPART":
            assertTrue(proximity.isDepart());
            break;
          case "CONNECT":
            assertTrue(proximity.isConnect());
            break;
          case "DISCONNECT":
            assertTrue(proximity.isDisconnect());
            break;
        }
      }
    }

    @Test
    @DisplayName("RFC 9074 Section 8.1: should parse with parameters")
    void should_parseWithParams_when_parametersProvided() throws Exception {
      Proximity proximity = new Proximity("PROXIMITY;X-CUSTOM=value:DEPART");
      assertEquals("DEPART", proximity.getValue());
      assertTrue(proximity.isValidProximity());
      assertTrue(proximity.isDepart());
      assertNotNull(proximity.getNamedAttribute("X-CUSTOM"));
    }

    @Test
    @DisplayName("RFC 9074 Section 8.1: should accept IANA token")
    void should_acceptIanaToken_when_xPrefixedValueProvided() throws Exception {
      Proximity proximity = new Proximity("PROXIMITY:X-CUSTOM-PROXIMITY");
      assertEquals("X-CUSTOM-PROXIMITY", proximity.getValue());
      assertTrue(proximity.isValidProximity(), "Should be valid IANA token");
    }
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    @DisplayName("RFC 9074 Section 8.1: should reject invalid value")
    void should_rejectInvalid_when_invalidValueProvided() throws Exception {
      Proximity proximity = new Proximity("PROXIMITY:INVALID-PROXIMITY");
      assertEquals("INVALID-PROXIMITY", proximity.getValue());
      assertFalse(proximity.isValidProximity());
    }

    @Test
    @DisplayName("RFC 9074 Section 8.1: should reject empty value")
    void should_rejectEmpty_when_emptyValueProvided() throws Exception {
      Proximity proximity = new Proximity("PROXIMITY:");
      assertEquals("", proximity.getValue());
      assertFalse(proximity.isValidProximity(), "Empty value should not be valid");
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 9074 Section 8.1: should serialize PROXIMITY")
    void should_serialize_when_toICalendarCalled() throws Exception {
      Proximity proximity = new Proximity("PROXIMITY:CONNECT");
      String icalOutput = proximity.toICalendar();
      assertTrue(icalOutput.contains("PROXIMITY:CONNECT"),
          "Serialized output should contain the proximity value");
    }
  }

  @Nested
  @DisplayName("VALARM Integration Tests")
  class ValarmIntegrationTests {

    @Test
    @DisplayName("RFC 9074 Section 8.1: should parse PROXIMITY in VALARM")
    void should_parseInValarm_when_valarmContainsProximity() {
      String icalData = "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//Test//Calendar//EN\n" +
          "BEGIN:VEVENT\n" +
          "UID:event-001@example.com\n" +
          "DTSTAMP:20230101T100000Z\n" +
          "DTSTART:20230101T110000Z\n" +
          "SUMMARY:Meeting\n" +
          "BEGIN:VALARM\n" +
          "UID:alarm-001\n" +
          "TRIGGER;VALUE=DATE-TIME:19760401T005545Z\n" +
          "DESCRIPTION:Remember to buy milk\n" +
          "PROXIMITY:DEPART\n" +
          "BEGIN:VLOCATION\n" +
          "UID:loc-001\n" +
          "NAME:Office\n" +
          "URL:geo:40.443,-79.945;u=10\n" +
          "END:VLOCATION\n" +
          "ACTION:DISPLAY\n" +
          "END:VALARM\n" +
          "END:VEVENT\n" +
          "END:VCALENDAR";

      try {
        ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
        parser.parse(new StringReader(icalData));
        assertTrue(parser.getAllErrors().isEmpty(),
            "PROXIMITY in VALARM should parse without errors");
      } catch (Exception e) {
        fail("PROXIMITY in VALARM should parse: " + e.getMessage());
      }
    }
  }
}
