package jg.practice.taskScheduler.service;

import java.time.LocalDate;
import jg.practice.taskScheduler.dto.request.AlarmCreateReq;
import jg.practice.taskScheduler.dto.request.AlarmUpdateReq;
import jg.practice.taskScheduler.entity.Alarm;
import jg.practice.taskScheduler.entity.AlarmHistory;
import jg.practice.taskScheduler.entity.enums.AlarmStatus;
import jg.practice.taskScheduler.entity.enums.Day;
import jg.practice.taskScheduler.repository.AlarmHistoryJpaRepository;
import jg.practice.taskScheduler.repository.AlarmJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmJpaRepository alarmJpaRepository;
    private final AlarmHistoryJpaRepository alarmHistoryJpaRepository;

    private final AlarmTaskService alarmTaskService;

    public Long create(AlarmCreateReq req) {
        int daysOfWeek = Day.getDaysOfWeek(req.getDaysOfWeeks());
        validateRepetition(req.getDate(), daysOfWeek);

        Alarm alarm = Alarm.create(req.getTime(), req.getDate(), daysOfWeek);
        alarm = alarmJpaRepository.save(alarm);

        alarmTaskService.register(alarm);

        return alarm.getAlIdx();
    }

    public Long update(AlarmUpdateReq req) {
        Alarm alarm = alarmJpaRepository.findById(req.getAlIdx())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객체"));
        int daysOfWeek = Day.getDaysOfWeek(req.getDaysOfWeeks());
        validateRepetition(req.getDate(), daysOfWeek);

        AlarmHistory alarmHistory = alarmHistoryJpaRepository.findById(req.getAlIdx())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객체"));
        alarmHistory.setAlStatus(AlarmStatus.CANCEL);
        alarmHistoryJpaRepository.save(alarmHistory);

        alarm.update(req.getTime(), req.getDate(), daysOfWeek);
        alarm = alarmJpaRepository.save(alarm);

        alarmTaskService.register(alarm);

        return alarm.getAlIdx();
    }

    private void validateRepetition(LocalDate date, int daysOfWeek) {
        if ((date == null && daysOfWeek == 0)
                || (date != null && daysOfWeek != 0)) {
            throw new IllegalArgumentException("날짜설정과 반복설정 중 하나만 설정 가능합니다.");
        }
    }


}
