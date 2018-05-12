package utils;

import model.Time;
import model.TimeWithFiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public final class TimeUtils {
    public static List<Time> prepareTimePoints(LocalDate date, List<LocalTime> timePoints, String offset) {
        int off = Integer
                .parseInt(
                        offset
                                .substring(0, 2)
                                .trim()
                );

        List<Time> preparedTimes = timePoints
                .stream()
                .filter(Objects::nonNull)
                .map(i -> new Time(
                        i.atDate(date),
                        i.atDate(date).minusMinutes(off),
                        i.atDate(date).plusMinutes(off)
                ))
                .sorted(Comparator.comparing(Time::getSelectedTime))
                .collect(Collectors.toList());
        
        return findAndMergeOverLapinTimes(preparedTimes, off);
    }

    private static List<Time> findAndMergeOverLapinTimes(List<Time> times, int off) {
        ListIterator<Time> timeListIterator = times.listIterator();

        while (timeListIterator.hasNext()) {
            Time first = timeListIterator.next();
            Time second;
            if (timeListIterator.hasNext()) {
                second = timeListIterator.next();
            } else {
                break;
            }
            if (isMergingNeeded(first, second, off)) {
                second.setFrom(first.getFrom());
                timeListIterator.previous();
                timeListIterator.previous();
                timeListIterator.remove();
            }
            if (timeListIterator.hasPrevious()) timeListIterator.previous();
        }
        return times;
    }

    private static boolean isMergingNeeded(Time first, Time second, int off) {
        return second.getSelectedTime().atZone(ZoneId.systemDefault()).toEpochSecond()
                - first.getSelectedTime().atZone(ZoneId.systemDefault()).toEpochSecond() <= off * 60;
    }

    public static List<TimeWithFiles> prepareTimesWithFiles(List<String> files, List<Time> times){
        return times
                .stream()
                .map(i-> new TimeWithFiles(i, files))
                .collect(Collectors.toList());
    }
}
