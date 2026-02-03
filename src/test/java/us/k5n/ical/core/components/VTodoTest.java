package us.k5n.ical.core.components;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import us.k5n.ical.*;

/**
 * RFC 5545 Section 3.6.2: VTODO Component Tests
 */
@DisplayName("RFC 5545 Section 3.6.2: VTODO Component")
public class VTodoTest {
  private ICalendarParser parser;

  @BeforeEach
  void setUp() {
    parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
  }

  private Todo createTodoFromICalendar(String icalStr) throws Exception {
    List<String> lines = new ArrayList<>(Arrays.asList(icalStr.split("\\r?\\n")));
    return new Todo(parser, 1, lines);
  }

  @Nested
  @DisplayName("Status Validation Tests")
  class StatusValidationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should validate NEEDS-ACTION status")
    void should_validateStatus_when_needsAction() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nSTATUS:NEEDS-ACTION\nEND:VTODO");
      assertTrue(todo.isValid());
      assertEquals(Constants.STATUS_NEEDS_ACTION, todo.getStatus());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should validate COMPLETED status with date")
    void should_validateStatus_when_completedWithDate() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nSTATUS:COMPLETED\nCOMPLETED:20230101T100000Z\n"
          + "PERCENT-COMPLETE:100\nEND:VTODO");
      assertTrue(todo.isValid());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should reject COMPLETED without PERCENT-COMPLETE")
    void should_reportErrors_when_completedWithoutPercent() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nSTATUS:COMPLETED\nEND:VTODO");
      List<String> errors = new ArrayList<>();
      todo.isValid(errors);
      assertFalse(errors.isEmpty());
      assertTrue(errors.stream().anyMatch(e -> e.contains("PERCENT-COMPLETE")));
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should reject 100% with non-COMPLETED status")
    void should_reportErrors_when_100PercentButNotCompleted() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nSTATUS:IN-PROCESS\nPERCENT-COMPLETE:100\nEND:VTODO");
      List<String> errors = new ArrayList<>();
      assertFalse(todo.isValid(errors));
      assertTrue(errors.contains("Todo with 100% PERCENT-COMPLETE must have STATUS:COMPLETED"));
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should reject CANCELLED with COMPLETED date")
    void should_reportErrors_when_cancelledWithCompletedDate() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nSTATUS:CANCELLED\nCOMPLETED:20230101T100000Z\nEND:VTODO");
      List<String> errors = new ArrayList<>();
      assertFalse(todo.isValid(errors));
      assertTrue(errors.contains("Todo with STATUS:CANCELLED should not have COMPLETED date"));
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should parse all valid statuses")
    void should_parseAllStatuses_when_validStatusProvided() throws Exception {
      String[] statuses = {"NEEDS-ACTION", "IN-PROCESS", "COMPLETED", "CANCELLED"};
      int[] statusConstants = {Constants.STATUS_NEEDS_ACTION, Constants.STATUS_IN_PROCESS,
          Constants.STATUS_COMPLETED, Constants.STATUS_CANCELLED};
      for (int i = 0; i < statuses.length; i++) {
        String icalStr = "BEGIN:VTODO\nUID:test-todo-" + i + "@example.com\nSUMMARY:Test Todo " + i
            + "\nSTATUS:" + statuses[i] + "\n";
        if (statuses[i].equals("COMPLETED")) {
          icalStr += "COMPLETED:20230101T100000Z\nPERCENT-COMPLETE:100\n";
        }
        icalStr += "END:VTODO";
        Todo todo = createTodoFromICalendar(icalStr);
        assertTrue(todo.isValid());
        assertEquals(statusConstants[i], todo.getStatus());
      }
    }
  }

  @Nested
  @DisplayName("Automatic Status Setting Tests")
  class AutomaticStatusTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should auto-set status when setCompleted called")
    void should_autoSetStatus_when_setCompletedCalled() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nSTATUS:IN-PROCESS\nEND:VTODO");
      assertEquals(Constants.STATUS_IN_PROCESS, todo.getStatus());
      Date completionDate = new Date("COMPLETED:20230101T100000Z");
      todo.setCompleted(completionDate);
      assertEquals(Constants.STATUS_COMPLETED, todo.getStatus());
      assertEquals(100, todo.getPercentComplete().intValue());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should auto-set status when percent set to 100")
    void should_autoSetStatus_when_percentSet100() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nSTATUS:IN-PROCESS\nEND:VTODO");
      todo.setPercentComplete(100);
      assertEquals(Constants.STATUS_COMPLETED, todo.getStatus());
      assertNotNull(todo.getCompleted());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should auto-set IN-PROCESS when percent set to 50")
    void should_autoSetInProcess_when_percentSet50() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nSTATUS:NEEDS-ACTION\nEND:VTODO");
      todo.setPercentComplete(50);
      assertEquals(Constants.STATUS_IN_PROCESS, todo.getStatus());
    }

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should clear completion date when CANCELLED")
    void should_clearCompletionDate_when_statusSetToCancelled() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nCOMPLETED:20230101T100000Z\nEND:VTODO");
      todo.setStatus(Constants.STATUS_CANCELLED);
      assertEquals(Constants.STATUS_CANCELLED, todo.getStatus());
      assertNull(todo.getCompleted());
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("RFC 5545 Section 3.6.2: should include STATUS in output")
    void should_includeStatus_when_toICalendarCalled() throws Exception {
      Todo todo = createTodoFromICalendar("BEGIN:VTODO\nUID:test-todo@example.com\n"
          + "SUMMARY:Test Todo\nSTATUS:COMPLETED\nEND:VTODO");
      String output = todo.toICalendar();
      assertTrue(output.contains("STATUS:COMPLETED"));
      assertTrue(output.contains("BEGIN:VTODO"));
      assertTrue(output.contains("END:VTODO"));
    }
  }
}
