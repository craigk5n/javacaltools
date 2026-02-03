/*
 * Copyright (C) 2005-2006 Craig Knudsen and other authors
 * (see AUTHORS for a complete list)
 *
 * JavaCalTools is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * A copy of the GNU Lesser General Public License is included in the Wine
 * distribution in the file COPYING.LIB. If you did not receive this copy,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 */

package us.k5n.ical.core.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Valarm;

/**
 * RFC 5545 Section 3.6.6: VALARM Component Tests
 *
 * Tests for the VALARM alarm component as defined in RFC 5545.
 * The VALARM component provides a group of properties that define
 * an alarm or reminder for the parent component.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.6.6: VALARM Component")
public class VAlarmTest {

  @Nested
  @DisplayName("Basic Property Parsing Tests")
  class BasicPropertyParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should parse ACTION property")
    void should_parseAction_when_actionPropertyProvided() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nEND:VALARM");
      assertTrue(alarm.isValid());
      assertEquals("DISPLAY", alarm.getAction());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should parse TRIGGER property")
    void should_parseTrigger_when_triggerPropertyProvided() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nEND:VALARM");
      assertTrue(alarm.isValid());
      assertEquals("-PT15M", alarm.getTrigger());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should parse SUMMARY property")
    void should_parseSummary_when_summaryPropertyProvided() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nSUMMARY:Meeting\nEND:VALARM");
      assertTrue(alarm.isValid());
      assertEquals("Meeting", alarm.getSummary().getValue());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should parse DESCRIPTION property")
    void should_parseDescription_when_descriptionPropertyProvided() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nDESCRIPTION:Test description\nEND:VALARM");
      assertTrue(alarm.isValid());
      assertEquals("Test description", alarm.getDescription().getValue());
    }
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should be invalid when ACTION is missing")
    void should_beInvalid_when_actionMissing() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nTRIGGER:-PT15M\nEND:VALARM");
      assertFalse(alarm.isValid());
      assertNull(alarm.getAction());
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should generate valid iCalendar output")
    void should_generateValidOutput_when_toICalendarCalled() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nSUMMARY:Test alarm\nEND:VALARM");
      String output = alarm.toICalendar();
      assertTrue(output.contains("BEGIN:VALARM"));
      assertTrue(output.contains("ACTION:DISPLAY"));
      assertTrue(output.contains("TRIGGER:"));
      assertTrue(output.contains("SUMMARY:Test alarm"));
      assertTrue(output.contains("END:VALARM"));
    }
  }

  @Nested
  @DisplayName("EMAIL Action Tests")
  class EmailActionTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should parse ATTENDEE property for EMAIL action")
    void should_parseAttendee_when_emailAction() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:EMAIL\nTRIGGER:-PT15M\nATTENDEE:mailto:user@example.com\nEND:VALARM");

      assertNotNull(alarm.getAttendees());
      assertEquals(1, alarm.getAttendees().size());
      assertEquals("mailto:user@example.com", alarm.getAttendees().get(0).getValue());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should parse multiple ATTENDEE properties")
    void should_parseMultipleAttendees_when_multipleProvided() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:EMAIL\nTRIGGER:-PT15M\nATTENDEE:mailto:user1@example.com\nATTENDEE:mailto:user2@example.com\nEND:VALARM");

      assertNotNull(alarm.getAttendees());
      assertEquals(2, alarm.getAttendees().size());
      assertEquals("mailto:user1@example.com", alarm.getAttendees().get(0).getValue());
      assertEquals("mailto:user2@example.com", alarm.getAttendees().get(1).getValue());
    }
  }

  @Nested
  @DisplayName("ATTACH Property Tests")
  class AttachPropertyTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should parse ATTACH property")
    void should_parseAttach_when_attachPropertyProvided() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nATTACH:http://example.com/alert.wav\nEND:VALARM");

      assertNotNull(alarm.getAttachments());
      assertEquals(1, alarm.getAttachments().size());
      assertEquals("http://example.com/alert.wav", alarm.getAttachments().get(0).getValue());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should parse multiple ATTACH properties")
    void should_parseMultipleAttachments_when_multipleProvided() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nATTACH:http://example.com/sound1.wav\nATTACH:http://example.com/sound2.wav\nEND:VALARM");

      assertNotNull(alarm.getAttachments());
      assertEquals(2, alarm.getAttachments().size());
      assertEquals("http://example.com/sound1.wav", alarm.getAttachments().get(0).getValue());
      assertEquals("http://example.com/sound2.wav", alarm.getAttachments().get(1).getValue());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.6: should include ATTENDEE and ATTACH in output")
    void should_includeAttendeeAndAttach_when_toICalendarCalled() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:EMAIL\nTRIGGER:-PT15M\nATTENDEE:mailto:user@example.com\nATTACH:http://example.com/alert.wav\nEND:VALARM");

      String output = alarm.toICalendar();

      assertTrue(output.contains("BEGIN:VALARM"));
      assertTrue(output.contains("ACTION:EMAIL"));
      assertTrue(output.contains("TRIGGER:-PT15M"));
      assertTrue(output.contains("ATTENDEE:mailto:user@example.com"));
      assertTrue(output.contains("ATTACH:http://example.com/alert.wav"));
      assertTrue(output.contains("END:VALARM"));
    }
  }

  @Nested
  @DisplayName("RFC 9074 Extension Tests")
  class RFC9074ExtensionTests {

    @Test
    @DisplayName("RFC 9074: should parse PROXIMITY property")
    void should_parseProximity_when_proximityPropertyProvided() throws Exception {
      Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nPROXIMITY:DEPART\nSUMMARY:Reminder\nEND:VALARM");

      String output = alarm.toICalendar();

      assertTrue(output.contains("BEGIN:VALARM"));
      assertTrue(output.contains("ACTION:DISPLAY"));
      assertTrue(output.contains("TRIGGER:-PT15M"));
      assertTrue(output.contains("PROXIMITY:DEPART"));
      assertTrue(output.contains("SUMMARY:Reminder"));
      assertTrue(output.contains("END:VALARM"));
    }

    @Test
    @DisplayName("RFC 9074: should parse STRUCTURED-DATA property")
    void should_parseStructuredData_when_structuredDataProvided() {
      try {
        Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nSUMMARY:Reminder\nSTRUCTURED-DATA;VALUE=BINARY;ENCODING=BASE64;FMTTYPE=application/xml:PGhlbGxvIHdvcmxkPg==\nEND:VALARM");
        assertNotNull(alarm, "VALARM with STRUCTURED-DATA should parse");

        String output = alarm.toICalendar();
        assertTrue(output.contains("STRUCTURED-DATA"), "Output should contain STRUCTURED-DATA");
        assertTrue(output.contains("PGhlbGxvIHdvcmxkPg=="), "Output should contain structured data value");
      } catch (Exception e) {
        fail("VALARM with STRUCTURED-DATA should parse: " + e.getMessage());
      }
    }
  }
}
