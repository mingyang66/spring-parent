package com.emily.boot.autoconfigure.threadpool;

import com.emily.boot.common.exception.PrintExceptionInfo;
import com.emily.boot.common.utils.constant.CharacterUtils;
import com.emily.boot.common.utils.log.LoggerUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program: spring-parent
 * @description: 异步线程池配置 AsyncConfigurer在applicationContext早期初始化，如果需要依赖于其它的bean，尽可能的将它们声明为lazy
 * @EnableAsync注解的order属性设置为Ordered.LOWEST_PRECEDENCE，是为了AsyncAnnotationBeanPostProcessor的执行顺序在其它后处理器之后，
 * 这样它就可以向现有的代理中添加代理（拦截器），而不是双重代理
 * @AutoConfigureBefore(TaskExecutionAutoConfiguration.class)注解是为了让容器中不创建默认的线程池
 * @create: 2020/08/21
 */
@EnableAsync
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(TaskExecutionAutoConfiguration.class)
@EnableConfigurationProperties(AsyncThreadPoolProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.async-thread-pool", name = "enable", havingValue = "true", matchIfMissing = false)
public class AsyncThreadPoolAutoConfiguration implements AsyncConfigurer, InitializingBean, DisposableBean {

    private AsyncThreadPoolProperties asyncThreadPoolProperties;

    public AsyncThreadPoolAutoConfiguration(AsyncThreadPoolProperties asyncThreadPoolProperties){
        this.asyncThreadPoolProperties = asyncThreadPoolProperties;
    }
    /**
     * 定义线程池
     * 使用{@link java.util.concurrent.LinkedBlockingQueue}(FIFO）队列，是一个用于并发环境下的阻塞队列集合类
     * ThreadPoolTaskExecutor不是完全被IOC容器管理的bean,可以在方法上加上@Bean注解交给容器管理,这样可以将taskExecutor.initialize()方法调用去掉，容器会自动调用;
     * 但是如果将@Configuration配置的proxyBeanMethods设置为false,即不使用代理模式，则需要添加上初始化方法
     * 在其它的Bean中要使用自定义的线程池要加上{@link org.springframework.context.annotation.Lazy}注解
     * @return
     */
    @Bean(ThreadPoolBeanNameUtils.THREAD_POOL_BEAN_NAME)
    @Override
    public Executor getAsyncExecutor() {
        //Java虚拟机可用的处理器数
        int processors = Runtime.getRuntime().availableProcessors();
        //定义线程池
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(Objects.nonNull(asyncThreadPoolProperties.getCorePoolSize()) ? asyncThreadPoolProperties.getCorePoolSize() : processors);
        //线程池最大线程数,默认：10000
        taskExecutor.setMaxPoolSize(Objects.nonNull(asyncThreadPoolProperties.getMaxPoolSize()) ? asyncThreadPoolProperties.getMaxPoolSize() : 10000);
        //线程队列最大线程数,默认：20000
        taskExecutor.setQueueCapacity(Objects.nonNull(asyncThreadPoolProperties.getQueueCapacity()) ? asyncThreadPoolProperties.getQueueCapacity() : 20000);
        //线程名称前缀
        taskExecutor.setThreadNamePrefix(StringUtils.isNotEmpty(asyncThreadPoolProperties.getThreadNamePrefix()) ? asyncThreadPoolProperties.getThreadNamePrefix() : "Async-ThreadPool-");
        //线程池中线程最大空闲时间，默认：60，单位：秒
        taskExecutor.setKeepAliveSeconds(asyncThreadPoolProperties.getKeepAliveSeconds());
        //核心线程是否允许超时，默认:false
        taskExecutor.setAllowCoreThreadTimeOut(asyncThreadPoolProperties.isAllowCoreThreadTimeOut());
        //IOC容器关闭时是否阻塞等待剩余的任务执行完成，默认:false（必须设置setAwaitTerminationSeconds）
        taskExecutor.setWaitForTasksToCompleteOnShutdown(asyncThreadPoolProperties.isWaitForTasksToCompleteOnShutdown());
        //阻塞IOC容器关闭的时间，默认：10秒（必须设置setWaitForTasksToCompleteOnShutdown）
        taskExecutor.setAwaitTerminationSeconds(asyncThreadPoolProperties.getAwaitTerminationSeconds());
        /**
         * 拒绝策略，默认是AbortPolicy
         * AbortPolicy：丢弃任务并抛出RejectedExecutionException异常
         * DiscardPolicy：丢弃任务但不抛出异常
         * DiscardOldestPolicy：丢弃最旧的处理程序，然后重试，如果执行器关闭，这时丢弃任务
         * CallerRunsPolicy：执行器执行任务失败，则在策略回调方法中执行任务，如果执行器关闭，这时丢弃任务
         */
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //初始化，如果配置类的proxyBeanMethods代理设置为ture,则容器可以自动调用此方法，否则需要手动初始化
        taskExecutor.initialize();

        return taskExecutor;
    }

    /**
     * 异步方法执行的过程中抛出的异常捕获
     *
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            String msg = StringUtils.EMPTY;
            if (ArrayUtils.isNotEmpty(objects) && objects.length > 0) {
                msg = StringUtils.join(msg, "参数是：");
                for (int i = 0; i < objects.length; i++) {
                    msg = StringUtils.join(msg, objects[i], CharacterUtils.ENTER);
                }
            }
            if (Objects.nonNull(throwable)) {
                msg = StringUtils.join(msg, PrintExceptionInfo.printErrorInfo(throwable));
            }
            LoggerUtils.error(method.getDeclaringClass(), msg);
        };
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(AsyncThreadPoolAutoConfiguration.class, "【销毁--自动化配置】----异步线程池组件【AsyncThreadPoolAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(AsyncThreadPoolAutoConfiguration.class, "【初始化--自动化配置】----异步线程池组件【AsyncThreadPoolAutoConfiguration】");
    }
}
