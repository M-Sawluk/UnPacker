package utils;

import model.DateType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    private static final String BRACKETSLESS_DATE_REGEX = "\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}\\:\\d{2}\\:\\d{2}\\:\\d{3}.*";

    public static DateType getDateType (String logFirstLine) {
        if(logFirstLine.matches(BRACKETSLESS_DATE_REGEX)) {
            return DateType.WITHOUT_BRACKETS;
        } else {
            return DateType.WITH_BRACKETS;
        }
    }

    public static LocalDateTime getDate(String line) {
        DateType dateType = getDateType(line);
        DateTimeFormatter formatter = dateType.getFormatter();
        int dateOffset = dateType.getDateOffset();
        return LocalDateTime.parse(line.substring(0, dateOffset), formatter);
    }
}
