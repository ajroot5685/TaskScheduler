package jg.practice.taskScheduler.service;

import jg.practice.taskScheduler.dto.NotificationDto;
import jg.practice.taskScheduler.entity.Alarm;
import jg.practice.taskScheduler.entity.AlarmHistory;
import jg.practice.taskScheduler.entity.enums.AlarmStatus;
import jg.practice.taskScheduler.repository.AlarmHistoryJpaRepository;
import jg.practice.taskScheduler.repository.AlarmJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskLogic {

    private final AlarmJpaRepository alarmJpaRepository;
    private final AlarmHistoryJpaRepository alarmHistoryJpaRepository;

    private final ApplicationEventPublisher eventPublisher;

    public void alarmTask(Long alIdx) {
        AlarmHistory alarmHistory = alarmHistoryJpaRepository.findById(alIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객체"));

        Alarm alarm = alarmHistory.getAlarm();

        if (alarm.isRepetition()) {
            alarm.setActivate(false);
            alarmJpaRepository.save(alarm);
        } else {
            // 다음 반복 알림 설정 - 비동기

        }

        if (alarm.isActivate()) {
            // 기타 설정에 따라 알림 전송
            NotificationDto notificationDto = NotificationDto.builder()
                    .alIdx(alIdx)
                    .title("알림 제목")
                    .content("알림 내용")
                    .deviceToken("디바이스 토큰")
                    .build();
            eventPublisher.publishEvent(notificationDto);
        }

        // 비동기 알림발송 로직에서 발송실패 시 CANCEL로 수정
        alarmHistory.setAlStatus(AlarmStatus.COMPLETE);
        alarmHistoryJpaRepository.save(alarmHistory);
    }
}
