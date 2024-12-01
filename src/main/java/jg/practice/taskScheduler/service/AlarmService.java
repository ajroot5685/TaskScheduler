package jg.practice.taskScheduler.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jg.practice.taskScheduler.dto.request.AlarmCreateReq;
import jg.practice.taskScheduler.dto.request.AlarmUpdateReq;
import jg.practice.taskScheduler.entity.Alarm;
import jg.practice.taskScheduler.entity.enums.Day;
import jg.practice.taskScheduler.repository.AlarmJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmJpaRepository alarmJpaRepository;

    public Long create(AlarmCreateReq req) {
        int daysOfWeek = getDaysOfWeek(req.getDaysOfWeeks());
        validateRepetition(req.getDate(), daysOfWeek);

        Alarm alarm = Alarm.create(req.getTime(), req.getDate(), daysOfWeek);
        alarm = alarmJpaRepository.save(alarm);

        return alarm.getAlIdx();
    }

    public Long update(AlarmUpdateReq req) {
        Alarm alarm = alarmJpaRepository.findById(req.getAlIdx())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객체"));
        int daysOfWeek = getDaysOfWeek(req.getDaysOfWeeks());
        validateRepetition(req.getDate(), daysOfWeek);

        alarm.update(req.getTime(), req.getDate(), daysOfWeek);

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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextAlarmTime;
        if (alarm.isRepetition()) {
            nextAlarmTime = getNextAlarmTimeInRepetition(alarm);
        } else {
            nextAlarmTime = LocalDateTime.of(alarm.getDate(), alarm.getTime());
        }


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
