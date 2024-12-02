package jg.practice.taskScheduler.service;

import java.util.Optional;
import java.util.Random;
import jg.practice.taskScheduler.dto.NotificationDto;
import jg.practice.taskScheduler.entity.AlarmHistory;
import jg.practice.taskScheduler.entity.enums.AlarmStatus;
import jg.practice.taskScheduler.repository.AlarmHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final Random random = new Random();

    private final AlarmHistoryJpaRepository alarmHistoryJpaRepository;

    @Async
    @Transactional
    @EventListener
    public void sendFcmNotification(NotificationDto notificationDto) {
        try {
            // FCM 알림 발송요철 로직 생략
            if (random.nextInt(1, 10) < 3) {
                // 발송 실패
                throw new IllegalArgumentException("FCM 발송 실패");
            }
            log.info("알림 발송: " + notificationDto.getTitle() + ", " + notificationDto.getContent());
        } catch (IllegalArgumentException e) {
            // 발송 실패 시 CANCEL 처리 - 보상 트랜잭션
            Optional<AlarmHistory> alarmHistoryOptional = alarmHistoryJpaRepository.findByIdWithXLock(
                    notificationDto.getAhIdx());

            // 오류로 인해 조회되지 않더라도 오류 발생 x -> 별도로 핸들링하기(ex. sentry를 통해 백엔드 팀에 알림 전송하기 등)
            alarmHistoryOptional.ifPresent(alarmHistory -> {
                alarmHistory.setAhStatus(AlarmStatus.CANCEL);
                alarmHistoryJpaRepository.save(alarmHistory);
            });
        }
    }
}
