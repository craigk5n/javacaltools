 package us.k5n.ical;

 import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 9073: PARTICIPANT-TYPE Property Tests
 *
 * Tests for the PARTICIPANT-TYPE property as defined in RFC 9073, Section 6.2.
 * PARTICIPANT-TYPE defines the type of participation in events/tasks.
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
 public class RFC9073_ParticipantTypeTest {

     @Test
     @DisplayName("RFC 9073: PARTICIPANT-TYPE basic parsing")
     void testParticipantTypeBasic() {
         String icalStr = "PARTICIPANT-TYPE:ACTIVE";

         try {
             ParticipantType pt = new ParticipantType(icalStr);
             assertEquals("ACTIVE", pt.getValue());
             assertTrue(pt.isValidType());
         } catch (Exception e) {
             fail("PARTICIPANT-TYPE should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: PARTICIPANT-TYPE with all RFC defined values")
     void testParticipantTypeAllValues() {
         String[] validValues = {
             "ACTIVE", "INACTIVE", "SPONSOR", "CONTACT", "BOOKING-CONTACT",
             "EMERGENCY-CONTACT", "PUBLICITY-CONTACT", "PLANNER-CONTACT",
             "PERFORMER", "SPEAKER"
         };

         for (String value : validValues) {
             String icalStr = "PARTICIPANT-TYPE:" + value;
             try {
                 ParticipantType pt = new ParticipantType(icalStr);
                 assertEquals(value, pt.getValue(), "Should parse value: " + value);
                 assertTrue(pt.isValidType(), "Should be valid type: " + value);
             } catch (Exception e) {
                 fail("PARTICIPANT-TYPE with value '" + value + "' should parse: " + e.getMessage());
             }
         }
     }

     @Test
     @DisplayName("RFC 9073: PARTICIPANT-TYPE with parameters")
     void testParticipantTypeWithParameters() {
         String icalStr = "PARTICIPANT-TYPE;ORDER=1:PERFORMER";

         try {
             ParticipantType pt = new ParticipantType(icalStr);
             assertEquals("PERFORMER", pt.getValue());
             assertTrue(pt.isValidType());
             // Check if parameters are parsed (basic check)
             assertNotNull(pt.attributeList);
         } catch (Exception e) {
             fail("PARTICIPANT-TYPE with parameters should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: PARTICIPANT-TYPE serialization")
     void testParticipantTypeSerialization() {
         ParticipantType pt = new ParticipantType();
         pt.setValue("SPEAKER");

         String icalOutput = pt.toICalendar();
         assertTrue(icalOutput.contains("PARTICIPANT-TYPE:SPEAKER"),
                   "Serialized output should contain the participant type");
     }

     @Test
     @DisplayName("RFC 9073: PARTICIPANT-TYPE with IANA token")
     void testParticipantTypeIanaToken() {
         String[] ianaTokens = {"X-CUSTOM-TYPE", "MYTYPE", "ORG-TYPE"};

         for (String token : ianaTokens) {
             String icalStr = "PARTICIPANT-TYPE:" + token;
             try {
                 ParticipantType pt = new ParticipantType(icalStr);
                 assertEquals(token, pt.getValue());
                 assertTrue(pt.isValidType(), "Should be valid IANA token: " + token);
             } catch (Exception e) {
                 fail("PARTICIPANT-TYPE with IANA token '" + token + "' should parse: " + e.getMessage());
             }
         }
     }

     @Test
     @DisplayName("RFC 9073: PARTICIPANT-TYPE in PARTICIPANT component")
     void testParticipantTypeInParticipant() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:PARTICIPANT\n" +
             "UID:participant-001@example.com\n" +
             "PARTICIPANT-TYPE:PERFORMER\n" +
             "END:PARTICIPANT\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "PARTICIPANT-TYPE in PARTICIPANT should parse without errors");

             DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
             Participant participant = ds.getAllParticipants().get(0);
             assertNotNull(participant.getParticipantType(), "Participant should have PARTICIPANT-TYPE");
             // Note: Participant currently stores participantType as String, not ParticipantType object
         } catch (Exception e) {
             fail("PARTICIPANT-TYPE in PARTICIPANT should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: PARTICIPANT-TYPE empty value")
     void testParticipantTypeEmptyValue() {
         ParticipantType pt = new ParticipantType();
         pt.setValue("");

         String icalOutput = pt.toICalendar();
         assertTrue(icalOutput.contains("PARTICIPANT-TYPE:"), "Empty participant type should serialize");
         assertFalse(pt.isValidType(), "Empty value should not be valid");
     }

     @Test
     @DisplayName("RFC 9073: PARTICIPANT-TYPE with nonstandard parameters")
     void testParticipantTypeNonstandardParameters() {
         String icalStr = "PARTICIPANT-TYPE;X-CUSTOM=value:CONTACT";

         try {
             ParticipantType pt = new ParticipantType(icalStr, ICalendarParser.PARSE_LOOSE);
             assertEquals("CONTACT", pt.getValue());
             assertTrue(pt.isValidType());
             // In loose mode, nonstandard parameters should be accepted
         } catch (Exception e) {
             fail("PARTICIPANT-TYPE with nonstandard parameters should parse in loose mode: " + e.getMessage());
         }
     }
 }