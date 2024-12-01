package jg.practice.taskScheduler.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDto {

    private Long alIdx;
    private String title;
    private String content;
    private String deviceToken;
}
