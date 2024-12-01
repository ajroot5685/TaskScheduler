package jg.practice.taskScheduler.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import jg.practice.taskScheduler.entity.enums.Day;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmCreateReq {

    @NotNull
    private LocalTime time;

    private LocalDate date; // 일반 알람인 경우 설정

    private List<Day> daysOfWeeks; // 반복 알람인 경우 설정
}
