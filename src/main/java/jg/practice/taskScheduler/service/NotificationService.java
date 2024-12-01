package jg.practice.taskScheduler.service;

import java.util.Random;
import jg.practice.taskScheduler.dto.NotificationDto;
import jg.practice.taskScheduler.entity.AlarmHistory;
import jg.practice.taskScheduler.entity.enums.AlarmStatus;
import jg.practice.taskScheduler.repository.AlarmHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationService {

    private final Random random = new Random();

    private final AlarmHistoryJpaRepository alarmHistoryJpaRepository;

    @Async
    @EventListener
    public void sendFcmNotification(NotificationDto notificationDto) {
        try {
            // FCM 알림 발송요철 로직 생략
            if (random.nextInt(1, 10) < 3) {
                // 발송 실패
                throw new IllegalArgumentException("FCM 발송 실패");
            }
        } catch (IllegalArgumentException e) {
            // 발송 실패 시 CANCEL 처리 - 보상 트랜잭션
            AlarmHistory alarmHistory = alarmHistoryJpaRepository.findById(notificationDto.getAhIdx())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객체"));
            alarmHistory.setAlStatus(AlarmStatus.CANCEL);
            alarmHistoryJpaRepository.save(alarmHistory);
        }
    }
}
