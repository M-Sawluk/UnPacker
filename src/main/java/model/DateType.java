package model;

import java.time.format.DateTimeFormatter;

public enum DateType {
    CA (DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss,SSSZZ]"), 31),
    OTHER (DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"), 23);

    private DateTimeFormatter formatter;
    private int dateOffset;

    DateType(DateTimeFormatter formatter, int dateOffset) {
        this.formatter = formatter;
        this.dateOffset = dateOffset;
    }

    public DateTimeFormatter getFormatter() { return formatter; }

    public int getDateOffset() { return dateOffset; }
}
