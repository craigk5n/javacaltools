package us.k5n.ical.core.properties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import us.k5n.ical.Classification;
import us.k5n.ical.Constants;

/**
 * RFC 5545 Section 3.8.1.3: CLASS Property Tests
 *
 * Tests for the CLASS property as defined in RFC 5545.
 * The CLASS property defines the access classification for a calendar component.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
@DisplayName("RFC 5545 Section 3.8.1.3: CLASS Property")
public class ClassificationPropertyTest implements Constants {

  @Nested
  @DisplayName("PUBLIC Classification Tests")
  class PublicClassificationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.3: should parse CLASS:PUBLIC from uppercase string")
    void should_parsePublic_when_uppercaseStringProvided() {
      try {
        Classification c = new Classification("CLASS: PUBLIC");
        assertTrue(c.getClassification() == PUBLIC, "PUBLIC != " + c.getClassification());
      } catch (Exception e) {
        fail("Failed to create PUBLIC classification from string: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.3: should parse CLASS:public from lowercase string")
    void should_parsePublic_when_lowercaseStringProvided() {
      try {
        Classification c = new Classification("CLASS: public");
        assertTrue(c.getClassification() == PUBLIC, "public != " + c.getClassification());
      } catch (Exception e) {
        fail("Failed to create PUBLIC classification from lowercase string: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.3: should create PUBLIC from constant")
    void should_createPublic_when_constantProvided() {
      try {
        Classification c = new Classification(PUBLIC);
        assertTrue(c.getClassification() == PUBLIC, "PUBLIC constant != " + c.getClassification());
      } catch (Exception e) {
        fail("Failed to create PUBLIC classification from constant: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("PRIVATE Classification Tests")
  class PrivateClassificationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.3: should parse CLASS:PRIVATE from uppercase string")
    void should_parsePrivate_when_uppercaseStringProvided() {
      try {
        Classification c = new Classification("CLASS: PRIVATE");
        assertTrue(c.getClassification() == PRIVATE,
            "PRIVATE != " + c.getClassification() + ", " + c.toICalendar());
      } catch (Exception e) {
        fail("Failed to create PRIVATE classification from string: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.3: should parse CLASS:private from lowercase string")
    void should_parsePrivate_when_lowercaseStringProvided() {
      try {
        Classification c = new Classification("CLASS: private");
        assertTrue(c.getClassification() == PRIVATE, "private != " + c.getClassification());
      } catch (Exception e) {
        fail("Failed to create PRIVATE classification from lowercase string: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.3: should create PRIVATE from constant")
    void should_createPrivate_when_constantProvided() {
      try {
        Classification c = new Classification(PRIVATE);
        assertTrue(c.getClassification() == PRIVATE, "PRIVATE constant != " + c.getClassification());
      } catch (Exception e) {
        fail("Failed to create PRIVATE classification from constant: " + e.toString());
      }
    }
  }

  @Nested
  @DisplayName("CONFIDENTIAL Classification Tests")
  class ConfidentialClassificationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.3: should parse CLASS:CONFIDENTIAL from uppercase string")
    void should_parseConfidential_when_uppercaseStringProvided() {
      try {
        Classification c = new Classification("CLASS: CONFIDENTIAL");
        assertTrue(c.getClassification() == CONFIDENTIAL, "CONFIDENTIAL != " + c.getClassification());
      } catch (Exception e) {
        fail("Failed to create CONFIDENTIAL classification from string: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.3: should parse CLASS:confidential from lowercase string")
    void should_parseConfidential_when_lowercaseStringProvided() {
      try {
        Classification c = new Classification("CLASS: confidential");
        assertTrue(c.getClassification() == CONFIDENTIAL, "confidential != " + c.getClassification());
      } catch (Exception e) {
        fail("Failed to create CONFIDENTIAL classification from lowercase string: " + e.toString());
      }
    }

    @Test
    @DisplayName("RFC 5545 Section 3.8.1.3: should create CONFIDENTIAL from constant")
    void should_createConfidential_when_constantProvided() {
      try {
        Classification c = new Classification(CONFIDENTIAL);
        assertTrue(c.getClassification() == CONFIDENTIAL, "CONFIDENTIAL constant != " + c.getClassification());
      } catch (Exception e) {
        fail("Failed to create CONFIDENTIAL classification from constant: " + e.toString());
      }
    }
  }
}
