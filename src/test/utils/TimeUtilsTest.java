package utils;

import model.Time;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class TimeUtilsTest {

    @Test
    public void testMerging1() {
        LocalDate now = LocalDate.now();
        String offset = "15";
        List<LocalTime> localTimes = Arrays.asList(LocalTime.of(20, 50),
                LocalTime.of(21, 40),
                LocalTime.of(21, 50));

        List<Time> times = TimeUtils.prepareTimePoints(LocalDate.now(), localTimes, offset);

        List<Time> expected = Arrays.asList(
                new Time(LocalDateTime.of(now, LocalTime.of(20, 50)),
                        LocalDateTime.of(now , LocalTime.of(20, 50)).minusMinutes(Integer.parseInt(offset)),
                        LocalDateTime.of(now , LocalTime.of(20, 50)).plusMinutes(Integer.parseInt(offset))
                        ),
                new Time(LocalDateTime.of(now, LocalTime.of(21, 50)),
                        LocalDateTime.of(now , LocalTime.of(21, 25)),
                        LocalDateTime.of(now , LocalTime.of(22, 5)))
                );

        Assertions.assertEquals(expected, times);

    }

    @Test
    public void testMerging2() {
        LocalDate now = LocalDate.now();
        String offset = "15";
        List<LocalTime> localTimes = Arrays.asList(
                LocalTime.of(20, 50),
                LocalTime.of(21, 40),
                LocalTime.of(21, 55)
        );

        List<Time> times = TimeUtils.prepareTimePoints(LocalDate.now(), localTimes, offset);

        List<Time> expected = Arrays.asList(
                new Time(LocalDateTime.of(now, LocalTime.of(20, 50)),
                        LocalDateTime.of(now , LocalTime.of(20, 50)).minusMinutes(Integer.parseInt(offset)),
                        LocalDateTime.of(now , LocalTime.of(20, 50)).plusMinutes(Integer.parseInt(offset))
                ),
                new Time(LocalDateTime.of(now, LocalTime.of(21, 55)),
                        LocalDateTime.of(now , LocalTime.of(21, 25)),
                        LocalDateTime.of(now , LocalTime.of(22, 10)))
        );

        Assertions.assertEquals(expected, times);

    }

    @Test
    public void testMerging3() {
        LocalDate now = LocalDate.now();
        String offset = "15";
        List<LocalTime> localTimes = Arrays.asList(
                LocalTime.of(20, 50),
                LocalTime.of(21, 40),
                LocalTime.of(21, 55),
                LocalTime.of(21, 50)
        );

        List<Time> times = TimeUtils.prepareTimePoints(LocalDate.now(), localTimes, offset);

        List<Time> expected = Arrays.asList(
                new Time(LocalDateTime.of(now, LocalTime.of(20, 50)),
                        LocalDateTime.of(now , LocalTime.of(20, 50)).minusMinutes(Integer.parseInt(offset)),
                        LocalDateTime.of(now , LocalTime.of(20, 50)).plusMinutes(Integer.parseInt(offset))
                ),
                new Time(LocalDateTime.of(now, LocalTime.of(21, 55)),
                        LocalDateTime.of(now , LocalTime.of(21, 25)),
                        LocalDateTime.of(now , LocalTime.of(22, 10)))
        );

        Assertions.assertEquals(expected, times);

    }

    @Test
    public void testMerging4() {
        LocalDate now = LocalDate.now();
        String offset = "15";
        List<LocalTime> localTimes = Arrays.asList(
                LocalTime.of(20, 50),
                LocalTime.of(21, 40),
                LocalTime.of(21, 55),
                LocalTime.of(21, 50),
                LocalTime.of(21, 25)
        );

        List<Time> times = TimeUtils.prepareTimePoints(LocalDate.now(), localTimes, offset);

        List<Time> expected = Arrays.asList(
                new Time(LocalDateTime.of(now, LocalTime.of(20, 50)),
                        LocalDateTime.of(now , LocalTime.of(20, 50)).minusMinutes(Integer.parseInt(offset)),
                        LocalDateTime.of(now , LocalTime.of(20, 50)).plusMinutes(Integer.parseInt(offset))
                ),
                new Time(LocalDateTime.of(now, LocalTime.of(21, 55)),
                        LocalDateTime.of(now , LocalTime.of(21, 10)),
                        LocalDateTime.of(now , LocalTime.of(22, 10)))
        );

        Assertions.assertEquals(expected, times);

    }

    @Test
    public void testMerging5() {
        LocalDate now = LocalDate.now();
        String offset = "15";
        List<LocalTime> localTimes = Arrays.asList(
                LocalTime.of(21, 50),
                LocalTime.of(21, 40),
                LocalTime.of(21, 55),
                LocalTime.of(21, 50),
                LocalTime.of(21, 25)
        );

        List<Time> times = TimeUtils.prepareTimePoints(LocalDate.now(), localTimes, offset);

        List<Time> expected = Arrays.asList(
                new Time(LocalDateTime.of(now, LocalTime.of(21, 55)),
                        LocalDateTime.of(now , LocalTime.of(21, 10)),
                        LocalDateTime.of(now , LocalTime.of(22, 10))
                )
        );

        Assertions.assertEquals(expected, times);

    }

    @Test
    public void nulltestMerging5() {
        LocalDate now = LocalDate.now();
        String offset = "15";
        List<LocalTime> localTimes = Arrays.asList(
                LocalTime.of(21, 50),
                LocalTime.of(21, 40),
                LocalTime.of(21, 55),
                LocalTime.of(21, 50),
                null
        );

        List<Time> times = TimeUtils.prepareTimePoints(LocalDate.now(), localTimes, offset);

        List<Time> expected = Arrays.asList(
                new Time(LocalDateTime.of(now, LocalTime.of(21, 55)),
                        LocalDateTime.of(now , LocalTime.of(21, 25)),
                        LocalDateTime.of(now , LocalTime.of(22, 10))
                )
        );

        Assertions.assertEquals(expected, times);

    }
}