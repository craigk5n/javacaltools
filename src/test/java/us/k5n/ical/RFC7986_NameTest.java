 package us.k5n.ical;

 import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * RFC 7986: NAME Property Tests
 *
 * Tests for the NAME property as defined in RFC 7986, Section 5.1.
 * NAME specifies the name of the calendar for presentation to users.
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
 public class RFC7986_NameTest {

     @Test
     @DisplayName("RFC 7986: NAME basic parsing")
     void testNameBasic() {
         String icalStr = "NAME:Company Vacation Days";

         try {
             Name name = new Name(icalStr);
             assertEquals("Company Vacation Days", name.getValue());
         } catch (Exception e) {
             fail("NAME should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: NAME with language parameter")
     void testNameWithLanguage() {
         String icalStr = "NAME;LANGUAGE=en:Company Vacation Days";

         try {
             Name name = new Name(icalStr);
             assertEquals("Company Vacation Days", name.getValue());
             // Check if parameters are parsed (basic check)
             assertNotNull(name.attributeList);
         } catch (Exception e) {
             fail("NAME with language parameter should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: NAME with alternate representation")
     void testNameWithAltRep() {
         String icalStr = "NAME;ALTREP=\"http://example.com/name\":Company Vacation Days";

         try {
             Name name = new Name(icalStr);
             assertEquals("Company Vacation Days", name.getValue());
             // Check if parameters are parsed (basic check)
             assertNotNull(name.attributeList);
         } catch (Exception e) {
             fail("NAME with ALTREP parameter should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: NAME serialization")
     void testNameSerialization() {
         Name name = new Name();
         name.setValue("Company Vacation Days");

         String icalOutput = name.toICalendar();
         assertTrue(icalOutput.contains("NAME:Company Vacation Days"),
                   "Serialized output should contain the calendar name");
     }

     @Test
     @DisplayName("RFC 7986: NAME in VCALENDAR")
     void testNameInCalendar() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "NAME:Company Vacation Days\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "NAME in VCALENDAR should parse without errors");

             DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
             // Note: Need to check if DataStore supports NAME property
             // For now, just verify parsing succeeds
         } catch (Exception e) {
             fail("NAME in VCALENDAR should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: Multiple NAME properties")
     void testMultipleNames() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "NAME;LANGUAGE=en:Company Vacation Days\n" +
             "NAME;LANGUAGE=fr:Jours de congé d'entreprise\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "Multiple NAME properties should parse without errors");
         } catch (Exception e) {
             fail("Multiple NAME properties should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: NAME with special characters")
     void testNameWithSpecialChars() {
         String specialName = "Company's \"Special\" Vacation Days & Holidays";

         String icalStr = "NAME:" + specialName;

         try {
             Name name = new Name(icalStr);
             assertEquals(specialName, name.getValue());
         } catch (Exception e) {
             fail("NAME with special characters should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: NAME empty value")
     void testNameEmptyValue() {
         Name name = new Name();
         name.setValue("");

         String icalOutput = name.toICalendar();
         assertTrue(icalOutput.contains("NAME:"), "Empty name should serialize");
     }
 }