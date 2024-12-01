package jg.practice.taskScheduler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jg.practice.taskScheduler.entity.enums.AlarmStatus;
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
public class AlarmHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ahIdx;

    @Column(nullable = false)
    @Builder.Default
    private String title = ""; // 알림 제목

    @Column(nullable = false)
    @Builder.Default
    private String content = ""; // 알림 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "al_idx")
    private Alarm alarm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private AlarmStatus alStatus;

    public static AlarmHistory create(Alarm alarm) {
        return AlarmHistory.builder()
                .alarm(alarm)
                .alStatus(AlarmStatus.PENDING)
                .build();
    }
}
