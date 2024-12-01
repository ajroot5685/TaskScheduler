package jg.practice.taskScheduler.service;

import jg.practice.taskScheduler.dto.AlarmTaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmTaskEventListener {

    @EventListener
    public void registerTask(AlarmTaskDto alarmTaskDto) {
        System.out.println("!!");
    }
}
