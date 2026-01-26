 package us.k5n.ical;

 import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 9073: LOCATION-TYPE Property Tests
 *
 * Tests for the LOCATION-TYPE property as defined in RFC 9073, Section 6.1.
 * LOCATION-TYPE provides a way to differentiate multiple locations in VLOCATION components.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
 public class RFC9073_LocationTypeTest {

     @Test
     @DisplayName("RFC 9073: LOCATION-TYPE basic parsing")
     void testLocationTypeBasic() {
         String icalStr = "LOCATION-TYPE:ROOM";

         try {
             LocationType lt = new LocationType(icalStr);
             assertEquals("ROOM", lt.getValue());
         } catch (Exception e) {
             fail("LOCATION-TYPE should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: LOCATION-TYPE with multiple values")
     void testLocationTypeMultipleValues() {
         String icalStr = "LOCATION-TYPE:ROOM,PROJECTOR";

         try {
             LocationType lt = new LocationType(icalStr);
             assertEquals("ROOM,PROJECTOR", lt.getValue());
         } catch (Exception e) {
             fail("LOCATION-TYPE with multiple values should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: LOCATION-TYPE with parameters")
     void testLocationTypeWithParameters() {
         String icalStr = "LOCATION-TYPE;ORDER=1:VENUE";

         try {
             LocationType lt = new LocationType(icalStr);
             assertEquals("VENUE", lt.getValue());
             // Check if parameters are parsed (basic check)
             assertNotNull(lt.attributeList);
         } catch (Exception e) {
             fail("LOCATION-TYPE with parameters should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: LOCATION-TYPE serialization")
     void testLocationTypeSerialization() {
         LocationType lt = new LocationType();
         lt.setValue("ROOM");

         String icalOutput = lt.toICalendar();
         assertTrue(icalOutput.contains("LOCATION-TYPE:ROOM"),
                   "Serialized output should contain the location type");
     }

     @Test
     @DisplayName("RFC 9073: LOCATION-TYPE with RFC 4589 values")
     void testLocationTypeRFC4589Values() {
         // Test some common values from RFC 4589
         String[] validValues = {"ROOM", "AUDITORIUM", "STADIUM", "PARKING", "RESTAURANT"};

         for (String value : validValues) {
             String icalStr = "LOCATION-TYPE:" + value;
             try {
                 LocationType lt = new LocationType(icalStr);
                 assertEquals(value, lt.getValue(), "Should parse RFC 4589 value: " + value);
             } catch (Exception e) {
                 fail("LOCATION-TYPE with RFC 4589 value '" + value + "' should parse: " + e.getMessage());
             }
         }
     }

     @Test
     @DisplayName("RFC 9073: LOCATION-TYPE with custom values")
     void testLocationTypeCustomValues() {
         String icalStr = "LOCATION-TYPE:CUSTOM-LOCATION-TYPE";

         try {
             LocationType lt = new LocationType(icalStr);
             assertEquals("CUSTOM-LOCATION-TYPE", lt.getValue());
         } catch (Exception e) {
             fail("LOCATION-TYPE with custom value should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: LOCATION-TYPE empty value")
     void testLocationTypeEmptyValue() {
         LocationType lt = new LocationType();
         lt.setValue("");

         String icalOutput = lt.toICalendar();
         assertTrue(icalOutput.contains("LOCATION-TYPE:"), "Empty location type should serialize");
     }

     @Test
     @DisplayName("RFC 9073: LOCATION-TYPE with nonstandard parameters")
     void testLocationTypeNonstandardParameters() {
         String icalStr = "LOCATION-TYPE;X-CUSTOM=value:VENUE";

         try {
             LocationType lt = new LocationType(icalStr, ICalendarParser.PARSE_LOOSE);
             assertEquals("VENUE", lt.getValue());
             // In loose mode, nonstandard parameters should be accepted
         } catch (Exception e) {
             fail("LOCATION-TYPE with nonstandard parameters should parse in loose mode: " + e.getMessage());
         }
     }
 }