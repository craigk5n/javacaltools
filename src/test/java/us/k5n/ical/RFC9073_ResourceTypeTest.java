 package us.k5n.ical;

 import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 9073: RESOURCE-TYPE Property Tests
 *
 * Tests for the RESOURCE-TYPE property as defined in RFC 9073, Section 6.3.
 * RESOURCE-TYPE provides a way to differentiate multiple resources in VRESOURCE components.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
 public class RFC9073_ResourceTypeTest {

     @Test
     @DisplayName("RFC 9073: RESOURCE-TYPE basic parsing")
     void testResourceTypeBasic() {
         String icalStr = "RESOURCE-TYPE:ROOM";

         try {
             ResourceType rt = new ResourceType(icalStr);
             assertEquals("ROOM", rt.getValue());
             assertTrue(rt.isValidType());
         } catch (Exception e) {
             fail("RESOURCE-TYPE should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: RESOURCE-TYPE with all RFC defined values")
     void testResourceTypeAllValues() {
         String[] validValues = {
             "ROOM", "PROJECTOR", "REMOTE-CONFERENCE-AUDIO", "REMOTE-CONFERENCE-VIDEO"
         };

         for (String value : validValues) {
             String icalStr = "RESOURCE-TYPE:" + value;
             try {
                 ResourceType rt = new ResourceType(icalStr);
                 assertEquals(value, rt.getValue(), "Should parse value: " + value);
                 assertTrue(rt.isValidType(), "Should be valid type: " + value);
             } catch (Exception e) {
                 fail("RESOURCE-TYPE with value '" + value + "' should parse: " + e.getMessage());
             }
         }
     }

     @Test
     @DisplayName("RFC 9073: RESOURCE-TYPE with parameters")
     void testResourceTypeWithParameters() {
         String icalStr = "RESOURCE-TYPE;ORDER=1:PROJECTOR";

         try {
             ResourceType rt = new ResourceType(icalStr);
             assertEquals("PROJECTOR", rt.getValue());
             assertTrue(rt.isValidType());
             // Check if parameters are parsed (basic check)
             assertNotNull(rt.attributeList);
         } catch (Exception e) {
             fail("RESOURCE-TYPE with parameters should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: RESOURCE-TYPE serialization")
     void testResourceTypeSerialization() {
         ResourceType rt = new ResourceType();
         rt.setValue("ROOM");

         String icalOutput = rt.toICalendar();
         assertTrue(icalOutput.contains("RESOURCE-TYPE:ROOM"),
                   "Serialized output should contain the resource type");
     }

     @Test
     @DisplayName("RFC 9073: RESOURCE-TYPE with IANA token")
     void testResourceTypeIanaToken() {
         String[] ianaTokens = {"X-CUSTOM-RESOURCE", "X-MY-RESOURCE"};

         for (String token : ianaTokens) {
             String icalStr = "RESOURCE-TYPE:" + token;
             try {
                 ResourceType rt = new ResourceType(icalStr);
                 assertEquals(token, rt.getValue());
                 assertTrue(rt.isValidType(), "Should be valid IANA token: " + token);
             } catch (Exception e) {
                 fail("RESOURCE-TYPE with IANA token '" + token + "' should parse: " + e.getMessage());
             }
         }
     }

     @Test
     @DisplayName("RFC 9073: RESOURCE-TYPE empty value")
     void testResourceTypeEmptyValue() {
         ResourceType rt = new ResourceType();
         rt.setValue("");

         String icalOutput = rt.toICalendar();
         assertTrue(icalOutput.contains("RESOURCE-TYPE:"), "Empty resource type should serialize");
         assertFalse(rt.isValidType(), "Empty value should not be valid");
     }

     @Test
     @DisplayName("RFC 9073: RESOURCE-TYPE with nonstandard parameters")
     void testResourceTypeNonstandardParameters() {
         String icalStr = "RESOURCE-TYPE;X-CUSTOM=value:ROOM";

         try {
             ResourceType rt = new ResourceType(icalStr, ICalendarParser.PARSE_LOOSE);
             assertEquals("ROOM", rt.getValue());
             assertTrue(rt.isValidType());
             // In loose mode, nonstandard parameters should be accepted
         } catch (Exception e) {
             fail("RESOURCE-TYPE with nonstandard parameters should parse in loose mode: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: RESOURCE-TYPE invalid value")
     void testResourceTypeInvalidValue() {
         String icalStr = "RESOURCE-TYPE:invalid-type";

         try {
             ResourceType rt = new ResourceType(icalStr);
             assertEquals("invalid-type", rt.getValue());
             // Should not be valid since it's not in the allowed list and not an IANA token
             assertFalse(rt.isValidType(), "Invalid value should not be valid");
         } catch (Exception e) {
             fail("RESOURCE-TYPE with invalid value should still parse: " + e.getMessage());
         }
     }
 }