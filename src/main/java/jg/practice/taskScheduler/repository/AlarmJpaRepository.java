package jg.practice.taskScheduler.repository;

import jg.practice.taskScheduler.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmJpaRepository extends JpaRepository<Alarm, Long> {
}
