package org.dromara.common.core.config;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.SpringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledExecutorService;

@AutoConfiguration
@RequiredArgsConstructor
public class VirtualThreadExecutionConfig implements SchedulingConfigurer {

    public static final String ASYNC_TASK_EXECUTOR_BEAN = "taskExecutor";

    private final ScheduledExecutorService scheduledExecutorService;

    @Bean(name = ASYNC_TASK_EXECUTOR_BEAN)
    public TaskExecutor taskExecutor() {
        if (SpringUtils.isVirtual()) {
            return new VirtualThreadTaskExecutor("virtual-async-");
        }
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("async-");
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
        executor.setMaxPoolSize((Runtime.getRuntime().availableProcessors() + 1) * 4);
        executor.setQueueCapacity(1024);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduledExecutorService);
    }
}
