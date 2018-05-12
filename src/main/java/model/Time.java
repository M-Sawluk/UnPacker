package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Time {
    private LocalDateTime selectedTime;
    private LocalDateTime from;
    private LocalDateTime to;

    public Time(LocalDateTime selectedTime, LocalDateTime from, LocalDateTime to) {
        this.selectedTime = selectedTime;
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getSelectedTime() { return selectedTime; }

    public LocalDateTime getFrom() { return from; }

    public LocalDateTime getTo() { return to; }

    public void setSelectedTime(LocalDateTime selectedTime) { this.selectedTime = selectedTime; }

    public void setFrom(LocalDateTime from) { this.from = from; }

    public void setTo(LocalDateTime to) { this.to = to; }

    @Override
    public String toString() {
        return "Time{" +
                "selectedTime=" + selectedTime +
                ", from=" + from +
                ", to=" + to +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return Objects.equals(selectedTime, time.selectedTime) &&
                Objects.equals(from, time.from) &&
                Objects.equals(to, time.to);
    }

    @Override
    public int hashCode() {

        return Objects.hash(selectedTime, from, to);
    }
}
