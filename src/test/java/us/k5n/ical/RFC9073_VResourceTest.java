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
 * RFC 9073: VRESOURCE Component Tests
 *
 * Tests for the VRESOURCE component as defined in RFC 9073, Section 7.3.
 * VRESOURCE provides typed references to external information about resources
 * used by calendar entities (rooms, projectors, conferencing capabilities, etc.).
 *
 * @author Craig Knudsen, craig@k5n.us
 */
 public class RFC9073_VResourceTest {

     @Test
     @DisplayName("RFC 9073: VRESOURCE basic parsing")
     void testVResourceBasic() {
         String icalData = "BEGIN:VRESOURCE\n" +
             "UID:456789-abcdef-98765432\n" +
             "NAME:The projector\n" +
             "RESOURCE-TYPE:PROJECTOR\n" +
             "STRUCTURED-DATA;VALUE=URI:http://dir.example.com/resources/projector.vcf\n" +
             "END:VRESOURCE";

         try {
             VResource vres = new VResource(icalData);
             assertEquals("456789-abcdef-98765432", vres.getUid());
             assertEquals("The projector", vres.getName());
             assertTrue(vres.isValid());
             assertEquals(1, vres.getStructuredDataList().size());
             assertEquals("http://dir.example.com/resources/projector.vcf", vres.getStructuredDataList().get(0));
         } catch (Exception e) {
             fail("VRESOURCE should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: VRESOURCE with resource type")
     void testVResourceWithResourceType() {
         String icalData = "BEGIN:VRESOURCE\n" +
             "UID:room-001\n" +
             "NAME:Conference Room A\n" +
             "RESOURCE-TYPE:ROOM\n" +
             "END:VRESOURCE";

         try {
             VResource vres = new VResource(icalData);
             assertEquals("room-001", vres.getUid());
             assertEquals("Conference Room A", vres.getName());
             assertNotNull(vres.getResourceType());
             assertEquals("ROOM", vres.getResourceType().getValue());
         } catch (Exception e) {
             fail("VRESOURCE with resource type should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: VRESOURCE with different resource types")
     void testVResourceWithDifferentTypes() {
         String[] types = {"ROOM", "PROJECTOR", "REMOTE-CONFERENCE-AUDIO", "REMOTE-CONFERENCE-VIDEO"};

         for (String type : types) {
             String icalData = "BEGIN:VRESOURCE\n" +
                 "UID:resource-001\n" +
                 "NAME:Test Resource\n" +
                 "RESOURCE-TYPE:" + type + "\n" +
                 "END:VRESOURCE";

             try {
                 VResource vres = new VResource(icalData);
                 assertEquals("resource-001", vres.getUid());
                 assertEquals("Test Resource", vres.getName());
                 assertNotNull(vres.getResourceType());
                 assertEquals(type, vres.getResourceType().getValue());
             } catch (Exception e) {
                 fail("VRESOURCE with type '" + type + "' should parse: " + e.getMessage());
             }
         }
     }

     @Test
     @DisplayName("RFC 9073: VRESOURCE serialization")
     void testVResourceSerialization() {
         VResource vres = new VResource();
         vres.setUid("resource-001");
         vres.setName("Conference Projector");
         vres.addStructuredData("http://example.com/resource.vcf");

         String icalOutput = vres.toICalendar();
         assertTrue(icalOutput.contains("BEGIN:VRESOURCE"));
         assertTrue(icalOutput.contains("UID:resource-001"));
         assertTrue(icalOutput.contains("NAME:Conference Projector"));
         assertTrue(icalOutput.contains("STRUCTURED-DATA;VALUE=URI:http://example.com/resource.vcf"));
         assertTrue(icalOutput.contains("END:VRESOURCE"));
     }

     @Test
     @DisplayName("RFC 9073: VRESOURCE in VEVENT")
     void testVResourceInEvent() {
         String icalData = "BEGIN:VCALENDAR\n" +
             "VERSION:2.0\n" +
             "PRODID:-//Test//Calendar//EN\n" +
             "BEGIN:VEVENT\n" +
             "UID:event-001@example.com\n" +
             "DTSTAMP:20230101T100000Z\n" +
             "DTSTART:20230101T110000Z\n" +
             "SUMMARY:Meeting\n" +
             "BEGIN:VRESOURCE\n" +
             "UID:projector-001\n" +
             "NAME:Main Projector\n" +
             "RESOURCE-TYPE:PROJECTOR\n" +
             "END:VRESOURCE\n" +
             "END:VEVENT\n" +
             "END:VCALENDAR";

         try {
             ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
             parser.parse(new StringReader(icalData));
             assertTrue(parser.getAllErrors().isEmpty(), "VRESOURCE in VEVENT should parse without errors");
         } catch (Exception e) {
             fail("VRESOURCE in VEVENT should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: VRESOURCE validation")
     void testVResourceValidation() {
         VResource vres = new VResource();
         assertFalse(vres.isValid(), "VRESOURCE without UID should be invalid");

         vres.setUid("test-uid");
         assertTrue(vres.isValid(), "VRESOURCE with UID should be valid");
     }

     @Test
     @DisplayName("RFC 9073: VRESOURCE with description")
     void testVResourceWithDescription() {
         String icalData = "BEGIN:VRESOURCE\n" +
             "UID:room-001\n" +
             "NAME:Conference Room A\n" +
             "DESCRIPTION:A large conference room with projector\n" +
             "RESOURCE-TYPE:ROOM\n" +
             "END:VRESOURCE";

         try {
             VResource vres = new VResource(icalData);
             assertEquals("room-001", vres.getUid());
             assertEquals("Conference Room A", vres.getName());
             assertEquals("A large conference room with projector", vres.getDescription());
             assertNotNull(vres.getResourceType());
             assertEquals("ROOM", vres.getResourceType().getValue());
         } catch (Exception e) {
             fail("VRESOURCE with description should parse: " + e.getMessage());
         }
     }

     @Test
     @DisplayName("RFC 9073: VRESOURCE with geo coordinates")
     void testVResourceWithGeo() {
         String icalData = "BEGIN:VRESOURCE\n" +
             "UID:location-001\n" +
             "NAME:Building A\n" +
             "GEO:37.7749;-122.4194\n" +
             "END:VRESOURCE";

         try {
             VResource vres = new VResource(icalData);
             assertEquals("location-001", vres.getUid());
             assertEquals("Building A", vres.getName());
             assertEquals("37.7749;-122.4194", vres.getGeo());
         } catch (Exception e) {
             fail("VRESOURCE with geo coordinates should parse: " + e.getMessage());
         }
     }
 }