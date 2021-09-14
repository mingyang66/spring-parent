package com.emily.infrastructure.context.helper;

import com.emily.infrastructure.context.ioc.IOCContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @program: spring-parent
 * @description: 线程池帮助类,TaskExecutionAutoConfiguration
 * @author: Emily
 * @create: 2021/09/12
 */
public class ThreadPoolHelper {
    /**
     * 获取线程池
     *
     * @return ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        return IOCContext.getBean(ThreadPoolTaskExecutor.class);
    }
}
