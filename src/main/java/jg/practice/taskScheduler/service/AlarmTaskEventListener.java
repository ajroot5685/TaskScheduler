package jg.practice.taskScheduler.service;

import jg.practice.taskScheduler.dto.AlarmTaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmTaskEventListener {

    private final TaskLogic taskLogic;
    private final ThreadPoolTaskScheduler taskScheduler;

    @EventListener
    public void registerTask(AlarmTaskDto alarmTaskDto) {
        taskScheduler.schedule(() -> taskLogic.alarmTask(alarmTaskDto.getAlIdx()), alarmTaskDto.getInstant());
    }
}
