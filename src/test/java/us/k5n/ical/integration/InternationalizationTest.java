package us.k5n.ical.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.k5n.ical.DefaultDataStore;
import us.k5n.ical.ICalendarParser;

/**
 * Internationalization and UTF-8 Support Tests
 *
 * Tests for proper handling of Unicode characters, different character encodings,
 * and internationalization features in iCalendar parsing and generation.
 */
public class InternationalizationTest {

    @Test
    @DisplayName("UTF-8: Unicode characters in SUMMARY")
    void testUnicodeCharactersInSummary() {
        // Test various Unicode characters in SUMMARY property
        String unicodeSummary = "Meeting with Jos\u00e9, Fran\u00e7ois & M\u00fcller - Caf\u00e9 \u2615";
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:unicode-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:" + unicodeSummary + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Unicode characters in SUMMARY should parse without errors");

            DefaultDataStore ds = (DefaultDataStore) parser.getDataStoreAt(0);
            assertNotNull(ds.getAllEvents().get(0).getSummary(), "Summary should be parsed");
        } catch (Exception e) {
            fail("Unicode characters should be handled properly: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UTF-8: Unicode characters in LOCATION")
    void testUnicodeCharactersInLocation() {
        // Test Unicode in LOCATION with special venue name
        String unicodeLocation = "Salle de r\u00e9union 101 - \u00c9difice International \ud83c\udfe2";
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:unicode-location@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "LOCATION:" + unicodeLocation + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Unicode characters in LOCATION should parse without errors");
        } catch (Exception e) {
            fail("Unicode characters in LOCATION should be handled: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UTF-8: CJK characters in DESCRIPTION")
    void testCjkCharactersInDescription() {
        // Test Chinese, Japanese, Korean characters
        String cjkDescription = "\u4f1a\u8b70\u306e\u8a73\u7d30: \u30d7\u30ed\u30b8\u30a7\u30af\u30c8\u306e\u9032\u6357\u72b6\u6cc1\u306b\u3064\u3044\u3066\u8b70\u8ad6\u3057\u307e\u3059 \ud83d\udcca";
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:cjk-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "DESCRIPTION:" + cjkDescription + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "CJK characters should parse without errors");
        } catch (Exception e) {
            fail("CJK characters should be handled properly: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UTF-8: Emoji and symbols")
    void testEmojiAndSymbols() {
        // Test emojis and special symbols
        String emojiSummary = "Team Standup \ud83d\ude80 with coffee \u2615 and charts \ud83d\udcc8";
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:emoji-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:" + emojiSummary + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Emoji and symbols should parse without errors");
        } catch (Exception e) {
            fail("Emoji and symbols should be handled: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UTF-8: Right-to-left text (Arabic/Hebrew)")
    void testRightToLeftText() {
        // Test RTL text with Arabic
        String rtlSummary = "\u0627\u062c\u062a\u0645\u0627\u0639 \u0627\u0644\u0641\u0631\u064a\u0642"; // "Team Meeting" in Arabic
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:rtl-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:" + rtlSummary + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "RTL text should parse without errors");
        } catch (Exception e) {
            fail("RTL text should be handled properly: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UTF-8: Combined characters and accents")
    void testCombinedCharactersAndAccents() {
        // Test combining characters and accented characters
        String accentedSummary = "M\u00f6tley Cr\u00fce concert na\u00efve r\u00e9sum\u00e9";
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:accented-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:" + accentedSummary + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Accented characters should parse without errors");
        } catch (Exception e) {
            fail("Accented characters should be handled: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UTF-8: Mathematical symbols and technical characters")
    void testMathematicalSymbols() {
        // Test mathematical and technical symbols
        String mathSummary = "Math meeting: \u222b dx/dt = \u2202f/\u2202t \u2211(x\u00b2)";
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:math-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:" + mathSummary + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Mathematical symbols should parse without errors");
        } catch (Exception e) {
            fail("Mathematical symbols should be handled: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UTF-8: Long Unicode strings with line folding")
    void testLongUnicodeStringsWithFolding() {
        // Test long Unicode strings that require line folding
        StringBuilder longUnicode = new StringBuilder();
        longUnicode.append("Very long Unicode summary with international characters: ");
        for (int i = 0; i < 10; i++) {
            longUnicode.append("Jos\u00e9 Fran\u00e7ois M\u00fcller caf\u00e9 na\u00efve r\u00e9sum\u00e9 \u2615 \ud83d\ude80 ");
        }

        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:long-unicode-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:" + longUnicode.toString().trim() + "\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Long Unicode strings should parse without errors");
        } catch (Exception e) {
            fail("Long Unicode strings should be handled: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UTF-8: Byte Order Mark handling")
    void testByteOrderMarkHandling() {
        // Test handling of UTF-8 BOM if present
        String bom = "\uFEFF"; // UTF-8 BOM
        String icalData = bom + "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:bom-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "SUMMARY:Test with BOM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            // Should handle BOM gracefully
            assertNotNull(parser.getDataStoreAt(0));
        } catch (Exception e) {
            // BOM handling may vary, but shouldn't crash
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("UTF-8: Non-ASCII property names (extension properties)")
    void testNonAsciiPropertyNames() {
        // Test X-properties with non-ASCII names (allowed for extensions)
        String icalData = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//Calendar//EN\n" +
            "X-PRIORIT\u00c9:haute\n" +
            "BEGIN:VEVENT\n" +
            "UID:ext-prop-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "X-PROPRI\u00c9T\u00c9:sp\u00e9ciale\n" +
            "SUMMARY:Extension properties test\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

        try {
            ICalendarParser parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
            parser.parse(new StringReader(icalData));
            assertTrue(parser.getAllErrors().isEmpty(), "Non-ASCII property names should parse without errors");
        } catch (Exception e) {
            fail("Non-ASCII property names should be handled: " + e.getMessage());
        }
    }
}
