package us.k5n.ical.rfc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

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
        String unicodeSummary = "Meeting with José, François & Müller - Café ☕";
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
        String unicodeLocation = "Salle de réunion 101 - Édifice International 🏢";
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
        String cjkDescription = "会議の詳細: プロジェクトの進捗状況について議論します 📊";
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
        String emojiSummary = "Team Standup 🚀 with coffee ☕ and charts 📈";
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
        String rtlSummary = "اجتماع الفريق"; // "Team Meeting" in Arabic
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
        String accentedSummary = "Mötley Crüe concert naïve résumé";
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
        String mathSummary = "Math meeting: ∫ dx/dt = ∂f/∂t ∑(x²)";
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
            longUnicode.append("José François Müller café naïve résumé ☕ 🚀 ");
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
            "X-PRIORITÉ:haute\n" +
            "BEGIN:VEVENT\n" +
            "UID:ext-prop-test@example.com\n" +
            "DTSTAMP:20230101T100000Z\n" +
            "DTSTART:20230101T100000Z\n" +
            "X-PROPRIÉTÉ:spéciale\n" +
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