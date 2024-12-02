package jg.practice.taskScheduler.service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import jg.practice.taskScheduler.dto.AlarmTaskDto;
import jg.practice.taskScheduler.entity.AlarmHistory;
import jg.practice.taskScheduler.repository.AlarmHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AlarmTaskEventListener {

    private final TaskLogicService taskLogicService;
    private final AlarmHistoryJpaRepository alarmHistoryJpaRepository;

    private final ThreadPoolTaskScheduler taskScheduler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void registerTask(AlarmTaskDto alarmTaskDto) {
        taskScheduler.schedule(() -> taskLogicService.alarmTask(alarmTaskDto.getAhIdx()), alarmTaskDto.getInstant());
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void reRegistrationPendingAlarms() {
        List<AlarmHistory> alarmHistories = alarmHistoryJpaRepository.findAllPendingAlarmWithSLock();

        alarmHistories.forEach(alarmHistory -> {
            Instant instant = alarmHistory.getSendAt().atZone(ZoneId.of("Asia/Seoul")).toInstant();
            taskScheduler.schedule(() -> taskLogicService.alarmTask(alarmHistory.getAhIdx()), instant);
        });
    }
}
