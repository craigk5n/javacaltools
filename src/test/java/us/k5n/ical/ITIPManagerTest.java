package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import us.k5n.ical.ITIPManager.ITIPMethod;

/**
 * Tests for ITIPManager - RFC 5546 iTIP Protocol Implementation
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class ITIPManagerTest {

    @Nested
    @DisplayName("ITIPMethod Enum Tests")
    class ITIPMethodEnumTests {

        @Test
        @DisplayName("All iTIP methods should have correct string values")
        void testMethodValues() {
            assertEquals("PUBLISH", ITIPMethod.PUBLISH.getValue());
            assertEquals("REQUEST", ITIPMethod.REQUEST.getValue());
            assertEquals("REPLY", ITIPMethod.REPLY.getValue());
            assertEquals("ADD", ITIPMethod.ADD.getValue());
            assertEquals("CANCEL", ITIPMethod.CANCEL.getValue());
            assertEquals("REFRESH", ITIPMethod.REFRESH.getValue());
            assertEquals("COUNTER", ITIPMethod.COUNTER.getValue());
            assertEquals("DECLINECOUNTER", ITIPMethod.DECLINECOUNTER.getValue());
        }

        @ParameterizedTest
        @DisplayName("fromString should parse valid method strings")
        @CsvSource({
            "PUBLISH, PUBLISH",
            "REQUEST, REQUEST",
            "REPLY, REPLY",
            "ADD, ADD",
            "CANCEL, CANCEL",
            "REFRESH, REFRESH",
            "COUNTER, COUNTER",
            "DECLINECOUNTER, DECLINECOUNTER"
        })
        void testFromStringValid(String input, String expected) {
            ITIPMethod method = ITIPMethod.fromString(input);
            assertNotNull(method, "Should parse valid method: " + input);
            assertEquals(expected, method.getValue());
        }

        @ParameterizedTest
        @DisplayName("fromString should be case-insensitive")
        @CsvSource({
            "publish, PUBLISH",
            "Request, REQUEST",
            "reply, REPLY",
            "Cancel, CANCEL"
        })
        void testFromStringCaseInsensitive(String input, String expected) {
            ITIPMethod method = ITIPMethod.fromString(input);
            assertNotNull(method, "Should parse case-insensitive: " + input);
            assertEquals(expected, method.getValue());
        }

        @ParameterizedTest
        @DisplayName("fromString should return null for invalid methods")
        @ValueSource(strings = {"INVALID", "UNKNOWN", "", "SEND", "RECEIVE"})
        void testFromStringInvalid(String input) {
            assertNull(ITIPMethod.fromString(input), "Should return null for invalid method: " + input);
        }

        @Test
        @DisplayName("fromString should return null for null input")
        void testFromStringNull() {
            assertNull(ITIPMethod.fromString(null));
        }
    }

    @Nested
    @DisplayName("Method Validation for Components Tests")
    class MethodValidationTests {

        @ParameterizedTest
        @DisplayName("PUBLISH method should be valid for appropriate components")
        @CsvSource({
            "VEVENT, true",
            "VTODO, true",
            "VJOURNAL, true",
            "VFREEBUSY, true",
            "VTIMEZONE, false",
            "VALARM, false"
        })
        void testPublishMethodValidation(String component, boolean expected) {
            assertEquals(expected,
                ITIPManager.isValidMethodForComponent(ITIPMethod.PUBLISH, component),
                "PUBLISH should be " + (expected ? "valid" : "invalid") + " for " + component);
        }

        @ParameterizedTest
        @DisplayName("REQUEST method should be valid for VEVENT and VTODO only")
        @CsvSource({
            "VEVENT, true",
            "VTODO, true",
            "VJOURNAL, false",
            "VFREEBUSY, false",
            "VTIMEZONE, false"
        })
        void testRequestMethodValidation(String component, boolean expected) {
            assertEquals(expected,
                ITIPManager.isValidMethodForComponent(ITIPMethod.REQUEST, component));
        }

        @ParameterizedTest
        @DisplayName("REPLY method should be valid for VEVENT and VTODO only")
        @CsvSource({
            "VEVENT, true",
            "VTODO, true",
            "VJOURNAL, false",
            "VFREEBUSY, false"
        })
        void testReplyMethodValidation(String component, boolean expected) {
            assertEquals(expected,
                ITIPManager.isValidMethodForComponent(ITIPMethod.REPLY, component));
        }

        @ParameterizedTest
        @DisplayName("CANCEL method should be valid for VEVENT and VTODO only")
        @CsvSource({
            "VEVENT, true",
            "VTODO, true",
            "VJOURNAL, false",
            "VFREEBUSY, false"
        })
        void testCancelMethodValidation(String component, boolean expected) {
            assertEquals(expected,
                ITIPManager.isValidMethodForComponent(ITIPMethod.CANCEL, component));
        }

        @Test
        @DisplayName("Validation should handle null method")
        void testNullMethod() {
            assertFalse(ITIPManager.isValidMethodForComponent(null, "VEVENT"));
        }

        @Test
        @DisplayName("Validation should handle null component")
        void testNullComponent() {
            assertFalse(ITIPManager.isValidMethodForComponent(ITIPMethod.REQUEST, null));
        }

        @Test
        @DisplayName("Validation should be case-insensitive for component names")
        void testCaseInsensitiveComponent() {
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.REQUEST, "vevent"));
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.REQUEST, "VEvent"));
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.REQUEST, "VEVENT"));
        }
    }

    @Nested
    @DisplayName("Operation to Method Mapping Tests")
    class OperationMappingTests {

        @ParameterizedTest
        @DisplayName("Standard operations should map to correct methods")
        @CsvSource({
            "publish, PUBLISH",
            "create, PUBLISH",
            "invite, REQUEST",
            "request, REQUEST",
            "respond, REPLY",
            "reply, REPLY",
            "update, REQUEST",
            "modify, REQUEST",
            "cancel, CANCEL",
            "add, ADD",
            "refresh, REFRESH"
        })
        void testOperationMapping(String operation, String expectedMethod) {
            ITIPMethod method = ITIPManager.getMethodForOperation(operation);
            assertNotNull(method, "Should map operation: " + operation);
            assertEquals(expectedMethod, method.getValue());
        }

        @Test
        @DisplayName("Unknown operations should return null")
        void testUnknownOperation() {
            assertNull(ITIPManager.getMethodForOperation("unknown"));
            assertNull(ITIPManager.getMethodForOperation("delete"));
            assertNull(ITIPManager.getMethodForOperation("remove"));
        }

        @Test
        @DisplayName("Null operation should return null")
        void testNullOperation() {
            assertNull(ITIPManager.getMethodForOperation(null));
        }
    }

    @Nested
    @DisplayName("Workflow Transition Tests")
    class WorkflowTransitionTests {

        @Test
        @DisplayName("REQUEST can be followed by REPLY")
        void testRequestToReply() {
            assertTrue(ITIPManager.isValidWorkflowTransition(
                ITIPMethod.REQUEST, ITIPMethod.REPLY));
        }

        @Test
        @DisplayName("REQUEST can be followed by COUNTER")
        void testRequestToCounter() {
            assertTrue(ITIPManager.isValidWorkflowTransition(
                ITIPMethod.REQUEST, ITIPMethod.COUNTER));
        }

        @Test
        @DisplayName("REQUEST can be followed by DECLINECOUNTER")
        void testRequestToDeclineCounter() {
            assertTrue(ITIPManager.isValidWorkflowTransition(
                ITIPMethod.REQUEST, ITIPMethod.DECLINECOUNTER));
        }

        @Test
        @DisplayName("COUNTER can be followed by DECLINECOUNTER")
        void testCounterToDeclineCounter() {
            assertTrue(ITIPManager.isValidWorkflowTransition(
                ITIPMethod.COUNTER, ITIPMethod.DECLINECOUNTER));
        }

        @Test
        @DisplayName("COUNTER can be followed by REPLY")
        void testCounterToReply() {
            assertTrue(ITIPManager.isValidWorkflowTransition(
                ITIPMethod.COUNTER, ITIPMethod.REPLY));
        }

        @ParameterizedTest
        @DisplayName("Invalid workflow transitions should return false")
        @CsvSource({
            "PUBLISH, REPLY",
            "PUBLISH, REQUEST",
            "REPLY, REQUEST",
            "CANCEL, REPLY",
            "ADD, COUNTER"
        })
        void testInvalidTransitions(String from, String to) {
            ITIPMethod fromMethod = ITIPMethod.fromString(from);
            ITIPMethod toMethod = ITIPMethod.fromString(to);
            assertFalse(ITIPManager.isValidWorkflowTransition(fromMethod, toMethod),
                "Transition from " + from + " to " + to + " should be invalid");
        }

        @Test
        @DisplayName("Null methods should return false for transition validation")
        void testNullTransition() {
            assertFalse(ITIPManager.isValidWorkflowTransition(null, ITIPMethod.REPLY));
            assertFalse(ITIPManager.isValidWorkflowTransition(ITIPMethod.REQUEST, null));
            assertFalse(ITIPManager.isValidWorkflowTransition(null, null));
        }
    }

    @Nested
    @DisplayName("Attendee Action Response Tests")
    class AttendeeActionTests {

        @ParameterizedTest
        @DisplayName("Standard attendee actions should map to REPLY")
        @ValueSource(strings = {"ACCEPT", "DECLINE", "TENTATIVE", "accept", "Accept"})
        void testStandardActionsMapToReply(String action) {
            assertEquals(ITIPMethod.REPLY,
                ITIPManager.getResponseMethodForAttendeeAction(action),
                action + " should map to REPLY");
        }

        @Test
        @DisplayName("COUNTER action should map to COUNTER method")
        void testCounterAction() {
            assertEquals(ITIPMethod.COUNTER,
                ITIPManager.getResponseMethodForAttendeeAction("COUNTER"));
        }

        @Test
        @DisplayName("DECLINE_COUNTER action should map to DECLINECOUNTER method")
        void testDeclineCounterAction() {
            assertEquals(ITIPMethod.DECLINECOUNTER,
                ITIPManager.getResponseMethodForAttendeeAction("DECLINE_COUNTER"));
        }

        @Test
        @DisplayName("Unknown actions should return null")
        void testUnknownAction() {
            assertNull(ITIPManager.getResponseMethodForAttendeeAction("UNKNOWN"));
            assertNull(ITIPManager.getResponseMethodForAttendeeAction("MAYBE"));
        }

        @Test
        @DisplayName("Null action should return null")
        void testNullAction() {
            assertNull(ITIPManager.getResponseMethodForAttendeeAction(null));
        }
    }

    @Nested
    @DisplayName("All iTIP Methods Coverage Tests")
    class AllMethodsCoverageTests {

        @Test
        @DisplayName("Should have exactly 8 iTIP methods defined")
        void testMethodCount() {
            assertEquals(8, ITIPMethod.values().length,
                "RFC 5546 defines exactly 8 iTIP methods");
        }

        @Test
        @DisplayName("ADD method should be valid for scheduling components")
        void testAddMethod() {
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.ADD, "VEVENT"));
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.ADD, "VTODO"));
            assertFalse(ITIPManager.isValidMethodForComponent(ITIPMethod.ADD, "VJOURNAL"));
        }

        @Test
        @DisplayName("REFRESH method should be valid for scheduling components")
        void testRefreshMethod() {
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.REFRESH, "VEVENT"));
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.REFRESH, "VTODO"));
            assertFalse(ITIPManager.isValidMethodForComponent(ITIPMethod.REFRESH, "VJOURNAL"));
        }

        @Test
        @DisplayName("COUNTER method should be valid for scheduling components")
        void testCounterMethod() {
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.COUNTER, "VEVENT"));
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.COUNTER, "VTODO"));
            assertFalse(ITIPManager.isValidMethodForComponent(ITIPMethod.COUNTER, "VFREEBUSY"));
        }

        @Test
        @DisplayName("DECLINECOUNTER method should be valid for scheduling components")
        void testDeclineCounterMethod() {
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.DECLINECOUNTER, "VEVENT"));
            assertTrue(ITIPManager.isValidMethodForComponent(ITIPMethod.DECLINECOUNTER, "VTODO"));
            assertFalse(ITIPManager.isValidMethodForComponent(ITIPMethod.DECLINECOUNTER, "VJOURNAL"));
        }
    }
}
