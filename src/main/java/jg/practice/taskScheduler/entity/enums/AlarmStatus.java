package jg.practice.taskScheduler.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmStatus {

    PENDING("대기중"),
    COMPLETE("발송 완료"),
    CANCEL("취소"),
    ;

    private String description;
}
