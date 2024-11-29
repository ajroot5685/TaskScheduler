package jg.practice.taskScheduler.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DaysOfWeek {

    MONDAY(1 << 0), // 0000001
    TUESDAY(1 << 1), // 0000010
    WEDNESDAY(1 << 2), // 0000100
    THURSDAY(1 << 3), // 0001000
    FRIDAY(1 << 4), // 0010000
    SATURDAY(1 << 5), // 0100000
    SUNDAY(1 << 6), // 1000000
    ;

    private final int value;
}
