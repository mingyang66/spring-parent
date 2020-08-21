package com.yaomy.control.test.config;

import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * @program: spring-parent
 * @description: 异步线程池配置
 * @create: 2020/08/21
 */
@EnableAsync
@Configuration
public class AsyncThreadPoolConfigurer implements AsyncConfigurer {
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
        taskExecutor.setCorePoolSize(processors);
        //线程池最大线程数
        taskExecutor.setMaxPoolSize(processors * 10);
        //线程队列最大线程数
        taskExecutor.setQueueCapacity(processors * 100);
        //线程名称前缀
        taskExecutor.setThreadNamePrefix("Async-ThreadPool-");
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
            LoggerUtils.error(method.getDeclaringClass(), StringUtils.join( method.getDeclaringClass().getName(), CharacterUtils.POINT_SYMBOL, method.getName(), "()发生异常，参数是：", JSONUtils.toJSONString(objects), ",异常：", throwable));
        };
    }
}
