package us.k5n.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test cases for Utils.
 * 
 * @author Craig Knudsen
 */
public class UtilsTest implements Constants {

    @BeforeEach
    public void setUp() {
        // Setup code, if necessary
    }

    @Test
    public void testDayOfWeekCalculation() {
        int[] wdays = { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY,
                Calendar.SATURDAY };
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(true);

        // Test date ranges from 1583 through 2200
        calendar.set(Calendar.YEAR, 1583);
        while (true) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int javaWeekday = calendar.get(Calendar.DAY_OF_WEEK);
            int calculatedWeekday = Utils.getDayOfWeek(year, month, day);
            assertEquals(javaWeekday, wdays[calculatedWeekday],
                    String.format("Weekday mismatch for %d/%d/%d: java weekday=%d, Utils weekday=%d",
                            month, day, year, javaWeekday, calculatedWeekday));
            // Increment date
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            if (year >= 2201)
                break;
        }
    }

    @ParameterizedTest
    @CsvSource({
            "2024, 2, 29, 4, 'Wrong day of the week for 2024-02-29'",
            "1700, 1, 1, 5, 'Wrong day of the week for 1700-01-01'", // Note: Linux "cal 1700" is wrong!!!
            "2200, 12, 31, 3, 'Wrong day of the week for 2200-12-31'"
    })
    public void testDayOfWeekEdgeCases(int year, int month, int day, int expectedDayOfWeek, String message) {

        assertEquals(expectedDayOfWeek, Utils.getDayOfWeek(year, month, day), message);
    }

    @Test
    public void testMimeTypeForFileExtension() {
        assertEquals("image/jpeg", Utils.getMimeTypeForExtension("picture.jpg"),
                "Wrong mime type for jpg");
        assertEquals("image/jpeg", Utils.getMimeTypeForExtension("picture.jpeg"),
                "Wrong mime type for jpeg");
        assertEquals("application/msword", Utils.getMimeTypeForExtension("file.doc"),
                "Wrong mime type for doc");
        assertEquals("application/excel", Utils.getMimeTypeForExtension("file.xls"),
                "Wrong mime type for xls");
        assertEquals("text/html", Utils.getMimeTypeForExtension("file.html"),
                "Wrong mime type for html");
    }
}