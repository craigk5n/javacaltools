 package us.k5n.ical;

 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import static org.junit.jupiter.api.Assertions.*;

 import us.k5n.ical.ICalendarParser;
 import us.k5n.ical.DefaultDataStore;

 import java.io.StringReader;

/**
 * RFC 9074: PROXIMITY Property Tests
 *
 * Tests for the PROXIMITY property as defined in RFC 9074, Section 8.1.
 * PROXIMITY indicates that a location-based trigger is applied to an alarm.
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
 public class RFC9074_ProximityTest {

     @Test
     @DisplayName("RFC 9074: PROXIMITY basic parsing")
     void testProximityBasic() throws Exception {
         String icalStr = "PROXIMITY:ARRIVE";

         Proximity proximity = new Proximity(icalStr);
         assertEquals("ARRIVE", proximity.getValue());
         assertTrue(proximity.isValidProximity());
         assertTrue(proximity.isArrive());
     }

     @Test
     @DisplayName("RFC 9074: PROXIMITY with all RFC defined values")
     void testProximityAllValues() throws Exception {
         String[] validValues = {"ARRIVE", "DEPART", "CONNECT", "DISCONNECT"};

         for (String value : validValues) {
             String icalStr = "PROXIMITY:" + value;
             Proximity proximity = new Proximity(icalStr);
             assertEquals(value, proximity.getValue(), "Should parse value: " + value);
             assertTrue(proximity.isValidProximity(), "Should be valid proximity: " + value);

             // Test specific type checks
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
     @DisplayName("RFC 9074: PROXIMITY with parameters")
     void testProximityWithParameters() throws Exception {
         String icalStr = "PROXIMITY;X-CUSTOM=value:DEPART";

         Proximity proximity = new Proximity(icalStr);
         assertEquals("DEPART", proximity.getValue());
         assertTrue(proximity.isValidProximity());
         assertTrue(proximity.isDepart());
         // Check if parameters are parsed (basic check)
         assertNotNull(proximity.attributeList);
     }

     @Test
     @DisplayName("RFC 9074: PROXIMITY serialization")
     void testProximitySerialization() throws Exception {
         Proximity proximity = new Proximity("PROXIMITY:CONNECT");
         String icalOutput = proximity.toICalendar();
         assertTrue(icalOutput.contains("PROXIMITY:CONNECT"),
                   "Serialized output should contain the proximity value");
     }

     @Test
     @DisplayName("RFC 9074: PROXIMITY with IANA token")
     void testProximityIanaToken() throws Exception {
         String icalStr = "PROXIMITY:X-CUSTOM-PROXIMITY";

         Proximity proximity = new Proximity(icalStr);
         assertEquals("X-CUSTOM-PROXIMITY", proximity.getValue());
         assertTrue(proximity.isValidProximity(), "Should be valid IANA token");
     }

     @Test
     @DisplayName("RFC 9074: PROXIMITY invalid value")
     void testProximityInvalidValue() throws Exception {
         String icalStr = "PROXIMITY:INVALID-PROXIMITY";

         Proximity proximity = new Proximity(icalStr);
         assertEquals("INVALID-PROXIMITY", proximity.getValue());
         // Should not be valid since it's not in the allowed list and not an IANA token
         assertFalse(proximity.isValidProximity());
     }

     @Test
     @DisplayName("RFC 9074: PROXIMITY in VALARM component")
     void testProximityInValarm() {
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
             assertTrue(parser.getAllErrors().isEmpty(), "PROXIMITY in VALARM should parse without errors");
         } catch (Exception e) {
             fail("PROXIMITY in VALARM should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9074: PROXIMITY empty value")
     void testProximityEmptyValue() throws Exception {
         Proximity proximity = new Proximity("PROXIMITY:");
         assertEquals("", proximity.getValue());
         assertFalse(proximity.isValidProximity(), "Empty value should not be valid");
     }
 }