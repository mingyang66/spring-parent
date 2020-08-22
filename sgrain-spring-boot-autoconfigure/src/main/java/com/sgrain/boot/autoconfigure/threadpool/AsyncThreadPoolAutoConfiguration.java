package com.sgrain.boot.autoconfigure.threadpool;

import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program: spring-parent
 * @description: 异步线程池配置
 * @create: 2020/08/21
 */
@EnableAsync
@Configuration
@EnableConfigurationProperties(AsyncThreadPoolProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.async-thread-pool", name = "enable", havingValue = "true", matchIfMissing = false)
public class AsyncThreadPoolAutoConfiguration implements AsyncConfigurer {

    @Autowired
    private AsyncThreadPoolProperties asyncThreadPoolProperties;
    /**
     * 定义线程池
     *
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        //Java虚拟机可用的处理器数
        int processors = Runtime.getRuntime().availableProcessors();
        //定义线程池
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(Objects.nonNull(asyncThreadPoolProperties.getCorePoolSize()) ? asyncThreadPoolProperties.getCorePoolSize() : processors);
        //线程池最大线程数
        taskExecutor.setMaxPoolSize(Objects.nonNull(asyncThreadPoolProperties.getMaxPoolSize()) ? asyncThreadPoolProperties.getMaxPoolSize() : processors*100);
        //线程队列最大线程数
        taskExecutor.setQueueCapacity(Objects.nonNull(asyncThreadPoolProperties.getMaxPoolSize()) ? asyncThreadPoolProperties.getMaxPoolSize() : processors*1000);
        //线程名称前缀
        taskExecutor.setThreadNamePrefix(StringUtils.isNotEmpty(asyncThreadPoolProperties.getThreadNamePrefix()) ? asyncThreadPoolProperties.getThreadNamePrefix() : "Async-ThreadPool-");
        //线程池中线程最大空闲时间，默认：60，单位：秒
        taskExecutor.setKeepAliveSeconds(asyncThreadPoolProperties.getKeepAliveSeconds());
        /**
         * 拒绝策略，默认是AbortPolicy
         * AbortPolicy：丢弃任务并抛出RejectedExecutionException异常
         * DiscardPolicy：丢弃任务但不抛出异常
         * DiscardOldestPolicy：丢弃最旧的处理程序，然后重试，如果执行器关闭，这时丢弃任务
         * CallerRunsPolicy：执行器执行任务失败，则在策略回调方法中执行任务，如果执行器关闭，这时丢弃任务
         */
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //初始化
        taskExecutor.initialize();

        return taskExecutor;
    }

    /**
     * 异步方法执行的过程中抛出的异常捕获
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            LoggerUtils.error(method.getDeclaringClass(), StringUtils.join( method.getDeclaringClass().getName(), CharacterUtils.POINT_SYMBOL, method.getName(), "()发生异常：", throwable));
        };
    }
}
