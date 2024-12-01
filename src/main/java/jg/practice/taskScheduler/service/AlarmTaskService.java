package jg.practice.taskScheduler.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import jg.practice.taskScheduler.dto.AlarmTaskDto;
import jg.practice.taskScheduler.entity.Alarm;
import jg.practice.taskScheduler.entity.AlarmHistory;
import jg.practice.taskScheduler.entity.enums.Day;
import jg.practice.taskScheduler.repository.AlarmHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmTaskService {

    private final AlarmHistoryJpaRepository alarmHistoryJpaRepository;

    private final ApplicationEventPublisher eventPublisher;

    public void register(Alarm alarm) {
        // 다음 알림 시간 구하기
        LocalDateTime nextAlarmTime;
        if (alarm.isRepetition()) {
            // 반복 알람
            nextAlarmTime = getNextAlarmTimeInRepetition(alarm);
        } else {
            // 일회용 알람
            nextAlarmTime = LocalDateTime.of(alarm.getDate(), alarm.getTime());
        }

        publish(alarm, nextAlarmTime);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerNextRepetitionAlarm(Alarm alarm) {
        LocalDateTime nextAlarmTime = getNextAlarmTimeInRepetition(alarm).plusMinutes(31);
        publish(alarm, nextAlarmTime);
    }

    private LocalDateTime getNextAlarmTimeInRepetition(Alarm alarm) {
        LocalDateTime now = LocalDateTime.now();
        Day today = Day.getDayFromDayOfWeek(now.getDayOfWeek());

        // 오늘 발송되어야 하는 알림
        if (now.toLocalTime().isBefore(alarm.getTime()) &&
                ((today.getValue() & alarm.getDaysOfWeek()) != 0)) {
            return LocalDateTime.of(now.toLocalDate(), alarm.getTime());
        }

        // 그 외
        List<Day> sortedList = Day.getSortedList(today);
        Day nextAlarmDay = sortedList.stream()
                .filter(day -> (day.getValue() & alarm.getDaysOfWeek()) != 0)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("요일 추출에 실패했습니다."));
        int weight = (nextAlarmDay.ordinal() - today.ordinal() + 7) % 7;
        return LocalDateTime.of(now.toLocalDate().plusDays(weight), alarm.getTime());
    }

    private void publish(Alarm alarm, LocalDateTime nextAlarmTime) {
        // 설정 시간에서 예상 소요 시간, 30분(여유시간) 빼기
        LocalDateTime alarmSendAt = nextAlarmTime.minusMinutes(30);
        Instant instant = alarmSendAt.atZone(ZoneId.of("Asia/Seoul")).toInstant();

        AlarmHistory alarmHistory = AlarmHistory.create(alarm, alarmSendAt);
        alarmHistory = alarmHistoryJpaRepository.save(alarmHistory);

        AlarmTaskDto alarmTaskDto = AlarmTaskDto.builder()
                .ahIdx(alarmHistory.getAhIdx())
                .instant(instant)
                .build();
        eventPublisher.publishEvent(alarmTaskDto);
    }
}
