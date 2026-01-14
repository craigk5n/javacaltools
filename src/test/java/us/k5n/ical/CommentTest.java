package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Comment.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class CommentTest implements Constants {

    @Test
    public void testCommentWithLanguage() {
        String str = "COMMENT;LANGUAGE=fr:Un commentaire";
        try {
            Comment comment = new Comment(str, PARSE_STRICT);
            assertNotNull(comment, "Comment should not be null");
            assertEquals("fr", comment.language, "Language should be 'fr'");
            assertEquals("Un commentaire", comment.getValue(), "Value should be correct");
            assertNull(comment.altrep, "ALTREP should be null when not specified");
        } catch (Exception e) {
            fail("Comment parsing failed: " + e.getMessage());
        }
    }

    @Test
    public void testCommentWithAltRep() {
        String str = "COMMENT;ALTREP=\"http://example.com/comment\":A comment";
        try {
            Comment comment = new Comment(str, PARSE_STRICT);
            assertNotNull(comment, "Comment should not be null");
            assertEquals("http://example.com/comment", comment.altrep, "ALTREP should be parsed");
            assertEquals("A comment", comment.getValue(), "Value should be correct");
            assertNull(comment.language, "Language should be null when not specified");
        } catch (Exception e) {
            fail("Comment parsing failed: " + e.getMessage());
        }
    }

    @Test
    public void testCommentWithoutAttributes() {
        String str = "COMMENT:This is a comment.";
        try {
            Comment comment = new Comment(str, PARSE_STRICT);
            assertNotNull(comment, "Comment should not be null");
            assertNull(comment.language, "Language should be null when not specified");
            assertNull(comment.altrep, "ALTREP should be null when not specified");
            assertEquals("This is a comment.", comment.getValue(), "Value should be correct");
        } catch (Exception e) {
            fail("Comment parsing failed: " + e.getMessage());
        }
    }

    @Test
    public void testCommentWithBothAttributes() {
        String str = "COMMENT;LANGUAGE=en;ALTREP=\"http://example.com/comment\":An English comment";
        try {
            Comment comment = new Comment(str, PARSE_STRICT);
            assertNotNull(comment, "Comment should not be null");
            assertEquals("en", comment.language, "Language should be 'en'");
            assertEquals("http://example.com/comment", comment.altrep, "ALTREP should be parsed");
            assertEquals("An English comment", comment.getValue(), "Value should be correct");
        } catch (Exception e) {
            fail("Comment parsing failed: " + e.getMessage());
        }
    }

    @Test
    public void testEmptyComment() {
        String str = "COMMENT:";
        try {
            Comment comment = new Comment(str, PARSE_STRICT);
            assertNotNull(comment, "Comment should not be null");
            assertNull(comment.language, "Language should be null");
            assertNull(comment.altrep, "ALTREP should be null");
            assertEquals("", comment.getValue(), "Value should be empty");
        } catch (Exception e) {
            fail("Comment parsing failed: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidAttribute() {
        String str = "COMMENT;INVALID=value:A comment";
        try {
            Comment comment = new Comment(str, PARSE_STRICT);
            fail("Should throw exception for invalid attribute");
        } catch (ParseException e) {
            // Expected for strict parsing
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getMessage());
        }
    }

    @Test
    public void testLooseParsingWithInvalidAttribute() {
        String str = "COMMENT;INVALID=value:A comment";
        try {
            Comment comment = new Comment(str, PARSE_LOOSE);
            assertNotNull(comment, "Comment should not be null in loose mode");
            assertEquals("A comment", comment.getValue(), "Value should be parsed correctly");
        } catch (Exception e) {
            fail("Loose parsing should not fail: " + e.getMessage());
        }
    }
}