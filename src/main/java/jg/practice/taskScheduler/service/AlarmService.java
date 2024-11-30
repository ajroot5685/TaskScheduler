package jg.practice.taskScheduler.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import jg.practice.taskScheduler.dto.request.AlarmCreateReq;
import jg.practice.taskScheduler.dto.request.AlarmUpdateReq;
import jg.practice.taskScheduler.entity.Alarm;
import jg.practice.taskScheduler.entity.enums.DaysOfWeek;
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

    private int getDaysOfWeek(List<DaysOfWeek> req) {
        int daysOfWeek = 0;
        if (req != null && !req.isEmpty()) {
            for (DaysOfWeek day : req) {
                daysOfWeek += day.getValue();
            }
        }
        return daysOfWeek;
    }
}
