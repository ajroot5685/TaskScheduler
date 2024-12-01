package jg.practice.taskScheduler.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AlarmTaskDto {

    private Long alIdx;
    private Instant instant;
}
