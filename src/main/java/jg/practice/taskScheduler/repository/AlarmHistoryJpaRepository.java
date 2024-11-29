package jg.practice.taskScheduler.repository;

import jg.practice.taskScheduler.entity.AlarmHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmHistoryJpaRepository extends JpaRepository<AlarmHistory, Long> {
}
