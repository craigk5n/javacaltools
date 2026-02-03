/*
 * Copyright (C) 2005-2006 Craig Knudsen and other authors
 * (see AUTHORS for a complete list)
 *
 * JavaCalTools is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 */

package us.k5n.ical.core.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.DefaultDataStore;
import us.k5n.ical.ICalendarParser;
import us.k5n.ical.Timezone;

/**
 * RFC 5545 Section 3.6.5: VTIMEZONE Component Tests
 */
@DisplayName("RFC 5545 Section 3.6.5: VTIMEZONE Component")
public class VTimezoneTest {
  private ICalendarParser parser;
  private List<String> textLines;

  @BeforeEach
  void setUp() {
    parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
    textLines = new ArrayList<String>();
  }

  @Nested
  @DisplayName("Basic Parsing Tests")
  class BasicParsingTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.5: should parse basic VTIMEZONE with only TZID")
    void should_parseBasicTimezone_when_onlyTzidProvided() throws Exception {
      textLines.add("BEGIN:VTIMEZONE");
      textLines.add("TZID:America/New_York");
      textLines.add("END:VTIMEZONE");
      Timezone timezone = new Timezone(parser, 1, textLines);
      assertTrue(timezone.isValid());
      assertEquals("America/New_York", timezone.getTimezoneId());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.5: should parse VTIMEZONE with LAST-MODIFIED")
    void should_parseTimezone_when_lastModifiedProvided() throws Exception {
      textLines.add("BEGIN:VTIMEZONE");
      textLines.add("TZID:Europe/London");
      textLines.add("LAST-MODIFIED:20230101T000000Z");
      textLines.add("END:VTIMEZONE");
      Timezone timezone = new Timezone(parser, 1, textLines);
      assertTrue(timezone.isValid());
      assertNotNull(timezone.getLastModified());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.5: should parse VTIMEZONE with TZURL")
    void should_parseTimezone_when_tzurlProvided() throws Exception {
      textLines.add("BEGIN:VTIMEZONE");
      textLines.add("TZID:Asia/Tokyo");
      textLines.add("TZURL:http://tz.example.org/Asia/Tokyo");
      textLines.add("END:VTIMEZONE");
      Timezone timezone = new Timezone(parser, 1, textLines);
      assertTrue(timezone.isValid());
      assertNotNull(timezone.getURL());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.5: should default to UTC when TZID is empty")
    void should_defaultToUtc_when_tzidEmpty() throws Exception {
      textLines.add("BEGIN:VTIMEZONE");
      textLines.add("END:VTIMEZONE");
      Timezone timezone = new Timezone(parser, 1, textLines);
      assertTrue(timezone.isValid());
      assertEquals("UTC", timezone.getTimezoneId());
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.5: should generate valid iCalendar output")
    void should_generateValidOutput_when_toICalendarCalled() throws Exception {
      textLines.add("BEGIN:VTIMEZONE");
      textLines.add("TZID:America/Los_Angeles");
      textLines.add("TZURL:http://example.com/timezones");
      textLines.add("END:VTIMEZONE");
      Timezone timezone = new Timezone(parser, 1, textLines);
      String output = timezone.toICalendar();
      assertTrue(output.contains("BEGIN:VTIMEZONE"));
      assertTrue(output.contains("TZID:America/Los_Angeles"));
      assertTrue(output.contains("END:VTIMEZONE"));
    }
  }

  @Nested
  @DisplayName("DataStore Integration Tests")
  class DataStoreIntegrationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.5: should integrate with DataStore")
    void should_integrateWithDataStore_when_storingTimezone() throws Exception {
      textLines.add("BEGIN:VTIMEZONE");
      textLines.add("TZID:Pacific/Honolulu");
      textLines.add("END:VTIMEZONE");
      Timezone timezone = new Timezone(parser, 1, textLines);
      DefaultDataStore dataStore = new DefaultDataStore();
      parser.addDataStore(dataStore);
      assertTrue(timezone.isValid());
      dataStore.storeTimezone(timezone);
      assertEquals(1, dataStore.getAllTimezones().size());
    }
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.5: should reject invalid TZID characters")
    void should_beInvalid_when_tzidContainsInvalidCharacters() throws Exception {
      textLines.clear();
      textLines.add("TZID:America/New York");
      textLines.add("BEGIN:STANDARD");
      textLines.add("DTSTART:20230101T020000");
      textLines.add("TZOFFSETFROM:-0500");
      textLines.add("TZOFFSETTO:-0500");
      textLines.add("END:STANDARD");
      Timezone timezone = new Timezone(parser, 1, textLines);
      List<String> errors = new ArrayList<>();
      assertFalse(timezone.isValid(errors));
      assertTrue(errors.contains("Timezone TZID contains invalid characters"));
    }
  }
}
