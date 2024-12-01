package jg.practice.taskScheduler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private LocalDate date; // 설정 날짜

    @Column(length = 7)
    private int daysOfWeek; // 반복 요일

    private boolean isRepetition; // 반복여부

    @Setter
    private boolean activate; // 사용자 설정에 따라 실행 여부를 결정하는 필드들(ex. 알림 on/off, 푸시알림 동의, 공휴일 등)

    public static Alarm create(LocalTime time, LocalDate date, int daysOfWeek) {
        return Alarm.builder()
                .time(time)
                .date(date)
                .daysOfWeek(daysOfWeek)
                .isRepetition(daysOfWeek != 0)
                .activate(true)
                .build();
    }

    public void update(LocalTime time, LocalDate date, int daysOfWeek) {
        this.time = time;
        this.date = date;
        this.daysOfWeek = daysOfWeek;
        this.isRepetition = daysOfWeek != 0;
    }
}