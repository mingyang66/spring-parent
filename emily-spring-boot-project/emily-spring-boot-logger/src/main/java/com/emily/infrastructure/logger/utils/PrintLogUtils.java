package com.emily.infrastructure.logger.utils;

import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.function.Supplier;


/**
 * 日志工具类
 *
 * @author :  Emily
 * @since :  2024/1/1 4:12 PM
 */
public class PrintLogUtils {
    /**
     * 记录请求日志
     *
     * @param message 日志信息
     */
    public static void printRequest(String message) {
        ThreadPoolLogHelper.defaultThreadPoolTaskExecutor().submit(() -> LogHolder.REQUEST.info(message));
    }

    /**
     * 记录请求日志
     *
     * @param supplier 日志信息
     */
    public static void printRequest(Supplier<String> supplier) {
        ThreadPoolLogHelper.defaultThreadPoolTaskExecutor().submit(() -> LogHolder.REQUEST.info(supplier.get()));
    }

    /**
     * 记录三方请求日志
     *
     * @param message 日志信息
     */
    public static void printThirdParty(String message) {
        ThreadPoolLogHelper.defaultThreadPoolTaskExecutor().submit(() -> LogHolder.THIRDPARTY.info(message));
    }

    /**
     * 记录三方请求日志
     *
     * @param supplier 日志信息
     */
    public static void printThirdParty(Supplier<String> supplier) {
        ThreadPoolLogHelper.defaultThreadPoolTaskExecutor().submit(() -> LogHolder.THIRDPARTY.info(supplier.get()));
    }

    /**
     * 记录应用程序请求日志
     *
     * @param message 日志信息
     */
    public static void printPlatform(String message) {
        ThreadPoolLogHelper.defaultThreadPoolTaskExecutor().submit(() -> LogHolder.PLATFORM.info(message));
    }

    /**
     * 记录应用程序请求日志
     *
     * @param supplier 日志信息
     */
    public static void printPlatform(Supplier<String> supplier) {
        ThreadPoolLogHelper.defaultThreadPoolTaskExecutor().submit(() -> LogHolder.PLATFORM.info(supplier.get()));
    }

    static class LogHolder {
        private static final Logger REQUEST = LoggerFactory.getModuleLogger(PrintLogUtils.class, "request", "request");
        private static final Logger THIRDPARTY = LoggerFactory.getModuleLogger(PrintLogUtils.class, "thirdParty", "thirdParty");
        private static final Logger PLATFORM = LoggerFactory.getModuleLogger(PrintLogUtils.class, "platform", "platform");
    }

    public static class ThreadPoolLogHelper {
        private static ThreadPoolTaskExecutor taskExecutor;
        private static ApplicationContext context;

        public static void init(ApplicationContext context) {
            ThreadPoolLogHelper.context = context;
        }

        /**
         * 获取线程池
         *
         * @since 20230811 新增获取默认线程池
         */
        static ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
            try {
                if (Objects.nonNull(taskExecutor)) {
                    return taskExecutor;
                }
                taskExecutor = context.getBean(ThreadPoolTaskExecutor.class);
            } catch (Exception exception) {
                int poolSize = 8;
                int maxPoolSize = 64;
                int queueCapacity = 10000;
                taskExecutor = new ThreadPoolTaskExecutor();
                taskExecutor.setCorePoolSize(poolSize);
                taskExecutor.setMaxPoolSize(maxPoolSize);
                taskExecutor.setQueueCapacity(queueCapacity);
                taskExecutor.initialize();
            }
            return taskExecutor;
        }
    }
}
