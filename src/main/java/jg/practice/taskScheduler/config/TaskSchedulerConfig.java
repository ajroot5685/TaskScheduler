package jg.practice.taskScheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class TaskSchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("TaskScheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 애플리케이션 종료 시 대기 여부
        scheduler.setAwaitTerminationSeconds(10); // 종료 대기시간
        return scheduler;
    }
}
