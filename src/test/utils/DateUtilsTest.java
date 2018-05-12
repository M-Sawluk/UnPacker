package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class DateUtilsTest {

    @Test
    public void getDateTest() {
        LocalDateTime date = DateUtils.getDate("[2018-01-19 10:00:00,000+00:00]");
        LocalDateTime expectedDate = LocalDateTime.of(2018, 1, 19, 10, 0, 0);
        Assertions.assertEquals(expectedDate, date);
    }

}