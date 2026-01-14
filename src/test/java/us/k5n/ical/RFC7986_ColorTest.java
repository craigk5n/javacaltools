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
 * RFC 7986: COLOR Property Tests
 *
 * Tests for the COLOR property as defined in RFC 7986, Section 5.9.
 * COLOR specifies a color used for displaying calendar data.
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
 public class RFC7986_ColorTest {

     @Test
     @DisplayName("RFC 7986: COLOR basic parsing")
     void testColorBasic() {
         String icalStr = "COLOR:turquoise";

         try {
             Color color = new Color(icalStr);
             assertEquals("turquoise", color.getValue());
             assertTrue(color.isValidColor());
         } catch (Exception e) {
             fail("COLOR should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: COLOR with parameters")
     void testColorWithParameters() {
         String icalStr = "COLOR;X-CUSTOM=value:turquoise";

         try {
             Color color = new Color(icalStr);
             assertEquals("turquoise", color.getValue());
             assertTrue(color.isValidColor());
             // Check if parameters are parsed (basic check)
             assertNotNull(color.attributeList);
         } catch (Exception e) {
             fail("COLOR with parameters should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: COLOR serialization")
     void testColorSerialization() {
         Color color = new Color();
         color.setValue("turquoise");

         String icalOutput = color.toICalendar();
         assertTrue(icalOutput.contains("COLOR:turquoise"),
                   "Serialized output should contain the color");
     }

     @Test
     @DisplayName("RFC 7986: COLOR in VCALENDAR")
     void testColorInCalendar() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "COLOR:turquoise\n" +
             "BEGIN:VEVENT\n" +
             "UID:event-001@example.com\n" +
             "DTSTAMP:20230101T100000Z\n" +
             "DTSTART:20230101T110000Z\n" +
             "SUMMARY:Test Event\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "COLOR in VCALENDAR should parse without errors");
         } catch (Exception e) {
             fail("COLOR in VCALENDAR should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: COLOR in VEVENT")
     void testColorInEvent() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:event-001@example.com\n" +
             "DTSTAMP:20230101T100000Z\n" +
             "DTSTART:20230101T110000Z\n" +
             "SUMMARY:Test Event\n" +
             "COLOR:blue\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "COLOR in VEVENT should parse without errors");
         } catch (Exception e) {
             fail("COLOR in VEVENT should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: COLOR CSS3 color names")
     void testColorCss3Names() {
         String[] css3Colors = {
             "aliceblue", "antiquewhite", "aqua", "aquamarine", "azure",
             "beige", "bisque", "black", "blanchedalmond", "blue",
             "blueviolet", "brown", "burlywood", "cadetblue", "chartreuse"
         };

         for (String colorName : css3Colors) {
             String icalStr = "COLOR:" + colorName;
             try {
                 Color color = new Color(icalStr);
                 assertEquals(colorName, color.getValue(), "Should parse CSS3 color: " + colorName);
                 assertTrue(color.isValidColor(), "Should be valid color: " + colorName);
             } catch (Exception e) {
                 fail("COLOR with CSS3 color '" + colorName + "' should parse: " + e.getMessage());
             }
         }
     }

     @Test
     @DisplayName("RFC 7986: COLOR case insensitive")
     void testColorCaseInsensitive() {
         String icalStr = "COLOR:Turquoise";

         try {
             Color color = new Color(icalStr);
             assertEquals("Turquoise", color.getValue());
             assertTrue(color.isValidColor());
         } catch (Exception e) {
             fail("COLOR should be case insensitive: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 7986: COLOR empty value")
     void testColorEmptyValue() {
         Color color = new Color();
         color.setValue("");

         String icalOutput = color.toICalendar();
         assertTrue(icalOutput.contains("COLOR:"), "Empty color should serialize");
         assertFalse(color.isValidColor(), "Empty value should not be valid");
     }

     @Test
     @DisplayName("RFC 7986: COLOR with invalid characters")
     void testColorInvalidCharacters() {
         String icalStr = "COLOR:red123";

         try {
             Color color = new Color(icalStr);
             assertEquals("red123", color.getValue());
             // Our validation rejects non-alphabetic characters
             assertFalse(color.isValidColor());
         } catch (Exception e) {
             fail("COLOR with invalid characters should still parse: " + e.getMessage());
         }
     }
 }