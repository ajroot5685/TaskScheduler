package jg.practice.taskScheduler.service;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import jg.practice.taskScheduler.dto.AlarmTaskDto;
import jg.practice.taskScheduler.dto.request.AlarmCreateReq;
import jg.practice.taskScheduler.dto.request.AlarmUpdateReq;
import jg.practice.taskScheduler.entity.Alarm;
import jg.practice.taskScheduler.entity.AlarmHistory;
import jg.practice.taskScheduler.entity.enums.Day;
import jg.practice.taskScheduler.repository.AlarmHistoryJpaRepository;
import jg.practice.taskScheduler.repository.AlarmJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmJpaRepository alarmJpaRepository;
    private final AlarmHistoryJpaRepository alarmHistoryJpaRepository;

    private final ApplicationEventPublisher eventPublisher;

    public Long create(AlarmCreateReq req) {
        int daysOfWeek = getDaysOfWeek(req.getDaysOfWeeks());
        validateRepetition(req.getDate(), daysOfWeek);

        Alarm alarm = Alarm.create(req.getTime(), req.getDate(), daysOfWeek);
        alarm = alarmJpaRepository.save(alarm);

        taskRegistration(alarm);

        return alarm.getAlIdx();
    }

    public Long update(AlarmUpdateReq req) {
        Alarm alarm = alarmJpaRepository.findById(req.getAlIdx())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객체"));
        int daysOfWeek = getDaysOfWeek(req.getDaysOfWeeks());
        validateRepetition(req.getDate(), daysOfWeek);

        alarm.update(req.getTime(), req.getDate(), daysOfWeek);

        taskRegistration(alarm);

        return alarm.getAlIdx();
    }

    private void validateRepetition(LocalDate date, int daysOfWeek) {
        if ((date == null && daysOfWeek == 0)
                || (date != null && daysOfWeek != 0)) {
            throw new IllegalArgumentException("날짜설정과 반복설정 중 하나만 설정 가능합니다.");
        }
    }

    private int getDaysOfWeek(List<Day> req) {
        int daysOfWeek = 0;
        if (req != null && !req.isEmpty()) {
            for (Day day : req) {
                daysOfWeek |= day.getValue();
            }
        }
        return daysOfWeek;
    }

    private void taskRegistration(Alarm alarm) {
        // 다음 알림 시간 구하기
        LocalDateTime nextAlarmTime;
        if (alarm.isRepetition()) {
            // 반복 알람
            nextAlarmTime = getNextAlarmTimeInRepetition(alarm);
        } else {
            // 일회용 알람
            nextAlarmTime = LocalDateTime.of(alarm.getDate(), alarm.getTime());
        }

        // 설정 시간에서 예상 소요 시간, 30분(여유시간) 빼기
        Instant alarmSendAt = nextAlarmTime.minusMinutes(30).atZone(ZoneId.of("Asia/Seoul")).toInstant();
        AlarmHistory alarmHistory = AlarmHistory.create(alarm);
        alarmHistory = alarmHistoryJpaRepository.save(alarmHistory);

        AlarmTaskDto alarmTaskDto = AlarmTaskDto.builder()
                .alIdx(alarmHistory.getAhIdx())
                .instant(alarmSendAt)
                .build();
        eventPublisher.publishEvent(alarmTaskDto);
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
}
