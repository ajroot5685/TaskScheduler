package jg.practice.taskScheduler.repository;

import java.util.List;
import jg.practice.taskScheduler.entity.AlarmHistory;
import jg.practice.taskScheduler.entity.enums.AlarmStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmHistoryJpaRepository extends JpaRepository<AlarmHistory, Long> {

    List<AlarmHistory> findAllByAlStatus(AlarmStatus alStatus);
}
