package us.k5n.ical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Classification.
 * 
 * These tests validate the behavior of the Classification class
 * in handling different classification types: PUBLIC, PRIVATE, and
 * CONFIDENTIAL.
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class ClassificationTest implements Constants {

    @BeforeEach
    public void setUp() {
    }

    /**
     * Test the creation of a PUBLIC classification using different formats.
     */
    @Test
    public void testPublicClassificationCreation() {
        try {
            Classification c = new Classification("CLASS: PUBLIC");
            assertTrue(c.getClassification() == PUBLIC, "PUBLIC != " + c.getClassification());
        } catch (Exception e) {
            fail("Failed to create PUBLIC classification from string: " + e.toString());
        }

        try {
            Classification c = new Classification("CLASS: public");
            assertTrue(c.getClassification() == PUBLIC, "public != " + c.getClassification());
        } catch (Exception e) {
            fail("Failed to create PUBLIC classification from lowercase string: " + e.toString());
        }

        try {
            Classification c = new Classification(PUBLIC);
            assertTrue(c.getClassification() == PUBLIC, "PUBLIC constant != " + c.getClassification());
        } catch (Exception e) {
            fail("Failed to create PUBLIC classification from constant: " + e.toString());
        }
    }

    /**
     * Test the creation of a PRIVATE classification using different formats.
     */
    @Test
    public void testPrivateClassificationCreation() {
        try {
            Classification c = new Classification("CLASS: PRIVATE");
            assertTrue(c.getClassification() == PRIVATE,
                    "PRIVATE != " + c.getClassification() + ", " + c.toICalendar());
        } catch (Exception e) {
            fail("Failed to create PRIVATE classification from string: " + e.toString());
        }

        try {
            Classification c = new Classification("CLASS: private");
            assertTrue(c.getClassification() == PRIVATE, "private != " + c.getClassification());
        } catch (Exception e) {
            fail("Failed to create PRIVATE classification from lowercase string: " + e.toString());
        }

        try {
            Classification c = new Classification(PRIVATE);
            assertTrue(c.getClassification() == PRIVATE, "PRIVATE constant != " + c.getClassification());
        } catch (Exception e) {
            fail("Failed to create PRIVATE classification from constant: " + e.toString());
        }
    }

    /**
     * Test the creation of a CONFIDENTIAL classification using different formats.
     */
    @Test
    public void testConfidentialClassificationCreation() {
        try {
            Classification c = new Classification("CLASS: CONFIDENTIAL");
            assertTrue(c.getClassification() == CONFIDENTIAL, "CONFIDENTIAL != " + c.getClassification());
        } catch (Exception e) {
            fail("Failed to create CONFIDENTIAL classification from string: " + e.toString());
        }

        try {
            Classification c = new Classification("CLASS: confidential");
            assertTrue(c.getClassification() == CONFIDENTIAL, "confidential != " + c.getClassification());
        } catch (Exception e) {
            fail("Failed to create CONFIDENTIAL classification from lowercase string: " + e.toString());
        }

        try {
            Classification c = new Classification(CONFIDENTIAL);
            assertTrue(c.getClassification() == CONFIDENTIAL, "CONFIDENTIAL constant != " + c.getClassification());
        } catch (Exception e) {
            fail("Failed to create CONFIDENTIAL classification from constant: " + e.toString());
        }
    }
}