package us.k5n.ical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Uid.
 * 
 * @author Craig Knudsen
 */
public class UidTest implements Constants {

    @BeforeEach
    public void setUp() {
        // Setup code, if necessary
    }

    @Test
    public void testUidParsing() {
        String str = "UID:SFSDFSDFSDF-SDFDFSDFSD@xxx.com";
        try {
            Uid uid = new Uid(str, PARSE_STRICT);
            assertNotNull(uid, "Uid should not be null");
            assertEquals("SFSDFSDFSDF-SDFDFSDFSD@xxx.com", uid.value, "Uid value is incorrect");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testUidWithSpecialCharacters() {
        String str = "UID:1234-5678_90@domain.com";
        try {
            Uid uid = new Uid(str, PARSE_STRICT);
            assertNotNull(uid, "Uid should not be null");
            assertEquals("1234-5678_90@domain.com", uid.value, "Uid value is incorrect");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    @Test
    public void testEmptyUid() {
        String str = "UID:";
        Exception exception = assertThrows(Exception.class, () -> {
            new Uid(str, PARSE_STRICT);
        });
        assertTrue(exception.getMessage().contains("Missing UID"), "Expected exception due to empty UID");
    }

    @Test
    public void testUidWithoutAtSymbol() {
        String str = "UID:1234567890domain.com";
        try {
            Uid uid = new Uid(str, PARSE_STRICT);
            assertNotNull(uid, "Uid should not be null");
            assertEquals("1234567890domain.com", uid.value, "Uid value is incorrect");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.toString());
        }
    }

    // TODO: make this true
    //@Test
    //public void testInvalidUidFormat() {
    //    String str = "UID invalidformat";
    //    Exception exception = assertThrows(Exception.class, () -> {
    //        new Uid(str, PARSE_STRICT);
    //    });
    //    assertTrue(exception.getMessage().contains("Invalid UID format"), "Expected invalid format exception");
    //}
}