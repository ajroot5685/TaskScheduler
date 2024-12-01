package jg.practice.taskScheduler.entity.enums;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Day {

    MONDAY(1 << 0, DayOfWeek.MONDAY), // 0000001
    TUESDAY(1 << 1, DayOfWeek.TUESDAY), // 0000010
    WEDNESDAY(1 << 2, DayOfWeek.WEDNESDAY), // 0000100
    THURSDAY(1 << 3, DayOfWeek.THURSDAY), // 0001000
    FRIDAY(1 << 4, DayOfWeek.FRIDAY), // 0010000
    SATURDAY(1 << 5, DayOfWeek.SATURDAY), // 0100000
    SUNDAY(1 << 6, DayOfWeek.SUNDAY), // 1000000
    ;

    private final int value;
    private final DayOfWeek dayOfWeek;

    public static Day getDayFromDayOfWeek(DayOfWeek dayOfWeek) {
        return Arrays.stream(Day.values())
                .filter(day -> day.dayOfWeek.equals(dayOfWeek))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요일입니다."));
    }

    public static List<Day> getDaysOfWeeksFromInt(int daysOfWeek) {
        List<Day> result = new ArrayList<>();
        for (Day value : Day.values()) {
            if ((value.getValue() & daysOfWeek) != 0) {
                result.add(value);
            }
        }
        return result;
    }

    public static List<Day> getSortedList(Day today) {
        // 오늘 이후 요일이 첫번째에 위치하도록 정렬
        Day[] days = Day.values();
        int todayIndex = today.ordinal();

        List<Day> result = new ArrayList<>();
        result.addAll(Arrays.asList(Arrays.copyOfRange(days, todayIndex + 1, days.length)));
        result.addAll(Arrays.asList(Arrays.copyOfRange(days, 0, todayIndex + 1)));

        return result;
    }
}
