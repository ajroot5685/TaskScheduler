package jg.practice.taskScheduler.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import jg.practice.taskScheduler.entity.AlarmHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmHistoryJpaRepository extends JpaRepository<AlarmHistory, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select ah from AlarmHistory ah where ah.ahStatus = 'PENDING'")
    List<AlarmHistory> findAllPendingAlarmWithSLock();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ah from AlarmHistory ah where ah.ahIdx = :ahIdx")
    Optional<AlarmHistory> findByIdWithXLock(@Param("ahIdx") Long ahIdx);

    @Modifying
    @Query("update AlarmHistory ah set ah.ahStatus = 'CANCEL' where ah.alarm.alIdx = :alIdx and ah.ahStatus = 'PENDING'")
    void cancelPendingAlarm(@Param("alIdx") Long alIdx);
}
