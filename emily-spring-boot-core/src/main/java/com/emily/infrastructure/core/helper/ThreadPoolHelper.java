package com.emily.infrastructure.core.helper;

import com.emily.infrastructure.core.context.ioc.IOCContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池帮助类, TaskExecutionAutoConfiguration
 *
 * @author Emily
 * @since 2021/09/12
 */
public class ThreadPoolHelper {
    /**
     * 获取线程池
     *
     * @return ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
        try {
            return IOCContext.getBean(ThreadPoolTaskExecutor.class);
        } catch (Exception exception) {
            int poolSize = 8;
            int maxPoolSize = 64;
            int queueCapacity = 10000;
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            threadPoolTaskExecutor.setCorePoolSize(poolSize);
            threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
            threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
            threadPoolTaskExecutor.initialize();
            return threadPoolTaskExecutor;
        }
    }
}
