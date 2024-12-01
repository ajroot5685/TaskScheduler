package jg.practice.taskScheduler.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDto {

    private Long ahIdx;
    private String title;
    private String content;
    private String deviceToken;
}
