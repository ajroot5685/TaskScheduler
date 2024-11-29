package jg.practice.taskScheduler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alIdx;

    @Column(nullable = false)
    private LocalTime time; // 설정 시간

    private LocalTime date; // 설정 날짜

    @Column(length = 7)
    private int daysOfWeek; // 반복 요일


    public Alarm create(LocalTime time, LocalTime date, int daysOfWeek) {
        return Alarm.builder()
                .time(time)
                .date(date)
                .daysOfWeek(daysOfWeek)
                .build();
    }

    public void update(LocalTime time, LocalTime date, int daysOfWeek) {
        this.time = time;
        this.date = date;
        this.daysOfWeek = daysOfWeek;
    }
}