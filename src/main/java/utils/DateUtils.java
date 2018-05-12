package utils;

import model.DateType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    private static final String OTHER_DATE_REGEX = "\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}\\:\\d{2}\\:\\d{2}\\:\\d{3}.*";
    private static final String CA_DATE_REGEX = "(\\[\\d{4}\\-\\d{2}\\-\\d{2}\\d{2}\\:\\d{2}\\:\\d{2}\\:\\d{3}\\].*)|(\\[\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}\\:\\d{2}\\:\\d{2}\\,\\d{3}\\+\\d{2}\\:\\d{2}\\].*)";

    public static DateType getDateType (String logFirstLine) {
        if(logFirstLine.matches(OTHER_DATE_REGEX)) {
            return DateType.OTHER;
        } else {
            return DateType.CA;
        }
    }

    public static LocalDateTime getDate(String line) {
        DateType dateType = DateUtils.getDateType(line);
        DateTimeFormatter formatter = dateType.getFormatter();
        int dateOffset = dateType.getDateOffset();
        return LocalDateTime.parse(line.substring(0, dateOffset), formatter);
    }
}
